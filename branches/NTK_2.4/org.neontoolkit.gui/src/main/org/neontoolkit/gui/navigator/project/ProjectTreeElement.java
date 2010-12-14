/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator.project;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.elements.AbstractProjectTreeElement;

/* 
 * Created on: 07.04.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, TreeDataProvider, extendableTreeProvider, Navigator
 */
/**
 * TreeElement used for the projects shown in the tree. This class is intended
 * to be subclassed. But actions may e.g. refer to this class to contribute
 * actions to all types of projects.
 */
public abstract class ProjectTreeElement extends AbstractProjectTreeElement {
    /**
     * @param provider
     */
    public ProjectTreeElement(String projectName, ITreeDataProvider provider) {
        super(projectName, provider);
    }
    
    /* (non-Javadoc)
     * @see org.neontoolkit.gui.navigator.elements.TreeElement#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o) && equal(getProjectName(), ((ProjectTreeElement)o).getProjectName());
    }
    
    /* (non-Javadoc)
     * @see org.neontoolkit.gui.navigator.elements.TreeElement#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() + String.valueOf(getProjectName()).hashCode();
    }
    
    public String getOntologyProjectClassName() {
        try {
            IOntologyProject ontoProject = OntologyProjectManager.getDefault().getOntologyProject(getProjectName());
            if (ontoProject != null) {
                return ontoProject.getClass().getName();
            }
        } catch (NeOnCoreException nce) {
            //Nothing to do
        }
        return null;
    }
}
