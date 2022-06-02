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
class SalaryAdvanceUnitTests {
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
    fun testForGetSalaryAdvanceInfoApi() {
        TestCase.assertNull(
            mockApiCaller!!.callForGetSalaryAdvanceInfoApi().execute()
                .body()!!
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(10, TimeUnit.SECONDS)
        TestCase.assertEquals("GET", recordedRequest!!.method)
    }
    @After
    fun shutDown() {
        mockApiCaller!!.tearDownServer()
    }
}