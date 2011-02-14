/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core.command.ontology;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.DatamodelCommand;
import org.neontoolkit.core.exception.NeOnCoreException;

/*
 * Created on 13.06.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class ExistsOntology extends DatamodelCommand {
	private Boolean _exists;

	/**
	 * 
	 */
	public ExistsOntology(String project, String ontologyUri) {
		super(project, ontologyUri);
	}
	/* (non-Javadoc)
	 * @see org.neontoolkit.datamodel.command.LoggedCommand#perform()
	 */
	
	@Override
	public void perform() throws CommandException {
		try {
			_exists =  getOntologyProject().getAvailableOntologyURIs().contains(getArgument(1));
		} catch (NeOnCoreException e) {
			throw new CommandException(e);
		}
	}
	
	public boolean exists() throws CommandException {
		if (_exists == null) {
			run();
		}
		return _exists;
	}

}
