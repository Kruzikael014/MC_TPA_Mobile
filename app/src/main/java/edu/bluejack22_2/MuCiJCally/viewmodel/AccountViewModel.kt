package edu.bluejack22_2.MuCiJCally.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.bumptech.glide.util.Util
import edu.bluejack22_2.MuCiJCally.model.Account
import edu.bluejack22_2.MuCiJCally.model.AccountSession
import edu.bluejack22_2.MuCiJCally.repository.AccountRepository
import edu.bluejack22_2.MuCiJCally.utility.EmailHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.security.auth.callback.Callback

class AccountViewModel : ViewModel() {
    private val accountRepository = AccountRepository

    fun getAccounts(): LiveData<List<Account>> {
        return accountRepository.accounts
    }

    fun attemptLogin(username: String, password: String, owner: AppCompatActivity): LiveData<AccountSession> {
        val output = MutableLiveData<AccountSession>()
        val session = AccountSession()

        output.value = session

        if(username.isEmpty() || password.isEmpty()) {
            session.success = false
            session.message = "Username and Password fields cannot be empty!"
            return output
        }

        var observer = object: Observer<List<Account>>{
            override fun onChanged(accounts: List<Account>) {
                session.success = false
                session.message = "Invalid credentials"
                accounts.forEach { acc ->
                    if (acc.username == username && Utility.checkPassword(password, acc.password)) {
                        val sessionID = Utility.generateSessionID()
                        acc.sessionID = sessionID
                        session.sessionID = Utility.generateSessionID()
                        session.account = acc
                        session.success = true
                        session.message = String.format("Sign in successfully! Hi, %s!", acc.username)
                        accountRepository.saveAccount(acc)
                        accountRepository.storeSession(session.sessionID,
                            owner.getPreferences(Context.MODE_PRIVATE))
                        accountRepository.accounts.removeObserver(this)
                        return@forEach
                    }
                }
                output.value = session
            }
        }
        accountRepository.accounts.observe(owner, observer)
        return output
    }

    /***
     * @return <p>An observable data that consists of string. This string is a possible error message during signup, if there are no error, proceed</p>
     */
    fun attemptSignup(username: String, email: String, password: String, confirm: String, owner: AppCompatActivity): LiveData<String> {
        var output: MutableLiveData<String> = MutableLiveData()
        var error = ""

        var observer = object: Observer<List<Account>> {
            override fun onChanged(accounts: List<Account>) {

                if(username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                    error = "All fields should not be empty!"
                }

                if(error.isEmpty()) {
                    var exists = false

                    accounts?.forEach { acc ->
                        if(acc.username == username) {
                            exists = true
                        }
                    }

                    if(exists) error = "Username should be unique"
                }

                if(error.isEmpty() && password.length < 6) {
                    error = "Password must have at least 6 characters"
                }

                if (error.isEmpty() && !password.matches("^(?=.*[a-zA-Z])(?=.*\\d).+$".toRegex())) {
                    error = "Password must have at least 1 digit and 1 alphabet"
                }

                if (error.isEmpty() && password != confirm) {
                    error = "Confirm Password doesn't match"
                }

                output.value = error
                accountRepository.accounts.removeObserver(this)
            }
        }

        accountRepository.accounts.observe(owner, observer)
        return output
    }

    fun getCurrentAccount(owner: AppCompatActivity): LiveData<AccountSession> {
        var output: MutableLiveData<AccountSession> = MutableLiveData()

        val sessionID = accountRepository.getSessionID(owner.getPreferences(Context.MODE_PRIVATE))
        var session = AccountSession()
        output.value = session

        if(sessionID == null) {
            session.message = "No Account"
            return output
        }

        var observer = object: Observer<List<Account>>{
            override fun onChanged(accounts: List<Account>) {
                session.success = false
                session.message = "Invalid credentials"

                accounts?.forEach { acc ->
                    if(acc.sessionID == sessionID) {
                        val sessionID = Utility.generateSessionID()
                        acc.sessionID = sessionID
                        session.sessionID = sessionID

                        session.account = acc
                        session.success = true
                        session.message = String.format("Sign in successfully! Hi, %s!", acc.username)

                        accountRepository.saveAccount(acc)
                        accountRepository.storeSession(sessionID, owner.getPreferences(Context.MODE_PRIVATE))

                        accountRepository.accounts.removeObserver(this)
                        return@forEach
                    }
                }
            }
        }
        accountRepository.accounts.observe(owner, observer)
        return output
    }

    fun getAccountByID(accountID: String, callback: (Account?) -> Unit) {
        accountRepository.getAccountByID(accountID) { fetchedAccount ->
            callback(fetchedAccount)
        }
    }

    fun sendVerificationEmail(dest: String, activity: Activity): Int {
        val code = (100000..999999).random()
        val message = String.format("Your verification code is %d.", code)
        val scope = CoroutineScope(Dispatchers.Main)

        scope.launch {
            EmailHandler().sendEmail(dest, "Verification Code", message)
        }
        return code
    }

    fun getAccountSession(owner: AppCompatActivity): AccountSession? {
        var output = owner.intent.getSerializableExtra("Session")
        if(output is AccountSession) {
            return output
        }
        return null
    }
}