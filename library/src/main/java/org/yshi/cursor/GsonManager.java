package org.yshi.cursor;

import com.google.gson.Gson;

/**
 * Created by sell on 20/11/13.
 */
public class GsonManager {
    private static GsonManager M_GSON_MANAGER;
    private Gson m_gson;

    private GsonManager() {
        m_gson = new Gson();
    }

    public static GsonManager getInstance() {
        if (M_GSON_MANAGER == null) {
            M_GSON_MANAGER = new GsonManager();
        }
        return M_GSON_MANAGER;
    }

    public Gson getGson() {
        return m_gson;
    }

    public void setGson(Gson gson) {
        m_gson = gson;
    }
}
