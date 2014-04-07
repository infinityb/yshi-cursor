package org.yshi.cursor;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

/**
 * Created by sell on 21/11/13.
 */
abstract public class InitialRequestProcessor<ItemType>
        extends AbstractRequestProcessor<ItemType> {

    public InitialRequestProcessor(
            CursorState<ItemType> state, String url,
            Response.Listener<CursorCollection<ItemType>> callback,
            Response.ErrorListener errback
    ) {
        super(state, url, callback, errback);
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
}
