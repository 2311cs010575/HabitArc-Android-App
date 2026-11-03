import http.server
import socketserver
import json
import sqlite3
import os
from urllib.parse import urlparse, parse_qs

PORT = 8000
DB_FILE = 'habitarc_shared.db'

def init_db():
    conn = sqlite3.connect(DB_FILE)
    c = conn.cursor()
    # Shared Tables for App and Website
    c.execute('''CREATE TABLE IF NOT EXISTS expenses
                 (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, amount REAL, category TEXT, date INTEGER)''')
    c.execute('''CREATE TABLE IF NOT EXISTS usage_stats
                 (id INTEGER PRIMARY KEY AUTOINCREMENT, app_name TEXT, usage_ms INTEGER, date INTEGER)''')
    c.execute('''CREATE TABLE IF NOT EXISTS settings
                 (key TEXT PRIMARY KEY, value TEXT)''')

    # Seed data
    c.execute("INSERT OR IGNORE INTO settings VALUES ('budget_limit', '1000.0')")
    c.execute("SELECT COUNT(*) FROM expenses")
    if c.fetchone()[0] == 0:
        c.execute("INSERT INTO expenses (title, amount, category, date) VALUES ('Grocery', 50.0, 'Food', 1739990400000)")
        c.execute("INSERT INTO usage_stats (app_name, usage_ms, date) VALUES ('Instagram', 3600000, 1739990400000)")
    conn.commit()
    conn.close()

class HabitArcSharedHandler(http.server.SimpleHTTPRequestHandler):
    def _set_headers(self, status=200):
        self.send_response(status)
        self.send_header('Content-type', 'application/json')
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        self.end_headers()

    def do_OPTIONS(self):
        self._set_headers()

    def do_GET(self):
        parsed_path = urlparse(self.path)
        if parsed_path.path == '/api/sync':
            conn = sqlite3.connect(DB_FILE)
            conn.row_factory = sqlite3.Row
            c = conn.cursor()

            expenses = [dict(row) for row in c.execute("SELECT * FROM expenses").fetchall()]
            usage = [dict(row) for row in c.execute("SELECT * FROM usage_stats").fetchall()]
            budget = c.execute("SELECT value FROM settings WHERE key='budget_limit'").fetchone()[0]

            data = {
                "expenses": expenses,
                "usage": usage,
                "budget_limit": float(budget),
                "ai_recommendation": "Shared Sync Active: Spend and Usage data synchronized across devices."
            }
            self._set_headers()
            self.wfile.write(json.dumps(data).encode())
            conn.close()
        else:
            return http.server.SimpleHTTPRequestHandler.do_GET(self)

    def do_POST(self):
        parsed_path = urlparse(self.path)
        content_length = int(self.headers['Content-Length'])
        post_data = json.loads(self.rfile.read(content_length))

        conn = sqlite3.connect(DB_FILE)
        c = conn.cursor()

        if parsed_path.path == '/api/add_expense':
            c.execute("INSERT INTO expenses (title, amount, category, date) VALUES (?, ?, ?, ?)",
                      (post_data['title'], post_data['amount'], post_data['category'], post_data['date']))
            conn.commit()
            self._set_headers(201)
            self.wfile.write(json.dumps({"status": "success"}).encode())

        elif parsed_path.path == '/api/update_budget':
            c.execute("INSERT OR REPLACE INTO settings (key, value) VALUES ('budget_limit', ?)", (str(post_data['limit']),))
            conn.commit()
            self._set_headers(200)
            self.wfile.write(json.dumps({"status": "updated"}).encode())

        conn.close()

if __name__ == "__main__":
    init_db()
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    with socketserver.TCPServer(("", PORT), HabitArcSharedHandler) as httpd:
        print(f"SHARED SERVICE RUNNING: http://localhost:{PORT}")
        print("Listening for App and Website requests...")
        httpd.serve_forever()
