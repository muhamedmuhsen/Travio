package com.example.data.local.security

import android.content.Context
import android.util.Base64
import android.util.Log
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.InvalidKeyException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val aead: Aead

    init {
        Log.d(TAG, "Initializing EncryptionManager")
        AeadConfig.register()
        val keysetHandle = try {
            Log.d(TAG, "Attempting to load existing keyset")
            AndroidKeysetManager.Builder()
                .withSharedPref(context, KEYSET_NAME, PREFERENCE_FILE)
                .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
                .withMasterKeyUri(MASTER_KEY_URI)
                .build()
                .keysetHandle
                .also { Log.d(TAG, "Successfully loaded existing keyset") }
        } catch (e: InvalidKeyException) {
            Log.w(TAG, "Invalid key exception, recreating keyset", e)
            // Key is corrupted or invalidated, delete and recreate
            context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
                .also { Log.d(TAG, "Cleared corrupted key preferences") }

            AndroidKeysetManager.Builder()
                .withSharedPref(context, KEYSET_NAME, PREFERENCE_FILE)
                .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
                .withMasterKeyUri(MASTER_KEY_URI)
                .build()
                .keysetHandle
                .also { Log.d(TAG, "Successfully created new keyset") }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during keyset initialization", e)
            throw e
        }
        aead = keysetHandle.getPrimitive(Aead::class.java)
        Log.d(TAG, "EncryptionManager initialized successfully")
    }

    fun encrypt(plainText: String): String {
        Log.d(TAG, "Encrypting data")
        return try {
            val cipherText = aead.encrypt(plainText.toByteArray(Charsets.UTF_8), null)
            Base64.encodeToString(cipherText, Base64.NO_WRAP)
                .also { Log.d(TAG, "Data encrypted successfully") }
        } catch (e: Exception) {
            Log.e(TAG, "Encryption failed", e)
            throw e
        }
    }

    fun decrypt(cipherText: String): String {
        Log.d(TAG, "Decrypting data")
        return try {
            val decodedCipherText = Base64.decode(cipherText, Base64.NO_WRAP)
            val plainText = aead.decrypt(decodedCipherText, null)
            String(plainText, Charsets.UTF_8)
                .also { Log.d(TAG, "Data decrypted successfully") }
        } catch (e: Exception) {
            Log.e(TAG, "Decryption failed", e)
            throw e
        }
    }

    companion object {
        private const val TAG = "EncryptionManager"
        private const val KEYSET_NAME = "master_keyset"
        private const val PREFERENCE_FILE = "master_key_preference"
        private const val MASTER_KEY_URI = "android-keystore://master_key"
    }
}
