# MusicStream - Streaming Musik Modern, Elegan dan Bebas Hambatan

MusicStream adalah aplikasi pemutar musik Android modern yang dirancang untuk memberikan pengalaman mendengarkan musik terbaik. Dengan antarmuka kaca gelap (Fluid Glassmorphism) bertema Midnight Velvet yang futuristik, memanjakan mata, dan dihiasi latar belakang ilustrasi karakter anime bernuansa malam berbintang, MusicStream menghadirkan jutaan lagu dengan streaming yang cepat, ringan, dan efisien.

---

## Fitur-Fitur Utama

### 1. Jutaan Lagu Tanpa Batas
Temukan dan dengarkan lagu dari seluruh dunia, mulai dari lagu pop hits terkini, indie senja, nostalgia lagu kenangan, hingga lagu santai pengiring fokus belajar dan bekerja.

### 2. Putar Cepat dan Hemat Kuota
Streaming musik langsung dimulai seketika tanpa perlu menunggu proses pemuatan yang lama. Aplikasi secara cerdas menyimpan data lagu yang pernah didengarkan di memori cache lokal, sehingga pemutaran ulang lagu favorit menjadi jauh lebih hemat kuota internet.

### 3. Nuansa Midnight Velvet dan Karakter Anime Estetik
Nikmati tema antarmuka eksklusif Midnight Velvet (Dark Purple dan Gothic Indigo) yang dipadukan dengan ilustrasi karakter anime berkualitas tinggi berlatar malam berbintang dengan headphone bercahaya lembut. Desain Fluid Glassmorphism transparan membuat visual karakter terlihat anggun di seluruh halaman (Beranda, Koleksi, hingga Pemutar Penuh) dengan keterbacaan teks yang tetap tajam dan kontras.

### 4. Ikon Aplikasi Kustom Bertema Midnight Velvet
Tampilan aplikasi dilengkapi dengan ikon kustom (Adaptive Icon dan Legacy Density) yang memadukan piringan hitam vinyl gelap, headphone violet neon, dan gelombang audio gothic indigo.

### 5. Lirik Lagu Real-Time (Karaoke Interaktif)
Bernyanyi bersama musisi favorit dengan sinkronisasi lirik otomatis yang mengikuti vokal lagu. Pengguna dapat mengetuk baris lirik mana saja untuk melompat langsung ke bagian lagu tersebut.

### 6. Pengatur Waktu Tidur (Sleep Timer)
Mendengarkan musik tenang sebelum tidur kini lebih nyaman dengan timer otomatis (15, 30, 45, atau 60 menit, atau berhenti tepat saat lagu berakhir). Aplikasi akan menghentikan pemutaran secara otomatis saat waktu tercapai.

### 7. Menu Praktis Titik Tiga (Song Action Sheet)
Setiap kartu dan daftar lagu dilengkapi tombol aksi cepat sekali sentuh untuk:
* Simpan Lagu: Menyimpan lagu ke daftar putar tersimpan lokal tanpa memakan ruang penyimpanan berkas besar.
* Masuk ke Favorit: Menandai lagu kesukaan atau menghapusnya dari favorit dengan status instan.
* Putar Berikutnya: Menyisipkan lagu tepat setelah lagu saat ini selesai diputar tanpa mengganggu susunan antrean yang ada.

### 8. Playlist Mandiri untuk Koleksi dan Lagu Tersimpan
Daftar lagu favorit dan lagu tersimpan bertindak sebagai playlist mandiri. Saat lagu diputar dari tab Favorit atau tab Tersimpan, pemutar akan secara eksklusif memutar dan mengacak daftar lagu di dalam halaman tersebut.

### 9. Filter Genre Dinamis di Beranda (Non-Hardcoded)
Halaman Beranda kini dilengkapi deretan filter genre yang diambil langsung secara dinamis dari katalog server YouTube Music (seperti Pop, Rock, Indie, R&B, Jazz, Dangdut, dan EDM). Memilih genre akan memperbarui kurasi lagu di Beranda secara in-place tanpa perlu berpindah ke halaman pencarian.

### 10. Tampilan Detail Section Beranda (See All)
Setiap bagian kurasi lagu di Beranda (seperti Top Charts Indonesia, Pilihan Cepat, atau Rilisan Terbaru) kini dilengkapi tombol See All yang membuka tampilan daftar vertikal penuh. Pengguna dapat langsung menikmati tombol Putar Semua dan Acak, navigasi kembali (BackHandler) yang mulus, serta daftar lagu lengkap tanpa memicu pencarian teks yang tidak diinginkan.

### 11. Pengurutan Hasil Pencarian Berdasarkan Popularitas
Hasil pencarian kini dapat diurutkan secara fleksibel:
* Relevansi: Berdasarkan algoritma pencarian terbaik.
* Terpopuler: Mengurutkan lagu berdasarkan jumlah penonton dan pemutaran terbanyak (view count) lengkap dengan indikator tayangan.
* Durasi: Mengurutkan lagu dari yang berdurasi paling panjang.
* Alfabetis: Mengurutkan judul lagu secara alfabetis dari A ke Z.

### 12. Pengurutan dan Quick Filter Artis di Perpustakaan
Tab Koleksi dan Simpan kini dapat dikelola dengan mudah:
* Urutkan berdasarkan Terbaru Ditambahkan, Judul (A-Z), Artis (A-Z), atau Durasi.
* Quick Filter Artis: Chip filter artis yang diekstrak secara otomatis dari lagu-lagu yang tersimpan di dalam koleksi.

### 13. Antrean Musik dan Putar Otomatis (Autoplay)
Musik berputar tanpa henti. Setelah lagu selesai, aplikasi memutar rekomendasi lagu berikutnya yang senada secara otomatis, lengkap dengan mode acak (shuffle) dan ulang (repeat).

### 14. Pemutar Mini Melayang (Floating Mini Player)
Jelajahi halaman pencarian atau koleksi sambil tetap mengontrol lagu yang sedang diputar melalui bar kontrol mini melayang di bagian bawah layar.

### 15. Pembersihan Cache Mandiri
Kelola ruang penyimpanan perangkat secara transparan dengan bar pemantau ukuran cache dan tombol pembersih cache streaming sekali sentuh.

### 16. Dukungan Bahasa Indonesia dan Inggris
Aplikasi mendukung lokalisasi antarmuka dwibahasa secara menyeluruh, menyesuaikan secara otomatis dengan bahasa sistem perangkat.

### 17. Pembaruan Aplikasi Otomatis (In-App Update)
Pembaruan aplikasi dapat diperiksa dan dipasang langsung melalui tombol periksa pembaruan di tab Koleksi tanpa perlu mencari berkas instalasi secara manual.

### 18. Arsitektur Modular dan Performa 120 FPS
Dibangun dengan prinsip Clean Architecture dan Jetpack Compose modular. Isolasi rekomposisi pada penggeser durasi pemutar memastikan animasi berjalan stabil pada 120 FPS tanpa stutter (zero dropped frames).

### 19. Eksplorasi Playlist Dinamis dari YouTube API (Zero-Hardcode)
Menghadirkan integrasi playlist dinamis murni langsung dari YouTube Music InnerTube API tanpa data hardcode:
* Lagu Single Tetap Utama: Lagu single tetap menjadi warga kelas satu di seluruh tampilan Beranda maupun hasil pencarian.
* Integrasi Hasil Pencarian Alami: Saat mencari judul lagu atau nama musisi, aplikasi mengekstrak lagu single (`videoRenderer`) dan playlist asli (`playlistRenderer`) dalam satu roundtrip API. Jika YouTube mengembalikan playlist, kartu playlist ditampilkan ringkas di atas hasil lagu; jika tidak ada, hasil pencarian 100% menampilkan deretan lagu single murni.
* Baris Playlist Tunggal di Beranda: Menyajikan satu baris horizontal playlist pilihan terkurasi yang ditarik secara dinamis dari katalog server YouTube Music Explore/Browse.
* Layar Detail Playlist & Antrean Lagu: Dilengkapi tombol Putar Semua dan Acak, pemuatan daftar lagu individual secara asinkron dari endpoint browse YouTube (`VL[playlistId]`), dukungan lirik real-time per lagu (format LRC sinkron), dan pemutaran berkelanjutan tanpa jeda.

---

## Integrasi Layanan API dan Status Operasional

MusicStream beroperasi menggunakan integrasi beberapa layanan antarmuka pemrograman aplikasi (API) pihak ketiga tanpa memerlukan kunci API berbayar (API key-free architecture). Berikut adalah rincian API yang digunakan beserta fungsi dan status operasionalnya:

### 1. YouTube InnerTube API (Private Native Endpoints)
* **Kategori**: Katalog Musik, Streaming Audio, dan Metadata.
* **Status**: Aktif dan Beroperasi Penuh.
* **Rincian Endpoint**:
  * **Search Endpoint** (`POST https://www.youtube.com/youtubei/v1/search`): Digunakan untuk melakukan pencarian lagu, artis, dan album melalui konteks klien WEB_REMIX secara instan dan bebas pembatasan kuota API resmi.
  * **Player Endpoint** (`POST https://www.youtube.com/youtubei/v1/player`): Digunakan untuk mengekstrak tautan stream audio langsung (direct audio stream m4a/webm) dengan bitrate optimal dan validasi signature timestamp (STS) melalui konteks klien ANDROID.
  * **Browse Endpoint** (`POST https://music.youtube.com/youtubei/v1/browse`): Digunakan untuk memuat kurasi Beranda, feed tangga lagu resmi Indonesia, serta daftar kategori genre dinamis.
  * **Session Origin** (`GET https://www.youtube.com`): Digunakan oleh modul InnerTubeSessionManager untuk menyinkronkan token sesi visitor data dan signature timestamp secara berkala setiap jam.
  * **Image CDN** (`GET https://i.ytimg.com/vi/{videoId}/hqdefault.jpg`): CDN gambar YouTube berkecepatan tinggi untuk menyajikan cover thumbnail lagu secara efisien dengan caching lokal.

### 2. LRCLIB API (Open Synchronized Lyrics)
* **Kategori**: Lirik Lagu Real-Time dan Karaoke Interaktif.
* **Status**: Aktif dan Beroperasi Penuh.
* **Rincian Endpoint**:
  * **Exact Match** (`GET https://lrclib.net/api/get`): Mengambil lirik lagu terverifikasi dengan stempel waktu per baris (format LRC sinkron) berdasarkan judul lagu, nama artis, dan durasi audio.
  * **Search Fallback** (`GET https://lrclib.net/api/search`): Sebagai jalur cadangan pencarian lirik berbasis query teks bebas jika pencarian eksak tidak membuahkan hasil.

### 3. GitHub Releases REST API
* **Kategori**: Pembaruan Aplikasi Otomatis (In-App Update).
* **Status**: Aktif dan Beroperasi Penuh.
* **Rincian Endpoint**:
  * **Latest Release** (`GET https://api.github.com/repos/MuhammadKrisna18/musicstream/releases/latest`): Memeriksa versi rilis terbaru pada repositori GitHub, menampilkan catatan perubahan rilis, serta mengunduh dan memasang berkas app-release.apk secara otomatis langsung dari dalam aplikasi.

---

## Petunjuk Pemasangan

1. Unduh berkas instalasi terbaru app-release.apk dari menu Releases di repositori GitHub ini.
2. Buka berkas APK yang telah diunduh pada perangkat Android.
3. Jika diminta konfirmasi keamanan, aktifkan opsi Izinkan pemasangan dari sumber ini.
4. Pilih Pasang (Install) dan tunggu proses instalasi selesai.

---

## Persyaratan Sistem

* Sistem Operasi: Android 7.0 (Nougat, API Level 24) atau versi yang lebih tinggi (Android 8, 9, 10, 11, 12, 13, 14, 15, 16).
* Jaringan: Memerlukan koneksi internet aktif (Wi-Fi atau Data Seluler) untuk pencarian dan streaming musik.
* Ukuran Berkas: Berkas instalasi APK berukuran sekitar 16 MB.

---

## Lisensi dan Hak Cipta

Aplikasi ini dikembangkan untuk tujuan portofolio dan edukasi rekayasa perangkat lunak Android modern menggunakan Kotlin, Jetpack Compose, Media3 ExoPlayer, dan InnerTube API. Seluruh konten hak cipta audio dan metadata merupakan milik dari pemegang hak cipta masing-masing.
