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
import java.util.HashMap;

/**
 * Created by sell on 21/11/13.
 */
abstract public class InitialRequestProcessor<ItemType>
        extends Request<CursorCollection<ItemType>>
        implements CursorRequestProcessor<ItemType>
{
    public final CursorState<ItemType> m_state;
    public final Response.Listener<CursorCollection<ItemType>> m_callback;

    public InitialRequestProcessor(CursorState<ItemType> state, String url,
                                   Response.Listener<CursorCollection<ItemType>> callback,
                                   Response.ErrorListener errback
    ) {
        super(Method.GET, url, errback);
        m_callback = callback;
        m_state = state;
    }

    @Override
    protected Response<CursorCollection<ItemType>> parseNetworkResponse(NetworkResponse response) {
        try {
            JsonParser parser = new JsonParser();
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JsonObject result_json = (JsonObject) parser.parse(json);
            CursorCollection<ItemType> cc = new CursorCollection<ItemType>(result_json, getResponseType());
            m_state.mergeInitial(cc);
            return Response.success(cc, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return com.android.volley.Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return com.android.volley.Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(CursorCollection<ItemType> rr) {
        if (m_callback != null) m_callback.onResponse(rr);
    }

    abstract protected Class<ItemType> getResponseType();

    @Override
    public HashMap<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.putAll(m_state.getHeaders());
        return headers;
    }
}
