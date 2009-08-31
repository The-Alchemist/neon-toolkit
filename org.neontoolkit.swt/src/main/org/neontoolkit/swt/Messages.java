/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.neontoolkit.swt;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.neontoolkit.swt.messages";//$NON-NLS-1$
	public static String EditableListSelectionDialog_1;
	public static String EditableListSelectionDialog_2;
	public static String ResourceSelectionDialog_0;
	public static String ResourceSelectionDialog_1;
	public static String StandardTableCellModifier_0;
	public static String StandardTableViewer_2;
	public static String StandardTableViewer_1;
	public static String StandardTableLabelProvider_0;
    public static String StandardTreeTableViewer_1;
	public static String TreeSelectionDialog_0;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}