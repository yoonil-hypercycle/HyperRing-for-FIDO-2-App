package com.hyperring.sdk.core
import android.app.Activity
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.hyperring.sdk.core.mock1.MockHyperRingData
import com.hyperring.sdk.core.mock1.MockHyperRingNFC
import com.hyperring.sdk.core.mock1.MockHyperRingTag
import com.hyperring.sdk.core.mock1.MockNFCStatus
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test


class MockHyperRingNFCTest {

    @Before
    fun setUp() {
        // Initialize MockHyperRingNFC before each test
        MockHyperRingNFC.initializeMockHyperRingNFC(ApplicationProvider.getApplicationContext<Context>())
    }
    @Test
    fun testGetNFCStatusInitialized() {
        // Test when MockHyperRingNFC is initialized
        val status = MockHyperRingNFC.getNFCStatus()
        assertEquals(MockNFCStatus.NFC_ENABLED, status)
    }


    @Test
    fun testGetNFCStatusNotInitialized() {
        // Test when MockHyperRingNFC is not initialized
        val context = ApplicationProvider.getApplicationContext<Context>()
        MockHyperRingNFC.initializeMockHyperRingNFC(null)
        val status = MockHyperRingNFC.getNFCStatus()
        assertEquals(MockNFCStatus.NFC_UNSUPPORTED, status)
    }

    @Test
    fun testStartNFCTagPollingEnabled() {
        // Create a mock Activity using MockK
        val mockActivity = mockk<Activity>()
        // Call the function under test
        MockHyperRingNFC.startNFCTagPolling(mockActivity) { mockTag ->
            // This callback should be called when NFC is enabled
            // Perform assertions here to verify the behavior
            // Verify that the callback is called with a mock tag
            assertNotNull(mockTag)
            // Since we can't directly access isPolling, we verify the behavior indirectly
            // For example, if the callback is called, it indicates that isPolling was set to true
            // Return a mock tag explicitly
            mockTag
        }
    }


    @Test
    fun testStartNFCTagPollingDisabled() {
        // Test starting NFC tag polling when NFC is disabled
        val mockActivity = mockk<Activity>()
        MockHyperRingNFC.initializeMockHyperRingNFC(null)
        MockHyperRingNFC.startNFCTagPolling(mockActivity) { mockTag ->
            // Assert that NFC polling is not enabled
            assertEquals(false, MockHyperRingNFC.isPolling)
            // Simulate NFC tag discovered
            mockTag
        }
    }
    @Test
    fun testStopNFCTagPolling() {
        // Test stopping NFC tag polling
        val mockActivity = mockk<Activity>()
        MockHyperRingNFC.stopNFCTagPolling(mockActivity)
        // Assert that NFC polling is stopped
        assertEquals(false, MockHyperRingNFC.isPolling)
    }

    @Test
    fun testWrite() {
        // Test writing to NFC tag
        val mockActivity = mockk<Activity>()
        val hyperRingTagId: Long? = 123456789
        val hyperRingTag = MockHyperRingTag()
        val hyperRingData = MockHyperRingData("Some data") // Provide the data to the constructor
        val result = MockHyperRingNFC.write(hyperRingTagId, hyperRingTag, hyperRingData)
        // Assert that writing was successful
        assertEquals(true, result)
    }


     @Test
     fun testRead() {
         // Test reading from NFC tag
         val mockActivity = mockk<Activity>()
         val hyperRingTagId: Long? = 123456789
         val hyperRingTag = MockHyperRingTag()
         val result = MockHyperRingNFC.read(hyperRingTagId, hyperRingTag)
         // Assert that reading was successful
         assertNotNull(result)
         // You can add more assertions based on the expected behavior of the read function
     }



}
