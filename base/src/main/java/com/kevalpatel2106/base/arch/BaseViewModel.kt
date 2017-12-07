package com.kevalpatel2106.base.arch

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.CallSuper
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
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()

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
        blockUi.postValue(false)
        errorMessage.postValue(null)
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()

        //Delete all the API connections.
        mCompositeDisposable.dispose()
    }

}