package org.yshi.cursor;

/**
 * Created by sell on 20/11/13.
 */

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sell on 20/11/13.
 */
public class CursorCollection<ItemType> {
    private final String m_left_token;
    private final Boolean m_left_is_terminal;
    private final String m_right_token;
    private final Boolean m_right_is_terminal;
    private final List<ItemType> m_results;

    private static <T> List<T> jsonArrayToList(Gson gson, JsonArray things, Class<T> clazz) {
        if (gson == null) throw new NullPointerException("gson == null");
        if (things == null) throw new NullPointerException("things == null");
        if (clazz == null) throw new NullPointerException("clazz == null");

        List<T> results = new ArrayList<T>(things.size());
        for (JsonElement je : things) {
            results.add(gson.fromJson(je, clazz));
        }
        return results;
    }

    public CursorCollection(JsonObject obj, Class<ItemType> clazz) {
        if (clazz == null) throw new NullPointerException("clazz == null");

        m_left_token = obj.get("left_cursor").getAsString();
        m_left_is_terminal = obj.get("left_cursor").getAsBoolean();
        m_right_token = obj.get("right_cursor").getAsString();
        m_right_is_terminal = obj.get("right_is_terminal").getAsBoolean();
        m_results = jsonArrayToList(GsonManager.getInstance().getGson(),
                obj.get("results").getAsJsonArray(), clazz);
    }

    public String getToken(int direction) {
        switch (direction) {
            case (CursorState.LEFT):
                return m_left_token;
            case (CursorState.RIGHT):
                return m_right_token;
            default:
                throw new RuntimeException("invalid direction");
        }
    }

    public boolean isTerminal(int direction) {
        switch (direction) {
            case (CursorState.LEFT):
                return m_left_is_terminal;
            case (CursorState.RIGHT):
                return m_right_is_terminal;
            default:
                throw new RuntimeException("invalid direction");
        }
    }

    public List<ItemType> getResults() {
        return m_results;
    }
}
