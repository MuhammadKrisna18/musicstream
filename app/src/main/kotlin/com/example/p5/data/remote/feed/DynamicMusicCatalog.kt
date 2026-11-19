package com.example.p5.data.remote.feed

import com.example.p5.domain.model.HomeSectionStyle

data class SeedSection(
    val id: String,
    val title: String,
    val subtitle: String,
    val query: String,
    val style: HomeSectionStyle
)

object DynamicMusicCatalog {

    val chartSeeds = listOf(
        SeedSection(
            id = "chart_indonesia_hits",
            title = "Top Charts Indonesia",
            subtitle = "Lagu paling populer dan viral di tanah air",
            query = "top hits indonesia 2026 viral official",
            style = HomeSectionStyle.RANKED_CHARTS
        ),
        SeedSection(
            id = "chart_billboard_hot",
            title = "Billboard Global Hits",
            subtitle = "Tangga lagu terpanas di seluruh dunia",
            query = "billboard hot 100 top music video official",
            style = HomeSectionStyle.RANKED_CHARTS
        ),
        SeedSection(
            id = "chart_viral_tiktok",
            title = "Hits Viral Terkini",
            subtitle = "Daftar musik yang sedang ramai diperbincangkan",
            query = "lagu viral tiktok fyp indonesia hits",
            style = HomeSectionStyle.RANKED_CHARTS
        ),
        SeedSection(
            id = "chart_pop_asia",
            title = "Asian Pop Charts",
            subtitle = "Koleksi lagu populer teratas dari Asia",
            query = "asian top pop hits official audio",
            style = HomeSectionStyle.RANKED_CHARTS
        )
    )

    val quickPicksSeeds = listOf(
        SeedSection(
            id = "quick_rilisan_baru",
            title = "Rilisan Terbaru",
            subtitle = "Karya musik segar yang baru saja dirilis",
            query = "latest official single music video 2026",
            style = HomeSectionStyle.QUICK_PICKS_GRID
        ),
        SeedSection(
            id = "quick_kpop_pulse",
            title = "K-Pop Pulse",
            subtitle = "Lagu K-Pop energik dan adiktif",
            query = "kpop hits music video dance official",
            style = HomeSectionStyle.QUICK_PICKS_GRID
        ),
        SeedSection(
            id = "quick_indie_fresh",
            title = "Indie & Alternatif",
            subtitle = "Eksplorasi warna musik indie yang menyegarkan",
            query = "indie alternative pop indonesia singles",
            style = HomeSectionStyle.QUICK_PICKS_GRID
        ),
        SeedSection(
            id = "quick_jazz_essentials",
            title = "Jazz Melodious",
            subtitle = "Koleksi instrumen dan vokal jazz pilihan",
            query = "smooth jazz vocal essentials official",
            style = HomeSectionStyle.QUICK_PICKS_GRID
        ),
        SeedSection(
            id = "quick_dangdut_hits",
            title = "Dangdut & Koplo Hits",
            subtitle = "Irama kendang koplo dan tembang populer nusantara",
            query = "dangdut koplo viral terbaru official",
            style = HomeSectionStyle.QUICK_PICKS_GRID
        )
    )

    val thematicSeeds = listOf(
        SeedSection(
            id = "theme_pop_indonesia",
            title = "Pop Indonesia Favorit",
            subtitle = "Lantunan pop nusantara yang dekat di hati",
            query = "pop indonesia terbaik hits official",
            style = HomeSectionStyle.STANDARD_CARDS
        ),
        SeedSection(
            id = "theme_indie_senja",
            title = "Melodi Indie Hangat",
            subtitle = "Petikan gitar akustik dan lirik puitis",
            query = "indie senja akustik santai indonesia",
            style = HomeSectionStyle.STANDARD_CARDS
        ),
        SeedSection(
            id = "theme_rnb_groove",
            title = "R&B & Soul Melodious",
            subtitle = "Alunan vokal lembut dan alur irama menawan",
            query = "rnb soul acoustic smooth official",
            style = HomeSectionStyle.STANDARD_CARDS
        ),
        SeedSection(
            id = "theme_rock_energy",
            title = "Rock & High Energy",
            subtitle = "Dentuman distorsi berenergi tinggi",
            query = "best rock band official music video",
            style = HomeSectionStyle.STANDARD_CARDS
        ),
        SeedSection(
            id = "theme_anime_jpop",
            title = "Anime & J-Pop Beats",
            subtitle = "Soundtrack anime populer dan irama J-Pop",
            query = "jpop anime opening theme songs official",
            style = HomeSectionStyle.STANDARD_CARDS
        ),
        SeedSection(
            id = "theme_nostalgia_2000s",
            title = "Nostalgia 2000-an",
            subtitle = "Deretan tembang kenangan awal milenium",
            query = "lagu indonesia 2000an hits terbaik",
            style = HomeSectionStyle.STANDARD_CARDS
        ),
        SeedSection(
            id = "theme_golden_90s",
            title = "Golden 90s Hits",
            subtitle = "Karya klasik tak lekang oleh waktu",
            query = "90s best acoustic pop hits",
            style = HomeSectionStyle.STANDARD_CARDS
        ),
        SeedSection(
            id = "theme_hiphop_flow",
            title = "Hip-Hop & Beat Urban",
            subtitle = "Rima tajam dan dentuman bass mendalam",
            query = "hip hop rap tracks official music",
            style = HomeSectionStyle.STANDARD_CARDS
        ),
        SeedSection(
            id = "theme_akustik_tenang",
            title = "Akustik Petikan Gitar",
            subtitle = "Koleksi lagu akustik dan petikan dawai pilihan",
            query = "lagu gitar akustik indonesia terbaik official",
            style = HomeSectionStyle.STANDARD_CARDS
        )
    )
}
