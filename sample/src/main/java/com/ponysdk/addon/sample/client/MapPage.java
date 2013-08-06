
package com.ponysdk.addon.sample.client;

import java.util.List;

import com.ponysdk.addon.maps.server.GeocoderResult;
import com.ponysdk.addon.maps.server.Map;
import com.ponysdk.addon.maps.server.Map.GeocoderResultHandler;
import com.ponysdk.ui.server.basic.IsPWidget;
import com.ponysdk.ui.server.basic.PButton;
import com.ponysdk.ui.server.basic.PFlowPanel;
import com.ponysdk.ui.server.basic.PHTML;
import com.ponysdk.ui.server.basic.PSimpleLayoutPanel;
import com.ponysdk.ui.server.basic.PSplitLayoutPanel;
import com.ponysdk.ui.server.basic.PWidget;
import com.ponysdk.ui.server.basic.event.PClickEvent;
import com.ponysdk.ui.server.basic.event.PClickHandler;
import com.ponysdk.ui.server.form2.formfield.StringTextBoxFormField;
import com.ponysdk.ui.terminal.PUnit;

public class MapPage implements IsPWidget, GeocoderResultHandler {

    private final PSplitLayoutPanel split = new PSplitLayoutPanel(PUnit.PX);
    private final PFlowPanel leftPane = new PFlowPanel();
    private final PSimpleLayoutPanel rightPane = new PSimpleLayoutPanel();
    private final PFlowPanel geocoderResults = new PFlowPanel();

    private final Map map = new Map();
    private final StringTextBoxFormField address;
    private final PButton find;

    public MapPage() {

        split.addStyleName("split");
        leftPane.addStyleName("left");
        rightPane.addStyleName("right");
        geocoderResults.addStyleName("geocoderResults");

        split.addWest(leftPane, 250);
        split.add(rightPane);

        rightPane.setWidget(map);

        address = new StringTextBoxFormField();
        find = new PButton("find");
        find.addClickHandler(new PClickHandler() {

            @Override
            public void onClick(final PClickEvent event) {
                map.find(address.getValue());
            }
        });

        map.addGeocoderResultHandler(this);

        leftPane.add(address);
        leftPane.add(find);
        leftPane.add(geocoderResults);
    }

    @Override
    public PWidget asWidget() {
        return split;
    }

    @Override
    public void onGeocoderResult(final List<GeocoderResult> results) {
        geocoderResults.clear();

        for (final GeocoderResult result : results) {
            final PHTML link = new PHTML(result.address);
            link.addClickHandler(new PClickHandler() {

                @Override
                public void onClick(final PClickEvent event) {
                    map.center(result.lat, result.lng);
                }
            });
            geocoderResults.add(link);
        }
    }

}
