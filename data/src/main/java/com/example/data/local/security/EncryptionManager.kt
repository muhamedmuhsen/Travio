package com.example.data.local.security

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.security.InvalidKeyException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val aead: Aead

    init {
        Timber.d("Initializing EncryptionManager")
        AeadConfig.register()
        val keysetHandle = try {
            Timber.d("Attempting to load existing keyset")
            AndroidKeysetManager.Builder()
                .withSharedPref(context, KEYSET_NAME, PREFERENCE_FILE)
                .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
                .withMasterKeyUri(MASTER_KEY_URI)
                .build()
                .keysetHandle
                .also { Timber.d("Successfully loaded existing keyset") }
        } catch (e: InvalidKeyException) {
            Timber.w(e, "Invalid key exception, recreating keyset")
            // Key is corrupted or invalidated, delete and recreate
            context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
                .also { Timber.d("Cleared corrupted key preferences") }

            AndroidKeysetManager.Builder()
                .withSharedPref(context, KEYSET_NAME, PREFERENCE_FILE)
                .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
                .withMasterKeyUri(MASTER_KEY_URI)
                .build()
                .keysetHandle
                .also { Timber.d("Successfully created new keyset") }
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error during keyset initialization")
            throw e
        }
        aead = keysetHandle.getPrimitive(Aead::class.java)
        Timber.d("EncryptionManager initialized successfully")
    }

    fun encrypt(plainText: String): String {
        Timber.d("Encrypting data")
        return try {
            val cipherText = aead.encrypt(plainText.toByteArray(Charsets.UTF_8), null)
            Base64.encodeToString(cipherText, Base64.NO_WRAP)
                .also { Timber.d("Data encrypted successfully") }
        } catch (e: Exception) {
            Timber.e(e, "Encryption failed")
            throw e
        }
    }

    fun decrypt(cipherText: String): String {
        Timber.d("Decrypting data")
        return try {
            val decodedCipherText = Base64.decode(cipherText, Base64.NO_WRAP)
            val plainText = aead.decrypt(decodedCipherText, null)
            String(plainText, Charsets.UTF_8)
                .also { Timber.d("Data decrypted successfully") }
        } catch (e: Exception) {
            Timber.e(e, "Decryption failed")
            throw e
        }
    }

    companion object {
        private const val KEYSET_NAME = "master_keyset"
        private const val PREFERENCE_FILE = "master_key_preference"
        private const val MASTER_KEY_URI = "android-keystore://master_key"
    }
}
