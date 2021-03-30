package io.connect.wifi.sdk.connect.delegate

/**
 * @suppress Internal api
 *
 * Interface for delegation
 *
 * @since 1.0.1
 */
interface ConnectionDelegate {

    /**
     * Make delegate implementation available for connection
     */
    fun prepareDelegate()

    /**
     * Connect to wifi using previously created delegate implementation
     */
    fun connect()
}