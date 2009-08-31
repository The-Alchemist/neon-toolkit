/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/
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
