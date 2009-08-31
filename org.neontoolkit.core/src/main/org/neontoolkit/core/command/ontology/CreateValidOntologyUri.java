/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/
package org.neontoolkit.core.command.ontology;

import java.net.URI;
import java.net.URISyntaxException;

import org.neontoolkit.core.Messages;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.LoggedCommand;
import org.neontoolkit.core.exception.InternalNeOnException;

/*
 * Created on 09.06.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class CreateValidOntologyUri extends LoggedCommand {
	
	private String _ontologyUri;
	
	public CreateValidOntologyUri(String ontologyUri) {
		super(ontologyUri);
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.datamodel.command.LoggedCommand#perform()
	 */
	@Override
	protected void perform() throws CommandException {
    	try {
    		String ontologyUri = getArgument(0).toString();
			URI uri = null;
			try {
				uri = new URI(ontologyUri);
				if (!uri.isAbsolute()) {
					throw new InternalNeOnException(new URISyntaxException(ontologyUri, Messages.CreateValidOntologyUri_0));
				}
			} catch (URISyntaxException e) {
				ontologyUri = "http://"+ontologyUri; //$NON-NLS-1$
				uri = new URI(ontologyUri);
				if (uri.getHost().equals("http")) { //$NON-NLS-1$
                    throw e;
				}
			}
			_ontologyUri = ontologyUri.toString();
    	} catch (Exception e) {
    		throw new CommandException(e);
    	}
	}
	
	public String getOntologyUri() throws CommandException {
		if (_ontologyUri == null) {
			run();
		}
		return _ontologyUri;
	}

}
