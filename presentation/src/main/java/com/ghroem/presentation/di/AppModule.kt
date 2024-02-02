package com.ghroem.presentation.di

import android.content.ContentResolver
import android.content.Context
import com.ghroem.data.data_source.local.pref.SharedPreferencesManagerImpl
import com.ghroem.data.data_source.local.realm.RealmManager
import com.ghroem.data.mapper.PlaylistMapperImpl
import com.ghroem.data.mapper.SongMapperImpl
import com.ghroem.data.model.PlaylistRealm
import com.ghroem.data.model.SongRealm
import com.ghroem.data.repository.PlaylistRepositoryImpl
import com.ghroem.data.repository.SongRepositoryImpl
import com.ghroem.domain.mapper.PlaylistMapper
import com.ghroem.domain.mapper.SongMapper
import com.ghroem.domain.model.Playlist
import com.ghroem.domain.model.Song
import com.ghroem.domain.repository.PlaylistRepository
import com.ghroem.domain.repository.SharedPreferencesManager
import com.ghroem.domain.repository.SongRepository
import com.ghroem.domain.use_case.playlist.AddPlaylist
import com.ghroem.domain.use_case.playlist.DeletePlaylist
import com.ghroem.domain.use_case.playlist.GetAllPlaylist
import com.ghroem.domain.use_case.playlist.GetPlaylistId
import com.ghroem.domain.use_case.song.GetSongsFromStorage
import com.ghroem.domain.use_case.playlist.GetSongsFromPlaylist
import com.ghroem.domain.use_case.playlist.PlaylistUseCase
import com.ghroem.domain.use_case.playlist.RenamePlaylist
import com.ghroem.domain.use_case.song.SongUseCase
import com.ghroem.domain.use_case.playlist.AddSongToPlaylist
import com.ghroem.domain.use_case.playlist.CheckSongInPlaylist
import com.ghroem.domain.use_case.playlist.GetPlaylistById
import com.ghroem.domain.use_case.playlist.RemoveSongFromPlaylist
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
	
	@Singleton
	@Provides
	fun provideSharedPreferencesManager(@ApplicationContext context: Context): SharedPreferencesManager {
		return SharedPreferencesManagerImpl(context)
	}
	
	@Provides
	@Singleton
	fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
		return context.contentResolver
	}
	
	@Singleton
	@Provides
	fun provideRealmManager(@ApplicationContext context: Context) = RealmManager(context)
	
	@Singleton
	@Provides
	fun provideSongMapper(): SongMapper<SongRealm, Song> {
		return SongMapperImpl()
	}
	
	@Singleton
	@Provides
	fun provideSongRepository(
		contentResolver: ContentResolver,
		sharedPreferencesManager: SharedPreferencesManager
	): SongRepository {
		return SongRepositoryImpl(contentResolver, sharedPreferencesManager)
	}
	
	@Singleton
	@Provides
	fun provideSongUseCase(songRepository: SongRepository): SongUseCase {
		return SongUseCase(
			getSongsFromStorage = GetSongsFromStorage(songRepository),
		)
	}
	
	@Singleton
	@Provides
	fun providePlaylistMapper(songMapper: SongMapper<SongRealm, Song>): PlaylistMapper<PlaylistRealm, Playlist> {
		return PlaylistMapperImpl(songMapper)
	}
	
	@Singleton
	@Provides
	fun providePlaylistRepository(
		sharedPreferencesManager: SharedPreferencesManager,
		realmManager: RealmManager,
		playlistMapper: PlaylistMapper<PlaylistRealm, Playlist>,
		songMapper: SongMapper<SongRealm, Song>
	): PlaylistRepository {
		return PlaylistRepositoryImpl(
			sharedPreferencesManager,
			realmManager,
			playlistMapper,
			songMapper
		)
	}
	
	@Singleton
	@Provides
	fun providePlaylistUseCase(playlistRepository: PlaylistRepository): PlaylistUseCase {
		return PlaylistUseCase(
			getAllPlaylist = GetAllPlaylist(playlistRepository),
			addPlaylist = AddPlaylist(playlistRepository),
			renamePlaylist = RenamePlaylist(playlistRepository),
			deletePlaylist = DeletePlaylist(playlistRepository),
			getPlaylistId = GetPlaylistId(playlistRepository),
			getPlaylistById = GetPlaylistById(playlistRepository),
			getSongsFromPlaylist = GetSongsFromPlaylist(playlistRepository),
			addSongToPlaylist = AddSongToPlaylist(playlistRepository),
			removeSongFromPlaylist = RemoveSongFromPlaylist(playlistRepository),
			checkSongInPlaylist = CheckSongInPlaylist(playlistRepository)
		)
	}
	
}
