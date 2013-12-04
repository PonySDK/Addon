package com.ponysdk.addon.spinner.server;

import java.math.BigDecimal;
import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ponysdk.addon.spinner.client.D;
import com.ponysdk.core.tools.ListenerCollection;
import com.ponysdk.core.tools.Objects;
import com.ponysdk.ui.server.basic.HasPValue;
import com.ponysdk.ui.server.basic.PElement;
import com.ponysdk.ui.server.basic.event.PHasText;
import com.ponysdk.ui.server.basic.event.PNativeEvent;
import com.ponysdk.ui.server.basic.event.PNativeHandler;
import com.ponysdk.ui.server.basic.event.PValueChangeEvent;
import com.ponysdk.ui.server.basic.event.PValueChangeHandler;

public class PSpinner extends PElement implements PHasText, HasPValue<String>,
		PValueChangeHandler<String>, PNativeHandler {

	private static Logger log = LoggerFactory.getLogger(PSpinner.class);

	private final ListenerCollection<PValueChangeHandler<String>> handlers = new ListenerCollection<PValueChangeHandler<String>>();

	private double min;
	private double max;
	private double step;
	private int page;
	private int decimals;

	private String value;

	private boolean enabled = true;
	private int tabindex;

	private boolean focused;

	public PSpinner(final Options options) {
		super("span");
		bindTerminalFunction("spinner");
		setOptions(options);
		addNativeHandler(this);
	}

	public void setOptions(final Options options) {

		final JSONObject jso = new JSONObject();

		if (options.min != null) {
			min = options.min;
			update(jso, D.MIN, doubleToString(options.min));
		}

		if (options.max != null) {
			max = options.max;
			update(jso, D.MAX, doubleToString(max));
		}

		if (options.step != null) {
			step = options.step;
			update(jso, D.STEP, doubleToString(options.step));
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

	protected String doubleToString(final double d) {
		return new BigDecimal(Double.toString(d)).toPlainString();
	}

	public void setMin(final double min) {
		if (this.min == min)
			return;

		this.min = min;
		updateAndSend(new JSONObject(), D.MIN, doubleToString(min));
	}

	public void setMax(final double max) {
		if (this.max == max)
			return;

		this.max = max;
		updateAndSend(new JSONObject(), D.MAX, doubleToString(max));
	}

	public void setStep(final double step) {
		if (this.step == step)
			return;

		this.step = step;
		updateAndSend(new JSONObject(), D.STEP, doubleToString(step));
	}

	public void setPage(final int page) {
		if (this.page == page)
			return;

		this.page = page;
		updateAndSend(new JSONObject(), D.PAGE, page);
	}

	public void setDecimals(final int decimals) {
		if (this.decimals == decimals)
			return;

		this.decimals = decimals;
		updateAndSend(new JSONObject(), D.DECIMAL, decimals);
	}

	@Override
	public void setValue(final String value) {
		if (Objects.quals(this.value, value))
			return;

		this.value = value;
		updateAndSend(new JSONObject(), D.TEXT, value);
	}

	private void update(final JSONObject jso, final String property,
			final Object v) {
		try {
			jso.put(property, v);
		} catch (final JSONException e) {
			log.error("Failed to update json object. " + property + "/" + v, e);
		}
	}

	private void updateAndSend(final JSONObject jso, final String property,
			final Object v) {
		update(jso, property, v);
		sendToNative(jso);
	}

	public static class Options {

		protected Double min;
		protected Double max;
		protected Double step;
		protected Integer page;
		protected Integer decimals;

		public Options() {
		}

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

		public Double getMin() {
			return min;
		}

		public Double getMax() {
			return max;
		}

		public Double getStep() {
			return step;
		}

		public Integer getPage() {
			return page;
		}

		public Integer getDecimals() {
			return decimals;
		}

	}

	@Override
	public void addValueChangeHandler(final PValueChangeHandler<String> handler) {
		handlers.add(handler);
	}

	@Override
	public void removeValueChangeHandler(
			final PValueChangeHandler<String> handler) {
		handlers.remove(handler);
	}

	@Override
	public Collection<PValueChangeHandler<String>> getValueChangeHandlers() {
		return handlers;
	}

	@Override
	public void onValueChange(final PValueChangeEvent<String> event) {
		value = event.getValue();
		for (final PValueChangeHandler<String> handler : handlers) {
			handler.onValueChange(event);
		}
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getText() {
		return getValue();
	}

	@Override
	public void setText(final String text) {
		setValue(text);
	}

	public void setEnabled(final boolean enabled) {
		if (Objects.equals(this.enabled, enabled))
			return;
		this.enabled = enabled;

		updateAndSend(new JSONObject(), D.ENABLED, enabled);
	}

	public void setTabindex(final int tabindex) {
		if (this.tabindex == tabindex)
			return;
		this.tabindex = tabindex;

		updateAndSend(new JSONObject(), D.TABINDEX, tabindex);
	}

	public void setFocus(final boolean focused) {
		if (Objects.equals(this.focused, focused))
			return;
		this.focused = focused;

		updateAndSend(new JSONObject(), D.FOCUSED, focused);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getTabindex() {
		return tabindex;
	}

	public boolean isFocused() {
		return focused;
	}

	@Override
	public void onNativeEvent(final PNativeEvent event) {
		try {
			final JSONObject data = event.getJsonObject();
			final String value = data.getString("value");
			final PValueChangeEvent<String> e = new PValueChangeEvent<String>(
					this, value);
			onValueChange(e);
		} catch (final JSONException e) {
			log.error("Failed to read json object.", e);
		}
	}
}
