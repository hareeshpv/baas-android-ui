package com.payu.baas.core

import androidx.core.content.ContextCompat
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import okhttp3.mockwebserver.RecordedRequest
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@Config(manifest= Config.NONE)
@RunWith(RobolectricTestRunner::class)
class KycSdkUnitTests {
//    private var activity: MainActivity? = null
    var context = InstrumentationRegistry.getInstrumentation().targetContext
    var mockApiCaller: MockApiCallManger? = null

    @Before
    fun setUp() {
//        activity = Robolectric.buildActivity(MainActivity::class.java)
//            .create()
//            .resume()
//            .get()
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
    fun testForKycSelfiApi() {
        TestCase.assertNotNull(
            mockApiCaller!!.callForKycSelfieApi(ContextCompat.getDrawable(context,R.mipmap.advance)!!).execute()
                .body()!!
        )
        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("POST", recordedRequest!!.method)
    }
    @Test
    @Throws(IOException::class)
    fun testForKycAadharApi() {
        TestCase.assertNotNull(
            mockApiCaller!!.callForKycAadharApi().execute()
                .body()!!
        )

        // test for correct request passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("POST", recordedRequest!!.method)
    }
    @Test
    @Throws(IOException::class)
    fun testForKycLocationApi() {
        TestCase.assertNull(
            mockApiCaller!!.callForKycLocationApi().execute()
                .body()!!
        )
        TestCase.assertNotNull(
            mockApiCaller!!.callForKycLocationApi().execute()
                .body()!!
        )
        // test for correct request method passed
        val recordedRequest: RecordedRequest? =
            mockApiCaller!!.mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        TestCase.assertEquals("POST", recordedRequest!!.method)
    }
    @After
    fun shutDown() {
        mockApiCaller!!.tearDownServer()
    }
}