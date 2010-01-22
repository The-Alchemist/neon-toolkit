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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.ui.IStartup;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.OntologyProjectManager;

public class RestoreProjects implements IStartup {
    
    private static Set<NeOnCoreException> startupExceptions = new HashSet<NeOnCoreException>();
    
    @Override
    public void earlyStartup() {
            String[] ontologyProjects = OntologyProjectManager.getDefault().getOntologyProjects();
            for (String ontologyProject: ontologyProjects) {
                try {
                    NeOnCorePlugin.getDefault().getOntologyProject(ontologyProject).restoreProject();
                } catch (NeOnCoreException e) {
                    startupExceptions.add(e);
                    // TODO: handle exception
                }
            }
    }
    
    public static Set<NeOnCoreException> getStartupExceptions() {
        return startupExceptions;
    }
    

}
