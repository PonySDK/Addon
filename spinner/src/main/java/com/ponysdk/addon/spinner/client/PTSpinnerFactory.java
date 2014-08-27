
package com.ponysdk.addon.spinner.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class PTSpinnerFactory implements EntryPoint {

    @Override
    public void onModuleLoad() {
        GWT.create(PTSpinner.class);
    }

}
