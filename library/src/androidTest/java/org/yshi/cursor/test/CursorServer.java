package org.yshi.cursor.test;

import com.google.gson.JsonObject;

/**
 * Created by sell on 05/04/14.
 */
public interface CursorServer {
    public enum Direction {
        LEFT,
        RIGHT
    }

    public int getItemCount();

    public JsonObject getInitial(Direction direction, int limit);

    public JsonObject get(String cursor, int limit);
}
