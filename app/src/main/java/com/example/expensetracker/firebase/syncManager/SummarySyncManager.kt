package com.example.expensetracker.firebase.syncManager

import android.util.Log
import com.example.expensetracker.data.dao.MonthlySummaryDao
import com.example.expensetracker.utils.FirebaseUtils
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SummarySyncManager @AssistedInject constructor (
    @Assisted private val onItemUpdated: () -> Unit,
    @Assisted private val uid: String,

    private val summaryDao: MonthlySummaryDao,
    private val databaseRoot: DatabaseReference
) {
    @AssistedFactory
    interface Factory {
        fun create(
            onItemUpdated: () -> Unit,
            uid: String
        ): SummarySyncManager
    }

    private val ref = databaseRoot
        .child("users")
        .child(uid)
        .child("summary")

    private var listener: ChildEventListener? = null

    fun startListening() {
        if (listener != null) return

        listener = object : ChildEventListener, ValueEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                handleUpsert(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("FirebaseChild", "updated: ${snapshot.value}")
                handleUpsert(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) = Unit
            override fun onDataChange(snapshot: DataSnapshot) {
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseChild", "cancelled", error.toException())
            }
        }
        ref.addChildEventListener(listener!!)
    }

    fun stopListening() {
        listener?.let { ref.removeEventListener(it) }
        listener = null
    }

    private fun handleUpsert(snapshot: DataSnapshot) {

        val summary = FirebaseUtils.snapshotToSummary(snapshot)

        CoroutineScope(Dispatchers.IO).launch {
            val changed = summaryDao.insertOrUpdateIfNewer(summary)
            if(changed) {
                withContext(Dispatchers.Main) {
                    onItemUpdated()
                }
            }
        }
    }
}