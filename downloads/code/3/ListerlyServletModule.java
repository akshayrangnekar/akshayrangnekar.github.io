package com.listerly.config.guice;

import com.google.inject.servlet.ServletModule;
import com.listerly.HelloWorldServlet;

public class ListerlyServletModule extends ServletModule {

	@Override
	protected void configureServlets() {
	    serve("/hi").with(HelloWorldServlet.class);
	}

}
