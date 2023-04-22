package edu.bluejack22_2.MuCiJCally

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.UserHandle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import edu.bluejack22_2.MuCiJCally.model.Account
import edu.bluejack22_2.MuCiJCally.utility.EmailHandler
import edu.bluejack22_2.MuCiJCally.utility.FirebaseHandler
import edu.bluejack22_2.MuCiJCally.utility.LayoutAssembler
import edu.bluejack22_2.MuCiJCally.utility.PageNavigator
import edu.bluejack22_2.MuCiJCally.viewmodel.AccountViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
//import edu.bluejack22_2.MuCiJCally.viewmodel.AccountViewModel
import kotlin.math.log

class LoginActivity : AppCompatActivity() {
    lateinit var signinBtn: Button
    lateinit var signupBtn: Button
    lateinit var viewModel: AccountViewModel
    lateinit var usernameTxt: EditText
    lateinit var passwordTxt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        LayoutAssembler(this, R.id.login_top_logo).initHeader()

        signinBtn = findViewById(R.id.btn_login_sign_in)
        signupBtn = findViewById(R.id.btn_login_sign_up)
        usernameTxt = findViewById(R.id.txt_login_username)
        passwordTxt = findViewById(R.id.txt_login_password)

        viewModel = ViewModelProvider(this)[AccountViewModel::class.java]
        eventHandling()
    }

    private fun eventHandling() {
        signinBtn.setOnClickListener {
            val username = usernameTxt.text.toString()
            val password = passwordTxt.text.toString()
            viewModel.attemptLogin(username, password, this).observe(this) {
                if(it == null) {
                    Toast.makeText(this, "Error occurred in getting login result", Toast.LENGTH_LONG).show()
                    return@observe
                }
                if(!it.success) {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    return@observe
                }
                intent.putExtra("Session", it)
                PageNavigator.switchPage(this, HomeActivity::class.java)
            }
        }
        signupBtn.setOnClickListener{
            val scope = CoroutineScope(Dispatchers.Main)

            scope.launch {
                EmailHandler().sendEmail("bacon.atplay@gmail.com", "dummy", "dummy data")
            }
//            PageNavigator.switchPage(this, RegisterActivity::class.java)
        }
    }
}