package pl.creativesstudio;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * @file ExampleInstrumentedTest.java
 * @brief Instrumented test for the TrackMyBusWAW application.
 *
 * This class contains instrumented tests that run on an Android device.
 * It verifies the correctness of the application in a real device environment.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 *
 * @version 1.1
 * @since 2024-12-16
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    /**
     * Tests whether the application context is correctly set.
     *
     * This test retrieves the context of the app under test and verifies
     * that the package name matches the expected value.
     */
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("pl.creativesstudio", appContext.getPackageName());
    }
}