
package com.ponysdk.addon.spinner.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class PTSpinnerFactory implements EntryPoint {

    @Override
    public void onModuleLoad() {
        GWT.create(PTSpinner.class);
        exportFactoryFunction();
    }

    public native void exportFactoryFunction() /*-{
                                               var that = this;
                                               $wnd.spinner = function(id, uiobject) {
                                                   var o = new $wnd.com.ponysdk.addon.spinner.PTSpinner();
                                                   o.setObjectID(id);
                                                   o.build(uiobject);
                                                   return o;
                                               }
                                               }-*/;

}
