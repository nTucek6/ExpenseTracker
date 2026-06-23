package com.example.expensetracker.firebase.database

import android.util.Log
import com.example.expensetracker.data.dao.ExpenseDao
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ExpenseSyncManager(
    private val uid: String,
    private val expenseDao: ExpenseDao
) {
    private val ref = FirebaseDatabase.getInstance()
        .getReference("users")
        .child(uid)
        .child("expenses")

    private var listener: ChildEventListener? = null

    fun startListening() {
        if (listener != null) return

       /* listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // parse remote data and upsert into Room
                for (child in snapshot.children) {
                    val id = child.child("id").getValue(String::class.java)
                    val title = child.child("title").getValue(String::class.java)
                    val amount = child.child("amount").getValue(Double::class.java)
                    val updatedAt = child.child("updatedAt").getValue(Long::class.java)

                    Log.d("FirebaseDataChanged", "id=$id title=$title amount=$amount updatedAt=$updatedAt")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Sync", "Listener cancelled", error.toException())
            }
        }*/

        //ref.addValueEventListener(listener!!)

        listener = object : ChildEventListener, ValueEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //here is added
                Log.d("FirebaseChild", "added: ${snapshot.key}")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //on update here
                Log.d("FirebaseChild", "changed: ${snapshot.key}")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //removed works code needed
                Log.d("FirebaseChild", "removed: ${snapshot.key}")
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

}