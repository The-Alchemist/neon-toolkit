/**
 * Copyright (c) 2008 ontoprise GmbH.
 */

package org.neontoolkit.gui.navigator.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/*
 * Created on 01.12.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public abstract class AbstractSelectionBasedHandler extends AbstractHandler {
	public static final String VAR_SELECTION = "selection"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection)HandlerUtil.getCurrentSelection(arg0);
		IWorkbenchPart part = HandlerUtil.getActivePart(arg0);
		return executeWithSelection(part, selection);
	}
	
	public abstract Object executeWithSelection(IWorkbenchPart part, IStructuredSelection selection) throws ExecutionException;

}
