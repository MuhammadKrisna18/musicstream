package com.example.p5.data.remote.innertube

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class InnerTubeParserTest {

    private val parser = InnerTubeParser()

    @Test
    fun parseSearchPayload_parsesBothSongsAndPlaylists() {
        val json = """
            {
              "contents": {
                "twoColumnSearchResultsRenderer": {
                  "primaryContents": {
                    "sectionListRenderer": {
                      "contents": [
                        {
                          "itemSectionRenderer": {
                            "contents": [
                              {
                                "videoRenderer": {
                                  "videoId": "vid123",
                                  "title": { "runs": [ { "text": "Hati-Hati di Jalan" } ] },
                                  "ownerText": { "runs": [ { "text": "Tulus" } ] },
                                  "lengthText": { "simpleText": "4:02" }
                                }
                              },
                              {
                                "playlistRenderer": {
                                  "playlistId": "PLtest123",
                                  "title": { "simpleText": "This is Tulus" },
                                  "shortBylineText": { "runs": [ { "text": "Tulus Official" } ] },
                                  "videoCount": "25"
                                }
                              }
                            ]
                          }
                        }
                      ]
                    }
                  }
                }
              }
            }
        """.trimIndent()

        val payload = parser.parseSearchPayload(json, limit = 10)
        assertEquals(1, payload.songs.size)
        assertEquals("vid123", payload.songs.first().id)
        assertEquals("Hati-Hati di Jalan", payload.songs.first().title)
        assertEquals("Tulus", payload.songs.first().artist)

        assertEquals(1, payload.playlists.size)
        assertEquals("PLtest123", payload.playlists.first().id)
        assertEquals("This is Tulus", payload.playlists.first().title)
        assertFalse(payload.playlists.first().subtitle.isBlank())
    }

    @Test
    fun parsePlaylistTracks_parsesTracksFromBrowseShelf() {
        val json = """
            {
              "contents": {
                "twoColumnBrowseResultsRenderer": {
                  "secondaryContents": {
                    "sectionListRenderer": {
                      "contents": [
                        {
                          "musicPlaylistShelfRenderer": {
                            "contents": [
                              {
                                "musicResponsiveListItemRenderer": {
                                  "playlistItemData": { "videoId": "track001" },
                                  "flexColumns": [
                                    {
                                      "musicResponsiveListItemFlexColumnRenderer": {
                                        "text": { "runs": [ { "text": "Monokrom" } ] }
                                      }
                                    },
                                    {
                                      "musicResponsiveListItemFlexColumnRenderer": {
                                        "text": { "runs": [ { "text": "Tulus" }, { "text": " • " }, { "text": "3:34" } ] }
                                      }
                                    }
                                  ]
                                }
                              }
                            ]
                          }
                        }
                      ]
                    }
                  }
                }
              }
            }
        """.trimIndent()

        val tracks = parser.parsePlaylistTracks(json)
        assertEquals(1, tracks.size)
        assertEquals("track001", tracks.first().id)
        assertEquals("Monokrom", tracks.first().title)
        assertEquals("Tulus", tracks.first().artist)
        assertEquals("3:34", tracks.first().durationFormatted)
    }

    @Test
    fun parseSearchPayload_parsesTabbedSearchResults_forYouTubeMusic() {
        val json = """
            {
              "contents": {
                "tabbedSearchResultsRenderer": {
                  "tabs": [
                    {
                      "tabRenderer": {
                        "content": {
                          "sectionListRenderer": {
                            "contents": [
                              {
                                "musicShelfRenderer": {
                                  "contents": [
                                    {
                                      "musicResponsiveListItemRenderer": {
                                        "playlistItemData": { "videoId": "song001" },
                                        "flexColumns": [
                                          {
                                            "musicResponsiveListItemFlexColumnRenderer": {
                                              "text": { "runs": [ { "text": "Raiso Ngapusi" } ] }
                                            }
                                          },
                                          {
                                            "musicResponsiveListItemFlexColumnRenderer": {
                                              "text": { "runs": [ { "text": "Lavora" }, { "text": " • " }, { "text": "4:12" } ] }
                                            }
                                          }
                                        ]
                                      }
                                    },
                                    {
                                      "musicResponsiveListItemRenderer": {
                                        "navigationEndpoint": {
                                          "browseEndpoint": {
                                            "browseId": "VLPLtestalbum"
                                          }
                                        },
                                        "flexColumns": [
                                          {
                                            "musicResponsiveListItemFlexColumnRenderer": {
                                              "text": { "runs": [ { "text": "Koleksi Lagu Jawa" } ] }
                                            }
                                          },
                                          {
                                            "musicResponsiveListItemFlexColumnRenderer": {
                                              "text": { "runs": [ { "text": "Playlist" }, { "text": " • " }, { "text": "Denny Caknan" } ] }
                                            }
                                          }
                                        ]
                                      }
                                    }
                                  ]
                                }
                              }
                            ]
                          }
                        }
                      }
                    }
                  ]
                }
              }
            }
        """.trimIndent()

        val payload = parser.parseSearchPayload(json, limit = 10)
        assertEquals(1, payload.songs.size)
        assertEquals("song001", payload.songs.first().id)
        assertEquals("Raiso Ngapusi", payload.songs.first().title)
        assertEquals("Lavora", payload.songs.first().artist)

        assertEquals(1, payload.playlists.size)
        assertEquals("VLPLtestalbum", payload.playlists.first().id)
        assertEquals("Koleksi Lagu Jawa", payload.playlists.first().title)
    }

    @Test
    fun parseArtistProfile_parsesHeaderTopSongsAndAlbums() {
        val json = """
            {
              "header": {
                "musicImmersiveHeaderRenderer": {
                  "title": { "runs": [ { "text": "Denny Caknan" } ] },
                  "description": { "runs": [ { "text": "Penyanyi Dangdut dan Pop Jawa" } ] },
                  "thumbnail": {
                    "musicThumbnailRenderer": {
                      "thumbnail": {
                        "thumbnails": [ { "url": "https://example.com/denny.jpg" } ]
                      }
                    }
                  },
                  "subscriptionButton": {
                    "subscribeButtonRenderer": {
                      "subscriberCountText": {
                        "runs": [ { "text": "7 Juta" } ]
                      }
                    }
                  }
                }
              },
              "contents": {
                "singleColumnBrowseResultsRenderer": {
                  "tabs": [
                    {
                      "tabRenderer": {
                        "content": {
                          "sectionListRenderer": {
                            "contents": [
                              {
                                "musicShelfRenderer": {
                                  "title": { "runs": [ { "text": "Lagu teratas" } ] },
                                  "bottomEndpoint": {
                                    "browseEndpoint": {
                                      "browseId": "VLOLAK_all_songs"
                                    }
                                  },
                                  "contents": [
                                    {
                                      "musicResponsiveListItemRenderer": {
                                        "playlistItemData": { "videoId": "vid001" },
                                        "flexColumns": [
                                          {
                                            "musicResponsiveListItemFlexColumnRenderer": {
                                              "text": { "runs": [ { "text": "Negoro Angin" } ] }
                                            }
                                          },
                                          {
                                            "musicResponsiveListItemFlexColumnRenderer": {
                                              "text": {
                                                "runs": [
                                                  {
                                                    "text": "Denny Caknan",
                                                    "navigationEndpoint": {
                                                      "browseEndpoint": {
                                                        "browseId": "UCrXRY_7SVVmc6TykwhRGUNQ"
                                                      }
                                                    }
                                                  },
                                                  { "text": " • " },
                                                  { "text": "3:45" }
                                                ]
                                              }
                                            }
                                          }
                                        ]
                                      }
                                    }
                                  ]
                                }
                              },
                              {
                                "musicCarouselShelfRenderer": {
                                  "header": {
                                    "musicCarouselShelfBasicHeaderRenderer": {
                                      "title": { "runs": [ { "text": "Album" } ] }
                                    }
                                  },
                                  "contents": [
                                    {
                                      "musicTwoRowItemRenderer": {
                                        "title": { "runs": [ { "text": "Kalih Welasku" } ] },
                                        "subtitle": { "runs": [ { "text": "Denny Caknan • 2023" } ] },
                                        "navigationEndpoint": {
                                          "browseEndpoint": {
                                            "browseId": "MPREb_album1"
                                          }
                                        }
                                      }
                                    }
                                  ]
                                }
                              }
                            ]
                          }
                        }
                      }
                    }
                  ]
                }
              }
            }
        """.trimIndent()

        val profile = parser.parseArtistProfile("UCrXRY_7SVVmc6TykwhRGUNQ", json)
        assertEquals("UCrXRY_7SVVmc6TykwhRGUNQ", profile.id)
        assertEquals("Denny Caknan", profile.name)
        assertEquals("https://example.com/denny.jpg", profile.avatarUrl)
        assertEquals("7 Juta", profile.subscriberCount)
        assertEquals("VLOLAK_all_songs", profile.allSongsPlaylistId)

        assertEquals(1, profile.topSongs.size)
        assertEquals("vid001", profile.topSongs.first().id)
        assertEquals("Negoro Angin", profile.topSongs.first().title)
        assertEquals("UCrXRY_7SVVmc6TykwhRGUNQ", profile.topSongs.first().channelId)

        assertEquals(1, profile.albums.size)
        assertEquals("MPREb_album1", profile.albums.first().id)
        assertEquals("Kalih Welasku", profile.albums.first().title)
    }

    @Test
    fun parseDurationToSeconds_handlesEmptyAndVariousFormats() {
        assertEquals(0, InnerTubeFormatter.parseDurationToSeconds(""))
        assertEquals(0, InnerTubeFormatter.parseDurationToSeconds("invalid"))
        assertEquals(45, InnerTubeFormatter.parseDurationToSeconds("45"))
        assertEquals(225, InnerTubeFormatter.parseDurationToSeconds("3:45"))
        assertEquals(3725, InnerTubeFormatter.parseDurationToSeconds("1:02:05"))
    }

    @Test
    fun parseViewCount_handlesIndonesianAndEnglishFormats() {
        assertEquals(1_200_000L, InnerTubeFormatter.parseViewCount("1,2 jt x ditonton"))
        assertEquals(1_200_000L, InnerTubeFormatter.parseViewCount("1.2M views"))
        assertEquals(450_000L, InnerTubeFormatter.parseViewCount("450 rb x diputar"))
        assertEquals(850_000L, InnerTubeFormatter.parseViewCount("850K views"))
        assertEquals(12_345_678L, InnerTubeFormatter.parseViewCount("12.345.678 ditonton"))
        assertEquals(12_345_678L, InnerTubeFormatter.parseViewCount("12,345,678 views"))
        assertEquals(890L, InnerTubeFormatter.parseViewCount("890 ditonton"))
        assertEquals(0L, InnerTubeFormatter.parseViewCount(""))
    }

    @Test
    fun parseSongFromResponsive_extractsDurationFromFixedColumns() {
        val json = org.json.JSONObject(
            """
            {
              "playlistItemData": { "videoId": "sigar123" },
              "flexColumns": [
                {
                  "musicResponsiveListItemFlexColumnRenderer": {
                    "text": { "runs": [ { "text": "Sigar" } ] }
                  }
                },
                {
                  "musicResponsiveListItemFlexColumnRenderer": {
                    "text": { "runs": [ { "text": "Denny Caknan" } ] }
                  }
                }
              ],
              "fixedColumns": [
                {
                  "musicResponsiveListItemFixedColumnRenderer": {
                    "text": { "runs": [ { "text": "4:48" } ] }
                  }
                }
              ]
            }
            """.trimIndent()
        )

        val song = parser.parseSongFromResponsive(json)
        org.junit.Assert.assertNotNull(song)
        assertEquals("sigar123", song!!.id)
        assertEquals("Sigar", song.title)
        assertEquals("Denny Caknan", song.artist)
        assertEquals("4:48", song.durationFormatted)
        assertEquals(288, song.durationSeconds)
    }

    @Test
    fun parseSongFromResponsive_extractsDurationFromThirdFlexColumn() {
        val json = org.json.JSONObject(
            """
            {
              "playlistItemData": { "videoId": "sekti123" },
              "flexColumns": [
                {
                  "musicResponsiveListItemFlexColumnRenderer": {
                    "text": { "runs": [ { "text": "Sekti" } ] }
                  }
                },
                {
                  "musicResponsiveListItemFlexColumnRenderer": {
                    "text": { "runs": [ { "text": "Denny Caknan" } ] }
                  }
                },
                {
                  "musicResponsiveListItemFlexColumnRenderer": {
                    "text": { "runs": [ { "text": "Album Sekti" } ] }
                  }
                },
                {
                  "musicResponsiveListItemFlexColumnRenderer": {
                    "text": { "runs": [ { "text": "5:12" } ] }
                  }
                }
              ]
            }
            """.trimIndent()
        )

        val song = parser.parseSongFromResponsive(json)
        org.junit.Assert.assertNotNull(song)
        assertEquals("sekti123", song!!.id)
        assertEquals("5:12", song.durationFormatted)
        assertEquals(312, song.durationSeconds)
    }

    @Test
    fun parseSongFromTwoRow_extractsDurationAndChannelIdFromSubtitle() {
        val json = org.json.JSONObject(
            """
            {
              "title": { "runs": [ { "text": "Cundamani" } ] },
              "subtitle": {
                "runs": [
                  { "text": "Lagu" },
                  { "text": " • " },
                  {
                    "text": "Denny Caknan",
                    "navigationEndpoint": {
                      "browseEndpoint": { "browseId": "UCrXRY_7SVVmc6TykwhRGUNQ" }
                    }
                  },
                  { "text": " • " },
                  { "text": "3:58" }
                ]
              },
              "navigationEndpoint": {
                "watchEndpoint": { "videoId": "cunda123" }
              }
            }
            """.trimIndent()
        )

        val song = parser.parseSongFromTwoRow(json)
        org.junit.Assert.assertNotNull(song)
        assertEquals("cunda123", song!!.id)
        assertEquals("Cundamani", song.title)
        assertEquals("Denny Caknan", song.artist)
        assertEquals("UCrXRY_7SVVmc6TykwhRGUNQ", song.channelId)
        assertEquals("3:58", song.durationFormatted)
        assertEquals(238, song.durationSeconds)
    }

    @Test
    fun parseSongFromTwoRow_returnsEmptyDurationWhenMissing() {
        val json = org.json.JSONObject(
            """
            {
              "title": { "runs": [ { "text": "Dumes" } ] },
              "subtitle": {
                "runs": [
                  { "text": "Wawes" }
                ]
              },
              "navigationEndpoint": {
                "watchEndpoint": { "videoId": "dumes123" }
              }
            }
            """.trimIndent()
        )

        val song = parser.parseSongFromTwoRow(json)
        org.junit.Assert.assertNotNull(song)
        assertEquals("dumes123", song!!.id)
        assertEquals("", song.durationFormatted)
        assertEquals(0, song.durationSeconds)
    }
}
