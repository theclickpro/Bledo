package com.theclickpro.bledo;

import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;
import java.util.Locale;

public class Util {
	
	private static ResourceBundle resBundle;
	private static Locale curLocale; 
	
	/**
	 * Sets the locale for Gettext _() function
	 * 
	 * @param domainName
	 * @param language
	 * @param country
	 */
	public static void setLocale(String domainName, String language, String country)
	{
		curLocale = new Locale(language, country);
		resBundle = ResourceBundle.getBundle(domainName, curLocale);
	}
	
	/**
	 * Gettext Port to Java
	 * 
	 * @param s String to Translate
	 * @return Translated String
	 */
	public static String _(String s)
	{
		return resBundle.getString(s);
	}
	

	/**
	 * PHP version of trim
	 * 
	 * @param str String to trim
	 * @param charlist List of characters to trim
	 * @return
	 */
	public static String trim(String str, String charlist)
	{
		    int l = 0;
		    int i = 0;
		    
		    l = str.length();
		    for (i = 0; i < l; i++) {
		        if (charlist.indexOf(str.charAt(i)) == -1) {
		            str = str.substring(i);
		            break;
		        }
		    }
		    
		    l = str.length();
		    for (i = l - 1; i >= 0; i--) {
		        if (charlist.indexOf(str.charAt(i)) == -1) {
		            str = str.substring(0, i + 1);
		            break;
		        }
		    }
		    
		    if (charlist.indexOf(str.charAt(0)) == -1)
		    {
		    	return str;
		    }
		    
		    return "";
	}
	
	
	/**
	 * Loads a class and returns an object of the class type
	 * 
	 * @param className full name of the class to load
	 * @param params Constructor Parameters
	 * @return Object
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Object loadClass(String className, Object[] params)
	throws IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException
	{
			Class[] clsParams = new Class[params.length];
			
			for (int i = 0; i < params.length; i++)
			{
				clsParams[i] = params[i].getClass();
			}
		
			Class c = Class.forName(className);
			java.lang.reflect.Constructor constructor = c.getConstructor(clsParams);
			Object o = constructor.newInstance(new Object[0]);
			return o;
	}
}




