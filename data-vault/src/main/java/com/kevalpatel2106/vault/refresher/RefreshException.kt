package com.kevalpatel2106.vault.refresher

/**
 * Created by Kevalpatel2106 on 29-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class RefreshException(override val message: String?, val errorCode: Int) : Throwable()