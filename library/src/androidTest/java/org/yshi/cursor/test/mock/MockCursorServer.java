package org.yshi.cursor.test.mock;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.yshi.cursor.test.interfaces.CursorServer;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by sell on 05/04/14.
 */
public class MockCursorServer implements CursorServer {
    private static final String TAG = "MockCursorServer";

    public final JsonArray m_obj;

    public MockCursorServer(InputStream input_stream) {
       m_obj = new JsonParser().parse(new InputStreamReader(input_stream)).getAsJsonArray();
    }

    @Override
    public int getItemCount() {
        return m_obj.size();

    }

    public JsonObject getInitial(Direction direction, int limit) {
        JsonObject out = new JsonObject();
        if (direction != Direction.LEFT) {
            throw new RuntimeException("NotImplemented");
        }

        int start_index = 0;
        int end_index = start_index + limit;

        JsonArray result_array = new JsonArray();
        boolean left_is_terminal = (Direction.LEFT == direction);
        boolean right_is_terminal = false;

        int last_index = start_index;
        for (int i = start_index; i < Math.min(end_index, m_obj.size()); i += 1) {
            result_array.add(m_obj.get(i));
            last_index = i + 1;
        }

        out.add("results", result_array);
        out.add("left_cursor", new JsonPrimitive("b0"));
        out.add("left_is_terminal", new JsonPrimitive(left_is_terminal));
        out.add("right_cursor", new JsonPrimitive(String.format("a%d", last_index)));
        out.add("right_is_terminal", new JsonPrimitive(right_is_terminal));
        return out;
    }

    public JsonObject get(String cursor, int limit) {
        JsonObject out = new JsonObject();
        Direction direction = Direction.fromChar(cursor.charAt(0));
        Log.e(TAG, String.format("starting with cursor=%s", cursor));
        int left_cursor, right_cursor;
        int start_index = Integer.parseInt(cursor.substring(1));

        JsonArray result_array = new JsonArray();
        boolean left_is_terminal = false;
        boolean right_is_terminal = false;

        int last_index;
        if (direction == Direction.LEFT) {
            int end_index = start_index + limit;
            last_index = start_index;
            for (int i = start_index; i < Math.min(end_index, m_obj.size()); i += 1) {
                result_array.add(m_obj.get(i));
                last_index = i + 1;
            }
            left_cursor = start_index;
            right_cursor = last_index;
        } else if (direction == Direction.RIGHT) {
            int end_index = start_index - limit;
            last_index = start_index - 1;
            for (int i = start_index - 1; Math.max(end_index, 0) <= i; i -= 1) {
                Log.e(TAG, String.format("running cursor=%s .. for(i=%d)", cursor, i));
                result_array.add(m_obj.get(i));
                last_index = i;
            }
            left_cursor = last_index;
            right_cursor = start_index;
            left_is_terminal = 0 == last_index;
        } else {
            throw new RuntimeException(String.format("Unknown direction %s", direction));
        }

        out.add("results", result_array);
        out.add("left_cursor", new JsonPrimitive(String.format("b%d", left_cursor)));
        out.add("left_is_terminal", new JsonPrimitive(left_is_terminal));
        out.add("right_cursor", new JsonPrimitive(String.format("a%d", right_cursor)));
        out.add("right_is_terminal", new JsonPrimitive(right_is_terminal));
        return out;
    }
}
