
package com.ponysdk.addon.sample.client;

import com.ponysdk.addon.spinner.server.PSpinner;
import com.ponysdk.addon.spinner.server.PSpinner.Options;
import com.ponysdk.ui.server.basic.IsPWidget;
import com.ponysdk.ui.server.basic.PButton;
import com.ponysdk.ui.server.basic.PFlexTable;
import com.ponysdk.ui.server.basic.PFlowPanel;
import com.ponysdk.ui.server.basic.PHTML;
import com.ponysdk.ui.server.basic.PLabel;
import com.ponysdk.ui.server.basic.PScrollPanel;
import com.ponysdk.ui.server.basic.PWidget;
import com.ponysdk.ui.server.basic.event.PClickEvent;
import com.ponysdk.ui.server.basic.event.PClickHandler;
import com.ponysdk.ui.server.basic.event.PValueChangeEvent;
import com.ponysdk.ui.server.basic.event.PValueChangeHandler;
import com.ponysdk.ui.server.form2.formfield.DoubleTextBoxFormField;
import com.ponysdk.ui.server.form2.formfield.FormField;
import com.ponysdk.ui.server.form2.formfield.IntegerTextBoxFormField;
import com.ponysdk.ui.server.form2.formfield.StringTextBoxFormField;

public class SpinnerPage implements IsPWidget, PValueChangeHandler<String> {

    private int row = 0;

    private final PScrollPanel scroll = new PScrollPanel();
    private final PFlowPanel flow = new PFlowPanel();
    private final PFlexTable table = new PFlexTable();
    private final StringTextBoxFormField valueFormField;
    private final DoubleTextBoxFormField minFormField;
    private final DoubleTextBoxFormField maxFormField;
    private final DoubleTextBoxFormField stepFormField;
    private final IntegerTextBoxFormField pageFormField;
    private final IntegerTextBoxFormField formatFormField;
    private final PSpinner spinner;

    public SpinnerPage() {

        final PSpinner.Options options = new PSpinner.Options();
        options.withMin(0d).withMax(100d).withStep(0.01d).withPage(10).withNumberFormat(2);

        valueFormField = new StringTextBoxFormField();
        minFormField = new DoubleTextBoxFormField();
        maxFormField = new DoubleTextBoxFormField();
        stepFormField = new DoubleTextBoxFormField();
        pageFormField = new IntegerTextBoxFormField();
        formatFormField = new IntegerTextBoxFormField();

        spinner = new PSpinner(options);
        spinner.addValueChangeHandler(this);

        valueFormField.setValue(options.getValue());
        minFormField.setValue(options.getMin());
        maxFormField.setValue(options.getMax());
        stepFormField.setValue(options.getStep());
        pageFormField.setValue(options.getPage());
        formatFormField.setValue(options.getDecimals());

        addField("Value", valueFormField);
        addField("Min", minFormField);
        addField("Max", maxFormField);
        addField("Step", stepFormField);
        addField("Page", pageFormField);
        addField("Decimals", formatFormField);

        table.setWidget(row++, 0, new PHTML("&nbsp;"));
        table.setWidget(row++, 0, spinner);
        table.getFlexCellFormatter().setColSpan(row - 1, 0, 3);

        flow.add(table);

        scroll.setWidget(flow);
    }

    private void addField(final String label, final FormField<?> formField) {
        final PButton update = new PButton("Update");
        update.addClickHandler(new PClickHandler() {

            @Override
            public void onClick(final PClickEvent event) {
                final PSpinner.Options options = buidOptions();
                spinner.setOptions(options);
            }
        });
        table.setWidget(row, 0, new PLabel(label));
        table.setWidget(row, 1, formField.asWidget());
        table.setWidget(row, 2, update);
        row++;
    }

    protected Options buidOptions() {
        final PSpinner.Options options = new PSpinner.Options();
        options.withMin(minFormField.getValue());
        options.withMax(maxFormField.getValue());
        options.withStep(stepFormField.getValue());
        options.withPage(pageFormField.getValue());
        options.withNumberFormat(formatFormField.getValue());
        options.withValue(valueFormField.getValue());
        return options;
    }

    @Override
    public PWidget asWidget() {
        return scroll;
    }

    @Override
    public void onValueChange(final PValueChangeEvent<String> event) {
        valueFormField.setValue(event.getValue());
    }

}
