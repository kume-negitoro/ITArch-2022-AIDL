package com.example.astserviceclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.content.*
import android.os.RemoteException
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import com.example.astservice.IMyAidlInterface
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {
    var iRemoteService: IMyAidlInterface? = null

    val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            iRemoteService = IMyAidlInterface.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            Log.e("ClientApplication", "Service has unexpectedly disconnected")
            iRemoteService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        if (iRemoteService == null) {
            val it = Intent("ASTService")
            it.setPackage("com.example.astservice")
            val bindSuccess = bindService(it, mConnection, Context.BIND_AUTO_CREATE)
            if (!bindSuccess) {
                Log.e("ClientApplication", "Failed bind service")
            }
        }

        button.setOnClickListener {
            try {
                val json = textInputEditText.text.toString()
                Log.d("ClientApplication", json)
                val ast = iRemoteService?.jsonParse(json)
                if (ast != null) {
                    Log.d("ClientApplication", ast)
                    textInputEditText.setText(ast)
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }
}