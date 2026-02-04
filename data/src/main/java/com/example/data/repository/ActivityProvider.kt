package com.example.data.repository

import android.app.Activity
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ActivityProvider @Inject constructor() {
    private var activityReference: WeakReference<Activity>? = null

    fun setCurrentActivity(activity: Activity) {
        activityReference = WeakReference(activity)
    }

    fun getActivity(): Activity? = activityReference?.get()
}