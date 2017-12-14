package com.kevalpatel2106.utils

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast


/**
 * Created by Keval on 19/11/17.
 * This is the extensions for the activity.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
/**
 * Display the snack bar.
 */
fun Fragment.showSnack(message: String,
                       actionName: String? = null,
                       actionListener: View.OnClickListener? = null,
                       duration: Int = Snackbar.LENGTH_SHORT) {

    activity?.let {
        if (activity is AppCompatActivity)
            (activity as AppCompatActivity).showSnack(message = message,
                    actionName = actionName,
                    actionListener = actionListener,
                    duration = duration)
    }
}

/**
 * Display the snack bar.
 */
fun Fragment.showSnack(@StringRes message: Int,
                       @StringRes actionName: Int = 0,
                       actionListener: View.OnClickListener? = null,
                       duration: Int = Snackbar.LENGTH_SHORT) {
    activity?.let {
        if (activity is AppCompatActivity)
            (activity as AppCompatActivity).showSnack(message = message,
                    actionName = actionName,
                    actionListener = actionListener,
                    duration = duration)
    }
}

/**
 * Show the toast for [Toast.LENGTH_SHORT] duration.
 */
fun Fragment.showToast(message: String) {
    activity?.let {
        if (activity is AppCompatActivity) (activity as AppCompatActivity).showToast(message = message)
    }
}

/**
 * Show the toast for [Toast.LENGTH_SHORT] duration.
 */
fun Fragment.showToast(@StringRes message: Int) {
    activity?.let {
        if (activity is AppCompatActivity) (activity as AppCompatActivity).showToast(message = message)
    }
}