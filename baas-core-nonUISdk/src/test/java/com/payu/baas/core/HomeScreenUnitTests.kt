package com.payu.baas.core

import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import java.lang.Exception
import okhttp3.mockwebserver.RecordedRequest
import org.robolectric.annotation.Config
import retrofit2.Call
import java.util.concurrent.TimeUnit

@Config(manifest= Config.NONE)
@RunWith(RobolectricTestRunner::class)
class HomeScreenUnitTests {
    var mockApiCaller: MockApiCallManger? = null

    @Before
    fun setUp() {
        mockApiCaller = MockApiCallManger()
        mockApiCaller!!.initialize()
    }

    /* Bellow all tests will be tested
    With  service created we can now call its method that should
   consume the MockResponse above.
   assertion to check if the result is as expected.
        */
    @Test
    @Throws(IOException::class)
    fun testForGetAccountBalanceDetailsApi() {
        TestCase.assertNull(
            mockApiCaller!!.callForGetAccountBalanceDetailsApi().execute()
                .body()!!.error
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("GET", recordedRequest!!.method)
    }
    @Test
    @Throws(IOException::class)
    fun testForGetAccountTransactionDetailsApi() {
        TestCase.assertNull(
            mockApiCaller!!.callForGetTransactionDetailsApi().execute()
                .body()!!.error
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("GET", recordedRequest!!.method)
    }
    @Test
    @Throws(IOException::class)
    fun testForGetAccountDetailsApi() {
        TestCase.assertNull(
            mockApiCaller!!.callForGetAccountDetailsApi().execute()
                .body()!!.error
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("GET", recordedRequest!!.method)
    }
    @Test
    @Throws(IOException::class)
    fun testForGetUserDetailsApi() {
        TestCase.assertNull(
            mockApiCaller!!.callForGetUserDetailsApi().execute()
                .body()
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("GET", recordedRequest!!.method)
    }
    @Test
    @Throws(IOException::class)
    fun testForUpdateEmailApi() {
        TestCase.assertNull(
            mockApiCaller!!.callForUpdateUserEmailApi().execute()
                .body()
        )

        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        // test for correct request method
        TestCase.assertEquals("POST", recordedRequest!!.method)
    }
    @Test
    @Throws(IOException::class)
    fun testForUpdateAddressApi() {
        TestCase.assertNull(
            mockApiCaller!!.callForUpdateUserAddressApi().execute()
                .body()
        )

        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        // test for correct request method
        TestCase.assertEquals("POST", recordedRequest!!.method)
    }
    @Test
    @Throws(IOException::class)
    fun testForUpdateMobileNumberApi() {
        TestCase.assertNull(
            mockApiCaller!!.callForUpdateUserMobileNumberApi().execute()
                .body()
        )

        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        // test for correct request method
        TestCase.assertEquals("POST", recordedRequest!!.method)
    }

    @Test
    @Throws(IOException::class)
    fun testForChangePasscode() {
        TestCase.assertEquals(
            "Password Updated Sucessfully",
            mockApiCaller!!.callForChangePasscodeApi(
                "json_set_passcode_success.json"
            ).execute()
                .body()!!.msg
        )
        //for getting request path and then checking the passcode
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(10, TimeUnit.SECONDS)
        TestCase.assertEquals("POST", recordedRequest!!.method)
    }

    @Test
    @Throws(IOException::class)
    fun testForResetPasscode() {
        TestCase.assertEquals(
            "Password Updated Sucessfully",
            mockApiCaller!!.callForResetPasscodeApi(
                "json_set_passcode_success.json"
            ).execute()
                .body()!!.msg
        )
        //for getting request path and then checking the passcode
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(10, TimeUnit.SECONDS)
        TestCase.assertEquals("POST", recordedRequest!!.method)
    }

    @Test
    @Throws(IOException::class)
    fun testForGetUserState() {
        TestCase.assertEquals(
            "2",  // in mockresponse we are passing 2 under message , so it will pass
            mockApiCaller!!.callForGetUserStateApi().execute()
                .body()!!.message
        )
        // below test will fail as we set mock response value 2
        TestCase.assertEquals(
            "3",  // in mockresponse we are passing 2 under message , so it will fail
            mockApiCaller!!.callForGetUserStateApi().execute()
                .body()!!.message
        )
        //for getting request path and then checking the passcode
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(10, TimeUnit.SECONDS)
        //TEST WILL FAIL
        TestCase.assertEquals("POST", recordedRequest!!.method)
        //PASS CASE
        TestCase.assertEquals("GET", recordedRequest!!.method)
    }

    @Test
    @Throws(IOException::class)
    fun testForGetAddressApi() {
        TestCase.assertEquals(
            "321, model towns",
            mockApiCaller!!.callForGetUserAddressApi(
                "json_get_user_address_success_response.json"
            ).execute()
                .body()!!.addressLine1
        )
        //error case true
        TestCase.assertEquals(
            "401",
            mockApiCaller!!.callForGetUserAddressApi(
                "json_get_user_address_error_response.json"
            ).execute()
                .body()!!.code
        )
        //for getting request path and then checking the passcode
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(10, TimeUnit.SECONDS)
        TestCase.assertEquals("POST", recordedRequest!!.method)
    }


    @After
    fun shutDown() {
        mockApiCaller!!.tearDownServer()
    }
}