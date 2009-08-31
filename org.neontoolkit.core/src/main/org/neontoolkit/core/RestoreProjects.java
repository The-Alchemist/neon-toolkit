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
