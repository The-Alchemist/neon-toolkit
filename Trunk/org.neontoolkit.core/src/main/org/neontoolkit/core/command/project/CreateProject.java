/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core.command.project;

import java.util.Properties;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.LoggedCommand;
import org.neontoolkit.core.project.OntologyProjectManager;

/* 
 * Created on: 29.07.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Command
 */
/**
 * Command for creating an ontology development project.
 */
public class CreateProject extends LoggedCommand {
	public CreateProject(String projectName, String ontologyLanguageId, Properties configProperties) {
		this(projectName, ontologyLanguageId, propertiesToStringArray(configProperties));
	}
	
    public CreateProject(String projectName, String ontologyLanguageId, String[][] configProperties) {
        super(projectName, ontologyLanguageId, configProperties);
    }

    @Override
	protected void checkArguments() throws IllegalArgumentException {
	    if (isNull(0) || isNull(1) || isNull(2)) {
	        throw new IllegalArgumentException(NULL_ARGUMENT_MESSAGE);
	    }
	}

	/*
	 * (non-Javadoc)
	 * @see org.neontoolkit.core.command.LoggedCommand#perform()
	 */
	@Override
	public void perform() throws CommandException {
	    String projectName = (String)getArgument(0);
	    String ontologyLanguageId = (String)getArgument(1);
	    Properties configProperties = stringArrayToProperties((String[][])getArgument(2));
		try {
		    OntologyProjectManager.getDefault().createOntologyProject(projectName, ontologyLanguageId, configProperties);
		} catch (Exception ce) {
		    //Problem while creating datamodel
		    //remove project
		    new RemoveProject(projectName, true).perform();
			throw new CommandException(ce);			
		}
	}
}
