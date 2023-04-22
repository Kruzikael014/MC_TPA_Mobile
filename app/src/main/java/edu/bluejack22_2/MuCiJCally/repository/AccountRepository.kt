package edu.bluejack22_2.MuCiJCally.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import edu.bluejack22_2.MuCiJCally.model.Account
import edu.bluejack22_2.MuCiJCally.utility.FirebaseHandler

object AccountRepository {
    private val firebase = FirebaseHandler

    var accounts: LiveData<List<Account>>

    init {
        val output = MutableLiveData<List<Account>>()
        accounts = output

        firebase.accountRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val accounts = mutableListOf<Account>()
                for (accountSnapshot in snapshot.children) {
                    val account = accountSnapshot.getValue(Account::class.java)
                    account?.let {
                        accounts.add(it)
                    }
                }
                output.value = accounts
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(error.message, error.details)
            }
        })
    }

    fun getAccountByID(accountID: String, callback: (Account?) -> Unit) {
        firebase.accountRef.orderByChild("id").equalTo(accountID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var account: Account? = null
                    for (snapshot in dataSnapshot.children) {
                        account = snapshot.getValue(Account::class.java)
                    }
                    callback(account)
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Failed to fetch data from server!")
                }
            })
    }

    fun storeSession(id: String, preferences: SharedPreferences) {
        if (preferences == null) return
        with(preferences.edit()) {
            putString("Session", id)
            apply()
        }
    }

    fun getSessionID(preferences: SharedPreferences): String? {
        var output: String? = null
        output = preferences.getString("Session Token", null)
        return output
    }

    fun saveAccount(account: Account) {
        firebase.accountRef.child(account.id).setValue(account)
    }
}