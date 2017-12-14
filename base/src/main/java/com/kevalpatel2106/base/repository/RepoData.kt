package com.kevalpatel2106.base.repository

/**
 * Created by Kevalpatel2106 on 29-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class RepoData<out T>(val isError: Boolean, val data: T?) {

    /**
     * Null [data] constructor.
     */
    constructor(isError: Boolean) : this(isError, null)

    var errorMessage: String? = null
        get() = if (isError) field else null

    var errorCode: Int = 0

    lateinit var source: SourceType
}