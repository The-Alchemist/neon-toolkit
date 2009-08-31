package org.neontoolkit.io.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;

public class ImportExportUtils {

	
    public static String getOntologyFileFormat(String projectName, URI physicalURI) throws CommandException {
    	return getOntologyFileFormat(projectName, physicalURI.toString());
    }
    
    public static String getOntologyFileFormat(String projectName, String physicalUri) throws CommandException {
//    	String fileExtension = getFileExtension(physicalUri);
    	String ontologyFormat = getOntologyFormatByFileExtension(physicalUri);
    	if(ontologyFormat == null) {
    	  //if file extension is not known throw an exception
    	  throw new CommandException("File extension not supported"); //$NON-NLS-1$
    	}
		return ontologyFormat;
    }
    
    
    public static String getOntologyFormatByFileExtension(String physicalUri) {
    	String extension = getFileExtension(physicalUri);
    	return getFormatForFileExtension(extension);
    }
    
    public static String getFileExtension(String physicalUri) {
		String extension = ""; //$NON-NLS-1$
        if(physicalUri.contains(".")) { //$NON-NLS-1$
        	extension = physicalUri.substring(physicalUri.lastIndexOf(".") + 1); //$NON-NLS-1$
        }
        return extension;
    }

    public static String getFormatForFileExtension(String extension) {
        return ""; //$NON-NLS-1$
    }
    
    private static List<String> getSupportedOntologyFileFormats(String projectName) throws NeOnCoreException {
        return getOntologyProject(projectName).getSupportedOntologyFileFormats();
    }
    
    public static boolean isTransformationRequired(String projectName, String ontologyFileFormat) throws CommandException {
    	try {
			return(!getSupportedOntologyFileFormats(projectName).contains(ontologyFileFormat));
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
		}
    }
                
    private static IOntologyProject getOntologyProject(String projectName) throws NeOnCoreException {
		return NeOnCorePlugin.getDefault().getOntologyProject(projectName);
    }
    
    public static void checkOntology(String project, String ontologyUri) throws NeOnCoreException {        
        if (Arrays.asList(getOntologyProject(project).getOntologies()).contains(ontologyUri)) {
            throw new InternalNeOnException("Ontology already exists: " + ontologyUri); //$NON-NLS-1$
        }
        if (getOntologyProject(project).getAvailableOntologyURIs().contains(ontologyUri)) {
            throw new InternalNeOnException("Ontology not opened:" + ontologyUri); //$NON-NLS-1$
        } 
    }

    public static void setOntologyDirty(String projectName, String ontologyUri, boolean dirty) throws NeOnCoreException {
        getOntologyProject(projectName).setOntologyDirty(ontologyUri, dirty);
    }

    public static String getOntologyUri(String physicalUri, String projectName) throws NeOnCoreException {
        return getOntologyProject(projectName).retrieveOntologyUri(physicalUri);
        
    }
    
    public static String getOntologyUri(String projectName, String physicalUri, List<String> ontologyFileFormats) {
        return physicalUri;
    }


    public static void addOntologiesToProject(String projectName, String[] ontologyUris) throws CommandException {
    	try {
    		IOntologyProject ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(projectName);
    		for (String ontologyUri : ontologyUris) {
    			ontologyProject.addOntology(ontologyUri);			
			}
		} catch (NeOnCoreException e) {
    		throw new CommandException(e);
		}
    }
    
	public static boolean isFileInWorkspace(String projectName, String physicalUri) throws NeOnCoreException {
        IProject project = NeOnCorePlugin.getDefault().getOntologyProject(projectName).getResource();
        URI projectUri = project.getLocationURI();
        return physicalUri.startsWith(projectUri.toString());
	}

    public static boolean copyFile(File source, File target) throws java.io.FileNotFoundException, java.io.IOException {
        if (!(source.isFile() && target.isFile()))
            throw new FileNotFoundException("source: " + source + "\ntarget: " + target); //$NON-NLS-1$ //$NON-NLS-2$
        byte[] buffer = new byte[4096];
        FileInputStream fis = new FileInputStream(source);
        FileOutputStream fos = new FileOutputStream(target);
        int nBytes = 0;
        while ((nBytes = fis.read(buffer)) != -1) {
            fos.write(buffer, 0, nBytes);
        }
        fis.close();
        fos.close();
        return true;
    }
    
	public static String copyOntologyFileToProject(String physicalUri, String projectName) {
		try {	        
	    	String targetUri = physicalUri;
	        if(!isFileInWorkspace(projectName, physicalUri.toString())){
	    		//ontology doesn't come from the project
	    		//ontology has to be copied to the project workspace
		        try {
                    IOntologyProject ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(projectName);
                    IProject project = getOntologyProject(projectName).getResource();
                    IFile file = project.getFile(ontologyProject.getNewOntologyFilenameFromURI(physicalUri, ontologyProject.getDefaultOntologyFileFormatExtension()));
			        targetUri = file.getLocationURI().toString();
			        URL url = URI.create(physicalUri).toURL();
			        InputStream is = url.openStream();
		        	file.create(is, true, null);
		        	is.close();
				} catch (FileNotFoundException e) {
					new NeonToolkitExceptionHandler().handleException(e);
				} catch (IOException e) {
					new NeonToolkitExceptionHandler().handleException(e);
				}
	    	}
	        return targetUri;
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (NeOnCoreException e) {
			e.printStackTrace();
		}
		return physicalUri;
	}

    public static String getProjectOntologyLanguage(String project) throws NeOnCoreException {
        return NeOnCorePlugin.getDefault().getOntologyProject(project).getOntologyLanguage();
    }

    public static String getFileFormatOntologyLanguage(String ontologyFileFormat) {
        return ontologyFileFormat.replaceFirst(".", ""); //$NON-NLS-1$ //$NON-NLS-2$
    }       

}
