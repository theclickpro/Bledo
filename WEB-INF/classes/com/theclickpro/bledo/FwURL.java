package com.theclickpro.bledo;

import java.util.HashMap;


public class FwURL
{
	private String controller = null;
	private String action = null;
	private HashMap<String, String> params;
	
	public FwURL(String url, String defController, String defAction)
	{
		controller = defController;
		action = defAction;
		parseUrl(url);
	}
	
	public FwURL(String url)
	{
		parseUrl(url);
	}
	
	private void parseUrl(String url)
	{
		params = new HashMap<String, String>();
		
		// fix empty url
		if (url == null) { url = ""; }
		
		
		// trim slashes
		url = com.theclickpro.bledo.Util.trim(url, "/");
		//String[] pieces = url.substring(1).split("/");
		
		// Explode
		String[] pieces = url.split("/");
		
		
		// controller
		if (pieces.length > 0)
		{
			if (pieces[0].trim().length() > 0)
			{
				controller = pieces[0].trim();
			}
		}
		
		
		// action
		if (pieces.length > 1)
		{
			if (pieces[1].trim().length() > 0)
			{
				action = pieces[1].trim();
			}
		}
		
		
		
		// parameters
		int keyKey = 0;
		int valKey = 0;
		String val = null;
		for (int i = 2; i < pieces.length; i += 2)
		{
			keyKey = i;
			valKey = i + 1;
			
			if (valKey >= pieces.length)
			{
				val = "";
			}
			else
			{
				val = pieces[valKey];
			}
			
			
			params.put(pieces[keyKey], val);
		}
	}
	
	public String getParam(String key)
	{
		return params.get(key);
	}
	public String getParam(String key, String defaultValue)
	{
		String val = getParam(key);
		
		if (val == null || val.isEmpty())
		{
			return defaultValue;
		}
		
		return val;
	}
	
	public String getController() { return controller; }
	public String getControllerProper()
	{
		return getController().substring(0, 1).toUpperCase() + getController().substring(1).toLowerCase() + "Controller";
	}
	
	public String getAction() { return action; }
	public String getActionProper()
	{
		return getAction().toLowerCase() + "Action";
	}
}
