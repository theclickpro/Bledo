package com.theclickpro.bledo;


import java.io.IOException;
import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Controller implements ControllerInterface {
	
	protected View view;
	
	protected String template = null;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected ServletContext context;
	protected FwURL url;
	protected Properties config;
	
	@Override
	public void setError(int errno, Exception e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setUp(HttpServletRequest req, HttpServletResponse resp, ServletContext context, FwURL url, Properties conf)
	{
		
		//
		response = resp;
		request = req;
		config = conf;
		this.url = url;
		this.context = context;
		
		
		/*
		 * Variables from Servlet
		 */
		response.setContentType("text/html");
		
		
		
		
		/*
		 * Setup View
		 */
		view = new View( context.getRealPath("/WEB-INF/view") );
		Enumeration<?> e = config.propertyNames();
		
		
		while (e.hasMoreElements())
		{
			String key = (String) e.nextElement();
			view.assign(key, config.getProperty(key));
		}
		view.assign("TITLE", "Default Title");
		view.assign("HEAD", "");
		view.assign("FOOTER", "");
		view.assign("request", request);
		view.assign("response", response);
		view.assign("url", url);
		
		
		template = "template." + config.getProperty("viewFileExtension");
		
		
		/*
		 * Session Variable
		 */
		//session = request.getSession();
	}
	
	
	public void init() throws Exception
	{
	}
	
	public void dispatch() throws IOException
	{
		// Action View
		if (!view.isKeySet("MAIN"))
		{
			File viewFile = new File( context.getRealPath("/WEB-INF/view/" + url.getController() + "/" + url.getAction() + "." + config.getProperty("viewFileExtension")) );
			if (viewFile.isFile())
			{
				view.assignTpl("MAIN", viewFile);
			}
			else
			{
				view.assign("MAIN", "");
			}
		}
		
		
		// Return Main View
		response.getWriter().print(view.parse(template));
	}


}
