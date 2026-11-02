import os
import re
import time
import random
import concurrent.futures
from typing import Optional
from fastapi import FastAPI, HTTPException, Query, Request, Response
from fastapi.responses import StreamingResponse
from fastapi.middleware.cors import CORSMiddleware
import yt_dlp
import requests
import uvicorn

app = FastAPI(
    title="MusicStream API",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
COOKIES_FILE = os.path.join(BASE_DIR, "cookies.txt")

def format_duration(seconds: Optional[int]) -> str:
    if not seconds:
        return "0:00"
    minutes = seconds // 60
    secs = seconds % 60
    return f"{minutes}:{secs:02d}"

def get_base_ydl_opts():
    opts = {
        'format': 'bestaudio/best',
        'quiet': True,
        'no_warnings': True,
        'skip_download': True,
        'noplaylist': True,
    }
    if os.path.exists(COOKIES_FILE) and os.path.getsize(COOKIES_FILE) > 0:
        opts['cookiefile'] = COOKIES_FILE
    return opts

@app.get("/")
def root():
    return {
        "status": "online",
        "service": "Music Streaming API",
        "cookies_loaded": os.path.exists(COOKIES_FILE) and os.path.getsize(COOKIES_FILE) > 0,
        "docs_url": "/docs"
    }

@app.get("/api/status")
def server_status():
    has_cookies = os.path.exists(COOKIES_FILE) and os.path.getsize(COOKIES_FILE) > 0
    return {
        "status": "healthy",
        "cookies_present": has_cookies,
        "cookies_file_path": COOKIES_FILE if has_cookies else None,
        "yt_dlp_version": yt_dlp.version.__version__,
    }

@app.get("/api/categories")
def get_categories():
    return {
        "mood_tags": [
            {"id": "top_hits", "label": "🔥 Top Hits", "query": "Trending Music 2026"},
            {"id": "chill_lofi", "label": "☕ Chill Lofi", "query": "Lofi Chill Beats"},
            {"id": "workout", "label": "⚡ Workout", "query": "Workout Motivation Music"},
            {"id": "rock", "label": "🎸 Rock Hits", "query": "Rock Classics Hits"},
            {"id": "acoustic", "label": "🌙 Acoustic", "query": "Acoustic Pop Songs"},
            {"id": "jazz", "label": "🎷 Smooth Jazz", "query": "Smooth Jazz Instrumental"},
            {"id": "edm", "label": "🎧 EDM Beats", "query": "EDM Dance Hits"}
        ],
        "categories": [
            {
                "id": "top_hits",
                "title": "Top Hits",
                "query": "Top Hits 2026",
                "icon": "fire",
                "start_color": "#FF6D00",
                "end_color": "#D84315"
            },
            {
                "id": "rock_metal",
                "title": "Rock & Metal",
                "query": "Rock Classics Hits",
                "icon": "electric_bolt",
                "start_color": "#E65100",
                "end_color": "#263238"
            },
            {
                "id": "lofi_chill",
                "title": "Lofi & Chill",
                "query": "Lofi Chill Beats",
                "icon": "nightlife",
                "start_color": "#FF8F00",
                "end_color": "#3E2723"
            },
            {
                "id": "pop_vibes",
                "title": "Pop Vibes",
                "query": "Pop Music Hits",
                "icon": "music_note",
                "start_color": "#FFA000",
                "end_color": "#E65100"
            },
            {
                "id": "indie_wave",
                "title": "Indie Wave",
                "query": "Indie Pop Hits",
                "icon": "headphones",
                "start_color": "#BF360C",
                "end_color": "#1A237E"
            },
            {
                "id": "workout_edm",
                "title": "Workout & EDM",
                "query": "EDM Workout Music",
                "icon": "bolt",
                "start_color": "#FF5722",
                "end_color": "#4A148C"
            },
            {
                "id": "acoustic_cafe",
                "title": "Acoustic Cafe",
                "query": "Acoustic Pop Songs",
                "icon": "library_music",
                "start_color": "#D84315",
                "end_color": "#3E2723"
            },
            {
                "id": "jazz_blues",
                "title": "Jazz & Blues",
                "query": "Smooth Jazz Instrumental",
                "icon": "graphic_eq",
                "start_color": "#FF6D00",
                "end_color": "#0D47A1"
            }
        ]
    }

SECTION_VARIATIONS = {
    "trending": [
        {"title": "Trending Now", "subtitle": "Lagu paling populer dan viral saat ini", "query": "trending music hits 2026"},
        {"title": "Global Viral Hits", "subtitle": "Hits yang sedang meledak di seluruh dunia", "query": "viral songs billboard global hits 2026"},
        {"title": "Top Streaming Hits", "subtitle": "Lagu paling banyak didengarkan minggu ini", "query": "top music official video trending 2026"},
        {"title": "TikTok Viral Sounds", "subtitle": "Sound dan lagu terpopuler di media sosial", "query": "trending tiktok songs viral music 2026"}
    ],
    "indonesia_top": [
        {"title": "Top Hits Indonesia", "subtitle": "Musik terfavorit dan viral di Indonesia", "query": "top hits indonesia lagu viral 2026"},
        {"title": "Populer Tanah Air", "subtitle": "Koleksi hits pop terbaik musisi Indonesia", "query": "lagu pop indonesia terbaru hits viral"},
        {"title": "Indie Senja Indonesia", "subtitle": "Lagu indie lokal yang syahdu dan bermakna", "query": "indie pop indonesia hits playlist"},
        {"title": "Akustik Hits Indonesia", "subtitle": "Cover akustik lagu Indonesia terfavorit", "query": "akustik lagu indonesia hits santai"}
    ],
    "new_releases": [
        {"title": "Rilisan Terbaru", "subtitle": "Karya musik terbaru dari para musisi", "query": "official music video 2026 new release"},
        {"title": "Fresh Friday Music", "subtitle": "Lagu-lagu yang baru saja diluncurkan", "query": "latest official music video singles 2026"},
        {"title": "Lagu Baru Indonesia", "subtitle": "Single musik terbaru karya anak bangsa", "query": "lagu baru indonesia official music video 2026"},
        {"title": "International New Beats", "subtitle": "Rilisan terbaru musisi mancanegara", "query": "new international official music video 2026"}
    ],
    "chill_acoustic": [
        {"title": "Chill & Akustik", "subtitle": "Teman santai dan rileks hari ini", "query": "acoustic chill pop acoustic cover"},
        {"title": "Coffee & Lofi", "subtitle": "Irama santai untuk menemani harimu", "query": "lofi chill beats relaxing music study"},
        {"title": "Late Night Vibes", "subtitle": "Lagu lembut pengantar istirahat malam", "query": "late night acoustic chill songs peaceful"},
        {"title": "Senja & Santai", "subtitle": "Musik syahdu pengiring waktu luang", "query": "lagu santai sore cafe acoustic vibe"}
    ],
    "workout_party": [
        {"title": "Energy & Workout", "subtitle": "Semangat dengan beat cepat dan bertenaga", "query": "edm workout motivation party hits"},
        {"title": "EDM Party Hits", "subtitle": "Dentuman bas menghentak untuk pesta", "query": "edm dance festival party hits 2026"},
        {"title": "Gym Pump & Focus", "subtitle": "Bakar semangat latihanmu sekarang", "query": "gym workout motivation music pump 2026"},
        {"title": "Upbeat Dance Pop", "subtitle": "Lagu pop energik yang bikin semangat", "query": "high energy upbeat pop dance music hits"}
    ]
}

def fetch_tracks_for_query(query: str, limit: int = 10) -> list:
    ydl_opts = {
        'quiet': True,
        'no_warnings': True,
        'extract_flat': True,
        'skip_download': True,
    }
    if os.path.exists(COOKIES_FILE) and os.path.getsize(COOKIES_FILE) > 0:
        ydl_opts['cookiefile'] = COOKIES_FILE

    search_query = f"ytsearch{min(limit, 20)}:{query}"
    results = []
    try:
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            search_results = ydl.extract_info(search_query, download=False)
            entries = search_results.get('entries', []) if search_results else []
            for item in entries:
                if not item:
                    continue
                video_id = item.get('id')
                if not video_id:
                    continue
                duration_sec = item.get('duration') or 0
                thumbnails = item.get('thumbnails') or []
                thumbnail_url = thumbnails[-1].get('url', "") if thumbnails else f"https://i.ytimg.com/vi/{video_id}/hqdefault.jpg"
                results.append({
                    "id": video_id,
                    "title": item.get('title', 'Unknown Title'),
                    "artist": item.get('uploader') or item.get('channel') or 'Unknown Artist',
                    "duration": int(duration_sec) if duration_sec else 0,
                    "duration_formatted": format_duration(int(duration_sec) if duration_sec else 0),
                    "thumbnail": thumbnail_url,
                    "url": f"https://www.youtube.com/watch?v={video_id}"
                })
        return results
    except Exception:
        return []

home_feed_cache = {
    "timestamp": 0.0,
    "data": None
}

@app.get("/api/home/feed")
def get_home_feed(refresh: bool = False):
    current_time = time.time()
    if not refresh and home_feed_cache["data"] is not None and (current_time - home_feed_cache["timestamp"] < 1800):
        return home_feed_cache["data"]

    chosen_configs = []
    for section_id, variations in SECTION_VARIATIONS.items():
        selected = random.choice(variations)
        chosen_configs.append({
            "id": section_id,
            "title": selected["title"],
            "subtitle": selected["subtitle"],
            "query": selected["query"],
            "limit": 10
        })

    sections = []
    with concurrent.futures.ThreadPoolExecutor(max_workers=5) as executor:
        future_to_config = {
            executor.submit(fetch_tracks_for_query, cfg["query"], cfg["limit"]): cfg
            for cfg in chosen_configs
        }
        for future in concurrent.futures.as_completed(future_to_config):
            cfg = future_to_config[future]
            try:
                tracks = future.result()
            except Exception:
                tracks = []
            sections.append({
                "id": cfg["id"],
                "title": cfg["title"],
                "subtitle": cfg["subtitle"],
                "query": cfg["query"],
                "tracks": tracks
            })

    ordered_sections = []
    for cfg in chosen_configs:
        match = next((s for s in sections if s["id"] == cfg["id"]), None)
        if match:
            ordered_sections.append(match)

    feed_data = {
        "sections": ordered_sections,
        "mood_tags": get_categories()["mood_tags"],
        "categories": get_categories()["categories"]
    }
    home_feed_cache["timestamp"] = current_time
    home_feed_cache["data"] = feed_data
    return feed_data

@app.get("/api/trending")
def get_trending(limit: int = 12):
    ydl_opts = {
        'quiet': True,
        'no_warnings': True,
        'extract_flat': True,
        'skip_download': True,
    }
    if os.path.exists(COOKIES_FILE) and os.path.getsize(COOKIES_FILE) > 0:
        ydl_opts['cookiefile'] = COOKIES_FILE

    search_query = f"ytsearch{min(limit, 25)}:trending music video"
    results = []

    try:
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            search_results = ydl.extract_info(search_query, download=False)
            entries = search_results.get('entries', []) if search_results else []

            for item in entries:
                if not item:
                    continue
                video_id = item.get('id')
                if not video_id:
                    continue

                duration_sec = item.get('duration') or 0
                thumbnails = item.get('thumbnails') or []
                thumbnail_url = thumbnails[-1].get('url', "") if thumbnails else f"https://i.ytimg.com/vi/{video_id}/hqdefault.jpg"

                results.append({
                    "id": video_id,
                    "title": item.get('title', 'Unknown Title'),
                    "artist": item.get('uploader') or item.get('channel') or 'Unknown Artist',
                    "duration": int(duration_sec) if duration_sec else 0,
                    "duration_formatted": format_duration(int(duration_sec) if duration_sec else 0),
                    "thumbnail": thumbnail_url,
                    "url": f"https://www.youtube.com/watch?v={video_id}"
                })

        return {"count": len(results), "results": results}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/api/search")
def search_music(q: str = Query(..., description="Search query"), limit: int = 15):
    if not q.strip():
        raise HTTPException(status_code=400, detail="Search query cannot be empty")

    ydl_opts = {
        'quiet': True,
        'no_warnings': True,
        'extract_flat': True,
        'skip_download': True,
    }
    if os.path.exists(COOKIES_FILE) and os.path.getsize(COOKIES_FILE) > 0:
        ydl_opts['cookiefile'] = COOKIES_FILE

    search_query = f"ytsearch{min(limit, 30)}:{q}"
    results = []

    try:
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            search_results = ydl.extract_info(search_query, download=False)
            entries = search_results.get('entries', []) if search_results else []

            for item in entries:
                if not item:
                    continue
                video_id = item.get('id')
                if not video_id:
                    continue

                duration_sec = item.get('duration') or 0
                thumbnails = item.get('thumbnails') or []
                thumbnail_url = ""
                if thumbnails:
                    thumbnail_url = thumbnails[-1].get('url', "")
                elif video_id:
                    thumbnail_url = f"https://i.ytimg.com/vi/{video_id}/hqdefault.jpg"

                results.append({
                    "id": video_id,
                    "title": item.get('title', 'Unknown Title'),
                    "artist": item.get('uploader') or item.get('channel') or 'Unknown Artist',
                    "duration": int(duration_sec) if duration_sec else 0,
                    "duration_formatted": format_duration(int(duration_sec) if duration_sec else 0),
                    "thumbnail": thumbnail_url,
                    "url": f"https://www.youtube.com/watch?v={video_id}"
                })

        return {"query": q, "count": len(results), "results": results}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

def extract_raw_audio_url(video_id: str) -> dict:
    video_url = f"https://www.youtube.com/watch?v={video_id}"
    ydl_opts = get_base_ydl_opts()

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        info = ydl.extract_info(video_url, download=False)
        if not info:
            raise HTTPException(status_code=404, detail="Song not found")

        stream_url = None
        audio_format = None
        bitrate = None

        if 'url' in info:
            stream_url = info['url']
            audio_format = info.get('ext')
            bitrate = info.get('abr')
        else:
            formats = info.get('formats', [])
            audio_formats = [f for f in formats if f.get('vcodec') == 'none' and f.get('url')]
            if audio_formats:
                best_audio = max(audio_formats, key=lambda f: f.get('abr') or f.get('tbr') or 0)
                stream_url = best_audio.get('url')
                audio_format = best_audio.get('ext')
                bitrate = best_audio.get('abr')

        if not stream_url:
            raise HTTPException(status_code=500, detail="Cannot extract stream URL")

        duration = int(info.get('duration') or 0)
        thumbnails = info.get('thumbnails') or []
        thumbnail_url = thumbnails[-1].get('url', "") if thumbnails else f"https://i.ytimg.com/vi/{video_id}/hqdefault.jpg"

        return {
            "id": video_id,
            "title": info.get('title'),
            "artist": info.get('uploader') or info.get('channel') or "Unknown Artist",
            "raw_stream_url": stream_url,
            "duration": duration,
            "duration_formatted": format_duration(duration),
            "thumbnail": thumbnail_url,
            "format": audio_format,
            "bitrate": bitrate
        }

@app.get("/api/stream/{video_id}")
def get_stream_url(video_id: str, request: Request):
    try:
        raw_data = extract_raw_audio_url(video_id)
        base = str(request.base_url).rstrip('/')
        proxy_stream_url = f"{base}/api/audio/{video_id}"

        return {
            "id": video_id,
            "title": raw_data['title'],
            "artist": raw_data['artist'],
            "stream_url": proxy_stream_url,
            "duration": raw_data['duration'],
            "duration_formatted": raw_data['duration_formatted'],
            "thumbnail": raw_data['thumbnail'],
            "format": raw_data['format'],
            "bitrate": raw_data['bitrate']
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.api_route("/api/audio/{video_id}", methods=["GET", "HEAD"])
def stream_audio(video_id: str, request: Request):
    try:
        raw_data = extract_raw_audio_url(video_id)
        target_url = raw_data["raw_stream_url"]

        headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Accept": "*/*",
            "Connection": "keep-alive"
        }

        if request.method == "HEAD":
            upstream = requests.head(target_url, headers=headers, timeout=15)
            response_headers = {
                "Accept-Ranges": "bytes",
                "Cache-Control": "public, max-age=3600"
            }
            if "Content-Length" in upstream.headers:
                response_headers["Content-Length"] = upstream.headers["Content-Length"]
            return Response(
                status_code=200,
                media_type=upstream.headers.get("Content-Type", "audio/mp4"),
                headers=response_headers
            )

        range_header = request.headers.get("Range")
        if range_header:
            headers["Range"] = range_header

        upstream = requests.get(target_url, headers=headers, stream=True, timeout=30)
        if upstream.status_code not in (200, 206):
            raise HTTPException(status_code=upstream.status_code, detail="Upstream audio error")

        media_type = upstream.headers.get("Content-Type", "audio/mp4")
        content_length = upstream.headers.get("Content-Length")

        def iter_chunks():
            try:
                for chunk in upstream.iter_content(chunk_size=65536):
                    if chunk:
                        yield chunk
            finally:
                upstream.close()

        response_headers = {
            "Accept-Ranges": "bytes",
            "Cache-Control": "public, max-age=3600"
        }
        if content_length:
            response_headers["Content-Length"] = content_length
        if "Content-Range" in upstream.headers:
            response_headers["Content-Range"] = upstream.headers["Content-Range"]

        return StreamingResponse(
            iter_chunks(),
            status_code=upstream.status_code,
            media_type=media_type,
            headers=response_headers
        )

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/api/download/{video_id}")
def download_audio_info(video_id: str, request: Request):
    stream_data = get_stream_url(video_id, request)
    safe_title = re.sub(r'[\\/*?:"<>|]', "", f"{stream_data['artist']} - {stream_data['title']}")
    file_ext = stream_data.get('format') or 'm4a'
    
    return {
        "id": video_id,
        "title": stream_data['title'],
        "artist": stream_data['artist'],
        "file_name": f"{safe_title}.{file_ext}",
        "download_url": stream_data['stream_url'],
        "thumbnail": stream_data['thumbnail'],
        "duration": stream_data['duration'],
        "duration_formatted": stream_data['duration_formatted'],
    }

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
