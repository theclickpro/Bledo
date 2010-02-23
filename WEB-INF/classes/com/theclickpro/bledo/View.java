package com.theclickpro.bledo;

import java.io.File;
//import java.io.FileInputStream;
import java.io.IOException;
//import java.io.StringWriter;
//import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

//import org.onemind.jxp.*;

import com.theclickpro.bledo.view.JSHTMLTemplate;

//import sun.org.mozilla.javascript.internal.Context;


public class View {
	
	//private FilePageSource pageSource;
	//private JxpContext context;
	//private JxpProcessor processor;
	
	private Map<String, Object> env = new HashMap<String, Object>();
	private Map<String, File> tplEnv = new HashMap<String, File>();
	
	private String baseDirectory;
	
	public View(String viewDirectory)
	{
		baseDirectory = viewDirectory;
	/*
		pageSource = new FilePageSource( viewFolder );
		context = new JxpContext(pageSource);
		processor = new JxpProcessor(context);
	*/
	}
	
	public void assign(String key, Object val)
	{
		env.put(key, val);
	}
	
	public boolean isKeySet(String key)
	{
		return env.containsKey(key);
	}
	
	public void assignTpl(String key, File file)
	{
		tplEnv.put(key, file);
	}
	public void assignTpl(String key, String fileName)
	{
		tplEnv.put(key, new File(baseDirectory + "/" + fileName));
	}
	
	private String parseTpl(File fileToParse) throws IOException
	{
		JSHTMLTemplate tpl = new JSHTMLTemplate(fileToParse);
		for (String k : env.keySet())
		{
			tpl.assign(k, env.get(k));
		}
		return tpl.parse();
	}
	
	public String parse(File fileToParse) throws IOException
	{
		for (String i : tplEnv.keySet())
		{
			File file = tplEnv.get(i);
			
			// already set
			if (env.containsKey(i)) continue;
			
			env.put(i, parseTpl(file));
		}
		
		return parseTpl(fileToParse);
	}
	
	public String parse(String fileName) throws IOException
	{
		return parse(new File(baseDirectory + "/" + fileName));
	}
}






