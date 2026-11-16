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
}
