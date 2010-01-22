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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.ontology.ExistsOntology;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.exception.OntologyAlreadyExistsException;
import org.neontoolkit.core.exception.OntologyNotOpenedException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.util.OntologyProjectFilter;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.io.command.AbstractOntologyImportCommand;
import org.neontoolkit.io.exception.OntologyImportException;
import org.neontoolkit.io.util.ImportExportUtils;
import org.neontoolkit.io.wizard.AbstractImportSelectionPage;
import org.neontoolkit.io.wizard.AbstractImportWizard;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.io.filefilters.AnyOWL2OntologyFileFilter;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.commands.ontology.RemoveOntology;

/* 
 * Created on 17.11.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Wizard, Import, FileSystem, Ontology
 */

/**
 * Import Wizard to select an (ontology-)file from the filesystem
 */
public class FileSystemImportWizard extends AbstractImportWizard {

    public String _ontoUri;

    public FileSystemImportWizard() {
        super();
        super.setFileFilter(new AnyOWL2OntologyFileFilter());
        super.setSupportedProjectOntologyLanguage(new OntologyProjectFilter(null, OWLManchesterProjectFactory.FACTORY_ID));
        setWindowTitle(Messages.FileSystemImportWizard_0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.AbstractOntologyImportWizard#getPageDescription()
     */
    @Override
    public String getPageDescription() {
        return Messages.FileSystemImportWizard_1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.AbstractOntologyImportWizard#getPageTitle()
     */
    @Override
    public String getPageTitle() {
        return Messages.FileSystemImportWizard_2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.AbstractImportWizard#getImportSelectionPage()
     */
    @Override
    public AbstractImportSelectionPage getImportSelectionPage() {
        return new FileSystemImportSelectionPage(getFileFilter());
    }
    
    @Override
    public String[] doImport(String project, URI[] physicalURIs, IProgressMonitor monitor) throws OntologyImportException {
    	String[] importedModules = new String[0];
        try {
            AbstractOntologyImportCommand ofsi;
            String selectedFileFormat = _pageSelection == null ? null : _pageSelection.getSelectedFileFormat();
            if (selectedFileFormat != null && physicalURIs.length == 1) {
                String[] fileFormats = new String[physicalURIs.length];
                Arrays.fill(fileFormats, selectedFileFormat);
                ofsi = new OntologyFileSystemImport(project, physicalURIs, monitor);
//                OWLManchesterProject manchesterProject = (OWLManchesterProject) NeOnCorePlugin.getDefault().getOntologyProject(project);
//                importedModules = manchesterProject.importOntologies(physicalURIs, false).toArray(new String[0]);
            } else {
                ofsi = new OntologyFileSystemImport(project, physicalURIs, monitor);
//                OWLManchesterProject manchesterProject = (OWLManchesterProject) NeOnCorePlugin.getDefault().getOntologyProject(project);
//                importedModules = manchesterProject.importOntologies(physicalURIs, false).toArray(new String[0]);
            }
            importedModules = ofsi.getImportedOntologyUris();            
        } catch (CommandException e) {
            throw new OntologyImportException(e.getCause() instanceof InterruptedException ? e.getCause() : e);
        } catch (Exception e) {
            throw new OntologyImportException(e);
		}
        return importedModules;
    }
        

    @Override
    public boolean performFinish() {
        final String projectName = _pageSelection.getSelectedProject();
        final URL[] urls = _pageSelection.getSelectedURLS();
        IRunnableWithProgress op = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    final IProgressMonitor mon = monitor;
                    mon.beginTask(Messages.FileSystemImportWizard_3, -1);
                        if (urls.length > 0) {
                            try {
                            	doFinish(projectName, urls, mon);
                            } catch (OntologyImportException e) {
                            	NeonToolkitExceptionHandler handler = new NeonToolkitExceptionHandler();
                                handler.handleException(e, e.getCause(), getShell());
                            }
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
    
@Override
    public String getOntologyUri(URL location, String projectName) throws NeOnCoreException {
    	try {
			return ImportExportUtils.getOntologyUri(location.toURI().toString(), projectName);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
    }

    @Override
    public void doFinish(String projectName, URL[] physicalURLs, IProgressMonitor monitor) throws OntologyImportException {
        try {
            _messages = new String[0][0];
            this._projectName = projectName;
            
            List<URI> physicalURIsToImport = new ArrayList<URI>();
            List<String> ontologyUrisToImport = new ArrayList<String>();
            List<String> ontologyUrisToMerge = new ArrayList<String>();
            
            for (URL physicalURL : physicalURLs) {
                URI physicalURI = physicalURL.toURI();
                String ontologyUri = getOntologyUri(projectName, physicalURI);
                //checkForMerge
                try{
                    ImportExportUtils.checkOntology(projectName, ontologyUri);
                } catch (OntologyAlreadyExistsException maee) {
                    if(askForMergeModules(maee.getMessage(), Messages.AbstractImportWizard_9)) {
                        ontologyUrisToMerge.add(ontologyUri);
                    } else {
                        continue;
                    }
                } catch (OntologyNotOpenedException mnoe) {
                    //Message that ontology has to be added to the project first!
                    String message = Messages.AbstractImportWizard_23 
                        + " '"  + ontologyUri + "' " +Messages.AbstractImportWizard_24; //$NON-NLS-1$ //$NON-NLS-2$
                    if(askForMergeModules(message, Messages.AbstractImportWizard_25)) {
//                        OntologyProject ontologyProject = (OntologyProject) NeOnCorePlugin.getDefault().getOntologyProject(projectName);
//                        ontologyProject.getOntologyManager().openOntology(ontologyUri, null, pl);
//                        ImportExportUtils.addOntologiesToProject(projectName, new String[]{ontologyUri});
//                        ontologyUrisToMerge.add(ontologyUri);
                    } else {
                        continue;
                    }
                }           
                physicalURIsToImport.add(physicalURI);
                ontologyUrisToImport.add(ontologyUri);
            }
          
            _onto = null;
            try {
                if(physicalURIsToImport.size() > 0) {
                    _onto = doImport(projectName, physicalURIsToImport.toArray(new URI[0]), monitor);
                    
                }
            } catch (OntologyImportException ex) {
                if(ex.getCause() instanceof InterruptedException) {
                    //delete canceled module when persistent storage and the module didn't exist before
                    IOntologyProject ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(projectName);
                    if(ontologyProject.isPersistent()) {
                        for (String ontologyUriToRemove : ontologyUrisToImport) {
                            if(!ontologyUrisToMerge.contains(ontologyUriToRemove)) {                        
                                if(monitor != null) {
                                    monitor.setTaskName(Messages.AbstractImportWizard_22);
                                }
                                //don't need to remove ontology?!
                                new RemoveOntology(projectName, ontologyUriToRemove, true).run();
                            }
                        }
                    }
                    String removedOntoUris = ""; //$NON-NLS-1$
                    String removedPhysUris = ""; //$NON-NLS-1$
                    for (int i = 0; i < ontologyUrisToImport.size(); i++) {
                        String ontoUri = ontologyUrisToImport.get(i);
                        if(!ontologyUrisToMerge.contains(ontoUri)) {
                            String physUri = physicalURIsToImport.get(i).toString();
                            removedOntoUris += ontoUri;
                            removedPhysUris += physUri;
                        }                           
                    }
                    //Message that canceled
                    String title = Messages.AbstractImportWizard_10;
                    String message = ""; //$NON-NLS-1$
                    if(removedOntoUris.length() > 0) {
                        message = 
                            Messages.AbstractImportWizard_14
                        + " '" + removedOntoUris + "' " +  //$NON-NLS-1$ //$NON-NLS-2$
                        Messages.AbstractImportWizard_15
                        + " '" + removedPhysUris + "' " +  //$NON-NLS-1$ //$NON-NLS-2$
                        Messages.AbstractImportWizard_16;
                    }
                    ShowInfo(title, message);
                    //TODO info for merged ontologies
        //              } else if (isMerged){
        //                  //Message that incomplete import
        //                  String title = Messages.getString("AbstractImportWizard.10"); //$NON-NLS-1$
        //                  String message = 
        //                      Messages.getString("AbstractImportWizard.11") //$NON-NLS-1$
        //                      + " " + moduleToImport + " " +  //$NON-NLS-1$ //$NON-NLS-2$
        //                      Messages.getString("AbstractImportWizard.12"); //$NON-NLS-1$
        //                  ShowInfo(title, message);
        //              }
                } else {
                    throw ex;
                }
            } finally {
                for (String ontologyUri : ontologyUrisToMerge) {
                    if(new ExistsOntology(projectName, ontologyUri).exists()) {
                        ImportExportUtils.setOntologyDirty(projectName, ontologyUri, true);
                    }
                }
//                final RefactoringStatus rs = IOInternalUtils.getErrorStatus(getMessages());
//                if(rs.hasEntries()) {
//                    getShell().getDisplay().syncExec(new Runnable() {
//                        public void run() {
//                            TransformationStatusDialog rsd = new TransformationStatusDialog(rs, "Ontology Import Info", getShell());
//                            rsd.open();
//                        }
//                    });
//                }
        
            }
          } catch (Throwable e) {
            if(e instanceof OntologyImportException) {
                throw (OntologyImportException) e;
            }
              throw new OntologyImportException(e);
          }
    }
    


    
}
