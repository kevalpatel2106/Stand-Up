package com.standup.app.main

import android.app.Application
import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.standup.app.billing.repo.MockBillingRepoImpl
import com.standup.app.dashboard.DashboardApi
import com.standup.app.diary.DiaryModule
import com.standup.app.stats.StatsModule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

/**
 * Created by Keval on 26/03/18.
 *
 * @author [kevalpatel2106](https : / / github.com / kevalpatel2106)
 */
@RunWith(JUnit4::class)
class MainViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rule1 = RxSchedulersOverrideRule()

    private lateinit var mockApplication: Application
    private val mockBillingRepoImpl = MockBillingRepoImpl()
    private val mockDiaryModule = DiaryModule()
    private val mockStatsModule = StatsModule()
    private val mockDashboardApi = DashboardApi()

    private lateinit var model: MainViewModel

    @Before
    fun setUp() {
        mockApplication = Mockito.mock(Application::class.java)

        model = MainViewModel(
                application = mockApplication,
                billingRepo = mockBillingRepoImpl,
                diaryModule = mockDiaryModule,
                statsModule = mockStatsModule,
                dashboardApi = mockDashboardApi
        )
    }


    @Test
    @Throws(Exception::class)
    fun checkInit() {
        mockBillingRepoImpl.isPremiumPurchased = true
        mockBillingRepoImpl.isError = false

        assertTrue(model.isDisplayBuyPro.value!!)
        assertFalse(model.blockUi.value!!)
        assertNull(model.errorMessage.value)
    }

    @Test
    @Throws(Exception::class)
    fun checkIsDisplayBuyPro_Positive() {
        mockBillingRepoImpl.isPremiumPurchased = true
        mockBillingRepoImpl.isError = false

        model.checkIfToDisplayBuyPro()

        assertTrue(model.isDisplayBuyPro.value!!)
        assertFalse(model.blockUi.value!!)
        assertNull(model.errorMessage.value)
    }

    @Test
    @Throws(Exception::class)
    fun checkIsDisplayBuyPro_Negative() {
        mockBillingRepoImpl.isPremiumPurchased = false
        mockBillingRepoImpl.isError = false

        model.checkIfToDisplayBuyPro()

        assertFalse(model.isDisplayBuyPro.value!!)
        assertFalse(model.blockUi.value!!)
        assertNull(model.errorMessage.value)
    }

    @Test
    @Throws(Exception::class)
    fun checkIsDisplayBuyPro_Error() {
        mockBillingRepoImpl.isPremiumPurchased = true
        mockBillingRepoImpl.isError = true

        model.checkIfToDisplayBuyPro()

        assertFalse(model.isDisplayBuyPro.value!!)
        assertFalse(model.blockUi.value!!)
        assertNull(model.errorMessage.value)
    }
}