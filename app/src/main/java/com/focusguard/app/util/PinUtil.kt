package com.focusguard.app.util

import java.security.MessageDigest

object PinUtil {

    fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPin(input: String, storedHash: String): Boolean {
        return hashPin(input) == storedHash
    }
}
