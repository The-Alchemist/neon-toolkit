/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.individualview.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.individualview.AnonymousIndividualViewItem;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOWLRenameHandler;

/*
 * Created on 01.12.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class RenameIndividualHandler extends AbstractOWLRenameHandler {

	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.flogic.ui.navigator.entity.RenameEntityHandler#getTitle()
	 */
	@Override
	public String getTitle() {
		return Messages.RenameIndividualHandler_0; 
	}

	@Override
	public Object executeWithSelection(IWorkbenchPart part, IStructuredSelection selection) throws ExecutionException {
	    if(selection.getFirstElement() instanceof AnonymousIndividualViewItem){
	        MessageDialog.openError(part.getSite().getShell(), Messages.RenameIndividualHandler_0, Messages.RenameIndividualHandler_1);
	        return null;
//	        TODO Dialog to convert a anonymous individual to an named individual (transformation command)
//	        if(!MessageDialog.openConfirm(part.getSite().getShell(), Messages.RenameIndividualHandler_0, Messages.RenameIndividualHandler_1))
//	            return null;
//	        AnonymousIndividualViewItem item = (AnonymousIndividualViewItem)selection.getFirstElement();
//	        
//	        
//	        return null;
	        
	    }else{
	        return super.executeWithSelection(part, selection);
	    }

	}
    @Override
    protected String getLocalName(String newId, String ontologyId, String projectId) throws NeOnCoreException {
        // not needed for OWL
        return null;
    }

    @Override
    protected String getNamespace(String newId, String ontologyId, String projectId) throws NeOnCoreException {
        // not needed for OWL
        return null;
    }
	
}
