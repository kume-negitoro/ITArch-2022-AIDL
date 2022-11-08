package com.example.astservice

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ASTService : Service() {
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private val binder = object : IMyAidlInterface.Stub() {
        override fun jsonParse(target: String) : String {
            return stringify(jsonParser()(target, 0))
        }
    }
}