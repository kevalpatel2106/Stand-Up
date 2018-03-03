/*
 *  Copyright 2018 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

/*
 *  Copyright 2018 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

/*
 *  Copyright 2018 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

/*
 *  Copyright 2018 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

/**
 * Created by Keval on 02/03/18.
 * Configuration parameters for the debug variant of the application.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
object DebugConfig {

    /**
     * Name of the application to display.
     */
    const val APPLICATION_NAME = "Stand Up QA"

    /**
     * Package name of the debug variant. This package name will add .debug postfix to the package
     * name defined in [DefaultConfig.applicationId].
     */
    const val PACKAGE_NAME = "${DefaultConfig.applicationId}.debug"

    //Debug key config
    /**
     * Relative path to the keystore file (.jks). Note that the build.gradle file for each module will
     * be located at ./module-dir/build.gradle.
     */
    const val KEYSTORE_FILE = "./misc/debugkey.jks"

    /**
     * Alias for the key store.
     */
    const val KEYSTORE_ALIAS = "stand_up_android"

    /**
     * Password for the keystore alias.
     */
    const val KEYSTORE_ALIASPASSWORD = "2ea8249e-8ffa-4b77-adde-c70ad4d99edc"

    /**
     * Password of the key store.
     */
    const val KEYSTORE_PASSWORD = "2ea8249e-8ffa-4b77-adde-c70ad4d99edc"

    // Google keys
    // See: https://console.developers.google.com/apis/credentials
    /**
     * Server/Client id for the debug key and [PACKAGE_NAME]. You can get this from the cloud console.
     * This id is used for OAuth 2.0 authentication in "Sign in with Google".
     */
    const val SERVER_CLIENT_ID = "921769590451-vi8kka7s7tnk5k8u8utiu9i9fsg68hn0.apps.googleusercontent.com"
    /**
     * Android API key for the debug key and [PACKAGE_NAME]. You can get this from the cloud console.
     */
    const val ANDROID_API_KEY = "AIzaSyBd3eBhrPv3HlTkQjlufwOAEsbwOkllYE4"

    // Facebook credentials (For facebook login)
    // See: https://developers.facebook.com/docs/facebook-login/android/
    /**
     * Unique identifier for the Facebook app. You can get this app id from Facebook Developer Console.
     */
    const val FACEBOOK_APP_ID = "154534775066463"
    /**
     * Deep link protocol scheme for the facebook sign in. You can get this app id from Facebook
     * Developer Console.
     */
    const val FB_LOGIN_PROTOCOL_SCHEME = "fb154534775066463"

    // Server config
    /**
     * URL of Stand Up server with out trailing '/'.
     */
    const val SERVER_BASE_URL = "http://192.168.0.3:8080"
}