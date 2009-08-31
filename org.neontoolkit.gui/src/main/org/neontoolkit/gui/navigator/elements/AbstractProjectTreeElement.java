/**
 * Copyright (c) 2008 ontoprise GmbH.
 */

package org.neontoolkit.gui.navigator.elements;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.neontoolkit.core.NeOnCorePlugin;
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
    
    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IProject.class) {
            return NeOnCorePlugin.getDefault().getProject(_projectName);
        }
        return super.getAdapter(adapter);
    }
}
