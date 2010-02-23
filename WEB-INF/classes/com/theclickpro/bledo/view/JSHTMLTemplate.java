package com.theclickpro.bledo.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class JSHTMLTemplate {
	
	private File tplFile;
	private enum Mode {
		outmode,
		commentmode,
		jsmode,
		stringmode,
		escapemode,
		shorttagmode
	};
	private Mode mode = Mode.outmode;
	
	private StringBuilder output = new StringBuilder("print(\"");
	private StringBuilder buff = new StringBuilder();
	
	private Map<String, Object> vars = new HashMap<String, Object>();
	
	public void assign(String key, Object obj) { vars.put(key, obj); }

	public JSHTMLTemplate(File f)
	{
		tplFile = f;
	}
	
	private void setOutMode() { mode = Mode.outmode; }
	private boolean isOutMode() { if (mode == Mode.outmode) { return true; } return false; }
	
	private void setCommentMode() { mode = Mode.commentmode; }
	private boolean isCommentMode() { if (mode == Mode.commentmode) { return true; } return false; }
	
	private void setJsMode() { mode = Mode.jsmode; }
	private boolean isJsMode() { if (mode == Mode.jsmode) { return true; } return false; }
	
	private void setStringMode() { mode = Mode.stringmode; }
	private boolean isStringMode() { if (mode == Mode.stringmode) { return true; } return false; }
	
	private void setEscapeMode() { mode = Mode.escapemode; }
	private boolean isEscapeMode() { if (mode == Mode.escapemode) { return true; } return false; }
	
	private void setShorttagMode() { mode = Mode.shorttagmode; }
	private boolean isShorttagMode() { if (mode == Mode.shorttagmode) { return true; } return false; }
	
	public String parse() throws FileNotFoundException, IOException
	{
		
		//return parseJSHTML(); 
		
		// INIT
		org.mozilla.javascript.Context cx = org.mozilla.javascript.Context.enter();
		org.mozilla.javascript.Scriptable scope = cx.initStandardObjects();
		
		
		
		// print ...
		JSObjects jsobjects = new JSObjects();
		Object wrappedOut = org.mozilla.javascript.Context.javaToJS(jsobjects, scope);
		org.mozilla.javascript.ScriptableObject.putProperty(scope, "Template", wrappedOut);
		
		
		// Assign View Values
		for (String k : vars.keySet())
		{
			//System.out.println(k + " " + vars.get(k));
			Object wrap = org.mozilla.javascript.Context.javaToJS(vars.get(k), scope);
			org.mozilla.javascript.ScriptableObject.putProperty(scope, k, wrap);
		}
		
		
		
		
		
		
		// Execute line by line
		String code = parseJSHTML(); 
		/*
		String[] codeLines = code.split("\n");
		int numLines = codeLines.length;
		for (int i = 0; i < numLines; i++)
		{
			cx.evaluateString(scope, codeLines[i] + "\n", tplFile.getName(), i + 1, null);
		}
		*/
		
		String output = "";
		try
		{
			cx.evaluateString(scope, "function print(s) {Template.out(s);}", "", 0, null);
			cx.evaluateString(scope, "function print(s) {Template.out(s);}", "", 0, null);
			cx.evaluateString(scope, code, tplFile.getName(), 0, null);
			output = jsobjects.getOutput();
		}
		catch (Exception e)
		{
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.close();
			output = sw.toString();
		}
		
		// return
		return output; //jsobjects.getOutput();
	}
	
	private String parseJSHTML() throws FileNotFoundException, IOException
	{
		BufferedReader reader = null;
		FileReader freader = null;
		
		try
		{
			freader = new FileReader(tplFile);
			reader =  new BufferedReader( freader );
			
			while (reader.ready())
			{
				buff.append((char) reader.read());
				
				/*
				if (buff.toString().equals("<"))
				{
					System.out.println("HEREE");
				}
				*/
					
				if (isOutMode())
				{
					handleOutputMode();
				}
				else
				{
					handleJsModes();
				}
			}
		}
		finally
		{
			IOUtils.closeQuietly(reader);
		}
		
		
		// closing checks
		checkClosingMode();
		
			
		// return
		return output.toString();
	}
	
	private void checkClosingMode()
	{
		if (isOutMode())
		{
			output.append("\");\n"); //   ");
		}
	}
	
	private void handleOutputMode()
	{
		if (buff.toString().equals("<"))
		{
			return; //almost JSMODE
		}
		else if (buff.toString().startsWith("<?"))
		{
			if (buff.length() > 2)
			{
				if (buff.toString().equals("<?="))
				{
					setShorttagMode();
					output.append("\");\nprint("); //   ");
					buff.setLength(0);
				}
				else if (buff.toString().equals("<?x"))
				{
					// <?xml
					output.append(buff);
					buff.setLength(0);
				}
				else
				{
					setJsMode();
					output.append("\");\n" + buff.substring(2)); //   ");
					buff.setLength(0);
				}
			}
		}
		else if (buff.toString().endsWith("\""))
		{
			output.append("\\\"");
			buff.setLength(0);
		}
		else if (buff.toString().endsWith("\'"))
		{
			output.append("\\\'");
			buff.setLength(0);
		}
		else if (buff.toString().equals("\n"))
		{
			output.append("\\n\");\nprint(\"");
			buff.setLength(0);
		}
		else if (buff.toString().equals("\t"))
		{
			output.append("\\t");
			buff.setLength(0);
		}
		else if (buff.toString().equals("\r"))
		{
			output.append("\\r");
			buff.setLength(0);
		}
		else
		{
			output.append(buff);
			buff.setLength(0);
		}
	}
	
	private void handleJsModes()
	{
		if (isCommentMode())
		{
			handleCommentMode();
		}
		else if (isJsMode())
		{
			handleJsMode();
		}
		else if (isStringMode())
		{
			handleStringMode();
		}
		else if (isEscapeMode())
		{
			handleEscapeMode();
		}
		else if (isShorttagMode())
		{
			handleShorttagMode();
		}
		else
		{
			output.append(buff);
			buff.setLength(0);
		}
	}
	
	private void handleCommentMode()
	{
		if (buff.toString().equals("*"))
		{
			return;
		}
		else if (buff.toString().equals("*/"))
		{
			setJsMode();
			output.append(buff);
			buff.setLength(0);
		}
		else
		{
			output.append(buff);
			buff.setLength(0);
		}
	}
	
	
	private void handleJsMode()
	{
		if (buff.toString().equals("/"))
		{
			return;
		}
		else if (buff.toString().equals("/*"))
		{
			setCommentMode();
			output.append(buff);
			buff.setLength(0);
		}
		else if (buff.toString().equals("\"") || buff.toString().equals("\'"))
		{
			setStringMode();
			output.append(buff);
			buff.setLength(0);
		}
		else if (buff.toString().equals("?"))
		{
			return;
		}
		else if (buff.toString().equals("?>"))
		{
			setOutMode();
			output.append("print(\"");
			buff.setLength(0);
		}
		else
		{
			output.append(buff);
			buff.setLength(0);
		}
		
	}
	
	private void handleStringMode()
	{
		if (buff.toString().equals("\\"))
		{
			setEscapeMode();
			output.append(buff);
			buff.setLength(0);
		}
		else if (buff.toString().equals("\"") || buff.toString().equals("\'"))
		{
			setJsMode();
			output.append(buff);
			buff.setLength(0);
		}
		else
		{
			output.append(buff);
			buff.setLength(0);
		}
	}
	
	private void handleEscapeMode()
	{
		setStringMode();
		output.append(buff);
		buff.setLength(0);
	}
	
	private void handleShorttagMode()
	{
		if (buff.toString().equals("?"))
		{
			return;
		}
		else if (buff.toString().equals("?>"))
		{
			setOutMode();
			output.append(");\nprint(\"");
			buff.setLength(0);
		}
		else
		{
			output.append(buff);
			buff.setLength(0);
		}
	}
}












