package com.kevalpatel2106.utils

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
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
fun AppCompatActivity.showSnack(message: String,
                                actionName: String? = null,
                                actionListener: View.OnClickListener? = null,
                                duration: Int = Snackbar.LENGTH_SHORT) {

    val snackbar = Snackbar.make((findViewById<ViewGroup>(android.R.id.content)).getChildAt(0),
            message, duration)

    actionName?.let {
        snackbar.setAction(actionName, actionListener)
        snackbar.setActionTextColor(ViewUtils.getAccentColor(this))
    }

    snackbar.show()
}

fun AppCompatActivity.showSnack(message: String,
                                actionName: Int,
                                actionListener: View.OnClickListener? = null,
                                duration: Int = Snackbar.LENGTH_SHORT) {
    showSnack(message, getString(actionName), actionListener, duration)
}

/**
 * Display the snack bar.
 */
fun AppCompatActivity.showSnack(@StringRes message: Int,
                                @StringRes actionName: Int = 0,
                                actionListener: View.OnClickListener? = null,
                                duration: Int = Snackbar.LENGTH_SHORT) {
    val snackbar = Snackbar.make((findViewById<ViewGroup>(android.R.id.content)).getChildAt(0),
            message, duration)
    if (actionName > 0) {
        snackbar.setAction(actionName, actionListener)
        snackbar.setActionTextColor(ViewUtils.getAccentColor(this))
    }

    snackbar.show()
}

/**
 * Show the toast for [Toast.LENGTH_SHORT] duration.
 */
fun AppCompatActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Show the toast for [Toast.LENGTH_SHORT] duration.
 */
fun AppCompatActivity.showToast(@StringRes message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}