/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.base.arch

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.CallSuper
import android.support.annotation.VisibleForTesting
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Kevalpatel2106 on 30-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
abstract class BaseViewModel : ViewModel() {

    /**
     * [CompositeDisposable] to hold all the disposables from Rx and repository.
     */
    @VisibleForTesting
    internal val mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    protected fun addDisposable(disposable: Disposable) = mCompositeDisposable.add(disposable)

    /**
     * Boolean to change the value when authentication API call starts/ends. So that UI can change
     * or enable/disable views.
     */
    val blockUi: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * The string to display as the error message on the screen.
     */
    val errorMessage: SingleLiveEvent<ErrorMessage> = SingleLiveEvent()

    init {
        blockUi.value = false
        errorMessage.value = null
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()

        //Delete all the API connections.
        mCompositeDisposable.dispose()
    }

    @VisibleForTesting
    internal fun clear() = onCleared()

    @VisibleForTesting
    internal fun addDisp(disposable: Disposable) = addDisposable(disposable)
}