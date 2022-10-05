package com.rikkei.training.musicapp.model

data class Chart(
    val albums: Albums,
    val artists: Artists,
    val playlists: Playlists,
    val podcasts: Podcasts,
    val tracks: Tracks
)
data class Artists(
    val `data`: List<DataX>,
    val total: Int
)
data class Albums(
    val `data`: List<Data>,
    val total: Int
)
data class AlbumX(
    val cover: String,
    val cover_big: String,
    val cover_medium: String,
    val cover_small: String,
    val cover_xl: String,
    val id: Int,
    val md5_image: String,
    val title: String,
    val tracklist: String,
    val type: String
)
data class ArtistX(
    val id: Int,
    val link: String,
    val name: String,
    val picture: String,
    val picture_big: String,
    val picture_medium: String,
    val picture_small: String,
    val picture_xl: String,
    val radio: Boolean,
    val tracklist: String,
    val type: String
)
data class Data(
    val artist: ArtistX,
    val cover: String,
    val cover_big: String,
    val cover_medium: String,
    val cover_small: String,
    val cover_xl: String,
    val explicit_lyrics: Boolean,
    val id: Int,
    val link: String,
    val md5_image: String,
    val position: Int,
    val record_type: String,
    val title: String,
    val tracklist: String,
    val type: String
)
data class DataX(
    val id: Int,
    val link: String,
    val name: String,
    val picture: String,
    val picture_big: String,
    val picture_medium: String,
    val picture_small: String,
    val picture_xl: String,
    val position: Int,
    val radio: Boolean,
    val tracklist: String,
    val type: String
)
data class DataXX(
    val checksum: String,
    val creation_date: String,
    val id: Long,
    val link: String,
    val md5_image: String,
    val nb_tracks: Int,
    val picture: String,
    val picture_big: String,
    val picture_medium: String,
    val picture_small: String,
    val picture_type: String,
    val picture_xl: String,
    val `public`: Boolean,
    val title: String,
    val tracklist: String,
    val type: String,
    val user: UserX
)
data class DataXXX(
    val available: Boolean,
    val description: String,
    val fans: Int,
    val id: Int,
    val link: String,
    val picture: String,
    val picture_big: String,
    val picture_medium: String,
    val picture_small: String,
    val picture_xl: String,
    val share: String,
    val title: String,
    val type: String
)
data class DataXXXX(
    val album: AlbumX,
    val artist: ArtistX,
    val duration: Int,
    val explicit_content_cover: Int,
    val explicit_content_lyrics: Int,
    val explicit_lyrics: Boolean,
    val id: Int,
    val link: String,
    val md5_image: String,
    val position: Int,
    val preview: String,
    val rank: Int,
    val title: String,
    val title_short: String,
    val title_version: String,
    val type: String
)
data class Playlists(
    val `data`: List<DataXX>,
    val total: Int
)
data class Podcasts(
    val `data`: List<DataXXX>,
    val total: Int
)
data class Tracks(
    val `data`: List<DataXXXX>,
    val total: Int
)
data class UserX(
    val id: Int,
    val name: String,
    val tracklist: String,
    val type: String
)