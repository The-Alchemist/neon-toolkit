/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.project.RenameProject;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.actions.AbstractSelectionBasedHandler;
import org.neontoolkit.gui.navigator.elements.IProjectElement;

/*
 * Created on 01.12.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class RenameProjectHandler extends AbstractSelectionBasedHandler {

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.navigator.actions.AbstractSelectionBasedHandler#executeWithSelection(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public Object executeWithSelection(IWorkbenchPart part,
			IStructuredSelection selection) {
        Object selectedElement = selection.getFirstElement();
        if (selectedElement instanceof IProjectElement) {
            IProject project = NeOnCorePlugin.getDefault().getProject(((IProjectElement) selectedElement).getProjectName());
            InputDialog dialog = new InputDialog(new Shell(),
                    Messages.RenameProjectHandler_2, 
                    Messages.RenameProjectHandler_3, 
                    project.getName(),
                    new ProjectNameValidator(project.getName()));
            dialog.open();
            String res = dialog.getValue();
            if (res != null && !res.equals(project.getName())) {
            	try {
					new RenameProject(project.getName(), res).run();
				} catch (CommandException e) {
					NeOnUIPlugin.getDefault().logError("", e); //$NON-NLS-1$
				}
            }
        }
		return null;
	}

}
