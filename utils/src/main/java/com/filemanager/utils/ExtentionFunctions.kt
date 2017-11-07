/*
 *  Copyright 2017 Keval Patel.
 *
 *  Licensed under the GNU General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.filemanager.utils

import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast

/**
 * Created by Keval on 08-Sep-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
fun AppCompatActivity.showToast(message: String) = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()

fun AppCompatActivity.getInstance() = this

fun AppCompatActivity.showToast(@StringRes message: Int) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun ViewGroup.inflate(resId: Int) = LayoutInflater.from(context).inflate(resId, this, false)!!