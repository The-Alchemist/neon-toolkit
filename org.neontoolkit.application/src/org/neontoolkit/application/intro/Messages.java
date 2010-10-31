package org.neontoolkit.application.intro;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.neontoolkit.application.intro.messages"; //$NON-NLS-1$
	public static String ApplicationWorkbenchAdvisor_StartPerspective;
	public static String ApplicationWorkbenchWindowAdvisor_ApplicationTitle;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
