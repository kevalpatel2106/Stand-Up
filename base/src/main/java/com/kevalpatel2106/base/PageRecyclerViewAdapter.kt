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

package com.kevalpatel2106.base

import android.content.Context
import android.support.annotation.CallSuper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevalpatel2106.base.PageRecyclerViewAdapter.RecyclerViewListener

/**
 * Created by Keval on 31-May-17.
 * Base class for loading [RecyclerView.Adapter] that handles page
 * complete callbacks. Use this class instead of [RecyclerView.Adapter] through out the application.

 * @author [&#39;https://github.com/kevalpatel2106&#39;]['https://github.com/kevalpatel2106']
 * *
 * @see RecyclerViewListener
 */

abstract class PageRecyclerViewAdapter<VH : PageRecyclerViewAdapter.PageViewHolder>(
        protected val context: Context,
        private val mCollection: Collection<*>,
        protected val listener: RecyclerViewListener?
) : RecyclerView.Adapter<PageRecyclerViewAdapter.PageViewHolder>() {

    companion object {
        @Suppress("PrivatePropertyName")
        const val TYPE_LOADER = 5364
    }

    private var mHasNext = false

    @CallSuper
    override fun getItemCount(): Int =
            if (mHasNext && listener != null)
                mCollection.size + 1
            else
                mCollection.size

    @CallSuper
    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        //notify on pag complete
        if (listener != null && mHasNext && position == mCollection.size - 1) listener.onPageComplete()

        if (holder is LoaderViewHolder) {
            //Progressbar will start loading automatically.

        } else {
            //Call abstract method
            @Suppress("UNCHECKED_CAST")
            bindView(holder as VH, position)
        }
    }

    @CallSuper
    override fun getItemViewType(position: Int): Int {
        return if (position == mCollection.size) TYPE_LOADER else prepareViewType(position)
    }

    @CallSuper
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PageViewHolder {
        return if (viewType == TYPE_LOADER) {
            LoaderViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.layout_loader, parent, false))
        } else {
            prepareViewHolder(parent, viewType)
        }
    }

    /**
     * Here you should bind the view holder with your view and data.

     * @param holder   [RecyclerView.ViewHolder]
     * *
     * @param position position of the row.
     */
    abstract fun bindView(holder: VH, position: Int)

    abstract fun prepareViewHolder(parent: ViewGroup?, viewType: Int): VH

    abstract fun prepareViewType(position: Int): Int


    fun setHasNext(hasNext: Boolean) {
        mHasNext = hasNext
    }

    /**
     * Loading view holder
     */
    internal class LoaderViewHolder(itemView: View?) : PageViewHolder(itemView)

    /**
     * Base view holder for pagination
     */
    open class PageViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

    /**
     * Listener to get notify when the list ends.
     */
    interface RecyclerViewListener {

        /**
         * Callback to call when whole list is displayed.
         */
        fun onPageComplete()

        /**
         * Callback  to get notify when any item from the list gets selected.
         */
        fun onItemSelected(pos: Int)
    }
}


