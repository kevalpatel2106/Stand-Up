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

package com.kevalpatel2106.common.userActivity.repo

import com.kevalpatel2106.common.userActivity.UserActivity
import com.kevalpatel2106.common.userActivity.UserActivityDaoMockImpl
import com.kevalpatel2106.common.userActivity.UserActivityHelper
import com.kevalpatel2106.common.userActivity.UserActivityType
import com.kevalpatel2106.network.NetworkApi
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.utils.TimeUtils
import io.reactivex.observers.TestObserver
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.nio.file.Paths
import java.util.*

/**
 * Created by Kevalpatel2106 on 01-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(Enclosed::class)
class UserActivityRepoImplTest {

    @RunWith(JUnit4::class)
    class InsertNewActivityTest {

        private lateinit var userActivityRepoImpl: UserActivityRepo
        private lateinit var userActivityDao: UserActivityDaoMockImpl
        private lateinit var mockWebServerManager: MockServerManager

        @Before
        fun setUp() {
            //Mock network set
            mockWebServerManager = MockServerManager()
            mockWebServerManager.startMockWebServer()

            //Mock database table
            userActivityDao = UserActivityDaoMockImpl(ArrayList())

            userActivityRepoImpl = UserActivityRepoImpl(
                    userActivityDao,
                    NetworkApi().getRetrofitClient(mockWebServerManager.getBaseUrl())
            )
        }

        @After
        fun tearUp() {
            mockWebServerManager.close()
        }

        @Test
        fun checkInsertNewUserActivity_WithInvalidStartTime() {
            try {

                val endTime = System.currentTimeMillis()
                val userActivityToInsert = UserActivity(
                        eventStartTimeMills = 0,
                        eventEndTimeMills = endTime,
                        isSynced = false,
                        type = UserActivityType.MOVING.toString().toLowerCase()
                )

                val testObserver = TestObserver<Long>()
                userActivityRepoImpl.insertNewUserActivity(userActivityToInsert).subscribe(testObserver)

                Assert.fail()
            } catch (e: IllegalArgumentException) {
                //Test passed
                //NO OP
            }
        }

        @Test
        fun checkInsertNewUserActivity_WithInvalidEndTime() {
            try {

                val startTime = System.currentTimeMillis() - 300_000L
                val userActivityToInsert = UserActivity(
                        eventStartTimeMills = startTime,
                        eventEndTimeMills = 0,
                        isSynced = false,
                        type = UserActivityType.MOVING.toString().toLowerCase()
                )

                val testObserver = TestObserver<Long>()
                userActivityRepoImpl.insertNewUserActivity(userActivityToInsert).subscribe(testObserver)

                testObserver.assertNoErrors()
                        .assertValueCount(1)
                        .assertValueAt(0) { it == 0L }
                        .assertComplete()
                Assert.assertEquals(userActivityDao.tableItems.size, 1)
                Assert.assertEquals(userActivityDao.tableItems[0].eventStartTimeMills, startTime)
                Assert.assertEquals(userActivityDao.tableItems[0].eventEndTimeMills, startTime
                        + UserActivityHelper.endTimeCorrectionValue)

                Assert.assertEquals(userActivityDao.tableItems[0].userActivityType, UserActivityType.MOVING)
                Assert.assertFalse(userActivityDao.tableItems[0].isSynced)
            } catch (e: IllegalArgumentException) {
                Assert.fail()
            }
        }

        @Test
        fun checkInsertNewUserActivity_WithNoLatestActivity() {
            val startTime = System.currentTimeMillis() - 3600000
            val endTime = System.currentTimeMillis()
            val userActivityToInsert = UserActivity(
                    eventStartTimeMills = startTime,
                    eventEndTimeMills = endTime,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase()
            )

            val testObserver = TestObserver<Long>()
            userActivityRepoImpl.insertNewUserActivity(userActivityToInsert)
                    .subscribe(testObserver)
            testObserver.awaitTerminalEvent()

            testObserver.assertNoErrors()
                    .assertValueCount(1)
                    .assertValueAt(0) { it == 0L }
                    .assertComplete()
            Assert.assertEquals(userActivityDao.tableItems.size, 1)
            Assert.assertEquals(userActivityDao.tableItems[0].eventStartTimeMills, startTime)
            Assert.assertEquals(userActivityDao.tableItems[0].eventEndTimeMills, endTime)
            Assert.assertEquals(userActivityDao.tableItems[0].userActivityType, UserActivityType.MOVING)
            Assert.assertFalse(userActivityDao.tableItems[0].isSynced)
        }

        @Test
        fun checkInsertNewUserActivity_WithLatestActivityOfSameType() {
            val startTime = System.currentTimeMillis() - 3600000
            //Set fake data.
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = System.currentTimeMillis() - 4200000,
                    eventEndTimeMills = System.currentTimeMillis() - 3600000,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = startTime,
                    eventEndTimeMills = startTime + 10_000L,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )

            val userActivityToInsert = UserActivity(
                    eventStartTimeMills = System.currentTimeMillis() - 180000,
                    eventEndTimeMills = System.currentTimeMillis(),
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase()
            )

            val testObserver = TestObserver<Long>()
            userActivityRepoImpl.insertNewUserActivity(userActivityToInsert)
                    .subscribe(testObserver)
            testObserver.awaitTerminalEvent()

            testObserver.assertNoErrors()
                    .assertValueCount(1)
                    .assertValueAt(0) { it == userActivityDao.tableItems[0].localId }
                    .assertComplete()
            Assert.assertEquals(userActivityDao.tableItems.size, 2)
            Assert.assertEquals(userActivityDao.tableItems[0].eventStartTimeMills, startTime)
            Assert.assertEquals(userActivityDao.tableItems[0].eventEndTimeMills, userActivityToInsert.eventEndTimeMills)
            Assert.assertEquals(userActivityDao.tableItems[0].userActivityType, UserActivityType.MOVING)
            Assert.assertFalse(userActivityDao.tableItems[0].isSynced)
        }

        @Test
        fun checkInsertNewUserActivity_DoNotMeagre_WithLatestActivityOfSameType() {
            val now = System.currentTimeMillis()

            //Set fake data.
            //Old activity
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = now - 4200_000,
                    eventEndTimeMills = now - 3600_000,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )

            //Latest activity
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = now - 1800_000,
                    eventEndTimeMills = now + 90_000L,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )

            //Activity to insert
            val userActivityToInsert = UserActivity(
                    eventStartTimeMills = now - 60_000,
                    eventEndTimeMills = now,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase()
            )

            val testObserver = TestObserver<Long>()
            userActivityRepoImpl.insertNewUserActivity(
                    newActivity = userActivityToInsert,
                    doNotMergeWithPrevious = true
            ).subscribe(testObserver)
            testObserver.awaitTerminalEvent()

            testObserver.assertNoErrors()
                    .assertValueCount(1)
                    .assertValueAt(0) { it == userActivityDao.tableItems[0].localId }
                    .assertComplete()
            Assert.assertEquals(userActivityDao.tableItems.size, 3)

            Assert.assertEquals(userActivityDao.tableItems[0].eventStartTimeMills, userActivityToInsert.eventStartTimeMills)
            Assert.assertEquals(userActivityDao.tableItems[0].eventEndTimeMills, userActivityToInsert.eventEndTimeMills)
            Assert.assertEquals(userActivityDao.tableItems[0].userActivityType, UserActivityType.MOVING)

            Assert.assertEquals(userActivityDao.tableItems[1].eventStartTimeMills, now - 1800_000)
            Assert.assertEquals(userActivityDao.tableItems[1].eventEndTimeMills, now + 90_000L)
            Assert.assertEquals(userActivityDao.tableItems[1].userActivityType, UserActivityType.MOVING)
            Assert.assertFalse(userActivityDao.tableItems[0].isSynced)
        }

        @Test
        fun checkInsertNewUserActivity_WithLatestActivityOfDifferentType() {
            //Set fake data.
            val endTimeOfNewEvent = System.currentTimeMillis()
            val startTimeOfNewEvent = endTimeOfNewEvent - 180000

            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = endTimeOfNewEvent - 4200000,
                    eventEndTimeMills = endTimeOfNewEvent - 3600000,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = endTimeOfNewEvent - 3600000,
                    eventEndTimeMills = startTimeOfNewEvent - UserActivityRepo.DURATION,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )

            val userActivityToInsert = UserActivity(
                    eventStartTimeMills = startTimeOfNewEvent,
                    eventEndTimeMills = endTimeOfNewEvent,
                    isSynced = false,
                    type = UserActivityType.SITTING.toString().toLowerCase()
            )

            val testObserver = TestObserver<Long>()
            userActivityRepoImpl.insertNewUserActivity(userActivityToInsert)
                    .subscribe(testObserver)
            testObserver.awaitTerminalEvent()

            testObserver.assertNoErrors()
                    .assertValueCount(1)
                    .assertValueAt(0) { it == 2L }
                    .assertComplete()

            Assert.assertEquals(userActivityDao.tableItems.size, 3)

            Assert.assertEquals(userActivityDao.tableItems[1].eventStartTimeMills, endTimeOfNewEvent - 3600000)
            Assert.assertEquals(userActivityDao.tableItems[1].eventEndTimeMills, startTimeOfNewEvent)
            Assert.assertEquals(userActivityDao.tableItems[1].userActivityType, UserActivityType.MOVING)

            Assert.assertEquals(userActivityDao.tableItems[0].eventStartTimeMills, startTimeOfNewEvent)
            Assert.assertEquals(userActivityDao.tableItems[0].eventEndTimeMills, endTimeOfNewEvent)
            Assert.assertEquals(userActivityDao.tableItems[0].userActivityType, UserActivityType.SITTING)
            Assert.assertFalse(userActivityDao.tableItems[0].isSynced)
        }

        @Test
        fun checkInsertNewUserActivity_WithLatestActivityOfDifferentTypeAndNotTracked() {
            //Set fake data.
            val endTimeOfNewEvent = System.currentTimeMillis()
            val startTimeOfNewEvent = endTimeOfNewEvent - 180000

            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = endTimeOfNewEvent - 4200000,
                    eventEndTimeMills = endTimeOfNewEvent - 3600000,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = endTimeOfNewEvent - 3600000,
                    eventEndTimeMills = startTimeOfNewEvent - 3 * UserActivityRepo.DURATION,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )

            val userActivityToInsert = UserActivity(
                    eventStartTimeMills = startTimeOfNewEvent,
                    eventEndTimeMills = endTimeOfNewEvent,
                    isSynced = false,
                    type = UserActivityType.SITTING.toString().toLowerCase()
            )

            val testObserver = TestObserver<Long>()
            userActivityRepoImpl.insertNewUserActivity(userActivityToInsert)
                    .subscribe(testObserver)
            testObserver.awaitTerminalEvent()

            testObserver.assertNoErrors()
                    .assertValueCount(1)
                    .assertValueAt(0) { it == 2L }
                    .assertComplete()

            Assert.assertEquals(userActivityDao.tableItems.size, 3)

            Assert.assertEquals(userActivityDao.tableItems[1].eventStartTimeMills,
                    endTimeOfNewEvent - 3600000)
            Assert.assertEquals(userActivityDao.tableItems[1].eventEndTimeMills,
                    startTimeOfNewEvent - 3 * UserActivityRepo.DURATION)
            Assert.assertEquals(userActivityDao.tableItems[1].userActivityType,
                    UserActivityType.MOVING)

            Assert.assertEquals(userActivityDao.tableItems[0].eventStartTimeMills, startTimeOfNewEvent)
            Assert.assertEquals(userActivityDao.tableItems[0].eventEndTimeMills, endTimeOfNewEvent)
            Assert.assertEquals(userActivityDao.tableItems[0].userActivityType, UserActivityType.SITTING)
            Assert.assertFalse(userActivityDao.tableItems[0].isSynced)
        }

    }

    @RunWith(JUnit4::class)
    class SendPendingActivitiesToServerTest {

        private val path = Paths.get("").toAbsolutePath().toString().let {
            return@let if (it.endsWith("common")) it else it.plus("/common")
        }
        val RESPONSE_DIR_PATH = String.format("%s/src/test/java/com/kevalpatel2106/common/userActivity/repo", path)


        private lateinit var userActivityRepo: UserActivityRepo
        private lateinit var userActivityDao: UserActivityDaoMockImpl
        private lateinit var mockWebServerManager: MockServerManager

        @Before
        fun setUp() {
            //Mock network set
            mockWebServerManager = MockServerManager()
            mockWebServerManager.startMockWebServer()

            //Mock database table
            userActivityDao = UserActivityDaoMockImpl(ArrayList())

            userActivityRepo = UserActivityRepoImpl(
                    userActivityDao,
                    NetworkApi("78653456", "test-token").getRetrofitClient(mockWebServerManager.getBaseUrl())
            )
        }

        @After
        fun tearUp() {
            mockWebServerManager.close()
        }

        @Test
        @Throws(Exception::class)
        fun checkWithNoActivity() {

            val testSubscribe = TestObserver<Int>()
            userActivityRepo.sendPendingActivitiesToServer().subscribe(testSubscribe)

            testSubscribe.awaitTerminalEvent()

            testSubscribe.assertComplete()
                    .assertValueCount(1)
                    .assertValueAt(0, { it == 0 })
                    .assertNoErrors()
        }

        @Test
        @Throws(Exception::class)
        fun checkWithNoPendingActivity() {
            //Set fake data. All activities are synced.
            val now = TimeUtils.todayMidnightCal(false).timeInMillis
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = now,
                    eventEndTimeMills = now + 4200_000,
                    isSynced = true,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = now - 2 * TimeUtils.ONE_DAY_MILLISECONDS,
                    eventEndTimeMills = now - 2 * TimeUtils.ONE_DAY_MILLISECONDS + 3600_000,
                    isSynced = true,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )

            val testSubscribe = TestObserver<Int>()
            userActivityRepo.sendPendingActivitiesToServer().subscribe(testSubscribe)

            testSubscribe.awaitTerminalEvent()

            testSubscribe.assertComplete()
                    .assertValueCount(1)
                    .assertValueAt(0, { it == 0 })
                    .assertNoErrors()
        }

        @Test
        @Throws(Exception::class)
        fun checkWithAllNotTrackedPendingActivity() {
            //Set fake data. All activities are synced.
            val now = TimeUtils.todayMidnightCal(false).timeInMillis
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = now,
                    eventEndTimeMills = now + 4200_000,
                    isSynced = false,
                    type = UserActivityType.NOT_TRACKED.toString().toLowerCase())
            )
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = now - 2 * TimeUtils.ONE_DAY_MILLISECONDS,
                    eventEndTimeMills = now - 2 * TimeUtils.ONE_DAY_MILLISECONDS + 3600_000,
                    isSynced = false,
                    type = UserActivityType.NOT_TRACKED.toString().toLowerCase())
            )

            val testSubscribe = TestObserver<Int>()
            userActivityRepo.sendPendingActivitiesToServer().subscribe(testSubscribe)

            testSubscribe.awaitTerminalEvent()

            testSubscribe.assertComplete()
                    .assertValueCount(1)
                    .assertValueAt(0, { it == 0 })
                    .assertNoErrors()
        }

        @Test
        @Throws(Exception::class)
        fun check_SyncPendingActivity_AllSyncSuccess() {
            //Set fake data. All activities are synced.
            val now = TimeUtils.todayMidnightCal(false).timeInMillis
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = now,
                    eventEndTimeMills = now + 4200_000,
                    isSynced = false,
                    type = UserActivityType.SITTING.toString().toLowerCase())
            )
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = now - 2 * TimeUtils.ONE_DAY_MILLISECONDS,
                    eventEndTimeMills = now - 2 * TimeUtils.ONE_DAY_MILLISECONDS + 3600_000,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )

            mockWebServerManager.enqueueResponse(File(RESPONSE_DIR_PATH
                    + "/save_activity_response.json"))
            mockWebServerManager.enqueueResponse(File(RESPONSE_DIR_PATH
                    + "/save_activity_response.json"))

            val testSubscribe = TestObserver<Int>()
            userActivityRepo.sendPendingActivitiesToServer().subscribe(testSubscribe)

            testSubscribe.awaitTerminalEvent()
            //Verify the request
            val request1 = mockWebServerManager.mockWebServer.takeRequest()
            Assert.assertEquals("/saveActivity", request1.path)
            Assert.assertNotNull(request1.getHeader("Authorization"))

            val request2 = mockWebServerManager.mockWebServer.takeRequest()
            Assert.assertEquals("/saveActivity", request2.path)
            Assert.assertNotNull(request2.getHeader("Authorization"))

            //Verify response
            testSubscribe.assertComplete()
                    .assertValueCount(1)
                    .assertValueAt(0, { it == 2 })
                    .assertNoErrors()

            Assert.assertTrue(userActivityDao.tableItems[0].isSynced)
            Assert.assertEquals(5126972522889216 /* From response */, userActivityDao.tableItems[0].remoteId)
            Assert.assertTrue(userActivityDao.tableItems[1].isSynced)
            Assert.assertEquals(5126972522889216 /* From response */, userActivityDao.tableItems[1].remoteId)
        }

        @Test
        @Throws(Exception::class)
        fun check_SyncPendingActivity_OneSyncFailed() {
            //Set fake data. All activities are synced.
            val now = TimeUtils.todayMidnightCal(false).timeInMillis
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = now,
                    eventEndTimeMills = now + 4200_000,
                    isSynced = false,
                    type = UserActivityType.SITTING.toString().toLowerCase())
            )
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = now - 2 * TimeUtils.ONE_DAY_MILLISECONDS,
                    eventEndTimeMills = now - 2 * TimeUtils.ONE_DAY_MILLISECONDS + 3600_000,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )

            mockWebServerManager.enqueueResponse(File(RESPONSE_DIR_PATH
                    + "/save_activity_response.json"))
            mockWebServerManager.enqueueResponse(File(RESPONSE_DIR_PATH
                    + "/authentication_field_missing.json"))

            val testSubscribe = TestObserver<Int>()
            userActivityRepo.sendPendingActivitiesToServer().subscribe(testSubscribe)

            testSubscribe.awaitTerminalEvent()

            //Verify the request
            val request1 = mockWebServerManager.mockWebServer.takeRequest()
            Assert.assertEquals("/saveActivity", request1.path)
            Assert.assertNotNull(request1.getHeader("Authorization"))

            val request2 = mockWebServerManager.mockWebServer.takeRequest()
            Assert.assertEquals("/saveActivity", request2.path)
            Assert.assertNotNull(request2.getHeader("Authorization"))

            //Verify response
            testSubscribe.assertComplete()
                    .assertValueCount(1)
                    .assertValueAt(0, { it == 1 })
                    .assertNoErrors()

            Assert.assertTrue(userActivityDao.tableItems[0].isSynced)
            Assert.assertEquals(5126972522889216 /* From response */, userActivityDao.tableItems[0].remoteId)
            Assert.assertFalse(userActivityDao.tableItems[1].isSynced)
            Assert.assertEquals(0, userActivityDao.tableItems[1].remoteId)
        }

        @Test
        @Throws(Exception::class)
        fun check_SyncPendingActivity_AllSyncFailed() {
            //Set fake data. All activities are synced.
            val now = TimeUtils.todayMidnightCal(false).timeInMillis
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = now,
                    eventEndTimeMills = now + 4200_000,
                    isSynced = false,
                    type = UserActivityType.SITTING.toString().toLowerCase())
            )
            userActivityDao.insert(UserActivity(
                    eventStartTimeMills = now - 2 * TimeUtils.ONE_DAY_MILLISECONDS,
                    eventEndTimeMills = now - 2 * TimeUtils.ONE_DAY_MILLISECONDS + 3600_000,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )

            mockWebServerManager.enqueueResponse(File(RESPONSE_DIR_PATH
                    + "/authentication_field_missing.json"))
            mockWebServerManager.enqueueResponse(File(RESPONSE_DIR_PATH
                    + "/authentication_field_missing.json"))

            val testSubscribe = TestObserver<Int>()
            userActivityRepo.sendPendingActivitiesToServer().subscribe(testSubscribe)

            testSubscribe.awaitTerminalEvent()

            //Verify the request
            val request1 = mockWebServerManager.mockWebServer.takeRequest()
            Assert.assertEquals("/saveActivity", request1.path)
            Assert.assertNotNull(request1.getHeader("Authorization"))

            val request2 = mockWebServerManager.mockWebServer.takeRequest()
            Assert.assertEquals("/saveActivity", request2.path)
            Assert.assertNotNull(request2.getHeader("Authorization"))

            //Verify the response
            testSubscribe.assertComplete()
                    .assertValueCount(1)
                    .assertValueAt(0, { it == 0 })
                    .assertNoErrors()

            Assert.assertFalse(userActivityDao.tableItems[0].isSynced)
            Assert.assertEquals(0, userActivityDao.tableItems[0].remoteId)
            Assert.assertFalse(userActivityDao.tableItems[1].isSynced)
            Assert.assertEquals(0, userActivityDao.tableItems[1].remoteId)
        }
    }

    @RunWith(JUnit4::class)
    class GetActivitiesFromServerTest {

        private val path = Paths.get("").toAbsolutePath().toString().let {
            return@let if (it.endsWith("common")) it else it.plus("/common")
        }
        val RESPONSE_DIR_PATH = String.format("%s/src/test/java/com/kevalpatel2106/common/userActivity/repo", path)


        private lateinit var userActivityRepo: UserActivityRepo
        private lateinit var userActivityDao: UserActivityDaoMockImpl
        private lateinit var mockWebServerManager: MockServerManager

        @Before
        fun setUp() {
            //Mock network set
            mockWebServerManager = MockServerManager()
            mockWebServerManager.startMockWebServer()

            //Mock database table
            userActivityDao = UserActivityDaoMockImpl(ArrayList())

            userActivityRepo = UserActivityRepoImpl(
                    userActivityDao,
                    NetworkApi("78653456", "test-token").getRetrofitClient(mockWebServerManager.getBaseUrl())
            )
        }

        @After
        fun tearUp() {
            mockWebServerManager.close()
        }

        @Test
        @Throws(Exception::class)
        fun checkWithNoActivity() {
            mockWebServerManager.enqueueResponse(File(RESPONSE_DIR_PATH
                    + "/get_activity_response.json"))

            val testSubscribe = TestObserver<Int>()
            userActivityRepo.getActivitiesFromServer().subscribe(testSubscribe)

            testSubscribe.awaitTerminalEvent()

            //Verify the request
            val request1 = mockWebServerManager.mockWebServer.takeRequest()
            Assert.assertEquals("/getActivity", request1.path)
            Assert.assertNotNull(request1.getHeader("Authorization"))

            //Verify the response
            testSubscribe
                    .assertValueCount(1)
                    .assertValueAt(0, { it == 2 })
                    .assertNoErrors()

            Assert.assertEquals(2, userActivityDao.tableItems.size)
            with(userActivityDao.tableItems[0]) {
                Assert.assertTrue(this.isSynced)
                Assert.assertEquals(5653294995210240 /* From response */, this.remoteId)
                Assert.assertEquals(TimeUtils.convertToMilli(1520585420827000000) /* From response */, this.eventStartTimeMills)
                Assert.assertEquals(TimeUtils.convertToMilli(1520585723812000000) /* From response */, this.eventEndTimeMills)
                Assert.assertEquals(UserActivityType.SITTING /* From response */, this.userActivityType)
            }

            with(userActivityDao.tableItems[1]) {
                Assert.assertTrue(this.isSynced)
                Assert.assertEquals(5110359589388288 /* From response */, this.remoteId)
                Assert.assertEquals(TimeUtils.convertToMilli(1520585238264000000) /* From response */, this.eventStartTimeMills)
                Assert.assertEquals(TimeUtils.convertToMilli(1520585420827000000) /* From response */, this.eventEndTimeMills)
                Assert.assertEquals(UserActivityType.MOVING /* From response */, this.userActivityType)
            }
        }

        @Test
        @Throws(Exception::class)
        fun checkWithActivityDifferentRemoteId() {
            //Set fake data. All activities are synced.
            val now = TimeUtils.todayMidnightCal(false).timeInMillis
            userActivityDao.insert(UserActivity(
                    remoteId = 43857643L,
                    eventStartTimeMills = now,
                    eventEndTimeMills = now + 4200_000,
                    isSynced = false,
                    type = UserActivityType.SITTING.toString().toLowerCase())
            )
            userActivityDao.insert(UserActivity(
                    remoteId = 438576445L,
                    eventStartTimeMills = now - 2 * TimeUtils.ONE_DAY_MILLISECONDS,
                    eventEndTimeMills = now - 2 * TimeUtils.ONE_DAY_MILLISECONDS + 3600_000,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )

            mockWebServerManager.enqueueResponse(File(RESPONSE_DIR_PATH
                    + "/get_activity_response.json"))

            val testSubscribe = TestObserver<Int>()
            userActivityRepo.getActivitiesFromServer().subscribe(testSubscribe)

            testSubscribe.awaitTerminalEvent()

            //Verify the request
            val request1 = mockWebServerManager.mockWebServer.takeRequest()
            Assert.assertEquals("/getActivity", request1.path)
            Assert.assertNotNull(request1.getHeader("Authorization"))

            //Verify the response
            testSubscribe
                    .assertValueCount(1)
                    .assertValueAt(0, { it == 2 })
                    .assertNoErrors()

            Assert.assertEquals(2 + 2, userActivityDao.tableItems.size)
            with(userActivityDao.tableItems[2]) {
                Assert.assertTrue(this.isSynced)
                Assert.assertEquals(5653294995210240 /* From response */, this.remoteId)
                Assert.assertEquals(TimeUtils.convertToMilli(1520585420827000000) /* From response */, this.eventStartTimeMills)
                Assert.assertEquals(TimeUtils.convertToMilli(1520585723812000000) /* From response */, this.eventEndTimeMills)
                Assert.assertEquals(UserActivityType.SITTING /* From response */, this.userActivityType)
            }

            with(userActivityDao.tableItems[3]) {
                Assert.assertTrue(this.isSynced)
                Assert.assertEquals(5110359589388288 /* From response */, this.remoteId)
                Assert.assertEquals(TimeUtils.convertToMilli(1520585238264000000) /* From response */, this.eventStartTimeMills)
                Assert.assertEquals(TimeUtils.convertToMilli(1520585420827000000) /* From response */, this.eventEndTimeMills)
                Assert.assertEquals(UserActivityType.MOVING /* From response */, this.userActivityType)
            }
        }

        @Test
        @Throws(Exception::class)
        fun checkWithActivitySameRemoteId() {
            //Set fake data. All activities are synced.
            val now = TimeUtils.todayMidnightCal(false).timeInMillis
            userActivityDao.insert(UserActivity(
                    remoteId = 5653294995210240L,
                    eventStartTimeMills = now,
                    eventEndTimeMills = now + 4200_000,
                    isSynced = false,
                    type = UserActivityType.MOVING.toString().toLowerCase())
            )
            userActivityDao.insert(UserActivity(
                    remoteId = 5110359589388288L,
                    eventStartTimeMills = now - 2 * TimeUtils.ONE_DAY_MILLISECONDS,
                    eventEndTimeMills = now - 2 * TimeUtils.ONE_DAY_MILLISECONDS + 3600_000,
                    isSynced = false,
                    type = UserActivityType.SITTING.toString().toLowerCase())
            )

            mockWebServerManager.enqueueResponse(File(RESPONSE_DIR_PATH
                    + "/get_activity_response.json"))

            val testSubscribe = TestObserver<Int>()
            userActivityRepo.getActivitiesFromServer().subscribe(testSubscribe)

            testSubscribe.awaitTerminalEvent()

            //Verify the request
            val request1 = mockWebServerManager.mockWebServer.takeRequest()
            Assert.assertEquals("/getActivity", request1.path)
            Assert.assertNotNull(request1.getHeader("Authorization"))

            //Verify the response
            testSubscribe.assertComplete()
                    .assertValueCount(1)
                    .assertValueAt(0, { it == 2 })
                    .assertNoErrors()

            Assert.assertEquals(2, userActivityDao.tableItems.size)
            with(userActivityDao.tableItems[0]) {
                Assert.assertTrue(this.isSynced)
                Assert.assertEquals(5653294995210240 /* From response */, this.remoteId)
                Assert.assertEquals(TimeUtils.convertToMilli(1520585420827000000) /* From response */, this.eventStartTimeMills)
                Assert.assertEquals(TimeUtils.convertToMilli(1520585723812000000) /* From response */, this.eventEndTimeMills)
                Assert.assertEquals(UserActivityType.SITTING /* From response */, this.userActivityType)
            }

            with(userActivityDao.tableItems[1]) {
                Assert.assertTrue(this.isSynced)
                Assert.assertEquals(5110359589388288 /* From response */, this.remoteId)
                Assert.assertEquals(TimeUtils.convertToMilli(1520585238264000000) /* From response */, this.eventStartTimeMills)
                Assert.assertEquals(TimeUtils.convertToMilli(1520585420827000000) /* From response */, this.eventEndTimeMills)
                Assert.assertEquals(UserActivityType.MOVING /* From response */, this.userActivityType)
            }
        }

        @Test
        @Throws(Exception::class)
        fun checkErrorFromServer() {
            mockWebServerManager.enqueueResponse(File(RESPONSE_DIR_PATH
                    + "/authentication_field_missing.json"))

            val testSubscribe = TestObserver<Int>()
            userActivityRepo.getActivitiesFromServer().subscribe(testSubscribe)

            testSubscribe.awaitTerminalEvent()

            //Verify the request
            val request1 = mockWebServerManager.mockWebServer.takeRequest()
            Assert.assertEquals("/getActivity", request1.path)
            Assert.assertNotNull(request1.getHeader("Authorization"))

            //Verify the response
            testSubscribe.assertErrorMessage("Required field missing.")

            Assert.assertTrue(userActivityDao.tableItems.isEmpty())
        }
    }
}
