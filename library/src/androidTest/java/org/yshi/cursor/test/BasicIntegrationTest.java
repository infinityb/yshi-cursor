package org.yshi.cursor.test;

import org.yshi.cursor.library.test.R;
import org.yshi.cursor.test.interfaces.CursorServer;
import org.yshi.cursor.test.mock.MockCursorServer;

import android.test.AndroidTestCase;

import com.google.gson.JsonObject;


public class BasicIntegrationTest extends AndroidTestCase {
    private static final String TAG = "BasicIntegrationTest";

    public void testMockCursorServer() throws Exception {
        CursorServer cursor_server = new MockCursorServer(
                getContext().getResources().openRawResource(R.raw.test_array));

        JsonObject response0 = cursor_server.getInitial(CursorServer.Direction.LEFT, 2);
        assertEquals(response0.get("left_cursor").getAsString(), "b0");
        assertTrue(response0.get("left_is_terminal").getAsBoolean());
        assertEquals(response0.get("right_cursor").getAsString(), "a2");
        assertFalse(response0.get("right_is_terminal").getAsBoolean());

        JsonObject response1 = cursor_server.get(response0.get("right_cursor").getAsString(), 2);
        assertEquals(response1.get("left_cursor").getAsString(), "b2");
        assertFalse(response1.get("left_is_terminal").getAsBoolean());
        assertEquals(response1.get("right_cursor").getAsString(), "a3");
        assertFalse(response1.get("right_is_terminal").getAsBoolean());

        JsonObject response2 = cursor_server.get(response1.get("left_cursor").getAsString(), 3);
        assertEquals(response2.get("left_cursor").getAsString(), "b0");
        assertTrue(response2.get("left_is_terminal").getAsBoolean());
        assertEquals(response2.get("right_cursor").getAsString(), "a2");
        assertFalse(response2.get("right_is_terminal").getAsBoolean());
    }
}