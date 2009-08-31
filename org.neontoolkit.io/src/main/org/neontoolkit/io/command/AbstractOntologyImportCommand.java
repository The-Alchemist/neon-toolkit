package org.neontoolkit.io.command;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.DatamodelCommand;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.io.util.ImportExportUtils;


/**
 * imports the ontologies placed at the given physical uris into a project
 * if the project's ontology language doesn't support the ontology file format a transformation is done
 * if the ontology file format is the default format for the project's ontology language it is copied
 * into the project's workspace before loading
 * otherwise the ontology is loaded first and than saved to the project's workspace
 *
 */

public abstract class AbstractOntologyImportCommand extends DatamodelCommand {
	
	protected String[][] _messages = new String[0][0];
	protected String[] _ontologyUris;
	private IProgressMonitor _monitor = new NullProgressMonitor();

	public AbstractOntologyImportCommand(String project, String[] physicalUris) {
		super(project, (Object) physicalUris);
	}

	public AbstractOntologyImportCommand(String project, URI[] physicalUris, IProgressMonitor monitor) {
    	this(project, getURIStrings(physicalUris));
		_monitor = monitor;
	}
	
	public String[] getPhysicalUris() {
		return (String[]) getArgument(1);
	}
	
	public IProgressMonitor getProgressMonitor() {
		return _monitor;
	}

    public void setProgressMonitor(IProgressMonitor monitor) {
        _monitor = monitor;
    }

	public String[] getImportedOntologyUris() throws CommandException {
		if(_ontologyUris == null) {
			run();
		}
		return _ontologyUris;
	}
	
    public String[][] getMessages() {
    	return _messages;
    }

	
	private static String[] getURIStrings(URI[] URIs) {
    	String[] Uris = new String[URIs.length];
    	for (int i = 0; i < URIs.length; i++) {
    		Uris[i] = URIs[i].toString();			
		}
    	return Uris;
	}
		
		
    protected static boolean removeFilesCopiedToWorkspace(List<String> physicalUris, String projectName) throws NeOnCoreException {
    	for (String physicalUri : physicalUris) {
			if(ImportExportUtils.isFileInWorkspace(projectName, physicalUri)) {
				File file = new File(physicalUri);
		        IProject project = NeOnCorePlugin.getDefault().getProject(projectName);
		        IFile iFile = project.getFile(file.getName());
		        try {
					iFile.delete(true, null);
				} catch (CoreException e) {
					return false;
				}
			}
		}
    	return true;
    }
    

//    protected boolean removeLogicalUrisCopiedToWorkspace(List<String> physicalUris, String projectName) throws NeOnCoreException {
//		OntologyManager connection = ((OntologyProject) getOntologyProject()).getOntologyManager(); 
//    	if(connection.getOntologyResolver() instanceof DefaultOntologyResolver) {
//	        DefaultOntologyResolver resolver = (DefaultOntologyResolver) connection.getOntologyResolver();
//	    	for (String physicalUri : physicalUris) {
//				if(ImportExportUtils.isFileInWorkspace(projectName, physicalUri)) {
//					Iterator<String> ontoUris = resolver.registeredOntologyURIs();
//					while (ontoUris.hasNext()) {
//						String ontoUri = (String) ontoUris.next();
//						if(resolver.getReplacement(ontoUri).equals(physicalUri)) {
//							resolver.unregisterReplacement(ontoUri);
//							break;
//						}
//					}
//				}
//			}
//    	}
//    	return true;
//    }
        
}
