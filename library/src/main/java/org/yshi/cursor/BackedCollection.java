package org.yshi.cursor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by sell on 21/11/13.
 */
public interface BackedCollection<ItemType> extends Iterable<ItemType> {
    public void mergeInitial(CursorCollection<? extends ItemType> coll);
    public void merge(int direction, CursorCollection<? extends ItemType> coll);
    public int writeArray(ItemType[] itemArray, int startIdx);
    public int size();
    public Class<ItemType> getItemType();
    public ItemType loadJson(JsonElement json_element);
}
