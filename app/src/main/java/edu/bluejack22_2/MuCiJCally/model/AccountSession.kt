package edu.bluejack22_2.MuCiJCally.model

class AccountSession: java.io.Serializable {
    var account: Account? = null
    var sessionID: String = ""
    var success: Boolean = false
    var message: String = ""
}