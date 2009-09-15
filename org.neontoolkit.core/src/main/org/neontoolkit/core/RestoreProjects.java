/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core;

import org.eclipse.ui.IStartup;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.OntologyProjectManager;

public class RestoreProjects implements IStartup {
    
    @Override
    public void earlyStartup() {
        try {
            String[] ontologyProjects = OntologyProjectManager.getDefault().getOntologyProjects();
            for (String ontologyProject: ontologyProjects) {
                NeOnCorePlugin.getDefault().getOntologyProject(ontologyProject).restoreProject();
            }            
        } catch (NeOnCoreException e) {
            // TODO: handle exception
        }
    }
    

}
