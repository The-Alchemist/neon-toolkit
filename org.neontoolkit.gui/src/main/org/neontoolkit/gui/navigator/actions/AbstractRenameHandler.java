/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyEntity;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.gui.properties.ProgressMonitorWithExceptionDialog;

/*
 * Created on 27.11.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public abstract class AbstractRenameHandler extends AbstractSelectionBasedHandler {
	public static final String VAR_ACTIVE_PART = "activePart";  //$NON-NLS-1$
	
	protected Display _display;

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.navigator.actions.AbstractSelectionBasedHandler#executeWithSelection(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public Object executeWithSelection(IWorkbenchPart part,
			IStructuredSelection selection) throws ExecutionException {
		if (selection.size() == 1 && selection.getFirstElement() instanceof AbstractOntologyTreeElement) {
		    _display = part.getSite().getShell().getDisplay();
		    AbstractOntologyEntity entity = (AbstractOntologyEntity)selection.getFirstElement();
			AbstractRenameEntityDialog dialog = getRenameEntityDialog(part.getSite().getShell(), entity);
			int status = dialog.open();
			if (status == IDialogConstants.OK_ID) {
				dialog.close();
				String newId = dialog.getIdentifier();
		        ProgressMonitorWithExceptionDialog progressDialog = new ProgressMonitorWithExceptionDialog(part.getSite().getShell());
		        IRunnableWithProgress runnable = getRenameRunnable(part.getSite().getShell(), newId, entity);
		        try {
			        progressDialog.runWithException(false, false, runnable);
		        } catch (Exception e) {
		        	new NeonToolkitExceptionHandler().handleException(Messages.AbstractRenameHandler_0, e, part.getSite().getShell());
		        }
			}
		}
		return null;
	}
	
	protected abstract AbstractRenameEntityDialog getRenameEntityDialog(Shell shell, AbstractOntologyEntity entity);
	
    protected abstract IRunnableWithProgress getRenameRunnable(Shell shell, String newId, AbstractOntologyEntity entity);

    protected abstract String getLocalName(String newId, String ontologyId, String projectId) throws NeOnCoreException;
	
	protected abstract String getNamespace(String newId, String ontologyId, String projectId) throws NeOnCoreException;
	
	/**
	 * Returns the title of the rename dialog.
	 * @return
	 */
	public abstract String getTitle();
}
