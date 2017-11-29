/*
 * Copyright 2017 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel2106.network

import java.net.HttpURLConnection

/**
 * Created by Keval on 08-Feb-17.
 * This class contains status codes for the api responses from the server.
 *
 * @author <a href="https://github.com/kevalpatel2106">Keval</a>
 */

object APIStatusCodes {
    const val SUCCESS_CODE = 0                              //Status code for success
    const val ERROR_CODE_REQUIRED_FIELD_MISSING = 1         //Status code for the error
    const val ERROR_CODE_GENERAL = 2                        //Status code for the error
    const val ERROR_CODE_EXCEPTION = 1002                    //Status code for exception
    const val ERROR_CODE_UNAUTHORIZED = HttpURLConnection.HTTP_UNAUTHORIZED                 //Status code for unauthorized
    const val ERROR_CODE_UNKNOWN_ERROR = 1001              //Status code for the error code that is not
}
