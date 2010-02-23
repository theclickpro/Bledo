package com.theclickpro.bledo.view;

public class JSObjects {
	
	private StringBuilder output = new StringBuilder();
	public void out(Object o) { output.append(o); }
	
	public String getOutput() { return output.toString(); }
}
