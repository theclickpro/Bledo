package com.theclickpro.bledo;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ControllerInterface {
	public void setUp(HttpServletRequest req, HttpServletResponse resp, ServletContext context, FwURL url, Properties conf);
	public void init() throws Exception;
	public void dispatch() throws IOException;
	public void setError(int errno, Exception e);
}
