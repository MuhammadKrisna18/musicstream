# Music Streaming Backend (FastAPI + yt-dlp)

Backend server untuk pencarian lagu, ekstraksi stream audio (m4a/opus), dan unduhan audio.

## Cara Menjalankan Server

Jalankan perintah berikut di terminal:
```powershell
# Menggunakan virtual environment yang sudah disiapkan:
.\server\.venv\Scripts\python.exe -m uvicorn main:app --app-dir server --host 0.0.0.0 --port 8000 --reload
```

Server akan aktif di:
- API Base: `http://localhost:8000`
- Dokumentasi Swagger UI: `http://localhost:8000/docs`
- Dari Android Emulator: `http://10.0.2.2:8000`
- Dari HP Fisik (WiFi yang sama): `http://<IP_LAPTOP_ANDA>:8000`

## Menaruh Cookies YouTube

Letakkan file cookies yang sudah Anda ekspor di direktori ini dengan nama:
`server/cookies.txt`

File ini diabaikan secara otomatis oleh `.gitignore` sehingga aman dan tidak akan ter-commit ke repositori git.

## Endpoint Utama

1. `GET /api/status` — Cek status server, versi yt-dlp, dan apakah cookies terdeteksi.
2. `GET /api/search?q={judul_lagu}&limit=15` — Mencari daftar lagu di YouTube.
3. `GET /api/stream/{video_id}` — Mendapatkan URL stream audio m4a/opus untuk diputar oleh ExoPlayer Android.
4. `GET /api/download/{video_id}` — Mendapatkan URL unduhan dan metadata file audio.
