package com.ghroem.domain.use_case.playlist

data class PlaylistUseCase(
	val getAllPlaylist: GetAllPlaylist,
	val addPlaylist: AddPlaylist,
	val renamePlaylist: RenamePlaylist,
	val deletePlaylist: DeletePlaylist,
	val getPlaylistId: GetPlaylistId,
	val getPlaylistById: GetPlaylistById,
	val getSongsFromPlaylist: GetSongsFromPlaylist,
	val addSongToPlaylist: AddSongToPlaylist,
	val removeSongFromPlaylist: RemoveSongFromPlaylist,
	val checkSongInPlaylist: CheckSongInPlaylist
)
