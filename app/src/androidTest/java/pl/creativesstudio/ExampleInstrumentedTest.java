package pl.creativesstudio;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Test instrumentalny dla aplikacji TrackMyBusWAW.
 * Testuje działanie aplikacji na urządzeniu fizycznym.
 *
 * @author Serhii
 * @version 1.0
 * @since 2024-12-16
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    /**
     * Testuje kontekst aplikacji.
     * Sprawdza, czy aplikacja działa w odpowiednim kontekście systemowym.
     *
     * @throws Exception Jeśli wystąpi błąd podczas testu.
     */
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("pl.creativesstudio", appContext.getPackageName());
    }
}