
package com.ponysdk.addon.spinner.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.NoExport;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.TextBox;

//public class PTSpinner {
//
//    public PTSpinner(final String objectID, final Element uiObject) {
//        final Label l1 = new Label("Spinner");
//        final Label l2 = new Label("ObjectID: " + objectID);
//
//        uiObject.appendChild(l1.getElement());
//        uiObject.appendChild(l2.getElement());
//    }
//
//}

@Export
public class PTSpinner implements Exportable, MouseDownHandler, MouseUpHandler, KeyDownHandler, KeyUpHandler, com.google.gwt.event.logical.shared.ValueChangeHandler<String>, MouseOutHandler, ClickHandler {

    // public native void exportUpdateFunction() /*-{
    // var that = this;
    // that.update = function(data) {
    // $entry(that.@com.ponysdk.addon.spinner.client.PTSpinner::update(Lcom/google/gwt/core/client/JavaScriptObject;)(data));
    // }
    // }-*/;

    private static final String ZEROS = "0000000000000000";

    private Element wrapper;
    private final InlineHTML up = new InlineHTML("<span class=\"u\"></span>");
    private final InlineHTML down = new InlineHTML("<span class=\"d\"></span>");
    private final TextBox textBox = new TextBox();

    private boolean timerScheduled = false;

    private boolean paged = false;
    private boolean increment = true;
    private boolean enabled = true;

    private BigDecimal lastSendValue = null;
    private BigDecimal value = null;

    private boolean hasMin = false;
    private boolean hasMax = false;

    private final int initialDelay = 200;
    private int currentDelay = initialDelay;

    private int page = 10;
    private int decimal = 0;
    private BigDecimal min = new BigDecimal(0);
    private BigDecimal max = new BigDecimal(0);
    private BigDecimal step = new BigDecimal(1);
    private BigDecimal pagedStep = new BigDecimal(10);

    private final List<ValueChangeHandler> handlers = new ArrayList<ValueChangeHandler>();

    private RefreshCommand refreshCommand;

    private String objectID;

    private class RefreshCommand implements RepeatingCommand {

        protected boolean cancelled = false;

        @Override
        public boolean execute() {

            if (cancelled) return false;

            if (increment) {
                increase();
            } else {
                decrease();
            }

            if (!timerScheduled) {
                // Timer cancelled, fire change
                paged = false;
                currentDelay = initialDelay;
                fire();
            } else {
                // Re-schedule
                currentDelay -= 10;
                if (currentDelay < 10) currentDelay = 10;
                scheduleRefresh(currentDelay);
            }

            return false;
        }

        public void cancel() {
            cancelled = true;
        }

    }

    public PTSpinner() {}

    public void setObjectID(final String objectID) {
        this.objectID = objectID;
    }

    public void build(final Element uiObject) {
        wrapper = uiObject;

        wrapper.appendChild(textBox.getElement());
        wrapper.appendChild(up.getElement());
        wrapper.appendChild(down.getElement());
        wrapper.setClassName("spinner");

        up.setStyleName("up");
        down.setStyleName("down");
        textBox.addStyleName("in");
        up.addStyleName("arrow");
        down.addStyleName("arrow");

        up.addMouseDownHandler(this);
        up.addMouseUpHandler(this);
        up.addMouseOutHandler(this);
        up.addClickHandler(this);
        down.addClickHandler(this);
        down.addMouseDownHandler(this);
        down.addMouseUpHandler(this);
        down.addMouseOutHandler(this);
        textBox.addKeyDownHandler(this);
        textBox.addKeyUpHandler(this);
        textBox.addValueChangeHandler(this);
    }

    private void enabled(final boolean enabled) {
        this.enabled = enabled;
        textBox.setEnabled(enabled);
        if (enabled) {
            wrapper.addClassName("enabled");
            wrapper.removeClassName("disabled");
        } else {
            wrapper.removeClassName("enabled");
            wrapper.addClassName("disabled");
        }
    }

    private void addValueChangeHandler(final ValueChangeHandler h) {
        handlers.add(h);
    }

    private void decrease() {
        if (value == null) {
            value = min;
        } else {
            if (paged) {
                value = value.subtract(pagedStep);
            } else {
                value = value.subtract(step);
            }
        }

        checkMinMax();

        refreshTextBox();
    }

    private void checkMinMax() {
        if (hasMin) {
            if (value.compareTo(min) < 0) {
                value = min;
                timerScheduled = false;
            }
        }
        if (hasMax) {
            if (value.compareTo(max) > 0) {
                value = max;
                timerScheduled = false;
            }
        }
    }

    private void increase() {

        if (value == null) {
            value = min;
        } else {
            if (paged) {
                value = value.add(pagedStep);
            } else {
                value = value.add(step);
            }
        }

        checkMinMax();

        refreshTextBox();
    }

    public void update(final JavaScriptObject jso) {
        log("updating with parameters: " + jso.toString());
        applyOptions(new JSONObject(jso));
    }

    private void applyOptions(final JSONObject options) {

        if (options.containsKey(D.MIN)) {
            hasMin = true;
            min = new BigDecimal(options.get(D.MIN).isString().stringValue());
        }
        if (options.containsKey(D.MAX)) {
            hasMax = true;
            max = new BigDecimal(options.get(D.MAX).isString().stringValue());
        }
        if (options.containsKey(D.STEP)) {
            step = new BigDecimal(options.get(D.STEP).isString().stringValue());
            pagedStep = step.multiply(new BigDecimal(page));
        }
        if (options.containsKey(D.PAGE)) {
            page = (int) options.get(D.PAGE).isNumber().doubleValue();
            pagedStep = step.multiply(new BigDecimal(page));
        }
        if (options.containsKey(D.DECIMAL)) decimal = (int) options.get(D.DECIMAL).isNumber().doubleValue();

        if (options.containsKey(D.TEXT)) {
            final String text = options.get(D.TEXT).isString().stringValue();
            if (text == null || text.isEmpty()) {
                value = null;
                lastSendValue = null;
                textBox.setText(text);
            } else {
                try {
                    value = new BigDecimal(text);
                    lastSendValue = value;
                    textBox.setText(format());
                } catch (final NumberFormatException e) {}
            }
        } else if (options.containsKey(D.ENABLED)) {
            enabled(options.get(D.ENABLED).isBoolean().booleanValue());
        } else if (options.containsKey(D.TABINDEX)) {
            textBox.setTabIndex((int) options.get(D.TABINDEX).isNumber().doubleValue());
        } else if (options.containsKey(D.FOCUSED)) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    textBox.setFocus(options.get(D.FOCUSED).isBoolean().booleanValue());
                }
            });
        }

        log("min: " + min);
        log("max: " + max);
        log("step: " + step);
    }

    @Override
    @NoExport
    public void onValueChange(final ValueChangeEvent<String> event) {
        final String valueAsString = event.getValue();
        if (valueAsString != null && !valueAsString.isEmpty()) {
            try {
                final BigDecimal newvalue = new BigDecimal(textBox.getText());
                if (!newvalue.equals(value)) {
                    value = newvalue;
                    refreshTextBox();
                    fire();
                }
            } catch (final NumberFormatException e) {
                //
                refreshTextBox();
            }
        } else {
            if (value != null) {
                value = null;
                fire();
            }
        }
    }

    private void refreshTextBox() {
        final String format = format();
        textBox.setText(format);
    }

    private String format() {

        if (value == null) return "";

        String v = value.toString();
        final int actual = decimals(v);
        int diff = actual - decimal;
        if (decimal == 0 && diff == 1) diff++;

        if (diff == 0) return v;
        if (diff > 0) return v.substring(0, v.length() - diff);

        if (actual == 0) v = v.concat(".");
        return v.concat(ZEROS.substring(0, -diff));
    }

    private static int decimals(final String v) {
        if (v == null) return 0;

        final int dotIndex = v.indexOf(".");
        if (dotIndex < 0) return 0;

        return (v.length() - (dotIndex + 1));
    }

    @Override
    @NoExport
    public void onKeyDown(final KeyDownEvent event) {
        if (enabled && !timerScheduled) {

            boolean trigger = false;
            if (event.isDownArrow()) {
                trigger = true;
                paged = false;
                increment = false;
            } else if (event.isUpArrow()) {
                trigger = true;
                paged = false;
                increment = true;
            } else if (event.getNativeKeyCode() == KeyCodes.KEY_PAGEDOWN) {
                trigger = true;
                paged = true;
                increment = false;
            } else if (event.getNativeKeyCode() == KeyCodes.KEY_PAGEUP) {
                trigger = true;
                paged = true;
                increment = true;
            }

            if (trigger) {
                timerScheduled = true;
                scheduleRefresh(initialDelay);
            }
        }
    }

    private void scheduleRefresh(final int delayMs) {
        if (refreshCommand != null) refreshCommand.cancel();

        refreshCommand = new RefreshCommand();
        Scheduler.get().scheduleFixedDelay(refreshCommand, delayMs);
    }

    @Override
    @NoExport
    public void onKeyUp(final KeyUpEvent event) {
        timerScheduled = false;

        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            fire();
        }
    }

    @Override
    @NoExport
    public void onMouseDown(final MouseDownEvent event) {
        if (enabled) {
            final Object sender = event.getSource();
            if (sender == up) {
                increment = true;
            } else {
                increment = false;
            }
            timerScheduled = true;
            scheduleRefresh(initialDelay);
        }
    }

    @Override
    @NoExport
    public void onMouseUp(final MouseUpEvent event) {
        timerScheduled = false;
    }

    @Override
    @NoExport
    public void onClick(final ClickEvent event) {
        if (enabled) {

            if (refreshCommand != null) {
                refreshCommand.cancel();
                refreshCommand = null;
            }

            final Object sender = event.getSource();
            if (sender == up) {
                increase();
            } else {
                decrease();
            }

            paged = false;
            currentDelay = initialDelay;
            fire();
        }
    }

    @Override
    @NoExport
    public void onMouseOut(final MouseOutEvent event) {
        timerScheduled = false;
    }

    private void fireIfNotEqual(final String prev, final String text) {
        if (prev != text && (prev == null || !prev.equals(text))) {
            fire();
        }
    }

    private void fire() {
        final String text = textBox.getText();
        if (text != null && !text.isEmpty()) {
            try {
                value = new BigDecimal(text);
            } catch (final NumberFormatException e) {}
        } else {
            value = null;
        }

        if (!valueChanged()) return;
        lastSendValue = value;

        for (final ValueChangeHandler h : handlers) {
            h.onValueChange(text);
        }
    }

    private boolean valueChanged() {
        if (lastSendValue == null) {
            if (value != null) return true;
        } else if (!lastSendValue.equals(value)) return true;
        return false;
    }

    private static interface ValueChangeHandler {

        public void onValueChange(String v);
    }

    private native void log(String msg) /*-{
                                              console.log(msg);
                                              }-*/;
}
