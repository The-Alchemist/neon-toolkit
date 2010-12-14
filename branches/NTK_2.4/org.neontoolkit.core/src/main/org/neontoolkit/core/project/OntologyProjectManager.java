/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core.project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.neontoolkit.core.IOntologyProjectFactory;
import org.neontoolkit.core.Messages;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.natures.OntologyProjectNature;
import org.neontoolkit.core.project.AbstractOntologyProject.EventTypes;

/**
 * @author Dirk Wenke
 *
 */
public class OntologyProjectManager {
    public static final String ONTOLOGY_PROJECT_ALL = "AllOntologyProjectTypes"; //$NON-NLS-1$

    private static OntologyProjectManager _singleton;
    // A map to store the already created ontology projects with the project name.
    private Map<String, IOntologyProject> _ontologyProjects;
    //The list of listeners for ontology project notifications (ontology modified, project removed)
    private Map<String, ListenerList> _ontologyProjectListenersByLanguage = new HashMap<String,ListenerList>();
    private Map<String, ListenerList> _ontologyProjectListenersById = new HashMap<String,ListenerList>();
    private Map<String, ListenerList> _primaryOntologyProjectListenersById = new HashMap<String,ListenerList>();
    
    private class InternalOntologyProjectListener implements IOntologyProjectListener, IProjectFailureListener {
        @Override
        public void projectRenamed(String oldProjectName, String newProjectName) {
            //wait for the resource change event. Handle the rename event if the resource
            //has changed and sent the event after the update operations.
        }
        
        @Override
        public void projectRemoved(String projectName) {
            //wait for the resource change event. Handle the rename event if the resource
            //has changed and sent the event after the update operations.
        }

        @Override
        public void ontologyAdded(String projectName, String ontologyUri) {
            fireAddRemoveEvent(projectName, EventTypes.ONTOLOGY_ADDED, ontologyUri);
        }

        @Override
        public void ontologyModified(String projectName, String ontologyUri, boolean modified) {
            fireOntologyChanged(projectName, ontologyUri, modified);
        }

        @Override
        public void ontologyRemoved(String projectName, String ontologyUri) {
            fireAddRemoveEvent(projectName, EventTypes.ONTOLOGY_REMOVED, ontologyUri);
        }

        @Override
        public void ontologyRenamed(String projectName, String oldOntologyUri, String newOntologyUri) {
            fireRenameEvent(projectName, EventTypes.ONTOLOGY_RENAMED, oldOntologyUri, newOntologyUri);
        }

        @Override
        public void projectAdded(String projectName) {
            // will not be sent by OntologyProject itself
        }
        
        @Override
        public void projectFailureOccured(String project, Exception exception) {
            fireFailure(project, exception);
        }

        @Override
        public void ontologyStructureModified(String projectName, String ontologyUri, boolean modified) {
            fireOntologyStructureChanged(projectName, ontologyUri, modified);
        }
    }
    
    private IResourceChangeListener _resourceListener = new ProjectChangedAdapter() {
        @Override
        public void projectRemoved(String projectName) {
            internalRemoveOntologyProject(projectName);
        }
        
        @Override
        public void projectRenamed(String oldName, String newName) {
            internalRenameOntologyProject(oldName, newName);
        }
    };
    
    private InternalOntologyProjectListener _projectListener = new InternalOntologyProjectListener();

    private OntologyProjectManager() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(_resourceListener);
    }
    
    private void initProjectMap() {
        _ontologyProjects = new HashMap<String, IOntologyProject>();
        try {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IProject[] projects = root.getProjects();
            for (int i = 0; i < projects.length; i++) {
                getOntologyProjectInternal(projects[i].getName(), false);
            }
        } catch (NeOnCoreException e) {
            NeOnCorePlugin.getDefault().logError(e);
        }

    }
    
    public static OntologyProjectManager getDefault() {
        if (_singleton == null) {
            _singleton = new OntologyProjectManager();
        }
        return _singleton;
    }

    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(_resourceListener);
        removeOntologyProjectListener(_projectListener);
        _singleton = null;
    }

    public void addPrimaryOntologyProjectListenerById(IOntologyProjectListener listener, String projectFactoryId) {
        ListenerList listeners = _primaryOntologyProjectListenersById.get(projectFactoryId);
        if (listeners == null) {
            listeners = new ListenerList();
            _primaryOntologyProjectListenersById.put(projectFactoryId, listeners);
        }
        listeners.add(listener);
    }

    public void addOntologyProjectListenerById(IOntologyProjectListener listener, String projectFactoryId) {
        ListenerList listeners = _ontologyProjectListenersById.get(projectFactoryId);
        if (listeners == null) {
            listeners = new ListenerList();
            _ontologyProjectListenersById.put(projectFactoryId, listeners);
        }
        listeners.add(listener);
    }

    public void addOntologyProjectListenerByLanguage(IOntologyProjectListener listener, String ontologyLanguage) {
        ListenerList listeners = _ontologyProjectListenersByLanguage.get(ontologyLanguage);
        if (listeners == null) {
            listeners = new ListenerList();
            _ontologyProjectListenersByLanguage.put(ontologyLanguage, listeners);
        }
        listeners.add(listener);
    }

    private Set<IOntologyProjectListener> getOntologyProjectListeners(String projectName) {
        IOntologyProject project = _ontologyProjects.get(projectName);
        if (project != null) {
            return getOntologyProjectListeners(project.getProjectFactoryId(), project.getOntologyLanguage());
        }
        return Collections.emptySet();
    }
    
    private Set<IOntologyProjectListener> getOntologyProjectListeners(String factoryId, String ontologyLanguage) {
        Set<IOntologyProjectListener> ontologyProjectListeners = new LinkedHashSet<IOntologyProjectListener>();
        
        Object[] listeners = _primaryOntologyProjectListenersById.get(factoryId) != null ?_primaryOntologyProjectListenersById.get(factoryId).getListeners() : new Object[0];
        for (Object ontologyProjectListener: listeners) {
            ontologyProjectListeners.add((IOntologyProjectListener) ontologyProjectListener);
        }
        Object[] ontologyProjectListenersAll = _primaryOntologyProjectListenersById.get(ONTOLOGY_PROJECT_ALL) != null ?_primaryOntologyProjectListenersById.get(ONTOLOGY_PROJECT_ALL).getListeners() : new Object[0];        
        for (Object ontologyProjectListener: ontologyProjectListenersAll) {
            ontologyProjectListeners.add((IOntologyProjectListener) ontologyProjectListener);
        }

        listeners = _ontologyProjectListenersById.get(factoryId) != null ?_ontologyProjectListenersById.get(factoryId).getListeners() : new Object[0];
        for (Object ontologyProjectListener: listeners) {
            ontologyProjectListeners.add((IOntologyProjectListener) ontologyProjectListener);
        }
        ontologyProjectListenersAll = _ontologyProjectListenersById.get(ONTOLOGY_PROJECT_ALL) != null ?_ontologyProjectListenersById.get(ONTOLOGY_PROJECT_ALL).getListeners() : new Object[0];        
        for (Object ontologyProjectListener: ontologyProjectListenersAll) {
            ontologyProjectListeners.add((IOntologyProjectListener) ontologyProjectListener);
        }
        listeners = _ontologyProjectListenersByLanguage.get(ontologyLanguage) != null ?_ontologyProjectListenersByLanguage.get(ontologyLanguage).getListeners() : new Object[0];
        for (Object ontologyProjectListener: listeners) {
            ontologyProjectListeners.add((IOntologyProjectListener) ontologyProjectListener);
        }
        ontologyProjectListenersAll = _ontologyProjectListenersByLanguage.get(ONTOLOGY_PROJECT_ALL) != null ?_ontologyProjectListenersByLanguage.get(ONTOLOGY_PROJECT_ALL).getListeners() : new Object[0];        
        for (Object ontologyProjectListener: ontologyProjectListenersAll) {
            ontologyProjectListeners.add((IOntologyProjectListener) ontologyProjectListener);
        }
        return ontologyProjectListeners;
    }

    public void removeOntologyProjectListener(IOntologyProjectListener listener) {
        for (Iterator<ListenerList> it = _ontologyProjectListenersById.values().iterator(); it.hasNext();) {
            it.next().remove(listener);
        }
        for (Iterator<ListenerList> it = _ontologyProjectListenersByLanguage.values().iterator(); it.hasNext();) {
            it.next().remove(listener);
        }
        for (Iterator<ListenerList> it = _primaryOntologyProjectListenersById.values().iterator(); it.hasNext();) {
            it.next().remove(listener);
        }
    }       

    public IOntologyProject createOntologyProject(String projectName, String projectFactoryId, Properties configProperties) throws NeOnCoreException {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();

        IProject projectHandle = root.getProject(projectName);
        if (projectHandle.exists()) {
            throwCoreException(getClass().getName(), Messages.NeOnCorePlugin_1 + projectName , null);
        }
        try {
            IProjectDescription description = workspace.newProjectDescription(projectName);
            description.setNatureIds(new String[] {OntologyProjectNature.ID});
            projectHandle.create(description, null);
            projectHandle.open(null);
    
            description = projectHandle.getDescription();
            description.setNatureIds(new String[] {OntologyProjectNature.ID});
            projectHandle.setDescription(description, null);

            storeSettings(projectHandle, configProperties);
    
            OntologyProjectNature nature = (OntologyProjectNature) projectHandle.getNature(OntologyProjectNature.ID);
            nature.setProjectFactoryId(projectFactoryId);
    
            IOntologyProject ontologyProject = getOntologyProject(projectName);
            return ontologyProject;
        } catch (CoreException ce) {
            throw new InternalNeOnException(ce);
        }
    }

    /**
     * Returns the ontology project instance for the project with the given name.
     * If the IontologyProject for this project has not yet been created, it is
     * created.
     * @param projectName
     * @return
     * @throws NeOnCoreException
     */
    public IOntologyProject getOntologyProject(String projectName) throws NeOnCoreException {
        if (_ontologyProjects == null) {
            initProjectMap();
        }
        return getOntologyProjectInternal(projectName, true);
    }
    
    private IOntologyProject getOntologyProjectInternal(String projectName, boolean sendEvents) throws NeOnCoreException {
        IOntologyProject ontologyProject = _ontologyProjects.get(projectName);
        if (ontologyProject == null) {
            synchronized (this) {
                ontologyProject = _ontologyProjects.get(projectName);
                if (ontologyProject == null) {
                    try {
                        IProject project = NeOnCorePlugin.getDefault().getProject(projectName);
                        if (project != null && project.isOpen()) {
                            if (project.hasNature(OntologyProjectNature.PREVIOUS_ID) || 
                                    project.hasNature(OntologyProjectNature.PREVIOUS_ID2)) {
                                //for legacy reasons, old nature ids must be supported
                                IProjectDescription description = project.getDescription();
                                String[] natures = description.getNatureIds();
                                List<String> convertedNatures = new ArrayList<String>();
                                for (String nature:natures) {
                                    if (nature.equals(OntologyProjectNature.PREVIOUS_ID) || 
                                            nature.equals(OntologyProjectNature.PREVIOUS_ID2)) {
                                        convertedNatures.add(OntologyProjectNature.ID);
                                    }
                                    else {
                                        convertedNatures.add(nature);
                                    }
                                }
                                description.setNatureIds(convertedNatures.toArray(new String[0]));
                                project.setDescription(description, new NullProgressMonitor());
                            }
                            if (project.hasNature(OntologyProjectNature.ID)) {
                                OntologyProjectNature nature = (OntologyProjectNature)project.getNature(OntologyProjectNature.ID);
                                String id = nature.getProjectFactoryId();
                                if (id == null) {
                                    return null;
                                }
                                
                                IOntologyProjectFactory factory = NeOnCorePlugin.getDefault().getOntologyProjectFactory(id);
                                if (factory == null) {
                                    return null;
                                }
                                ontologyProject = factory.createOntologyProject(project);
                                _ontologyProjects.put(projectName, ontologyProject);

                                ontologyProject.addOntologyProjectListener(_projectListener);

                                ontologyProject.init();

                                if (sendEvents) {
                                    for (IOntologyProjectListener listener: getOntologyProjectListeners(projectName)) {
                                        listener.projectAdded(projectName);
                                    }
                                }
                            }
                        }
                    } catch (CoreException ce) {
                        throw new InternalNeOnException(ce);
                    }
                }
            }
        }
        return ontologyProject;
    }
    
    public boolean existsOntologyProject(String projectName) {
        return _ontologyProjects.get(projectName) != null;
    }

    /**
     * Returns the name of all projects in the current workspace that have the
     * ontology project nature.
     * @return
     * @throws NeOnCoreException
     */
    public String[] getOntologyProjects() {
        if (_ontologyProjects == null) {
            initProjectMap();
        }
        return _ontologyProjects.keySet().toArray(new String[0]);
    }

    /**
     * Returns the name of all projects in the current workspace that have the
     * ontology project nature and the given ontology language.
     * @return
     * @throws NeOnCoreException
     */
    public String[] getOntologyProjects(String ontologyLanguage) throws NeOnCoreException {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject[] projects = root.getProjects();
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < projects.length; i++) {
            IOntologyProject project = getOntologyProject(projects[i].getName());
            if (project != null && ontologyLanguage.equals(project.getOntologyLanguage())) {
                list.add(project.getName());
            }
        }
        return list.toArray(new String[0]);
    }

    public void setSettings(String project, Properties properties, IProgressMonitor monitor) throws NeOnCoreException {
        _ontologyProjects.remove(project);
        storeSettings(NeOnCorePlugin.getDefault().getProject(project), properties);
        IOntologyProject ontoProject = getOntologyProject(project);
        if (project != null) {
            ontoProject.restoreProject();
        }
    }

    private void storeSettings(IProject projectHandle, Properties properties) throws NeOnCoreException {
        try {
            IFile file = projectHandle.getFile(OntologyProjectNature.PROJECT_SETTINGS_FILENAME); 
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            properties.store(os, Messages.NeOnCorePlugin_2); 
            if (file.exists()) {
                file.setContents(new ByteArrayInputStream(os.toByteArray()), IFile.FORCE, null);
            }
            else {
                file.create(new ByteArrayInputStream(os.toByteArray()), true, null);
            }
        } catch (FileNotFoundException e) {
            throwCoreException(getClass().getName(), "Error accessing the project setting file.", e); //$NON-NLS-1$
        } catch (IOException e) {
            throwCoreException(getClass().getName(), "Error accessing the project setting file.", e); //$NON-NLS-1$
        } catch (CoreException e) {
            throwCoreException(getClass().getName(), "Error accessing the project setting file.", e); //$NON-NLS-1$
        }
    }
    
    public Properties getSettings(String projectName) {
        IProject project = NeOnCorePlugin.getDefault().getProject(projectName);
        if (project != null && project.exists()) {
            IFile settingsFile = project.getFile(OntologyProjectNature.PROJECT_SETTINGS_FILENAME);
            if (settingsFile != null && settingsFile.exists()) {
                try {
                    if(!settingsFile.isSynchronized(0)) {
                        settingsFile.refreshLocal(0, null);
                    }
                    Properties properties = new Properties();
                    InputStream stream = settingsFile.getContents();
                    properties.load(stream);
                    stream.close();
                    return properties;
                } catch (CoreException ce) {
                    NeOnCorePlugin.getDefault().logError("Error accessing settings file for project " + projectName, ce); //$NON-NLS-1$
                } catch (IOException ioe) {
                    NeOnCorePlugin.getDefault().logError("Error accessing settings file for project " + projectName, ioe); //$NON-NLS-1$
                }
            }
        }
        return null;
    }

    private void throwCoreException(String className, String message, Exception exception) throws NeOnCoreException {
        throw new InternalNeOnException(message);
    }
    
    private void internalRemoveOntologyProject(String project) {
        IOntologyProject ontoProject = _ontologyProjects.get(project);
        if (ontoProject != null) {
            String factoryId = ontoProject.getProjectFactoryId();
            String ontologyLanguage = ontoProject.getOntologyLanguage();
            _ontologyProjects.remove(project);
            fireProjectRemoved(project, factoryId, ontologyLanguage);
        }
    }

    private void internalRenameOntologyProject(String oldName, String newName) {
        IOntologyProject project = _ontologyProjects.remove(oldName);
        if (project != null) {
            _ontologyProjects.put(newName, project);
            project.setName(newName);
            fireRenameEvent(newName, EventTypes.PROJECT_RENAMED, oldName, newName);
        }
    }

    private void fireAddRemoveEvent(String project, EventTypes type, String argument) {
        Set<IOntologyProjectListener> listeners = getOntologyProjectListeners(project);
        for(IOntologyProjectListener listener: listeners) {
            switch (type) {
                case ONTOLOGY_ADDED:
                    listener.ontologyAdded(project, argument);
                    break;
                case ONTOLOGY_REMOVED:
                    listener.ontologyRemoved(project, argument);
                    break;
                default:
                    break;
            }
        }
    }

    private void fireRenameEvent(String project, EventTypes type, String oldId, String newId) {
        Set<IOntologyProjectListener> listeners = getOntologyProjectListeners(project);
        for(IOntologyProjectListener listener: listeners) {
            switch (type) {
                case ONTOLOGY_RENAMED:
                    listener.ontologyRenamed(project, oldId, newId);
                    break;
                case PROJECT_RENAMED:
                    listener.projectRenamed(oldId, newId);
                    break;
                default:
                    break;
            }
        }
    }
    
    private void fireOntologyChanged(String project, String ontologyUri, boolean changed) {
        Set<IOntologyProjectListener> listeners = getOntologyProjectListeners(project);
        for(IOntologyProjectListener listener: listeners) {
            listener.ontologyModified(project, ontologyUri, changed);
        }
    }

    private void fireOntologyStructureChanged(String project, String ontologyUri, boolean changed) {
        Set<IOntologyProjectListener> listeners = getOntologyProjectListeners(project);
        for(IOntologyProjectListener listener: listeners) {
            listener.ontologyStructureModified(project, ontologyUri, changed);
        }
    }

    private void fireProjectRemoved(String project, String factoryId, String ontologyLanguage) {
        Set<IOntologyProjectListener> listeners = getOntologyProjectListeners(factoryId, ontologyLanguage);
        for(IOntologyProjectListener listener: listeners) {
            listener.projectRemoved(project);
        }
    }
    
    private void fireFailure(String project, Exception e) {
        Set<IOntologyProjectListener> listeners = getOntologyProjectListeners(project);
        for(IOntologyProjectListener listener: listeners) {
            if (listener instanceof IProjectFailureListener) {
                ((IProjectFailureListener)listener).projectFailureOccured(project, e);
            }
        }
    }
}
