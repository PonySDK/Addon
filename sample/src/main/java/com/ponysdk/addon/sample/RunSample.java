
package com.ponysdk.addon.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ponysdk.core.main.Main;
import com.ponysdk.core.servlet.ApplicationLoader;
import com.ponysdk.core.servlet.HttpServlet;

public class RunSample {

    private static Logger log = LoggerFactory.getLogger(RunSample.class);

    public static void main(final String[] args) {
        try {
            final ApplicationLoader applicationLoader = new ApplicationLoader();

            final HttpServlet httpServlet = new HttpServlet();
            httpServlet.setEntryPointClassName("com.ponysdk.addon.sample.client.SampleEntryPoint");

            final Main main = new Main();
            main.setApplicationContextName("sample");
            main.setPort(8081);
            main.setHttpServlet(httpServlet);
            main.setHttpSessionListener(applicationLoader);
            main.setServletContextListener(applicationLoader);
            main.start();

        } catch (final Throwable e) {
            log.error("", e);
        }
    }

}
