package com.theclickpro.bledo;

import javax.servlet.http.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.catalina.servlets.DefaultServlet;

import com.theclickpro.bledo.Controller;
import com.theclickpro.bledo.FwURL;
import com.theclickpro.bledo.IO;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

//import java.lang.reflect.*;
//import java.util.Map;
import java.util.Properties;
//import java.util.HashMap;

public class Dispatcher extends DefaultServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7082794792528524852L;
	private Properties config;
	private ServletContext context;
	
	public void init(ServletConfig servConf) throws ServletException
	{
		super.init(servConf);
		
		/*
		 * Init ServletContext
		 */
		context = getServletContext();
		
		/*
		 * Init Config
		 */
		config = new Properties();
		String configPath = context.getRealPath("/WEB-INF/dispatch.properties");
		try
		{
			config.load(new FileInputStream(configPath));
		}
		catch (FileNotFoundException e)
		{
			throw new ServletException(configPath + " not found");
		}
		catch (IOException e)
		{
			throw new ServletException("Error Reading " + configPath);
		}
	}
	
	private void dispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		// Web Structure
		IO wio = new IO();
		wio.config = this.config;
		wio.req = req;
		wio.resp = resp;
		wio.url = new FwURL(
					req.getPathInfo(),
					this.config.getProperty("defaultAction"),
					this.config.getProperty("defaultController")
					);
		
		wio.context = this.context;
		
		dispatchController(wio);
	}
	
	private void dispatchController(IO wio) throws IOException
	{
		String controllerName = wio.url.getControllerProper();
		String actionName = wio.url.getActionProper();
		
		
		Controller controller = null;
		
		try {
			
			if (!controllerName.matches("[a-zA-Z0-9]*"))
			{
				throw new ClassNotFoundException("Only Alphanumeric controllers");
			}
			
			if (!actionName.matches("[a-zA-Z0-9]*"))
			{
				throw new NoSuchMethodException("Only Alphanumeric actions");
			}
			
		
			controller = (Controller) Util.loadClass(this.config.getProperty("controllerPackage") + "." + controllerName, new Object[0]);
			controller.setUp(wio.req, wio.resp, wio.context, wio.url, wio.config);
			controller.init();
			controller.getClass().getMethod(actionName).invoke(controller);
			controller.dispatch();
			
		}
		catch (ClassNotFoundException e)
		{
			errorControllerDispatch(404, wio, e);
		}
		catch (NoSuchMethodException e)
		{
			errorControllerDispatch(404, wio, e);
		}
		catch (InstantiationException e)
		{
			errorControllerDispatch(404, wio, e);
		}
		catch (IllegalAccessException e)
		{
			errorControllerDispatch(404, wio, e);
		}
		catch (InvocationTargetException e)
		{
			errorControllerDispatch(404, wio, e);
		}
		catch (IOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			errorControllerDispatch(503, wio, e);
		}
		
	}
	
	private void errorControllerDispatch(int errno, IO io, Exception ex) throws IOException
	{
		try
		{
			
			ControllerInterface err = (ControllerInterface) Util.loadClass(this.config.getProperty("controllerPackage") + "." + this.config.getProperty("errorController"), new Object[0]);
			err.setUp(io.req, io.resp, io.context, io.url, io.config);
			err.setError(errno, ex);
			err.dispatch();
//log("dispatchController: ErrorController");
		}
		catch (Exception e)
		{
			//xe.printStackTrace();
			try
			{
//log(e.getMessage());
				io.resp.getWriter().println("<html><head><title>Error</title></head><body><h1>An error has ocurred. Please try again later.</h1>" + e.getMessage() + "</body></html>");
//log("dispatchController: -no-controller- error");
			} catch (IOException xxe)
			{
				//nothing to do!!! ?
			}
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{
		if (req.getPathInfo().startsWith(this.config.getProperty("staticReservePath")))
		{
			super.doGet(req, resp);
		}
		else
		{
			dispatch(req, resp);
		}
	}
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{
		if (req.getPathInfo().startsWith(this.config.getProperty("staticReservePath")))
		{
			super.doPost(req, resp);
		}
		else
		{
			dispatch(req, resp);
		}
	}
}

