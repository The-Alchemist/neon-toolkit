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
	public static String Application_promptForWorkspace_title;
	public static String Application_promptForWorkspace_message;
	public static String StringFormatter_prefix_URL;
	public static String StringFormatter_suffix_URL;
	public static String StringFormatter_colon_URL;
	public static String StringFormatter_colon_config;
	public static String StringFormatter_slash_URL;
	public static String StringFormatter_slash_config;
	
	public static String IDEApplication_workspaceMandatoryTitle;
	public static String IDEApplication_workspaceMandatoryMessage;
	public static String IDEApplication_workspaceInUseTitle;
	public static String IDEApplication_workspaceInUseMessage;
	public static String IDEApplication_workspaceEmptyTitle;
	public static String IDEApplication_workspaceEmptyMessage;
	public static String IDEApplication_workspaceInvalidTitle;
	public static String IDEApplication_workspaceInvalidMessage;
	public static String IDEApplication_workspaceCannotBeSetTitle;
	public static String IDEApplication_workspaceCannotBeSetMessage;
	public static String IDEApplication_workspaceCannotLockTitle;
	public static String IDEApplication_workspaceCannotLockMessage;
	public static String IDEApplication_versionTitle;
	public static String IDEApplication_versionMessage_1;
	public static String IDEApplication_versionMessage_2;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
