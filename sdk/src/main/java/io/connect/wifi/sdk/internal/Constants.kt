package io.connect.wifi.sdk.internal

/**
 * @suppress Internal api
 *
 * Keep names of rule types identifiers
 */
internal class Constants {

    companion object {
        const val TYPE_WPA2_SUPPORT = "wpa2_old_method"
        const val TYPE_WPA2_SUGGESTION = "wpa2_suggestion_method"
        const val TYPE_WPA2_API30 = "wpa2_api30_method"
        const val TYPE_PASSPOINT_AOUCP = "passpoint_aoupc_method"
        const val TYPE_PASSPOINT_RESULT = "passpoint_30_method"
        const val TYPE_WPA2_ENTERPRISE_SUGGESTION = "wpa2_enterprise_eap_ttls_suggestion_method"
        const val TYPE_WPA2_PROFILE = "wpa2_profile"
    }
}