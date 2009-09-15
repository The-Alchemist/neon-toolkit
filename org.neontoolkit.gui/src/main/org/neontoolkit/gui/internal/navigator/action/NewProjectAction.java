/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.internal.navigator.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/* 
 * Created on: 30.06.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Action, Project
 */
/**
 * Action for the creation of a new project.
 */
public class NewProjectAction implements IViewActionDelegate {

	private ISelection _selection;

	public NewProjectAction() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@SuppressWarnings("restriction")
	public void run(IAction action) {
        //	 Create wizard selection wizard.
        IWorkbench workbench = PlatformUI.getWorkbench();
        org.eclipse.ui.internal.dialogs.NewWizard wizard = new org.eclipse.ui.internal.dialogs.NewWizard();
        wizard.setProjectsOnly(true);
        IStructuredSelection selectionToPass = StructuredSelection.EMPTY;

        if (_selection instanceof IStructuredSelection) {
            selectionToPass = (IStructuredSelection) _selection;
        }
        wizard.init(workbench, selectionToPass);
        IDialogSettings workbenchSettings = org.eclipse.ui.internal.WorkbenchPlugin.getDefault().getDialogSettings();
        IDialogSettings wizardSettings = workbenchSettings.getSection("NewWizardAction"); //$NON-NLS-1$
        if (wizardSettings == null) {
            wizardSettings = workbenchSettings.addNewSection("NewWizardAction"); //$NON-NLS-1$
        }
        wizard.setDialogSettings(wizardSettings);
        wizard.setForcePreviousAndNextButtons(true);

        // Create wizard dialog.
        Shell parent;
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        if (window == null) {
            parent = new Shell();
        } else {
            parent = window.getShell();
        }
        WizardDialog dialog = new WizardDialog(parent, wizard);
        dialog.create();

        // Open wizard.
        dialog.open();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		_selection = selection;
	}
}
