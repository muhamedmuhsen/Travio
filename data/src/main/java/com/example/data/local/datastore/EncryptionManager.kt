package com.example.data.local.datastore

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionManager @Inject constructor(private val context: Context) {
    private val aead: Aead

    init {
        AeadConfig.register()
        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREFERENCE_FILE)
            .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
        aead = keysetHandle.getPrimitive(Aead::class.java)
    }

    fun encrypt(plainText: String): String {
        val cipherText = aead.encrypt(plainText.toByteArray(Charsets.UTF_8), null)
        return Base64.encodeToString(cipherText, Base64.NO_WRAP)
    }

    fun decrypt(cipherText: String): String {
        val decodedCipherText = Base64.decode(cipherText, Base64.NO_WRAP)
        val plainText = aead.decrypt(decodedCipherText, null)
        return String(plainText, Charsets.UTF_8)
    }

    companion object {
        private const val KEYSET_NAME = "master_keyset"
        private const val PREFERENCE_FILE = "master_key_preference"
        private const val MASTER_KEY_URI = "android-keystore://master_key"
    }
}