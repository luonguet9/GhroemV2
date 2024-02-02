package com.ghroem.presentation.receive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ghroem.presentation.service.MusicService
import com.ghroem.presentation.utils.ACTION_MUSIC
import com.ghroem.presentation.utils.ACTION_MUSIC_SERVICE

class ActionReceive : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val actionReceive = intent?.getIntExtra(ACTION_MUSIC, 0)

        val intentService = Intent(context, MusicService::class.java)
        intentService.putExtra(ACTION_MUSIC_SERVICE, actionReceive)

        context?.startService(intentService)
    }
}
