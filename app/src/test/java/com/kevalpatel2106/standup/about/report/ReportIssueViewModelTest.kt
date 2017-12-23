package com.kevalpatel2106.standup.about.report

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.standup.R
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Keval on 23/12/17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class ReportIssueViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var model: ReportIssueViewModel

    @Before
    fun setUp() {
        model = ReportIssueViewModel()
    }

    @Test
    fun checkReportIssueWithInvalidTitle() {
        model.reportIssue("", "This is test issue description.")
        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertEquals(model.errorMessage.value!!.errorRes, R.string.error_invalid_issue_title)
    }

    @Test
    fun checkReportIssueWithInvalidDescription() {
        model.reportIssue("This is test issue title.", "")
        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertEquals(model.errorMessage.value!!.errorRes, R.string.error_invalid_issue_description)
    }

}