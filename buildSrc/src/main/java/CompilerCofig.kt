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
 * Created by Keval on 03/03/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
object CompilerCofig {

    const val IS_MULTIDEX_ENABLED = true

    const val ADB_TIMEOUT = 10 * 60 * 1000  // 10 minutes

    const val MAX_METHOD_COUNT = 1_10_000

    const val MAX_DEX_PROCESS_COUNT = 6
    const val PREDEX_LIBRARIES = true

    const val CRUNCHER_PROCESS_COUNT = 4

    const val LINT_ABORT_ON_ERROR = false
}