/**
 * Copyright (c) 2008 ontoprise GmbH.
 */

package com.ontoprise.ontostudio.owl.gui.navigator;

import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.gui.Messages;

/*
 * Created on 01.12.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class RenameOWLEntityHandler extends AbstractOWLRenameHandler {

	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.flogic.ui.navigator.entity.RenameEntityHandler#getTitle()
	 */
	@Override
	public String getTitle() {
		return Messages.RenameClazzHandler_0; 
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
