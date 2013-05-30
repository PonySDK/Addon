
package com.ponysdk.addon.spinner.server;

import org.json.JSONException;
import org.json.JSONObject;

import com.ponysdk.addon.spinner.client.D;
import com.ponysdk.ui.server.basic.PElement;

public class PSpinner extends PElement {

    private double min;
    private double max;
    private double step;
    private int page;
    private int decimals;

    public PSpinner(final Options options) {
        super("span");
        bindTerminalFunction("spinner");
        setOptions(options);
    }

    public void setOptions(final Options options) {

        final JSONObject jso = new JSONObject();

        if (options.min != null) {
            min = options.min;
            update(jso, D.MIN, String.valueOf(options.min));
        }

        if (options.max != null) {
            max = options.max;
            update(jso, D.MAX, String.valueOf(options.max));
        }

        if (options.step != null) {
            step = options.step;
            update(jso, D.STEP, String.valueOf(options.step));
        }

        if (options.page != null) {
            page = options.page;
            update(jso, D.PAGE, options.page);
        }

        if (options.decimals != null) {
            decimals = options.decimals;
            update(jso, D.DECIMAL, options.decimals);
        }

        sendToNative(jso);
    }

    public void setMin(final double min) {
        if (this.min == min) return;

        this.min = min;
        updateAndSend(new JSONObject(), D.MIN, min);
    }

    public void setMax(final double max) {
        if (this.max == max) return;

        this.max = max;
        updateAndSend(new JSONObject(), D.MAX, max);
    }

    public void setStep(final double step) {
        if (this.step == step) return;

        this.step = step;
        updateAndSend(new JSONObject(), D.STEP, step);
    }

    public void setPage(final int page) {
        if (this.page == page) return;

        this.page = page;
        updateAndSend(new JSONObject(), D.PAGE, page);
    }

    public void setDecimals(final int decimals) {
        if (this.decimals == decimals) return;

        this.decimals = decimals;
        updateAndSend(new JSONObject(), D.DECIMAL, decimals);
    }

    private void update(final JSONObject jso, final String property, final Object v) {
        try {
            jso.put(property, v);
        } catch (final JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void updateAndSend(final JSONObject jso, final String property, final Object v) {
        update(jso, property, v);
        sendToNative(jso);
    }

    public static class Options {

        protected Double min;
        protected Double max;
        protected Double step;
        protected Integer page;
        protected Integer decimals;

        public Options() {}

        public Options withMin(final Double m) {
            this.min = m;
            return this;
        }

        public Options withMax(final Double m) {
            this.max = m;
            return this;
        }

        public Options withPage(final Integer p) {
            this.page = p;
            return this;
        }

        public Options withStep(final Double s) {
            this.step = s;
            return this;
        }

        public Options withNumberFormat(final Integer nf) {
            this.decimals = nf;
            return this;
        }
    }
}
