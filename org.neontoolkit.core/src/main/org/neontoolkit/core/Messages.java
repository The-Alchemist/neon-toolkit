/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.neontoolkit.core.messages";//$NON-NLS-1$
	public static String AbstractOntologyProject_0;
    public static String AbstractOntologyProject_1;
    public static String CreateUniqueOntologyUri_1;
    public static String CreateValidOntologyUri_0;
    public static String NeOnCorePlugin_1;
    public static String NeOnCorePlugin_2;
	public static String OntologyProjectNature_0;
    public static String OntologyProjectNature_1;
	public static String OntologyProjectNature_2;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
