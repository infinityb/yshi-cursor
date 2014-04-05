package org.yshi.cursor;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by sell on 20/11/13.
 */
abstract public class CursorState<ItemType> {
    private static final String TAG = "CursorState";

    // public static final int INITIAL = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    private boolean m_initial_is_requesting = false;

    private boolean m_left_is_requesting = false;
    private CursorCollection<ItemType> m_left_prev_coll = null;
    private boolean m_right_is_requesting = false;
    private CursorCollection<ItemType> m_right_prev_coll = null;
    private BackedCollection<ItemType> m_backed_collection = null;

    public CursorState(
            BackedCollection<ItemType> backed_collection
    ) {
        m_initial_is_requesting = true;
        m_backed_collection = backed_collection;
    }

    public BackedCollection<ItemType> getBackedCollection() {
        return m_backed_collection;
    }

    private CursorCollection<ItemType> prevCollFromDirection(int direction) {
        switch (direction) {
            case (LEFT):
                return m_left_prev_coll;
            case (RIGHT):
                return m_right_prev_coll;
            default:
                throw new RuntimeException("invalid direction");
        }
    }

    abstract protected String getNextUrl(int direction, String cursorToken);

    abstract public Request<CursorCollection<ItemType>> getInitialRequest(
            Response.Listener<CursorCollection<ItemType>> callback,
            Response.ErrorListener errback);

    public synchronized void mergeInitial(CursorCollection<ItemType> coll) {
        Log.e(TAG, String.format("%s.mergeInitial(%s)", this, coll));
        if (coll == null) {
            throw new RuntimeException("coll must not be null");
        }
        m_backed_collection.mergeInitial(coll);
        m_initial_is_requesting = false;
        m_left_prev_coll = coll;
        m_right_prev_coll = coll;
    }

    public synchronized void merge(int direction, CursorCollection<ItemType> coll) {
        if (coll == null) {
            throw new RuntimeException("coll must not be null");
        }
        m_backed_collection.merge(direction, coll);
        switch (direction) {
            case (LEFT):
                m_left_prev_coll = coll;
                // m_backed_collection.merge(direction, coll);
                m_left_is_requesting = false;
                break;
            case (RIGHT):
                m_right_prev_coll = coll;
                // m_backed_collection.merge(direction, coll);
                m_right_is_requesting = false;
                break;
            default:
                throw new RuntimeException("invalid direction");
        }
    }

    protected void mergeError(int direction) {
        switch (direction) {
            case (LEFT):
                m_left_is_requesting = false;
                break;
            case (RIGHT):
                m_right_is_requesting = false;
                break;
            default:
                throw new RuntimeException("invalid direction");
        }
    }

    protected String getNextUrl(int direction) {
        return getNextUrl(direction, prevCollFromDirection(direction).getToken(direction));
    }

    private Response.Listener<CursorCollection<ItemType>> getCallback(
            final int direction,
            final Response.Listener<CursorCollection<ItemType>> callback
    ) {
        return new Response.Listener<CursorCollection<ItemType>>() {
            @Override
            public void onResponse(CursorCollection<ItemType> coll) {
                // m_backed_collection.merge(direction, coll);
                callback.onResponse(coll);
            }
        };
    }

    private Response.ErrorListener getErrback(
            final int direction,
            final Response.ErrorListener errback
    ) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mergeError(direction);
                errback.onErrorResponse(volleyError);
            }
        };
    }

    public synchronized boolean firstRequestFinished() {
        return !m_initial_is_requesting;
    }

    /**
     * @param direction
     * @param callback
     * @param errback
     * @return
     * @throws AlreadyRequesting
     */
    public synchronized Request<CursorCollection<ItemType>> nextRequest(
            int direction,
            Response.Listener<CursorCollection<ItemType>> callback,
            Response.ErrorListener errback
    ) throws AlreadyRequesting, TerminalCollection {

        switch (direction) {
            case (LEFT):
                if (m_left_is_requesting)
                    throw new AlreadyRequesting();
                break;
            case (RIGHT):
                if (m_right_is_requesting)
                    throw new AlreadyRequesting();
                break;
            default:
                throw new RuntimeException("invalid direction");
        }

        CursorCollection<ItemType> target = prevCollFromDirection(direction);
        if (target == null) {
            return getInitialRequest(callback, errback);
        }
//        if (target.isTerminal(direction)) {
//            throw new TerminalCollection();
//        }
        return new ContinuedCursorRequestProcessor<ItemType>(
                this, direction, m_backed_collection.getItemType(),
                getCallback(direction, callback),
                getErrback(direction, errback));
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        return new TreeMap<String, String>();
    }
}
