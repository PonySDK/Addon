
package com.ponysdk.addon.sample.client;

import com.ponysdk.addon.spinner.server.PSpinner;
import com.ponysdk.ui.server.basic.IsPWidget;
import com.ponysdk.ui.server.basic.PButton;
import com.ponysdk.ui.server.basic.PFlowPanel;
import com.ponysdk.ui.server.basic.PLabel;
import com.ponysdk.ui.server.basic.PWidget;
import com.ponysdk.ui.server.basic.event.PClickEvent;
import com.ponysdk.ui.server.basic.event.PClickHandler;

public class NumberTextBoxPage implements IsPWidget {

    private final PFlowPanel flow = new PFlowPanel();

    public NumberTextBoxPage() {

        final PButton add = new PButton("Add");
        add.addClickHandler(new PClickHandler() {

            @Override
            public void onClick(final PClickEvent event) {
                flow.add(new PSpinner(new PSpinner.Options().withMin(1d).withMax(10000d).withPage(10).withStep(10d).withNumberFormat(2)));
            }
        });

        flow.add(new PLabel("Number text box"));
        flow.add(add);
    }

    @Override
    public PWidget asWidget() {
        return flow;
    }

}
