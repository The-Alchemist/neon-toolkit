/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.io.command;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.neontoolkit.core.command.DatamodelCommand;

public abstract class AbstractOntologyExportCommand extends DatamodelCommand {
	
	protected String[][] _messages = new String[0][0];
	private IProgressMonitor _monitor = new NullProgressMonitor();
	
	public AbstractOntologyExportCommand(String project, String ontologyUri, String physicalUri, String fileFormat) {
		super(project, ontologyUri, physicalUri, fileFormat);
	}

	public AbstractOntologyExportCommand(String project, String ontologyUri, String physicalUri, String fileFormat, IProgressMonitor monitor) {
    	this(project, ontologyUri, physicalUri, fileFormat);
		_monitor = monitor;
	}
	
		
    public String[][] getMessages() {
    	return _messages;
    }	

    public String getOntologyUri() {
    	return (String) getArgument(1);
    }
    
    public String getPhysicalUri() {
    	return (String) getArgument(2);
    }
    
    public String getFileFormat() {
    	return (String) getArgument(3);
    }
    
    public IProgressMonitor getProgressMonitor() {
    	return _monitor;
    }    
    
}
