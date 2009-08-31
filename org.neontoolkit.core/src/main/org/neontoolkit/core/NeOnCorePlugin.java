/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.IOntologyProjectListener;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class NeOnCorePlugin extends LoggingPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.neontoolkit.core"; //$NON-NLS-1$
    // The identifier of the ontologyProject extension point
    public static final String EXT_POINT_ONTOLOGY_PROJECT = PLUGIN_ID + ".ontologyProject"; //$NON-NLS-1$

    // A map containing the identifiers of ontology project factories and the factories itself
    private Map<String,IOntologyProjectFactory> _ontologyProjectFactories;

    // The shared instance
    private static NeOnCorePlugin plugin;

    /**
     * The constructor
     */
    public NeOnCorePlugin() {
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static NeOnCorePlugin getDefault() {
        return plugin;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        // initialize the extensions of org.neontoolkit.core.ontologyProjects
        initOntologyProjectFactories();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
        OntologyProjectManager.getDefault().dispose();
    }

    /**
     * Returns the handle for the project with the given name.
     * 
     * @param projectName
     * @return
     */
    public IProject getProject(String projectName) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        return root.getProject(projectName);
    }

    /**
     * Returns the ontology project instance for the project with the given name. If the IontologyProject for this project has not yet been created, it is
     * created.
     * 
     * @param projectName
     * @return
     * @throws NeOnCoreException
     */
    public IOntologyProject getOntologyProject(String projectName) throws NeOnCoreException {
        return OntologyProjectManager.getDefault().getOntologyProject(projectName);
    }

    public void addOntologyProjectListener(IOntologyProjectListener listener) {
        addOntologyProjectListenerById(listener, OntologyProjectManager.ONTOLOGY_PROJECT_ALL);
    }

    public void addOntologyProjectListenerByLanguage(IOntologyProjectListener listener, String projectFactoryId) {
        OntologyProjectManager.getDefault().addOntologyProjectListenerByLanguage(listener, projectFactoryId);
    }

    public void addOntologyProjectListenerById(IOntologyProjectListener listener, String projectFactoryId) {
        OntologyProjectManager.getDefault().addOntologyProjectListenerById(listener, projectFactoryId);
    }

    public void removeOntologyProjectListener(IOntologyProjectListener listener) {
        OntologyProjectManager.getDefault().removeOntologyProjectListener(listener);
    }

    /**
     * Initializes the ontologyProject extension point.
     */
    private void initOntologyProjectFactories() {
        _ontologyProjectFactories = new HashMap<String,IOntologyProjectFactory>();

        // Initialize the extensions of the ontology project factory
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IExtensionPoint extPoint = reg.getExtensionPoint(EXT_POINT_ONTOLOGY_PROJECT);
        IExtension[] extension = extPoint.getExtensions();
        for (int i = 0; i < extension.length; i++) {
            IConfigurationElement[] confElems = extension[i].getConfigurationElements();
            for (int j = 0; j < confElems.length; j++) {
                if (confElems[j].getName().equals("factory")) { //$NON-NLS-1$
                    try {
                        // factory found, register it
                        String id = confElems[j].getAttribute("id"); //$NON-NLS-1$
                        IOntologyProjectFactory factory = (IOntologyProjectFactory) confElems[j].createExecutableExtension("class"); //$NON-NLS-1$
                        _ontologyProjectFactories.put(id, factory);
                    } catch (CoreException ce) {
                        logError("OntologyManager factory could not be instantiated", ce); //$NON-NLS-1$
                    }
                }
            }
        }
    }

    public void addOntologyProjectFactory(String id, IOntologyProjectFactory factory) {
        _ontologyProjectFactories.put(id, factory);
    }

    public void removeOntologyProjectFactory(String id) {
        _ontologyProjectFactories.remove(id);
    }

    public IOntologyProjectFactory getOntologyProjectFactory(String id) {
        IOntologyProjectFactory factory = _ontologyProjectFactories.get(id);
        if (factory == null) {
            logWarning("Can't get ontologyProjectFactory for id '" + id + // //$NON-NLS-1$
                    "' known factories: " + _ontologyProjectFactories.toString(), null);  //$NON-NLS-1$
        }
        return factory;
    }

    public void updateMarkers(IOntologyProject project) {
        updateMarkers(project.getName(), project.getProjectFailure());
    }

    /**
     * updates the markers of a project asynchronously
     * 
     * @param projectName
     * @param e
     */
    public void updateMarkers(final String projectName, final Exception e) {
        IProject project = getProject(projectName);
        if (project == null || !project.exists()) {
            return;
        }
        try {
            project.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
            createMarker(project, e);
        } catch (CoreException ce) {
            logError("Error updating markers.", ce); //$NON-NLS-1$
        }
    }

    private void createMarker(IProject project, final Exception exception) throws CoreException {
        project.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
        if (exception != null) {
            IMarker marker = project.createMarker(IMarker.PROBLEM);
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            marker.setAttribute(IMarker.MESSAGE, exception.getLocalizedMessage());
        }
    }
}
