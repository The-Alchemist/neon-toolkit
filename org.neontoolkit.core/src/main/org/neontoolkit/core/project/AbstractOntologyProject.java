/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 23.03.2009
 * Created by: Mika Maier-Collin
 *             Dirk Wenke
 ******************************************************************************/
package org.neontoolkit.core.project;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ListenerList;
import org.neontoolkit.core.Messages;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.project.RenameProject;
import org.neontoolkit.core.exception.IllegalProjectException;
import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.natures.OntologyProjectNature;

/**
 * @author Dirk Wenke
 *
 */
public abstract class AbstractOntologyProject implements IOntologyProject {
    protected enum EventTypes {
        ONTOLOGY_ADDED, ONTOLOGY_REMOVED, ONTOLOGY_RENAMED, ONTOLOGY_CHANGED, PROJECT_REMOVED, PROJECT_RENAMED;
    }
    /** Executor for waiting on events while reading and dispatching events on the GUI thread. */
    protected static ExecutorService _readAndDispatchExecutor = Executors.newCachedThreadPool();

    //The list of listeners for "modified" notifications
    protected ListenerList _ontologyProjectListeners = new ListenerList();
    
    protected Set<String> _modifiedOntologyUris = new HashSet<String>();
    protected String _projectName;
    protected boolean _suppressEvents = false;
    protected Exception _projectFailure;
    
    public AbstractOntologyProject(String name) {
        _projectName = name;
    }

    @Override
    public void addOntology(String ontologyUri) throws NeOnCoreException {
        try {
            OntologyProjectNature nature = getOntologyProjectNature();
            if (nature != null) {
                // always fire event (we need this for RestoreProjects, 
                // else tree won' t be updated after restore has finished)
                nature.addOntology(ontologyUri);
                fireAddRemoveEvent(EventTypes.ONTOLOGY_ADDED, ontologyUri);
            }
        } catch (CoreException e) {
            throw new IllegalProjectException(Messages.AbstractOntologyProject_0,e);
        }
    }

    @Override
    public void removeOntology(String ontologyUri, boolean removeContent) throws NeOnCoreException {
        try {
            OntologyProjectNature nature = getOntologyProjectNature();
            if (nature != null && nature.removeOntology(ontologyUri)) {
                fireAddRemoveEvent(EventTypes.ONTOLOGY_REMOVED, ontologyUri);
            }
        } catch (CoreException e) {
            throw new IllegalProjectException(Messages.AbstractOntologyProject_0,e);
        }
    }

    @Override
    public void addOntologyProjectListener(IOntologyProjectListener listener) {
        _ontologyProjectListeners.add(listener);
    }
    
    @Override
    public void dispose(boolean removeContent) throws NeOnCoreException{
        try {
            IProject resource = getResource();
            if(resource != null && resource.exists()) {
                Set<String> explicitlyRemovedOntologies = new LinkedHashSet<String>();
                Set<String> remainingOntologies = new LinkedHashSet<String>(Arrays.asList(getOntologies()));
                while (remainingOntologies.size() > 0) {
                    String ontologyURI = remainingOntologies.iterator().next();
                    explicitlyRemovedOntologies.add(ontologyURI);
                    try {
                        removeOntology(ontologyURI, removeContent);
                    } catch (NeOnCoreException e) {
                        //ontology could not be removed, ignore
                    }
                    // note: removing an ontology may remove importing ontologies... maybe not all if we got an exception
                    remainingOntologies = new LinkedHashSet<String>(Arrays.asList(getOntologies()));
                    // avoid explicitly removing ontologies twice... needed to avoid endless loops if removing fails
                    remainingOntologies.removeAll(explicitlyRemovedOntologies);
                }
            }
            getResource().delete(true, true, null);
            fireAddRemoveEvent(EventTypes.PROJECT_REMOVED, getName());
        } catch (CoreException e) {
            throw new InternalNeOnException(e);
        }
    }

    @Override
    public String[] getDirtyOntologies() throws NeOnCoreException {
        return (String[]) _modifiedOntologyUris.toArray(new String[0]);
    }

    @Override
    public String getName() {
        return _projectName;
    }

    @Override
    public String[] getOntologies() throws NeOnCoreException {
        try {
            OntologyProjectNature nature = getOntologyProjectNature();
            if (nature != null) {
                return nature.getOntologies();
            }
        } catch (CoreException ce) {
            throw new IllegalProjectException(Messages.AbstractOntologyProject_0,ce);
        }
        return new String[0];
    }

    @Override
    public IProject getResource() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        return root.getProject(_projectName);
    }

    @Override
    public boolean isImportsVisible(String ontologyUri) {
        try {            
            return getOntologyProjectNature().getShowOntologyImports(ontologyUri);
        } catch (CoreException e) {
            NeOnCorePlugin.getDefault().logError(Messages.AbstractOntologyProject_1+getName(), e);
        }
        return false;
    }

    @Override
    public boolean isOntologyDirty(String ontologyUri) {
        return !isPersistent() && _modifiedOntologyUris.contains(ontologyUri);
    }

    @Override
    public void removeOntologProjectListener(IOntologyProjectListener listener) {
        _ontologyProjectListeners.add(listener);
    }

    @Override
    public void renameProject(String newName) throws NeOnCoreException {
        try {
            String oldName = getName();
            new RenameProject(oldName, newName).run();
            setName(newName);
            fireRenameEvent(EventTypes.PROJECT_RENAMED, oldName, newName);
        } catch (CommandException e) {
            throw new InternalNeOnException(e);
        }                
    }
    
    @Override
    public void setName(String projectName) {
        _projectName = projectName;
    }

    @Override
    public void setImportsVisible(String ontologyUri, boolean isVisible) {
        try {
            getOntologyProjectNature().setShowOntologyImports(ontologyUri, isVisible);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOntologyDirty(String ontologyUri, boolean isDirty) {
        if(_suppressEvents) {
            return;
        }
        boolean currentState = _modifiedOntologyUris.contains(ontologyUri);
        if (currentState != isDirty) {
            boolean isPersistent = false;
            try {
                isPersistent = isPersistent();
            } catch (Exception e) {
                NeOnCorePlugin.getDefault().logError("", e); //$NON-NLS-1$
            }
            if (!isPersistent) {
                if(isDirty) {
                    _modifiedOntologyUris.add(ontologyUri);
                } else {
                    _modifiedOntologyUris.remove(ontologyUri);
                }
            }
            fireOntologyChanged(ontologyUri, isDirty);
        }
    }

    /**
     * Returns the OntologyProjectNature of this project. Returns null if the project 
     * does not exist or does not have the OntologyProjectNature.
     * @return
     * @throws CoreException
     */
    public OntologyProjectNature getOntologyProjectNature() throws CoreException {
        IProject p = getResource();
        if (p != null) {
            return (OntologyProjectNature) p.getNature(OntologyProjectNature.ID);
        }
        return null;
    }

    protected void fireAddRemoveEvent(EventTypes type, String argument) {
        if(_suppressEvents) {
            return;
        }
        Object[] listeners = _ontologyProjectListeners.getListeners();
        for(int i = 0; i < _ontologyProjectListeners.size(); i++) {
            IOntologyProjectListener listener = (IOntologyProjectListener)listeners[i];
            switch (type) {
                case ONTOLOGY_ADDED:
                    listener.ontologyAdded(getName(), argument);
                    break;
                case ONTOLOGY_REMOVED:
                    listener.ontologyRemoved(getName(), argument);
                    break;
                case PROJECT_REMOVED:
                    listener.projectRemoved(argument);
                    break;
                default:
                    break;
            }
        }
    }

    protected void fireRenameEvent(EventTypes type, String oldId, String newId) {
        if(_suppressEvents) {
            return;
        }
        Object[] listeners = _ontologyProjectListeners.getListeners();
        for(int i = 0; i < _ontologyProjectListeners.size(); i++) {
            IOntologyProjectListener listener = (IOntologyProjectListener)listeners[i];
            switch (type) {
                case ONTOLOGY_RENAMED:
                    listener.ontologyRenamed(getName(), oldId, newId);
                    break;
                case PROJECT_RENAMED:
                    listener.projectRenamed(oldId, newId);
                    break;
                default:
                    break;
            }
        }
    }
    
    protected void fireOntologyChanged(String ontologyUri, boolean changed) {
        if(_suppressEvents) {
            return;
        }
        Object[] listeners = _ontologyProjectListeners.getListeners();
        for(int i = 0; i < _ontologyProjectListeners.size(); i++) {
            IOntologyProjectListener listener = (IOntologyProjectListener)listeners[i];
            listener.ontologyModified(getName(), ontologyUri, changed);
        }
    }
    
    protected void fireOntologyStructureChanged(String ontologyUri, boolean changed) {
        if(_suppressEvents) {
            return;
        }
        Object[] listeners = _ontologyProjectListeners.getListeners();
        for(int i = 0; i < _ontologyProjectListeners.size(); i++) {
            IOntologyProjectListener listener = (IOntologyProjectListener)listeners[i];
            listener.ontologyStructureModified(getName(), ontologyUri, changed);
        }
    }
    
    protected void fireFailure(Exception e) {
        if(_suppressEvents) {
            return;
        }
        Object[] listeners = _ontologyProjectListeners.getListeners();
        for(int i = 0; i < _ontologyProjectListeners.size(); i++) {
            IOntologyProjectListener listener = (IOntologyProjectListener)listeners[i];
            if (listener instanceof IProjectFailureListener) {
                ((IProjectFailureListener)listener).projectFailureOccured(getName(), e);
            }
        }
    }

    @Override
    public void setProjectFailure(Exception failure) {
        if (failure == _projectFailure) {
            return;
        }
        _projectFailure = failure;
        NeOnCorePlugin.getDefault().updateMarkers(this);
        fireFailure(failure);
    }
    
    @Override
    public Exception getProjectFailure() {
        return _projectFailure;
    }

    protected abstract boolean hasLegalOntologyExtension(String fileName);
    
    /**
     * returns true if the resource is known as project configuration file
     * @param resource
     * @return
     */
    protected static boolean isInternalResource(IResource resource) {
        String resName = resource.getName();
        if(resource.getType() != IFile.FILE) {
            return true;
        }
        if (OntologyProjectNature.ONTOLOGY_REF_FILENAME.equals(resName)
                || OntologyProjectNature.PROJECT_SETTINGS_FILENAME.equals(resName) 
                || resName.equals(".project") //$NON-NLS-1$
                || resName.endsWith(".rptdesign")) { //$NON-NLS-1$
            return true;
        }
        return false;
    }

    protected static String getNameFromUri(String uri) {
        while (uri.endsWith("#") || uri.endsWith("/")) {  //$NON-NLS-1$//$NON-NLS-2$
            uri = uri.substring(0, uri.length() - 1);
        }
        int lastIndex = uri.lastIndexOf('#');
        uri = uri.substring(lastIndex + 1, uri.length());
        lastIndex = uri.lastIndexOf('/');
        return uri.substring(lastIndex + 1, uri.length());
    }

    @Override
    public URI[] getOntologyFiles() throws CoreException {
        List<URI> uriList = new ArrayList<URI>();
        getResource().refreshLocal(IResource.DEPTH_INFINITE, null);
        IResource[] resources = getResource().members(false);
        for (IResource resource : resources) {
            if(!isInternalResource(resource) && hasLegalOntologyExtension(resource.getName())) {                 
                uriList.add(resource.getLocationURI());
            }
        }
        return uriList.toArray(new URI[0]);
    }
    
    public String getNewOntologyFilenameFromURI(String uri, String fileExtension) {
        return getNewOntologyFilename(getNameFromUri(uri), fileExtension);
    }
    
    public String getNewOntologyFilename(String filenameProposal, String fileExtension) {
        String fileName = filenameProposal;
        IProject project = getResource();
        if(fileExtension == null) {
            fileExtension = getDefaultOntologyFileFormatExtension();
        }
        if(fileName.endsWith(fileExtension)) {
            fileName = fileName.substring(0, fileName.length() - fileExtension.length());
        }
        String fileNumber = ""; //$NON-NLS-1$
        int i = 1;
        try {
            URI uriUri = new URI("scheme", "ssp", fileName); //$NON-NLS-1$ //$NON-NLS-2$
            fileName = uriUri.getRawFragment();
        } catch (URISyntaxException e1) {
            try {
                fileName = URLEncoder.encode(fileName, "utf-8"); //$NON-NLS-1$
            } catch (UnsupportedEncodingException e2) {
                e1.printStackTrace();
            }
        }
        while(project.getFile(fileName + fileNumber + fileExtension).exists() 
                || existsPhysicalUri(fileName + fileNumber + fileExtension)) {                      
            fileNumber = "_" + i; //$NON-NLS-1$
            i++;
        }
        return fileName + fileNumber + fileExtension;
    }
    
    @Override
    public String toString() {
        return getName() + " [" + getOntologyLanguage() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
