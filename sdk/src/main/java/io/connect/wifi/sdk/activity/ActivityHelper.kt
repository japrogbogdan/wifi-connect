package io.connect.wifi.sdk.activity

import android.app.Activity
import android.content.Intent

/**
 * Helper to call activity.startActivityForResult(Intent, Int) function
 */
interface ActivityHelper {

    /**
     * Provide cached activity reference
     */
    fun provideActivityReference(): Activity?

    /**
     * Take activity reference from provideActivityReference() & call activity.startActivityForResult(Intent, Int)
     */
    fun startActivityForResult(intent: Intent, requestCode: Int)

    /**
     * Remove activity reference
     */
    fun cleanup()
}