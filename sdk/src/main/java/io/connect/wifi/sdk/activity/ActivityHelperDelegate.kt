package io.connect.wifi.sdk.activity

import android.app.Activity
import android.content.Intent
import java.lang.ref.SoftReference

/**
 * Holder of activity reference
 */
class ActivityHelperDelegate(activity: Activity) : ActivityHelper {

    private var activityReference: SoftReference<Activity> = SoftReference(activity)

    override fun provideActivityReference() = activityReference.get()

    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        activityReference.get()?.startActivityForResult(intent, requestCode)
    }

    override fun cleanup() {
        activityReference.clear()
    }
}