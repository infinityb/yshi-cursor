package org.yshi.cursor;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by sell on 20/11/13.
 */
// Request<CursorCollection<ItemType>>
public class ContinuedCursorRequestProcessor<ItemType> extends AbstractRequestProcessor<ItemType> {

    public final int m_direction;
    public final Class<ItemType> m_clazz;

    public static String getUrl(CursorState<?> state, int direction) {
        return state.getNextUrl(direction);
    }

    public ContinuedCursorRequestProcessor(
            CursorState<ItemType> state, int direction, Class<ItemType> clazz,
            Response.Listener<CursorCollection<ItemType>> callback,
            Response.ErrorListener errback
    ) {
        super(state, getUrl(state, direction), callback, errback);
        m_direction = direction;
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

    protected Class<ItemType> getResponseType() {
        return m_clazz;
    }
}
