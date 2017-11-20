package com.kevalpatel2106.network

/**
 * Created by Keval on 17/11/17.
 * This class holds network config.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
object NetworkConfig {
    //Time out config.
    internal const val READ_TIMEOUT = 1L
    internal const val WRITE_TIMEOUT = 1L
    internal const val CONNECTION_TIMEOUT = 1L

    //Error messages.
    internal const val ERROR_MESSAGE_INTERNET_NOT_AVAILABLE = "Internet is not available. Please try again."
    internal const val ERROR_MESSAGE_SOMETHING_WRONG = "Something went wrong."
    internal const val ERROR_MESSAGE_BAD_REQUEST = "Invalid request. Please try again."
    internal const val ERROR_MESSAGE_NOT_FOUND = "Cannot perform the request. Please try again."
    internal const val ERROR_MESSAGE_SERVER_BUSY = "Server is too busy at this moment. Please try again."

    //Unauthorized broadcast action
    const val BROADCAST_UNAUTHORIZED = "com.kevalpatel2106.network.unauthorized"
}