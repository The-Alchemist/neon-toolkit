/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/
package org.neontoolkit.gui.navigator.ontology;

import java.net.URI;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;

/**
 * @author mda99
 * Created on: Dec 8, 2009
 */
public class RemoteOntologyCheckWizard extends MessageDialog {

    private boolean OKtoSave = true;
    
    public RemoteOntologyCheckWizard(String projectName, String ontologyUri, Shell shell){
        super(shell, "Saving Remote Ontology", null, 
                "The ontology "+ontologyUri+" which you are trying to save is not currently stored in the local workspace (it is probably on the Web). In order to save it, would you like to copy it locally?", 0, new String[] {"Yes", "No"}, 0);
        if (isRemote(projectName, ontologyUri)){
            int res = this.open();
            if (res==0) {
                copyOntologyToWorkspace(projectName, ontologyUri);
            } else OKtoSave = false;
        }
    }
   
    private void copyOntologyToWorkspace(String projectName, String ontologyUri) {
        IOntologyProject ontoProject;
        try {
            ontoProject = NeOnCorePlugin.getDefault().getOntologyProject(projectName);
            IOntologyProject ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(projectName);
            IProject project = ontoProject.getResource();
            String physicalUri = ontologyProject.getPhysicalURIForOntology(ontologyUri);
            IFile file = project.getFile(ontologyProject.getNewOntologyFilenameFromURI(physicalUri, ontologyProject.getDefaultOntologyFileFormatExtension()));
            String targetUri = file.getLocationURI().toString();
            ontoProject.setPhysicalURIForOntology(ontologyUri, targetUri);
        } catch (NeOnCoreException e) {
            OKtoSave = false;
            e.printStackTrace();
        }   
    }

    private boolean isRemote(String projectName, String ontologyUri) {
        IOntologyProject ontoProject;
        try {
            ontoProject = NeOnCorePlugin.getDefault().getOntologyProject(projectName);
        String physicalURI = ontoProject.getPhysicalURIForOntology(ontologyUri);
        IProject project = ontoProject.getResource();
        URI projURI = project.getLocationURI();
        return !physicalURI.startsWith(projURI.toString());
        } catch (NeOnCoreException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean shouldSave(){
        return OKtoSave;
    }
    
    
}
