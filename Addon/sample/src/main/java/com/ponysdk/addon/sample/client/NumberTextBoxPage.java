
package com.ponysdk.addon.sample.client;

import com.ponysdk.addon.spinner.server.PSpinner;
import com.ponysdk.ui.server.basic.IsPWidget;
import com.ponysdk.ui.server.basic.PButton;
import com.ponysdk.ui.server.basic.PFlexTable;
import com.ponysdk.ui.server.basic.PFlowPanel;
import com.ponysdk.ui.server.basic.PLabel;
import com.ponysdk.ui.server.basic.PScrollPanel;
import com.ponysdk.ui.server.basic.PWidget;
import com.ponysdk.ui.server.basic.event.PClickEvent;
import com.ponysdk.ui.server.basic.event.PClickHandler;
import com.ponysdk.ui.server.basic.event.PValueChangeEvent;
import com.ponysdk.ui.server.basic.event.PValueChangeHandler;

public class NumberTextBoxPage implements IsPWidget, PValueChangeHandler<String> {

    private final PScrollPanel scroll = new PScrollPanel();
    private final PFlowPanel flow = new PFlowPanel();
    private final PFlexTable table = new PFlexTable();

    public NumberTextBoxPage() {

        final PButton add = new PButton("Add");
        add.addClickHandler(new PClickHandler() {

            @Override
            public void onClick(final PClickEvent event) {

                for (int i = 0; i < 20; i++) {
                    for (int j = 0; j < 8; j++) {
                        final PSpinner.Options options = new PSpinner.Options().withMin(1d).withMax(10000d).withPage(10).withStep(0.01d).withNumberFormat(2);
                        final PSpinner spinner = new PSpinner(options);
                        spinner.addValueChangeHandler(NumberTextBoxPage.this);
                        table.setWidget(i, j, spinner);

                        // final PNumberTextBox.Options op = new
                        // PNumberTextBox.Options().withMin(1d).withMax(10000d).withPage(10).withStep(0.01d).withNumberFormat(2);
                        // final PNumberTextBox pn = new PNumberTextBox(op);
                        // pn.addValueChangeHandler(NumberTextBoxPage.this);
                        // table.setWidget(i, j, pn);
                    }
                }
            }
        });

        flow.add(new PLabel("Number text box"));
        flow.add(add);
        flow.add(table);

        scroll.setWidget(flow);
    }

    @Override
    public PWidget asWidget() {
        return scroll;
    }

    @Override
    public void onValueChange(final PValueChangeEvent<String> event) {
        System.out.println("Spinner value: " + event.getValue());
    }

}
