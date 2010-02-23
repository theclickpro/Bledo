package example.controller;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.theclickpro.bledo.FwURL;
import com.theclickpro.bledo.View;

public class ErrorController implements com.theclickpro.bledo.ControllerInterface {
	
	protected String template = "template.jshtml";
	
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected ServletContext context;
	protected FwURL url;
	protected Properties config;
	
	protected int errno;
	protected Exception ex;
	
	public void setUp(HttpServletRequest req, HttpServletResponse resp, ServletContext context, FwURL url, Properties conf)
	{
		response = resp;
		request =req;
		config = conf;
		this.url = url;
		this.context = context;
	}
	
	public void init() { }
	
	public void setError(int errno, Exception e)
	{
		this.errno = errno;
		this.ex = e;
	}
	
	public void dispatch() throws IOException
	{
		View view = new View( context.getRealPath("/WEB-INF/view") );
		view.assign("BASEURL", config.getProperty("BASEURL"));
		// Return Main View
		
		
		view.assign("MAIN", "Error : " + errno + "<br /> Ex: " + ex.getMessage());
		
		response.getWriter().print(view.parse(template));
	}

}
