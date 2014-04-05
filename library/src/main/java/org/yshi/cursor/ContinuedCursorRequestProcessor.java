package org.yshi.cursor;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by sell on 20/11/13.
 */
public class ContinuedCursorRequestProcessor<ItemType>
        extends Request<CursorCollection<ItemType>>
        implements CursorRequestProcessor<ItemType>
{

    public final int m_direction;
    public final CursorState<ItemType> m_state;
    public final Response.Listener<CursorCollection<ItemType>> m_callback;
    public final Class<ItemType> m_clazz;

    public static String getUrl(CursorState<?> state, int direction) {
        return state.getNextUrl(direction);
    }

    public ContinuedCursorRequestProcessor(CursorState<ItemType> state, int direction, Class<ItemType> clazz,
                                           Response.Listener<CursorCollection<ItemType>> callback,
                                           Response.ErrorListener errback
    ) {
        super(Method.GET, getUrl(state, direction), errback);
        m_direction = direction;
        m_state = state;
        m_callback = callback;
        m_clazz = clazz;
    }

    @Override
    protected Response<CursorCollection<ItemType>> parseNetworkResponse(NetworkResponse response) {
        try {
            JsonParser parser = new JsonParser();
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JsonObject result_json = (JsonObject) parser.parse(json);
            CursorCollection<ItemType> rr = new CursorCollection<ItemType>(result_json, getResponseType());
            m_state.merge(m_direction, rr);
            return Response.success(rr, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return com.android.volley.Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return com.android.volley.Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(CursorCollection<ItemType> rr) {
        m_callback.onResponse(rr);
    }

    protected Class<ItemType> getResponseType() {
        return m_clazz;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.putAll(m_state.getHeaders());
        return headers;
    }
}
