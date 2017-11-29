package com.kevalpatel2106.network

/**
 * Created by Keval on 11/11/17.
 * This si custom exception to throw whenever there is error generated from the server.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see [NWException]
 */
@Deprecated("Not use")
internal class NWException(val errorCode: Int, message: String) : Exception(message)
