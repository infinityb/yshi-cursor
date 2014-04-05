package org.yshi.cursor.test;

import org.yshi.cursor.library.test.R;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.gson.JsonObject;


public class BasicIntegrationTest extends AndroidTestCase {
    private static final String TAG = "BasicIntegrationTest";

    public void testMockCursorServer() throws Exception {
        CursorServer cursor_server = new MockCursorServer(
                getContext().getResources().openRawResource(R.raw.test_array));

        JsonObject response = cursor_server.getInitial(CursorServer.Direction.LEFT, 2);
        assertEquals(response.get("left_cursor").getAsString(), "b0");
        assertTrue(response.get("left_is_terminal").getAsBoolean());
        assertEquals(response.get("right_cursor").getAsString(), "a2");
        assertFalse(response.get("right_is_terminal").getAsBoolean());

        response = cursor_server.get(response.get("right_cursor").getAsString(), 2);
        assertEquals(response.get("left_cursor").getAsString(), "b2");
        assertFalse(response.get("left_is_terminal").getAsBoolean());
        assertEquals(response.get("right_cursor").getAsString(), "a3");
        assertFalse(response.get("right_is_terminal").getAsBoolean());
    }
}