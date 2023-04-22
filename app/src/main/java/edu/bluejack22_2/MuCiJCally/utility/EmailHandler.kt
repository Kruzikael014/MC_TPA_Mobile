package edu.bluejack22_2.MuCiJCally.utility

import android.util.Log
import edu.bluejack22_2.MuCiJCally.utility.private.Credential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailHandler {
    // TODO: Create credential class (use your own instead)
    val credential = Credential
    var session: Session

    private val props = Properties()

    init {
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.port"] = "587"

        session = Session.getInstance(props, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(credential.email, credential.password)
            }
        })
    }

    suspend fun sendEmail(target: String, subject: String, body: String) {
        withContext(Dispatchers.IO) {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress(credential.email))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(target))
            message.subject = subject
            message.setText(body)

            Transport.send(message)
        }
    }
}