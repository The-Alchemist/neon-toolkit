/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator.elements;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.gui.history.EmptyOWLHistoryEntry;
import org.neontoolkit.gui.history.IOWLHistoryEntry;
import org.neontoolkit.gui.navigator.ITreeDataProvider;

/*
 * Created on 02.10.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
/**
 * This is an abstract superclass for all tree elements that represent
 * projects or elements contained in projects.
 */
public abstract class AbstractProjectTreeElement extends TreeElement implements IProjectElement, IAdaptable {
	private String _projectName; 
	/**
	 * 
	 */
	public AbstractProjectTreeElement(String projectName, ITreeDataProvider provider) {
		super(provider);
		_projectName = projectName;
	}

	/*
	 * (non-Javadoc)
	 * @see org.neontoolkit.gui.navigator.elements.IProjectElement#getProjectName()
	 */
    @Override
	public String getProjectName() {
		return _projectName;
	}
	
	/**
	 * Returns the project resource that corresponds to the project name of this tree element.
	 * @return
	 */
	public IProject getProjectResource() {
		return NeOnCorePlugin.getDefault().getProject(_projectName);
	}
	
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        return _projectName;
    }
    
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IProject.class) {
            return NeOnCorePlugin.getDefault().getProject(_projectName);
        }
        return super.getAdapter(adapter);
    }
    /** 
     * has to be overridden in order to be considered by the History 
     * @return OWL History Entry, in case its not overridden, an empty one
     */ 
    public IOWLHistoryEntry getOWLHistoryEntry(){ 
        return new EmptyOWLHistoryEntry(); 
    } 

}
