/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.dialogs.SelectionDialog;

/* 
 * Created on 14.06.2004
 * Created by Dirk Wenke
 *
 * Keywords: Ontology, Save, Shutdown
 */

public class PreShutdownHandler implements IWorkbenchListener {

	public void postShutdown(IWorkbench workbench) {
		
	}
	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		SelectionDialog dialog = new SaveOntologyDialog(workbench.getActiveWorkbenchWindow().getShell());
		dialog.open();
		int res = dialog.getReturnCode();
		if (res ==1)
			return false;
		else
			return true;
	}
}
