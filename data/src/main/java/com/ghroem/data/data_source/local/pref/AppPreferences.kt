package com.ghroem.data.data_source.local.pref

import android.content.Context
import com.ghroem.data.utils.REPEAT_MODE
import com.ghroem.data.utils.SHUFFLE_MODE

class AppPreferences(context: Context) : Preferences(context) {
	var shuffleMode by booleanPref(SHUFFLE_MODE, false)
	var repeatMode by intPref(REPEAT_MODE, 0)
}
