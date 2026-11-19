package com.example.p5.domain.model

import java.util.Calendar

enum class TimePeriod {
    MORNING,
    AFTERNOON,
    EVENING,
    NIGHT
}

data class HomeTimeGreeting(
    val period: TimePeriod,
    val greetingTitle: String,
    val greetingSubtitle: String,
    val moodSectionTitle: String,
    val moodSectionSubtitle: String,
    val moodQuery: String
)

object HomeTimeContextResolver {

    fun resolve(hourOfDay: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): HomeTimeGreeting {
        return when (hourOfDay) {
            in 5..10 -> HomeTimeGreeting(
                period = TimePeriod.MORNING,
                greetingTitle = "Selamat Pagi",
                greetingSubtitle = "Awali harimu dengan alunan musik penuh semangat",
                moodSectionTitle = "Semangat Pagi",
                moodSectionSubtitle = "Pilihan lagu segar untuk menemani aktivitas pagimu",
                moodQuery = "lagu semangat pagi pop hits indonesia"
            )
            in 11..14 -> HomeTimeGreeting(
                period = TimePeriod.AFTERNOON,
                greetingTitle = "Selamat Siang",
                greetingSubtitle = "Temani fokus dan produktivitas siangmu",
                moodSectionTitle = "Teman Fokus Siang",
                moodSectionSubtitle = "Rekomendasi lagu enerjik dan fokus di siang hari",
                moodQuery = "lagu teman kerja fokus pop hits indonesia"
            )
            in 15..18 -> HomeTimeGreeting(
                period = TimePeriod.EVENING,
                greetingTitle = "Selamat Sore",
                greetingSubtitle = "Nikmati suasana santai sore dengan musik pilihan",
                moodSectionTitle = "Santai Senja",
                moodSectionSubtitle = "Alunan akustik dan indie hangat menjelang senja",
                moodQuery = "lagu indie senja akustik santai indonesia"
            )
            else -> HomeTimeGreeting(
                period = TimePeriod.NIGHT,
                greetingTitle = "Selamat Malam",
                greetingSubtitle = "Waktunya bersantai dengan melodi malam yang tenang",
                moodSectionTitle = "Nuansa Malam & Chill",
                moodSectionSubtitle = "Melodi tenang untuk menemani waktu istirahatmu",
                moodQuery = "lagu pengantar malam akustik chill pop indonesia"
            )
        }
    }
}
