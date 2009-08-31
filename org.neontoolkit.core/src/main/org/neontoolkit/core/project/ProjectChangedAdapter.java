/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 23.06.2009
 * Created by: diwe
 ******************************************************************************/
package org.neontoolkit.core.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;

/**
 * @author diwe
 *
 */
public class ProjectChangedAdapter implements IResourceChangeListener {

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        IResourceDelta delta = event.getDelta();
        if (delta == null) {
            return;
        }
        IResourceDelta[] res = delta.getAffectedChildren();
        if (res != null && res.length > 0) {
            for (int i = 0; i < res.length; i++) {
                IResource resource = res[i].getResource();
                if (resource instanceof IProject) {
                    if (res[i].getKind() == IResourceDelta.REMOVED) {
                        if (i<res.length-1) {
                            if (res[i+1].getKind() == IResourceDelta.ADDED && res[i+1].getResource() instanceof IProject) {
                                //Might be a rename operation
                                if (res[i].getFlags() == IResourceDelta.MOVED_TO && (res[i+1].getFlags() & IResourceDelta.MOVED_FROM) == IResourceDelta.MOVED_FROM) {
                                    projectRenamed(resource.getName(), res[i+1].getResource().getName());
                                    return;
                                }
                                continue;
                            }
                        }
                        else if (i > 0) {
                            if (res[i-1].getKind() == IResourceDelta.ADDED && res[i-1].getResource() instanceof IProject) {
                                //Might be a rename operation
                                if (res[i].getFlags() == IResourceDelta.MOVED_TO && (res[i-1].getFlags() & IResourceDelta.MOVED_FROM) == IResourceDelta.MOVED_FROM) {
                                    projectRenamed(resource.getName(), res[i-1].getResource().getName());
                                    return;
                                }
                                continue;
                            }
                            
                        }
                        projectRemoved(resource.getName());
                    }
                }
            }
        }
    }
    
    public void projectRenamed(String oldName, String newName) {
        //default implementation does nothing
    }
    
    public void projectRemoved(String projectName) {
        //default implementation does nothing
    }

}
