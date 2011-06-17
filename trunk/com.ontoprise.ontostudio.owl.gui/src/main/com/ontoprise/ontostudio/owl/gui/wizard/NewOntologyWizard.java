/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.wizard;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.ontology.CreateValidOntologyUri;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.SharedImages;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.model.commands.ontology.CreateOntology;

/* 
 * Created on: 12.11.2004
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Ontology, Wizard
 */
/**
 * This class provides a wizard for the creation of new ontologies.
 * @author Nico Stieler
 */
public class NewOntologyWizard extends Wizard implements INewWizard {

    protected IStructuredSelection _selectedElement;
    protected NewOntologyWizardPage _page;
    private boolean _fixed;
    private String _fixedProjectSelection;
    private String _fixedOntologyName;

    public NewOntologyWizard() {
        setDefaultPageImageDescriptor(NeOnUIPlugin.getDefault().getImageRegistry().getDescriptor(SharedImages.ONTOLOGY));
        setWindowTitle(Messages.NewOntologyWizard_1); 
        setNeedsProgressMonitor(true);
    }
    
    public static String getId() {
        return "com.ontoprise.ontostudio.owl.gui.wizard.NewOntologyWizard"; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    @Override
	public void addPages() {
        _page = new NewOntologyWizardPage(_selectedElement);
        if(_fixed)
            _page.setFixed(_fixedProjectSelection, _fixedOntologyName);
        addPage(_page);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
	public boolean performFinish() {
        try {
            String projectName = _page.getProjectName();
            String ontologyUri = new CreateValidOntologyUri(_page.getOntologyUri()).getOntologyUri();
            String defaultNs = _page.getDefaultNamespace();
            String filename = OntologyProjectManager.getDefault().getOntologyProject(projectName).getNewOntologyFilenameFromURI(ontologyUri, ".owl"); //$NON-NLS-1$
			new CreateOntology(projectName, ontologyUri, defaultNs, filename).run(); 
			
		} catch (Exception e) {
			new NeonToolkitExceptionHandler().handleException(Messages.NewOntologyWizard_0, e, _page.getShell());
			return false;
		}
		return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     *      org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    	_selectedElement = (IStructuredSelection)workbench.getActiveWorkbenchWindow().getSelectionService().getSelection(NeOnUIPlugin.ID_ONTOLOGY_NAVIGATOR);
    }

    public void setSelection(IStructuredSelection selection) {
        this._selectedElement = selection;
    }
    
    public String createFileName(String projectName, String filenameProposal) {
        String fileName = filenameProposal;
        String fileExtension = ".owl"; //$NON-NLS-1$
        IProject project = NeOnCorePlugin.getDefault().getProject(projectName);
        if(fileName.endsWith(fileExtension)) {
            fileName = fileName.substring(0, fileName.length() - fileExtension.length());
        }
        String fileNumber = ""; //$NON-NLS-1$
        int i = 1;
        try {
            URI uriUri = new URI("module", "", fileName); //$NON-NLS-1$ //$NON-NLS-2$
            fileName = uriUri.getRawFragment();
        } catch (URISyntaxException e1) {
            try {
                fileName = URLEncoder.encode(fileName, "utf-8"); //$NON-NLS-1$
            } catch (UnsupportedEncodingException e2) {
                e1.printStackTrace();
            }
        }
        
        String newFileName = fileName + fileNumber + fileExtension;
        while(project.getFile(newFileName).exists() 
                || existsPhysicalUri(project, newFileName)) {                      
            fileNumber = "_" + i; //$NON-NLS-1$
            i++;
            newFileName = fileName + fileNumber + fileExtension;
        }
        return newFileName;
    }
    
    public boolean existsPhysicalUri(IProject project, String fileName) {
        URI physicalUri = getOntologyUri(project, fileName);
        try {
            for (URI uri: NeOnCorePlugin.getDefault().getOntologyProject(project.getName()).getOntologyFiles()) {
                if (physicalUri.equals(uri)) {
                    return true;
                }
            }
            return false;
        } catch (CoreException e) {
            throw new RuntimeException(e);
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the URI for a given file name within the project
     * @param fileName
     * @return
     */
    private URI getOntologyUri(IProject resource, String fileName) {
        IFile file = resource.getFile(fileName);
        URI uri = file.getLocationURI();
        return uri;
    }

    /**
     * @param fixed
     * @param selectionString
     */
    public void setFixed(boolean fixed, String fixedProjectSelection, String fixedOntologyName) {
        _fixed = fixed;
        _fixedProjectSelection = fixedProjectSelection;
        _fixedOntologyName = fixedOntologyName;
    }
    
}
