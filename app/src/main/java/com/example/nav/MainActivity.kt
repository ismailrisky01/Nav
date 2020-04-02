package com.example.nav

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    private var listener: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit =
        { _, _, _ -> }

    fun setOnActivityResultListener(listener: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit) {
        this.listener = listener
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        listener.invoke(requestCode, resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            logD("Not Login")
            GoogleAuth(this).signIn(this) {googleUser->
                if (googleUser != null)
                    logD("User Login Success")
                else
                    logD("User Login Failed")
            }
        } else {
            logD("User ${user?.displayName}")
        }

        ID_Logout.setOnClickListener{
            GoogleAuth(this).signOut {
                logD("Sign Out Sucess")
            }
        }


        val newDevice = Device("-aaa", "Test", "Test", "Test")
        Repository.init("-111")

        Repository.setDevice(newDevice)

        val device = Repository.getDevice("-aaa")
        GlobalScope.launch(IO) {
            device.collect {
                logD(it.toString())
            }
        }
    }


    @Parcelize
    data class Device(
        var id: String,
        var name: String,
        var nopol: String,
        var merk: String
    ) : Parcelable {
        constructor() : this("", "", "", "")
    }
}
