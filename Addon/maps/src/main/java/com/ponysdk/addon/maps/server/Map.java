
package com.ponysdk.addon.maps.server;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ponysdk.core.tools.ListenerCollection;
import com.ponysdk.ui.server.basic.PHTML;
import com.ponysdk.ui.server.basic.event.PNativeEvent;
import com.ponysdk.ui.server.basic.event.PNativeHandler;

public class Map extends PHTML implements PNativeHandler {

    private static Logger log = LoggerFactory.getLogger(Map.class);

    public interface GeocoderResultHandler {

        public void onGeocoderResult(List<GeocoderResult> geocoderResults);
    }

    private final ListenerCollection<GeocoderResultHandler> geocoderHandlers = new ListenerCollection<Map.GeocoderResultHandler>();

    public Map() {
        bindTerminalFunction("bindMapAddOn");
        addNativeHandler(this);
    }

    public void find(final String address) {
        sendToClient("find", address);
    }

    public void center(final String lat, final String lng) {
        try {
            final JSONObject jso = new JSONObject();
            jso.put("lat", lat);
            jso.put("lng", lng);
            sendToClient("center", jso);
        } catch (final JSONException e) {
            log.error("Failed to update json object.", e);
        }
    }

    private void sendToClient(final String property, final Object value) {
        try {
            final JSONObject jso = new JSONObject();
            jso.put(property, value);
            sendToNative(jso);
        } catch (final JSONException e) {
            log.error("Failed to update json object. " + property + "/" + value, e);
        }
    }

    public void addGeocoderResultHandler(final GeocoderResultHandler h) {
        geocoderHandlers.add(h);
    }

    @Override
    public void onNativeEvent(final PNativeEvent event) {
        try {
            final JSONObject data = event.getJsonObject();
            if (data.has("geocoder")) {
                final List<GeocoderResult> geocoderResults = new ArrayList<GeocoderResult>();
                final JSONArray results = data.getJSONArray("geocoder");
                for (int i = 0; i < results.length(); i++) {
                    final JSONObject result = results.getJSONObject(i);
                    final GeocoderResult r = new GeocoderResult();
                    r.address = result.getString("address");
                    r.lat = result.getString("lat");
                    r.lng = result.getString("lng");
                    geocoderResults.add(r);
                }
                for (final GeocoderResultHandler handler : geocoderHandlers) {
                    handler.onGeocoderResult(geocoderResults);
                }
            }
        } catch (final JSONException e) {
            log.error("Failed to read json object.", e);
        }
    }
}
