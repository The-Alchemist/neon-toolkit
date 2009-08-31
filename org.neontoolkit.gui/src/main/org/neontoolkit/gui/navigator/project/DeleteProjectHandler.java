/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.navigator.project;

import org.neontoolkit.core.command.project.RemoveProject;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.actions.AbstractConfirmDeleteHandler;

/* 
 * Created on: 01.07.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Action
 */
/**
 * Handler for the removal of a project.
 */
public class DeleteProjectHandler extends AbstractConfirmDeleteHandler {
	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractDeleteAction#doDelete(java.lang.Object[], java.lang.Object[], boolean)
	 */
	@Override
	public boolean doDelete(Object[] items, Object[] parentItems,
			boolean checked) {
		for (int i = 0; i < items.length; i++) {
			if (items[i] instanceof ProjectTreeElement) {
				try {
					String projectName = ((ProjectTreeElement) items[i]).getProjectName();
					new RemoveProject(projectName, checked).run();
				} catch (Exception e) {
					new NeonToolkitExceptionHandler().handleException(Messages.DeleteProjectAction_2, e, getShell());
					return false;
				}
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractDeleteAction#getDialogMessage()
	 */
	@Override
	public String getDialogMessage() {
		return Messages.DeleteProjectAction_0;	} 

	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractDeleteAction#getCheckBoxText()
	 */
	@Override
	public String getCheckBoxText() {
		if (isAtLeastOneElementPersistent()) {
			return Messages.DeleteProjectAction_1; 
		} else {
			return null;
		}
	}

	private boolean isAtLeastOneElementPersistent() {
		boolean persistent = false;
		for (Object o:getSelection().toArray()) {
			if (o instanceof ProjectTreeElement) {
				try {
					String projectName = ((ProjectTreeElement)o).getProjectName();
					persistent |= OntologyProjectManager.getDefault().getOntologyProject(projectName).isPersistent();        
				} catch (Exception e) {
					NeOnUIPlugin.getDefault().logError("", e); //$NON-NLS-1$
				}
			}
		}
		return persistent;
	}

}
