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

import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.gui.Messages;
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
