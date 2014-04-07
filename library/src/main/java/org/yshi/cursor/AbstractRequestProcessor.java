package org.yshi.cursor;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by sell on 06/04/14.
 */
abstract public class AbstractRequestProcessor<ItemType> extends Request<CursorCollection<ItemType>> {

    public final CursorState<ItemType> m_state;
    public final Response.Listener<CursorCollection<ItemType>> m_callback;

    public AbstractRequestProcessor(CursorState<ItemType> state, String url,
                                   Response.Listener<CursorCollection<ItemType>> callback,
                                   Response.ErrorListener errback
    ) {
        super(Method.GET, url, errback);
        m_callback = callback;
        m_state = state;
    }

    protected CursorState<ItemType> getState() {
        return m_state;
    }

    @Override
    protected void deliverResponse(CursorCollection<ItemType> rr) {
        if (m_callback != null) m_callback.onResponse(rr);
    }

    abstract protected Class<ItemType> getResponseType();

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return new TreeMap<String, String>(m_state.getHeaders());
    }
}
