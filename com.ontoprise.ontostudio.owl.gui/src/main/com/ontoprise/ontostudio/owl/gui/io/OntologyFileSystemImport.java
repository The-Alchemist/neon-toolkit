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

import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.io.command.AbstractOntologyImportCommand;
import org.neontoolkit.io.util.ImportExportUtils;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;

import com.ontoprise.ontostudio.owl.model.OWLManchesterProject;
import com.ontoprise.ontostudio.owl.model.util.file.OWLFileUtilities;
import com.ontoprise.ontostudio.owl.model.util.file.OWLOntologyInfo;

public class OntologyFileSystemImport extends AbstractOntologyImportCommand {
    
	protected static boolean answerDiaolog;

    public OntologyFileSystemImport(String project, String[] physicalUris) {
		super(project, physicalUris);
	}

	public OntologyFileSystemImport(String project, URI[] physicalUris, IProgressMonitor monitor) {
    	super(project, physicalUris, monitor);
	}
		
	
	/*
	 * (non-Javadoc)
	 * @see org.neontoolkit.datamodel.command.LoggedCommand#perform()
	 */
	@Override
	public void perform() throws CommandException {
    	try {    	
    		_ontologyUris = importFileSystem();
    	} catch (Exception e) {
    		throw new CommandException(e);
    	}
	}
	
	@Override
	public String[] getImportedOntologyUris() throws CommandException {
		if(super.getImportedOntologyUris() == null) {
			run();
		}
		return super.getImportedOntologyUris();
	}
	
    private String[] importFileSystem() throws Exception {    	
        _messages = new String[0][0];
                
        List<String> importedOntologyUris = new ArrayList<String>();        
//    	List<String> physicalUrisToImport = new ArrayList<String>();
        List<URI> physicalURIsToImport = new ArrayList<URI>();

        IOntologyProject ontologyProject = null;
        ontologyProject = getOntologyProject();
        String[] uris = getPhysicalUris();
        for (int i = 0; i < uris.length; i++) {
            String physicalUri = uris[i];
            URI physicalURI = URI.create(physicalUri);
            OWLOntologyInfo ontologyInfo = OWLFileUtilities.getOntologyInfo(physicalURI);
            if(ImportExportUtils.isTransformationRequired(getProjectName(), ontologyInfo.getOntologyFormat().toString())) {
                String ontologyFileName = ontologyProject.getNewOntologyFilenameFromURI(physicalUri, ontologyProject.getDefaultOntologyFileFormatExtension());
                URI targetURI = ontologyProject.getResource().getFile(ontologyFileName).getLocationURI();
                OWLFileUtilities.transformOntology(physicalURI, new RDFXMLOntologyFormat(), targetURI);
                physicalURI = targetURI;
            } else if(!ontologyProject.isPersistent()) {
                //if Project is RAM based and no transformation is needed than copy file to workspace 
                physicalUri = ImportExportUtils.copyOntologyFileToProject(physicalUri, getProjectName()).toString();                
                physicalURI = URI.create(physicalUri);
            }                                                    
        	physicalURIsToImport.add(physicalURI);
		}
                    	            
        if(physicalURIsToImport.size() > 0) {
            try {
                importedOntologyUris.addAll(
                        ((OWLManchesterProject) ontologyProject).importOntologies(physicalURIsToImport.toArray(new URI[0]), false, getProgressMonitor()));
            } catch (Exception e) {
                List<String> physicalUrisToImport = new ArrayList<String>();
                for (URI uri: physicalURIsToImport) {
                    physicalUrisToImport.add(uri.toString());
                }
                block:
                {
    	            if(e.getCause() != null && e.getCause() instanceof Exception && e.getCause().getMessage() != null) {
                        if(e.getCause().getMessage().contains("java.lang.InterruptedException")) { //$NON-NLS-1$
                            if(removeFilesCopiedToWorkspace(physicalUrisToImport, getProjectName())) {
//                                removeLogicalUrisCopiedToWorkspace(physicalUrisToImport, getProjectName());
                             }
                            throw new InterruptedException();
                        }
                        if(e.getCause() instanceof UnknownHostException) {
                            e.printStackTrace();
                            OntologyFileSystemImport.answerDiaolog = true;
                            try{
                                //NICO shell can not be created
//                              final IWorkbench workbench = PlatformUI.getWorkbench();
//                              final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//                              final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                                final Shell shell = new Shell(new Display());
                                final String title = "Problem while loading an ontology"; //$NON-NLS-1$
                                final String message =  e.getCause().getMessage() + "\n\n Do you want to continue?"; //$NON-NLS-1$
                                shell.getDisplay().syncExec(new Runnable() {
                                    @Override
                                    public void run() {
                                        OntologyFileSystemImport.answerDiaolog =  MessageDialog.openQuestion(shell, title, message);
                                    }
                                });
                            } catch (RuntimeException e1) {
                                break block;  
                            } finally {
                                if(OntologyFileSystemImport.answerDiaolog)
                                    break block;  
                            }
                        }
    	            }
	               if(removeFilesCopiedToWorkspace(physicalUrisToImport, getProjectName())) {
//	                   removeLogicalUrisCopiedToWorkspace(physicalUrisToImport, getProjectName());
                   }
    	            throw e;
                }
    	    }     		
    	}
        	
		String[] importedOntologyUriStrings = importedOntologyUris.toArray(new String[importedOntologyUris.size()]); 
//		ImportExportUtils.addOntologiesToProject(getProjectName(), importedOntologyUriStrings);
        return importedOntologyUriStrings;
    }
    
//  protected boolean removeLogicalUrisCopiedToWorkspace(List<String> physicalUris, String projectName) throws NeOnCoreException {
//  OntologyManager connection = ((OntologyProject) getOntologyProject()).getOntologyManager(); 
//  if(connection.getOntologyResolver() instanceof DefaultOntologyResolver) {
//      DefaultOntologyResolver resolver = (DefaultOntologyResolver) connection.getOntologyResolver();
//      for (String physicalUri : physicalUris) {
//          if(ImportExportUtils.isFileInWorkspace(projectName, physicalUri)) {
//              Iterator<String> ontoUris = resolver.registeredOntologyURIs();
//              while (ontoUris.hasNext()) {
//                  String ontoUri = (String) ontoUris.next();
//                  if(resolver.getReplacement(ontoUri).equals(physicalUri)) {
//                      resolver.unregisterReplacement(ontoUri);
//                      break;
//                  }
//              }
//          }
//      }
//  }
//  return true;
//}
	
}
