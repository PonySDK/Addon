
package com.ponysdk.addon.sample.client;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ponysdk.core.UIContext;
import com.ponysdk.core.main.EntryPoint;
import com.ponysdk.ui.server.basic.IsPWidget;
import com.ponysdk.ui.server.basic.PDockLayoutPanel;
import com.ponysdk.ui.server.basic.PFlowPanel;
import com.ponysdk.ui.server.basic.PHTML;
import com.ponysdk.ui.server.basic.PLabel;
import com.ponysdk.ui.server.basic.PRootLayoutPanel;
import com.ponysdk.ui.server.basic.PSimpleLayoutPanel;
import com.ponysdk.ui.server.basic.event.PClickEvent;
import com.ponysdk.ui.server.basic.event.PClickHandler;
import com.ponysdk.ui.terminal.PUnit;

public class SampleEntryPoint implements EntryPoint {

    private static Logger log = LoggerFactory.getLogger(SampleEntryPoint.class);

    private PSimpleLayoutPanel content;
    private final Map<String, IsPWidget> widgetByName = new HashMap<String, IsPWidget>();

    private PFlowPanel headerContainer;
    private PFlowPanel pageContainer;

    @Override
    public void start(final UIContext uiContext) {

        final PLabel title = new PLabel("Add-on sample");
        title.addStyleName("title");

        content = new PSimpleLayoutPanel();
        content.addStyleName("content");

        headerContainer = new PFlowPanel();
        pageContainer = new PFlowPanel();

        headerContainer.addStyleName("headerContainer");
        pageContainer.addStyleName("pageContainer");

        headerContainer.add(title);
        headerContainer.add(pageContainer);

        final PDockLayoutPanel layout = new PDockLayoutPanel(PUnit.PX);
        layout.addNorth(headerContainer, 100);
        layout.add(content);

        PRootLayoutPanel.get().add(layout);

        addPage("Spinner", SpinnerPage.class);
        addPage("Map", MapPage.class);

        show(SpinnerPage.class);
    }

    private <T extends IsPWidget> void addPage(final String pageName, final Class<T> widgetClass) {
        final PHTML pageTitle = new PHTML(pageName);
        pageTitle.addStyleName("pageName");
        pageTitle.addClickHandler(new PClickHandler() {

            @Override
            public void onClick(final PClickEvent event) {
                show(widgetClass);
            }
        });
        pageContainer.add(pageTitle);
    }

    private <T extends IsPWidget> void show(final Class<T> widgetClass) {
        try {
            IsPWidget w = widgetByName.get(widgetClass.getName());
            if (w == null) {
                w = widgetClass.newInstance();
                widgetByName.put(widgetClass.getName(), w);
            }
            content.setWidget(w);
        } catch (final Throwable e) {
            log.error("", e);
        }
    }

    @Override
    public void restart(final UIContext uiContext) {
        start(uiContext);
    }

}
