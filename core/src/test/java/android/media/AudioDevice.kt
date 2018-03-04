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

package android.media

import org.mockito.Mockito

/**
 * Created by Keval on 04/03/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
fun getAudioDevice(audioDeviceType: Int): AudioDeviceInfo {

    //Mocking final classes
    val audioDeviceInfo = Mockito.mock(AudioDeviceInfo::class.java)
    Mockito.`when`(audioDeviceInfo.type).thenReturn(audioDeviceType)
    return audioDeviceInfo
}