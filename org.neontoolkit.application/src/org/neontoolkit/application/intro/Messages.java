package org.neontoolkit.application.intro;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.neontoolkit.application.intro.messages"; //$NON-NLS-1$
	public static String ApplicationWorkbenchAdvisor_StartPerspective;
	public static String ApplicationWorkbenchWindowAdvisor_ApplicationTitle;
	public static String Application_configurationFile_line1;
	public static String Application_configurationFile_line2;
	public static String Application_configurationFile_line3;
	public static String Application_configurationFile_line4;
	public static String Application_configurationFile_recentWorkspace;
	public static String Application_configurationFile_recentWorkspace_seperator;
	public static String Application_configurationFile_directory;
	public static String Application_configurationFile_connector;
	public static String Application_configurationFile_configFileName;
	public static String Application_datePattern;
	public static String Application_defaultWorkspace;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
