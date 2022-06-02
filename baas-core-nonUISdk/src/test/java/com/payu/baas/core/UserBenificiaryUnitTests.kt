package com.payu.baas.core

import android.telephony.PhoneNumberUtils
import android.view.View
import android.widget.EditText
import com.payu.baas.core.model.apiModels.GetAccountBalanceApiModel
import com.payu.baas.core.model.model.*
import com.payu.baas.core.model.responseModels.ApiResponse
import com.payu.baas.core.model.responseModels.SaveAddressResponse
import com.payu.baas.core.model.responseModels.VerifyOtpResponse
import com.payu.baas.core.util.BaaSConstants
import com.payu.baas.core.util.JsonUtils
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
class UserBenificiaryUnitTests {
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
    fun testForCreateUserBenificiaryApi() {
        TestCase.assertNull(
            mockApiCaller!!.callForCreateUserBenificiariesApi().execute()
                .body()!!
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("POST", recordedRequest!!.method)
        TestCase.assertEquals("GET", recordedRequest!!.method) // failure case
    }
    @Test
    @Throws(IOException::class)
    fun testForGetUserBenificiaryApi() {
        TestCase.assertNull(
            mockApiCaller!!.callForGetUserBenificiaryApi().execute()
                .body()!!.error
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("GET", recordedRequest!!.method)
        TestCase.assertEquals("POST", recordedRequest!!.method) // failure case
    }
    @Test
    @Throws(IOException::class)
    fun testForUpdateUserBenificiaryApi() {
        TestCase.assertNull(
            mockApiCaller!!.callForUpdateUserBenificiariesApi().execute()
                .body()!!
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("PUT", recordedRequest!!.method)
        TestCase.assertEquals("GET", recordedRequest!!.method) // failure case
        TestCase.assertEquals("POST", recordedRequest!!.method) // failure case
    }
    @Test
    @Throws(IOException::class)
    fun testForUserBenificiaryBankTransferApi() {
        TestCase.assertNull(
            mockApiCaller!!.callForUserBenificiaryBankTransferApi().execute()
                .body()!!
        )
        // test case for missing fields in request body (cuureny is missing here in request)
        TestCase.assertNull(
            mockApiCaller!!.callForUserBenificiaryBankTransferApi_FailureCase().execute()
                .body()!!
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("POST", recordedRequest!!.method)
        TestCase.assertEquals("GET", recordedRequest!!.method) // failure case
        TestCase.assertEquals("PUT", recordedRequest!!.method) // failure case

    }
    @Test
    @Throws(IOException::class)
    fun testForGetRecentUserBenificiariesApi() {
        TestCase.assertNull(
            mockApiCaller!!.callForGetRecentUserBenificiariesApi().execute()
                .body()!!
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("GET", recordedRequest!!.method)
    }
    @Test
    @Throws(IOException::class)
    fun testForDeleteBenificiariesApi() {
        TestCase.assertNull(
            mockApiCaller!!.callForDeleteUserBenificiary().execute()
                .body()!!
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("DELETE", recordedRequest!!.method)
    }
    @Test
    @Throws(IOException::class)
    fun testForGetTranseferChargesApi() {
        TestCase.assertNull(
            mockApiCaller!!.callForGetTransferChargesApi().execute()
                .body()
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("GET", recordedRequest!!.method)
    }

    @After
    fun shutDown() {
        mockApiCaller!!.tearDownServer()
    }
}