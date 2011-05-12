/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.osgi.framework.BundleContext;

import com.ontoprise.ontostudio.owl.gui.syntax.ISyntaxManager;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;

/**
 * This is the plugin activator for the gui plugin. this plugin provides various extension points.
 *@author Nico Stieler
 */
public class OWLPlugin extends AbstractUIPlugin {
    public static final String ID = "com.ontoprise.ontostudio.owl.gui"; //$NON-NLS-1$

    public static final String TOOLBARACTIONS = "com.ontoprise.ontostudio.owl.gui.toolbaractions"; //$NON-NLS-1$
    public static final String DISPLAY_TOOLBAR_ACTIONS = "com.ontoprise.ontostudio.owl.gui.display.toolbaractions"; //$NON-NLS-1$

    public static final String OWL_ONTOLOGY_NAVIGATOR = "com.ontoprise.ontostudio.owl.views.navigator"; //$NON-NLS-1$
    public static final String EXT_POINT_SYNTAX_MANAGER = "com.ontoprise.ontostudio.owl.gui.syntaxDefinition"; //$NON-NLS-1$

    /*
     * Syntax Manger
     */
    public static final String SYNTAX = OWLPlugin.class.getName() + ".syntax"; //$NON-NLS-1$
    public static final String DEFAULT_SYNTAX = OWLPlugin.getDefault().getFirstRegisteredSyntaxName(); 

    /*
     * Entity Labels
     */
    public static final String SPECIFIC_LANGUAGE_PREFERENCE = OWLPlugin.class.getName() + ".specific_language"; //$NON-NLS-1$
    public static final int DISPLAY_LANGUAGE = 4; 
    
    /*
     * Namespace changes
     */
    public static final String NAMESPACE_PREFERENCE = OWLPlugin.class.getName() + ".namespacePreference"; //$NON-NLS-1$

    //	public static final String EXPERTISE_PREFERENCE = OWLPlugin.class.getName() + ".expertise"; //$NON-NLS-1$
    public static final String EXTERNAL_REPRESENTATION_PREFERENCE = OWLPlugin.class.getName() + ".externalrepresentation"; //$NON-NLS-1$

    public static final String FONT_WIDGET_FONT = "com.ontoprise.ontostudio.gui.widgetfont"; //$NON-NLS-1$

    public static final String SHOW_NB_INSTANCES_PREFERENCE = OWLPlugin.class.getName() + ".shownbinstances"; //$NON-NLS-1$

    public static final String SHOW_INSTANCES_OF_ALL_SUBCLASSES_PREFERENCE = OWLPlugin.class.getName() + ".showInstancesOfAllSubclasses"; //$NON-NLS-1$
    public static final String SHOW_PROPERTY_MEMBERS_OF_ALL_SUBPROPERTIES_PREFERENCE = OWLPlugin.class.getName() + ".showPropertyMembersOfAllSubproperties"; //$NON-NLS-1$
    public static final String SHOW_PROPERTIES_OF_ALL_SUPERCLASSES_IN_DOMAIN_VIEW_PREFERENCE = OWLPlugin.class.getName() + ".showPropertiesOfAllSuperclassesInTheDomainView"; //$NON-NLS-1$ 
    public static final String SHOW_PROPERTIES_OF_ALL_SUPERCLASSES_IN_RANGE_VIEW_PREFERENCE = OWLPlugin.class.getName() + ".showPropertiesOfAllSuperclassesInTheRangeView"; //$NON-NLS-1$ 

    // The shared instance.
    private static OWLPlugin _plugin;

    private FontRegistry _fontRegistry;
    private Set<ISyntaxManager> _syntaxManagers;

    /**
     * Default constructor invoked by the framework
     */
    public OWLPlugin() {
        _plugin = this;
    }

    /**
     * Returns the shared instance.
     */
    public static OWLPlugin getDefault() {
        if (_plugin == null) {
            _plugin = new OWLPlugin();
        }
        return _plugin;
    }

    /**
     * Collect all registered SyntaxManagers and store them in a list.
     */
    private void initSyntaxManagerExtensionPoint() {
        _syntaxManagers = new HashSet<ISyntaxManager>();
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IExtensionPoint extPoint = reg.getExtensionPoint(EXT_POINT_SYNTAX_MANAGER);
        IExtension[] extension = extPoint.getExtensions();

        for (int i = 0; i < extension.length; i++) {
            IConfigurationElement[] confElems = extension[i].getConfigurationElements();
            for (int j = 0; j < confElems.length; j++) {
                if (confElems[j].getName().equals("manager")) { //$NON-NLS-1$
                    try {
                        ISyntaxManager manager = (ISyntaxManager) confElems[j].createExecutableExtension("class"); //$NON-NLS-1$
                        manager.setSyntaxName(confElems[j].getAttribute("name")); //$NON-NLS-1$
                        _syntaxManagers.add(manager);
                    } catch (CoreException e) {
                        OWLPlugin.logError(e);
                    }
                }
            }
        }

    }

    public Set<ISyntaxManager> getRegisteredSyntaxManagers() {
        return _syntaxManagers;
    }

    public String getFirstRegisteredSyntaxName() {
        if (_syntaxManagers == null) {
            return "Manchester Syntax"; //$NON-NLS-1$
        }
        return _syntaxManagers.iterator().next().getSyntaxName();
    }

    public ISyntaxManager getSyntaxManager() {
        ISyntaxManager result = _syntaxManagers.iterator().next();
        IPreferenceStore store = OWLModelPlugin.getDefault().getPreferenceStore();
        for (ISyntaxManager manager: _syntaxManagers) {
            if (manager.getSyntaxName().equals(store.getString(OWLPlugin.SYNTAX))) {
                result = manager;
            }
        }
        return result;
    }

    public FontRegistry getFontRegistry() {
        if (_fontRegistry == null) {
            IThemeManager manager = PlatformUI.getWorkbench().getThemeManager();
            ITheme theme = manager.getTheme("com.ontoprise.ontostudio.gui.uitheme"); //$NON-NLS-1$
            if (theme != null) {
                _fontRegistry = theme.getFontRegistry();
            } else {
                _fontRegistry = JFaceResources.getFontRegistry();
            }
        }
        return _fontRegistry;
    }

    /**
     * Returns the name of all projects in the current workspace that have the
     * ontology project nature and are F-Logic projects.
     * @return
     * @throws CoreException
     */
    public String[] getOntologyProjects() throws NeOnCoreException {
        return OntologyProjectManager.getDefault().getOntologyProjects(OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE);
    }
    
    public static void logError(Exception e) {
        logError("", e); //$NON-NLS-1$
    }

    /**
     * Default error logging
     * 
     * @param message error message
     * @param e exception
     */
    public static void logError(String message, Exception e) {
        ILog log = getDefault().getLog();
        log.log(new Status(IStatus.ERROR, OWLPlugin.ID, 1, message, e));
        NeonToolkitExceptionHandler oeh = new NeonToolkitExceptionHandler();
        oeh.handleException(e);
    }

    public static void logWarning(Exception e) {
        logWarning("", e); //$NON-NLS-1$
    }

    /**
     * Default warning logging
     * 
     * @param message warning message
     * @param e exception
     */
    public static void logWarning(String message, Exception e) {
        ILog log = getDefault().getLog();
        log.log(new Status(IStatus.WARNING, OWLPlugin.ID, 1, message, e));
    }

    /**
     * Default warning logging
     * 
     * @param message warning message
     */
    public static void logWarning(String message) {
        ILog log = getDefault().getLog();
        log.log(new Status(IStatus.WARNING, OWLPlugin.ID, 1, message, null));
    }

    public static void logInfo(Exception e) {
        logInfo("", e); //$NON-NLS-1$
    }

    /**
     * Default info logging
     * 
     * @param message info message
     * @param e exception
     */
    public static void logInfo(String message, Exception e) {
        ILog log = getDefault().getLog();
        log.log(new Status(IStatus.INFO, OWLPlugin.ID, 1, message, e));
    }

    /**
     * This method is called upon plug-in activation
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        initSyntaxManagerExtensionPoint();
    }

    /**
     * This method is called when the plug-in is stopped
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
    }

    // public boolean isExpertLevel() {
    // return getPreferenceStore().getBoolean(OWLPlugin.EXPERTISE_PREFERENCE);
    // }

    // public void setExpertLevel(boolean value) {
    // getPreferenceStore().setValue(OWLPlugin.EXPERTISE_PREFERENCE, value);
    // }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
     */
    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        OWLSharedImages.register(reg);
    }
}
