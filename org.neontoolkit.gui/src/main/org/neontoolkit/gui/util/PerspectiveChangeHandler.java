/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.util;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.neontoolkit.gui.Messages;

/* 
 * Created on: 30.01.2008
 * Created by: Dirk Wenke
 *
 * Keywords: 
 */
/**
 * TODO enter file comment
 */

public class PerspectiveChangeHandler {
	public static final String PROMPT = MessageDialogWithToggle.PROMPT;
	public static final String ALWAYS = MessageDialogWithToggle.ALWAYS; 
	public static final String NEVER = MessageDialogWithToggle.NEVER; 
	
	public static final String LABEL_ALWAYS = "Always"; //$NON-NLS-1$
	public static final String LABEL_NEVER = "Never"; //$NON-NLS-1$
	public static final String LABEL_PROMPT = "Prompt"; //$NON-NLS-1$

	/**
	 * NOTE: Must be called from UI thread.
	 * @param perspectiveId
	 * @param plugin
	 * @param preferenceKey
	 */
	public static void switchPerspective(String perspectiveId, AbstractUIPlugin plugin, String preferenceKey) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IPerspectiveDescriptor desc = workbench.getPerspectiveRegistry().findPerspectiveWithId(perspectiveId);
		if (workbench.getActiveWorkbenchWindow().getActivePage().getPerspective().equals(desc)) {
			//perspective already open => do nothing
			return;
		}

		String value = plugin.getPreferenceStore().getString(preferenceKey);
		if (value.length() == 0 || value.equals(PROMPT)) {
			MessageDialogWithToggle dialog = MessageDialogWithToggle.openYesNoQuestion(
					workbench.getActiveWorkbenchWindow().getShell(), 
					Messages.PerspectiveChangeHandler_3, 
					Messages.PerspectiveChangeHandler_4+desc.getLabel()+Messages.PerspectiveChangeHandler_5, 
					Messages.PerspectiveChangeHandler_6, 
					false, 
					plugin.getPreferenceStore(), 
					preferenceKey); 
			if (dialog.getReturnCode() == 2) {
				workbench.getActiveWorkbenchWindow().getActivePage().setPerspective(desc);
			}
		}
		else if (value.equals(ALWAYS)) {
            workbench.getActiveWorkbenchWindow().getActivePage().setPerspective(desc);
		}
	}

	public static void switchPerspective(String perspectiveId) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IPerspectiveDescriptor desc = workbench.getPerspectiveRegistry().findPerspectiveWithId(perspectiveId);
		if (workbench.getActiveWorkbenchWindow().getActivePage().getPerspective().equals(desc)) {
			//perspective already open => do nothing
			return;
		}
        workbench.getActiveWorkbenchWindow().getActivePage().setPerspective(desc);
	}
}
