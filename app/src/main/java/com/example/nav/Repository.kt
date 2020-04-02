package com.example.nav

import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

object Repository {
    lateinit var realtime: DatabaseReference
    lateinit var fireStore: DocumentReference

    private lateinit var uid: String

    fun init(uid: String) = apply {
        this.uid = uid
        realtime = FirebaseDatabase.getInstance().reference
        fireStore = FirebaseFirestore.getInstance().collection("User").document(Repository.uid)
    }

    fun setDevice(device: MainActivity.Device) {
        realtime.child(device.id).setValue(device)
    }

    fun getDevice(id: String) = callbackFlow<MainActivity.Device?> {
        val listener = realtime.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val device = p0.getValue(MainActivity.Device::class.java)
                offer(device)
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        awaitClose { realtime.removeEventListener(listener) }
    }
}