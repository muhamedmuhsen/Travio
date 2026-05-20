package com.example.travio

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder
import com.example.travio.config.AppEnvironmentConfig
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import timber.log.Timber
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@HiltAndroidApp
class Travio : Application() {
    override fun onCreate() {
        super.onCreate()

        // Fails fast when tester/production variants are built with placeholder endpoint values.
        val environmentConfig = AppEnvironmentConfig.fromBuildConfig()

        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .components {
                    add(SvgDecoder.Factory())
                }
                .apply {
                    if (environmentConfig.enableDebugDiagnostics) {
                        okHttpClient(buildUnsafeOkHttpClient())
                    }
                }
                .build()
        )

        if (environmentConfig.enableDebugDiagnostics) {
            Timber.plant(Timber.DebugTree())
        }

        val stripePublishableKey = getString(R.string.stripe_publishable_key).trim()
        val isStripeKeyMissing = stripePublishableKey.isBlank() || stripePublishableKey == "pk_test_replace_me"
        require(!isStripeKeyMissing || environmentConfig.enableDebugDiagnostics) {
            "stripe_publishable_key is missing. Set it in app/config/environment.secrets.properties."
        }
        if (!isStripeKeyMissing) {
            PaymentConfiguration.init(this, stripePublishableKey)
        }
    }

    private fun buildUnsafeOkHttpClient(): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())
        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
            .build()
    }
}
