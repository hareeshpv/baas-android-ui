package com.payu.baas.core

import android.telephony.PhoneNumberUtils
import android.view.View
import android.widget.EditText
import com.payu.baas.core.model.apiModels.GetAccountBalanceApiModel
import com.payu.baas.core.model.model.*
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.core.util.BaaSConstants
import com.payu.baas.core.util.JsonUtils
import com.payu.baas.core.model.responseModels.CardSetPinResponse
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import okhttp3.mockwebserver.RecordedRequest
import org.robolectric.annotation.Config
import retrofit2.Response
import java.util.concurrent.TimeUnit

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class CardApisUnitTests {
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
    fun testForGetCardImageApi() {
        TestCase.assertNotNull(
            mockApiCaller!!.callForGetImageApi().execute()
                .body()!!.cardImageUrl
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("GET", recordedRequest!!.method)
    }

    @Test
    @Throws(IOException::class)
    fun testForGetCardDetailsDataApi() {
        TestCase.assertEquals(
            "ACTIVE",
            mockApiCaller!!.callForGetCardDetailsDataApi().execute()
                .body()!!.status
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("GET", recordedRequest!!.method)
    }

    @Test
    @Throws(IOException::class)
    fun testForGetCardTransactionModeDataApi() {
        // will return true as allow field is false at position 0
        TestCase.assertEquals(
            false,
            mockApiCaller!!.callForGetCardTransactionModeDataApi().execute()
                .body()!!.transactionModes!!.get(0).allow
        )
        // will return false(failure) as allow field is false at position 0
        TestCase.assertEquals(
            "CONTACTLESS",
            mockApiCaller!!.callForGetCardTransactionModeDataApi().execute()
                .body()!!.transactionModes!!.get(0).allow
        )
        // will return true
        TestCase.assertEquals(
            "ECOM",
            mockApiCaller!!.callForGetCardTransactionModeDataApi().execute()
                .body()!!.transactionModes!!.get(0).channel
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("GET", recordedRequest!!.method)
    }

    @Test
    @Throws(IOException::class)
    fun testForSetCardTransactionModeDataApi() {
        TestCase.assertEquals(
            "Limit-Block config set successful",
            mockApiCaller!!.callForSetCardTransactionModeDataApi().execute()
                .body()!!.message
        )
    }

    @Test
    @Throws(IOException::class)
    fun testForSetBlockCardDataApi() {
        //returns true
        TestCase.assertEquals(
            "ACTIVE",
            mockApiCaller!!.callForSetBlockCardDataApi().execute()
                .body()!!.status
        )
        //returns false
        TestCase.assertEquals(
            "INACTIVE",
            mockApiCaller!!.callForSetBlockCardDataApi().execute()
                .body()!!.status
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        //Returns true
        TestCase.assertEquals("PUT", recordedRequest!!.method)
        //Returns false and throwa Failure error
        TestCase.assertEquals("POST", recordedRequest!!.method)

    }

    @Test
    @Throws(IOException::class)
    fun testForSetUnBlockCardDataApi() {
        TestCase.assertEquals(
            "INACTIVE",
            mockApiCaller!!.callForSetUnBlockCardDataApi().execute()
                .body()!!.status
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("PUT", recordedRequest!!.method)
    }

    @Test
    @Throws(IOException::class)
    fun testForSetPinDataApi() {
        var call: Response<CardSetPinResponse> = mockApiCaller!!.callForSetPinDataApi().execute()
        TestCase.assertNotNull(
            call.body()!!.redirectUrl
        )
        //if  url not null then check is correct url retreived
        TestCase.assertEquals(
            "https://secure.uat.happay.in/pinset/cpin_77b63715dcfc4b949e3a902a9d133276",
            call.body()!!.redirectUrl
        )

        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("PUT", recordedRequest!!.method)
    }

    @Test
    @Throws(IOException::class)
    fun testForSetCardLimitApi() {
        var call: Response<SetLimitResponse> =
            mockApiCaller!!.callForSetCardLimitDataApi().execute()
        TestCase.assertNotNull(
            call.body()!!.message
        )
        //if  message not null then check is success message retreived
        TestCase.assertEquals(
            "Limit-Block config set successful",
            call.body()!!.message
        )

        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("POST", recordedRequest!!.method)
    }

    @Test
    @Throws(IOException::class)
    fun testForGetCardLimitApi() {
        var call: Response<GetLimitsResponse> =
            mockApiCaller!!.callForGetCardLimitDataApi().execute()
        TestCase.assertNotNull(
            call.body()!!.limitConfigs!!
        )
        //if  url not null then check is success message retreived
        TestCase.assertEquals(
            "POS",
            call.body()!!.limitConfigs!!.swipeChannel
        )

        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("GET", recordedRequest!!.method)
        TestCase.assertEquals("PUT", recordedRequest!!.method)
    }

    @Test
    @Throws(IOException::class)
    fun testForUpdateCardPinSetStatusApi() {
        var call: Response<UpdateCardPinSetStatusResponse> =
            mockApiCaller!!.callForUpdateCardPinSetStatusDataApi().execute()
        TestCase.assertNotNull(
            call.body()!!.message
        )
        //if  url not null then check is success message retreived
        TestCase.assertEquals(
            "Limit-Block config set successful",
            call.body()!!.message
        )

        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("PUT", recordedRequest!!.method)
    }

    @Test
    @Throws(IOException::class)
    fun testForGetCardPinApi() {
        var call: Response<GetCardPinStatusResponse> =
            mockApiCaller!!.callForGetCardPinDataApi().execute()
        TestCase.assertNotNull(
            call.body()!!.status
        )
        //if  url not null then check is success message retreived
        TestCase.assertEquals(
            "Limit-Block config set successful",
            call.body()!!.status
        )

        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("GET", recordedRequest!!.method)
    }

    @Test
    @Throws(IOException::class)
    fun testForValidateCardKitApi() {
        TestCase.assertTrue(
            mockApiCaller!!.callForValidateCardKitDataApi().execute().body()!!.validated == true
        )
        // when not validated and test will fail as we passing false and receiving false
        TestCase.assertTrue(
            mockApiCaller!!.callForValidateFalseCardKitApi().execute().body()!!.validated == true
        )
    }

    @Test
    @Throws(IOException::class)
    fun testForCardReorderApi() {
        TestCase.assertTrue(
            mockApiCaller!!.callForCardReorderApi().execute().body()!!.message.equals("Re-ordered")
        )

    }

    @Test
    @Throws(IOException::class)
    fun testForValidateCardKitStatusApi() {
        TestCase.assertTrue(
            mockApiCaller!!.callForValidateCardKitStatusApi().execute().body()!!.
            message.equals("Delivered")
        )
        TestCase.assertTrue(
            mockApiCaller!!.callForValidateCardKitStatusApi().execute().body()!!.
            message.equals("Intransit")
        )
    }

    @After
    fun shutDown() {
        mockApiCaller!!.tearDownServer()
    }
}