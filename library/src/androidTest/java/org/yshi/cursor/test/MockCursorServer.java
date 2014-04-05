package org.yshi.cursor.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 * Created by sell on 05/04/14.
 */
public class MockCursorServer implements CursorServer {

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
        Character direction = cursor.charAt(0);

        // if (direction != Direction.LEFT) {
        if (direction != 'a') {
            throw new RuntimeException("NotImplemented");
        }

        int start_index = Integer.parseInt(cursor.substring(1));
        int end_index = start_index + limit;

        JsonArray result_array = new JsonArray();
        boolean left_is_terminal = false;
        boolean right_is_terminal = false;

        int last_index = start_index;
        for (int i = start_index; i < Math.min(end_index, m_obj.size()); i += 1) {
            result_array.add(m_obj.get(i));
            last_index = i + 1;
        }

        out.add("results", result_array);
        out.add("left_cursor", new JsonPrimitive(String.format("b%d", start_index)));
        out.add("left_is_terminal", new JsonPrimitive(left_is_terminal));
        out.add("right_cursor", new JsonPrimitive(String.format("a%d", last_index)));
        out.add("right_is_terminal", new JsonPrimitive(right_is_terminal));
        return out;
    }
}
