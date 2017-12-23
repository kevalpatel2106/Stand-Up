package com.kevalpatel2106.standup.about.donate;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Keval on 23/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

@Retention(RetentionPolicy.SOURCE)
@IntDef({2, 5, 10, 20})
@interface DonationAmount {
}
