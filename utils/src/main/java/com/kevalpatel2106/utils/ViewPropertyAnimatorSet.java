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

package com.kevalpatel2106.utils;

import android.animation.Animator;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;

import java.util.ArrayList;

/**
 * Created by Keval on 08/01/18.
 * A very naive implementation of a set of
 * {@link android.support.v4.view.ViewPropertyAnimatorCompat}.
 * Borrowed from support lib's {@link android.support.v7.view.ViewPropertyAnimatorCompatSet}.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

public class ViewPropertyAnimatorSet {

    private final ArrayList<ViewPropertyAnimator> mAnimators;

    private long mDuration = -1;
    private Interpolator mInterpolator;
    private Animator.AnimatorListener mListener;

    private boolean mIsStarted;
    private final Animator.AnimatorListener mProxyListener = new Animator.AnimatorListener() {
        private boolean mProxyStarted = false;
        private int mProxyEndCount = 0;

        void onEnd() {
            mProxyEndCount = 0;
            mProxyStarted = false;
            onAnimationsEnded();
        }

        @Override
        public void onAnimationStart(final Animator animator) {
            if (mProxyStarted) {
                return;
            }
            mProxyStarted = true;
            if (mListener != null) {
                mListener.onAnimationStart(null);
            }
        }

        @Override
        public void onAnimationEnd(final Animator animator) {
            if (++mProxyEndCount == mAnimators.size()) {
                if (mListener != null) {
                    mListener.onAnimationEnd(null);
                }
                onEnd();
            }
        }

        @Override
        public void onAnimationCancel(final Animator animator) {
            //NO OP
        }

        @Override
        public void onAnimationRepeat(final Animator animator) {
            //NO OP
        }
    };

    public ViewPropertyAnimatorSet() {
        mAnimators = new ArrayList<>();
    }

    public ViewPropertyAnimatorSet play(ViewPropertyAnimator animator) {
        if (!mIsStarted) {
            mAnimators.add(animator);
        }
        return this;
    }

    public ViewPropertyAnimatorSet playSequentially(ViewPropertyAnimator anim1,
                                                    ViewPropertyAnimator anim2) {
        mAnimators.add(anim1);
        anim2.setStartDelay(anim1.getDuration());
        mAnimators.add(anim2);
        return this;
    }

    public void start() {
        if (mIsStarted) return;
        for (ViewPropertyAnimator animator : mAnimators) {
            if (mDuration >= 0) {
                animator.setDuration(mDuration);
            }
            if (mInterpolator != null) {
                animator.setInterpolator(mInterpolator);
            }
            if (mListener != null) {
                animator.setListener(mProxyListener);
            }
            animator.start();
        }

        mIsStarted = true;
    }

    void onAnimationsEnded() {
        mIsStarted = false;
    }

    public void cancel() {
        if (!mIsStarted) {
            return;
        }
        for (ViewPropertyAnimator animator : mAnimators) {
            animator.cancel();
        }
        mIsStarted = false;
    }

    public ViewPropertyAnimatorSet setDuration(long duration) {
        if (!mIsStarted) {
            mDuration = duration;
        }
        return this;
    }

    public ViewPropertyAnimatorSet setInterpolator(Interpolator interpolator) {
        if (!mIsStarted) {
            mInterpolator = interpolator;
        }
        return this;
    }

    public ViewPropertyAnimatorSet setListener(Animator.AnimatorListener listener) {
        if (!mIsStarted) {
            mListener = listener;
        }
        return this;
    }
}
