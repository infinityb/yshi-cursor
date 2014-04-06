package org.yshi.cursor.test.interfaces;

import com.google.gson.JsonObject;

/**
 * Created by sell on 05/04/14.
 */
public interface CursorServer {
    public enum Direction {
        LEFT, RIGHT;

        public static Direction fromChar(Character chr) {
            switch (chr) {
                case 'a':
                    return Direction.LEFT;
                case 'b':
                    return Direction.RIGHT;
                default:
                    throw new RuntimeException(String.format("Invalid direction character %s", chr));
            }
        }
    }

    public int getItemCount();

    public JsonObject getInitial(Direction direction, int limit);

    public JsonObject get(String cursor, int limit);
}
