/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.io;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.io.exception.OntologyImportException;
import org.neontoolkit.io.wizard.AbstractImportSelectionPage;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProject;
import com.ontoprise.ontostudio.owl.model.util.file.UnknownOWLOntologyFormatException;

/**
 * This class provides a wizard for the creation of new ontologies.
 */
public class WebImportOntologyWizard extends FileSystemImportWizard {

    public static final String ID = "com.ontoprise.ontostudio.owl.gui.io.ImportOntologyWizard"; //$NON-NLS-1$
    private WebImportOntologyWizardPage _pageSelection;

    public WebImportOntologyWizard() {
        super();
        setWindowTitle("Import Ontology from the Web"); //$NON-NLS-1$
    }

    @Override
    public AbstractImportSelectionPage getImportSelectionPage() {
    	_pageSelection = new WebImportOntologyWizardPage(getFileFilter());
    	return _pageSelection;
    }

    @Override
    public String getPageDescription() {
    	return "Imports an Ontology from the Web"; //$NON-NLS-1$
    }

    @SuppressWarnings("nls")
    @Override
    public String getPageTitle() {
        return ("Import Ontology from the Web");
    }
 
    @Override
    public boolean performFinish() {
        final String projectName = _pageSelection.getSelectedProject();
        final URI url = _pageSelection.getPhysicalUri();
        IRunnableWithProgress op = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    final IProgressMonitor mon = monitor;
                    mon.beginTask(Messages.FileSystemImportWizard_3, -1);
                            try {
                            	doFinish(projectName, url, mon);
                            } catch (OntologyImportException e) {
                            	NeonToolkitExceptionHandler handler = new NeonToolkitExceptionHandler();
                                handler.handleException(e, e.getCause(), getShell());
                            }
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                } 
            }
        };
        try {
            IWizardContainer container = getContainer();
            container.run(true, true, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            NeonToolkitExceptionHandler handler = new NeonToolkitExceptionHandler();
            handler.handleException((String) null, realException, getShell());
            return false;
        }
        return true;
    }
    
    
    public void doFinish(String projectName, URI physicalURL, IProgressMonitor monitor) throws OntologyImportException {
        try {
            OWLManchesterProject omp = (OWLManchesterProject)NeOnCorePlugin.getDefault().getOntologyProject(projectName);
            // String physicalUri = ImportExportUtils.copyOntologyFileToProject(physicalURL.toString(), omp.getName()).toString();                
            // URI physicalURI = URI.create(physicalUri);
            Set<String> res = omp.importOntologies(new URI[]{physicalURL}, false);
            if (res.size()!=0) omp.setOntologyDirty(res.iterator().next(), true);
		} catch (OWLOntologyCreationException e) {
			throw new OntologyImportException(e);
		} catch (UnknownOWLOntologyFormatException e) {
			throw new OntologyImportException(e);
		} catch (NeOnCoreException e) {
			throw new OntologyImportException(e);
		}	        
    }
    
}
