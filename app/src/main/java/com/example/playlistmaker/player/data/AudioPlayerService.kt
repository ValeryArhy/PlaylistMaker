package com.example.playlistmaker.player.data

import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.ServiceCompat
import android.app.Service
import android.content.pm.ServiceInfo
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.app.formatTime
import com.example.playlistmaker.player.domain.api.AudioPlayerServiceController
import com.example.playlistmaker.player.domain.api.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioPlayerService : Service(), AudioPlayerServiceController {

    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + job)

    private var mediaPlayer: MediaPlayer? = null

    private enum class InternalState { IDLE, PREPARING, PLAYING, PAUSED }
    private var internalState = InternalState.IDLE

    private val _playerStateFlow = MutableStateFlow(PlayerState.STOPPED)
    override fun getPlayerStateFlow(): StateFlow<PlayerState> = _playerStateFlow

    private val _trackPositionFlow = MutableStateFlow("00:00")
    override fun getTrackPositionFlow(): StateFlow<String> = _trackPositionFlow

    private var currentTrackName: String = ""
    private var currentArtistName: String = ""

    private var progressJob: Job? = null

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): AudioPlayerServiceController = this@AudioPlayerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onUnbind(intent: Intent?): Boolean {
        return true
    }

    override fun onCreate() {
        super.onCreate()


        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }

        createNotificationChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        job.cancel()
    }

    override fun prepareAndPlay(trackUrl: String, trackName: String, artistName: String) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }

        currentTrackName = trackName
        currentArtistName = artistName

        mediaPlayer?.apply {
            reset()
            setDataSource(trackUrl)

            setOnPreparedListener {
                internalState = InternalState.PAUSED
                _playerStateFlow.value = PlayerState.PAUSED
            }

            setOnCompletionListener {

                internalState = InternalState.PAUSED
                _playerStateFlow.value = PlayerState.STOPPED
                _trackPositionFlow.value = "00:00"
                stopProgressUpdates()
                stopForegroundNotification(shouldStopService = false)
            }


            prepareAsync()
            internalState = InternalState.PREPARING
            _playerStateFlow.value = PlayerState.PREPARING
        }
    }

    override fun togglePlayPause() {
        if (internalState == InternalState.PLAYING) {
            pause()
        } else if (internalState == InternalState.PAUSED) {
            play()
        }
    }

    private fun play() {
        mediaPlayer?.let {
            if (internalState == InternalState.PAUSED) {
                it.start()
                internalState = InternalState.PLAYING
                _playerStateFlow.value = PlayerState.PLAYING
                startProgressUpdates()
            }
        }
    }

    override fun pause() {
        mediaPlayer?.let {
            if (internalState == InternalState.PLAYING) {
                it.pause()
                internalState = InternalState.PAUSED
                _playerStateFlow.value = PlayerState.PAUSED
                stopProgressUpdates()
            }
        }
    }

    override fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        internalState = InternalState.IDLE
        _playerStateFlow.value = PlayerState.IDLE
        stopProgressUpdates()
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressJob = serviceScope.launch {
            while (isActive && internalState == InternalState.PLAYING) {
                _trackPositionFlow.value = formatTime(mediaPlayer?.currentPosition ?: 0)
                delay(300L)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun startForegroundNotification(trackName: String, artistName: String) {
        val notification = createNotification(trackName, artistName)

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK       )
    }

    override fun stopForegroundNotification(shouldStopService: Boolean) {
        if (shouldStopService) {
            stopSelf()
        } else {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
        }
    }

    private fun createNotification(trackName: String, artistName: String) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("$artistName - $trackName")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

    companion object {
        private const val CHANNEL_ID = "audio_player_channel"
        private const val NOTIFICATION_ID = 1001
    }
}