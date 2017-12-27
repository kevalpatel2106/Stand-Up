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

package com.kevalpatel2106.base.uiController

import android.content.Context
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Keval on 17-Dec-16.
 * Base fragment is base class for [Fragment]. This handles connection and disconnect of Rx bus.
 * Use this class instead of [AppCompatActivity] through out the application.

 * @author [&#39;https://github.com/kevalpatel2106&#39;]['https://github.com/kevalpatel2106']
 */

abstract class BaseFragment : Fragment() {
    /**
     * [CompositeDisposable] that holds all the subscriptions.
     */
    @VisibleForTesting
    internal val mCompositeDisposable = CompositeDisposable()

    /**
     * Boolean to indicate the fragment is already created before.
     */
    private var mIsAlreadyCreated = false

    /**
     * Instance of the caller.
     */
    protected lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dispose()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Bind butter knife
        ButterKnife.bind(this, view)

        if (savedInstanceState == null && !mIsAlreadyCreated) {
            mIsAlreadyCreated = true
            runForFirstCreation()
        }
    }

    /**
     * This method will be called only when the fragment is created for the first time. Whenever
     * fragment  gets restarted because of the configuration changes or the fragment is popped back
     * from the back stack this method will not called as the fragment instance is not created
     * instead old instance is utilized.
     *
     * Note: Make sure you set retain instance to true while creating the fragment.
     */
    open fun runForFirstCreation() {
        //Do nothing
        //Empty method
    }


    /**
     * Add the subscription to the [CompositeDisposable].

     * @param disposable [Disposable]
     */
    fun addSubscription(disposable: Disposable?) {
        if (disposable == null) return
        mCompositeDisposable.add(disposable)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        //Context of an activity
        mContext = context!!
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //Whenever activity restarts dispose all  the previous connections.
        dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose()
    }

    /**
     * Dispose the composite disposable manually. This will remove the connection between all api
     * observables and observers.
     */
    @Suppress("MemberVisibilityCanPrivate")
    protected fun dispose() = mCompositeDisposable.dispose()

    /**
     * Finish the activity holding the fragment.
     */
    protected fun finish() = activity?.finish()

    /**
     * A method that will give you the callback from the parent activity when the back button
     * pressed.
     *
     * @return True if the back pressed event is handled by the fragment else false.
     */
    open fun onBackPressed(): Boolean = false
}
