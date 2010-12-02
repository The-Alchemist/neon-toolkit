package org.neontoolkit.application.intro.util;

import java.net.URL;

import org.neontoolkit.application.intro.Messages;

public class StringFormaterForURL {
	
	 private static final String prefix_URL = Messages.StringFormatter_prefix_URL;
	 private static final String suffix_URL = Messages.StringFormatter_suffix_URL;

	 private static final String colon_URL = Messages.StringFormatter_colon_URL;
	 private static final String colon_config = Messages.StringFormatter_colon_config;
	 private static final String slash_URL = Messages.StringFormatter_slash_URL;
	 private static final String slash_config = Messages.StringFormatter_slash_config;
	 
	
	/**
	 * determines and returns a string of the URL in the correct format for the configuration file
	 * 
	 * @param url - URL to transfer to a string
	 * @return string of the URL in the correct format
	 */
	public static String translateUrlToConfig(URL url) {
		 String urlString = url.toString();
		 if(urlString.startsWith(prefix_URL))
			 urlString = urlString.substring(prefix_URL.length());
		 if(urlString.endsWith(suffix_URL))
			 urlString = urlString.substring(0,urlString.length() - suffix_URL.length());

		 urlString = urlString.replace(colon_URL, colon_config);
		 urlString = urlString.replace(slash_URL, slash_config);
		return urlString;
	}

	/**
	 * determines and returns a string in the correct format for the URL based on the text of the configuration file
	 * 
	 * @param input - to transfer to a string for a URL
	 * @return string of the URL in the correct format
	 */
	public static String translateConfigToUrlString(String input) {
		 String urlString = input;
		 if(!urlString.startsWith(prefix_URL))
			 urlString = prefix_URL + urlString;
		 if(!urlString.endsWith(suffix_URL))
			 urlString = urlString + suffix_URL;

		 urlString = urlString.replace(colon_config , colon_URL);
		 urlString = urlString.replace(slash_config, slash_URL);
		return urlString;
	}
}
