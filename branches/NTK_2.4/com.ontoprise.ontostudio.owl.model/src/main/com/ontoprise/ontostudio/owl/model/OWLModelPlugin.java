/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author Nico Stieler
 * This is the plugin activator for the gui plugin. this plugin provides various extension points.
 */
public class OWLModelPlugin extends AbstractUIPlugin {
    public static final String ID = "com.ontoprise.ontostudio.owl.model"; //$NON-NLS-1$

    public static final String DISPLAY_TOOLBAR_ACTIONS = "com.ontoprise.ontostudio.owl.gui.display.toolbaractions"; //$NON-NLS-1$

    public static final String OWL_ONTOLOGY_NAVIGATOR = "com.ontoprise.ontostudio.owl.views.navigator"; //$NON-NLS-1$
    public static final String SHOW_IMPORTED = OWLModelPlugin.class.getName() + ".owlShowImportedAxioms"; //$NON-NLS-1$
    public static final String SHOW_AXIOMS = OWLModelPlugin.class.getName() + ".owlShowAxiomsInGui"; //$NON-NLS-1$
    public static final String USE_TOOLBAR = OWLModelPlugin.class.getName() + ".owlUseToolbarForComplexClasses"; //$NON-NLS-1$
    public static final String SHOW_ACTUAL_ONTOLOGY = OWLModelPlugin.class.getName() + ".owlShowActualOntologyInIdentifier"; //$NON-NLS-1$ 
    public static final String EDIT_IMPORTED_AXIOMS_WITHOUT_ASKING = OWLModelPlugin.class.getName() + ".owlEditImportedWithoutAsking"; //$NON-NLS-1$
    public static final String REMOVE_IMPORTED_AXIOMS_WITHOUT_ASKING = OWLModelPlugin.class.getName() + ".owlRemoveImportedWithoutAsking"; //$NON-NLS-1$ 
    public static final String INSERT_EXPLICIT_CLASS_ASSERTION_AXIOM_TO_OWLTHING_OPEN_DIALOG = OWLModelPlugin.class.getName() + ".owlInsertExplicitClassAssertionAxiomToOwlthingOpenDialog"; //$NON-NLS-1$
    public static final String INSERT_EXPLICIT_CLASS_ASSERTION_AXIOM_TO_OWLTHING_YES_OR_NO = OWLModelPlugin.class.getName() + ".owlInsertExplicitClassAssertionAxiomToOwlthingYesOrNo"; //$NON-NLS-1$ 
    public static final String SHOW_RESTRICTION_IN_CLASS_TAXONOMY_TAB = OWLModelPlugin.class.getName() + ".owlShowRestrictionsInClassTaxonomyTab"; //$NON-NLS-1$ 
    
    public static final String LANGUAGE_PREFERENCE = OWLModelPlugin.class.getName() + ".language"; //$NON-NLS-1$
    public static final String NAMESPACE_PREFERENCE = OWLModelPlugin.class.getName() + ".namespace"; //$NON-NLS-1$
    //	public static final String EXPERTISE_PREFERENCE = OWLPlugin.class.getName() + ".expertise"; //$NON-NLS-1$
    public static final String EXTERNAL_REPRESENTATION_PREFERENCE = OWLModelPlugin.class.getName() + ".externalrepresentation"; //$NON-NLS-1$

    public static final String FONT_WIDGET_FONT = "com.ontoprise.ontostudio.gui.widgetfont"; //$NON-NLS-1$


    // The shared instance.
    private static OWLModelPlugin _plugin;

    private FontRegistry _fontRegistry;

    /**
     * Default constructor invoked by the framework
     */
    public OWLModelPlugin() {
        _plugin = this;
    }

    /**
     * Returns the shared instance.
     */
    public static OWLModelPlugin getDefault() {
        if (_plugin == null) {
            _plugin = new OWLModelPlugin();
        }
        return _plugin;
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
        log.log(new Status(IStatus.ERROR, OWLModelPlugin.ID, 1, message, e));
        Logger.getLogger(OWLModelPlugin.class).error(e);
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
        log.log(new Status(IStatus.WARNING, OWLModelPlugin.ID, 1, message, e));
    }

    /**
     * Default warning logging
     * 
     * @param message warning message
     */
    public static void logWarning(String message) {
        ILog log = getDefault().getLog();
        log.log(new Status(IStatus.WARNING, OWLModelPlugin.ID, 1, message, null));
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
        log.log(new Status(IStatus.INFO, OWLModelPlugin.ID, 1, message, e));
    }

    /**
     * This method is called upon plug-in activation
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
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

    public static class Images {

        private static URL _fgIconBaseURL = null;
        private static final String PATH_SUFFIX = "icons/"; //$NON-NLS-1$
        private static Hashtable<ImageDescriptor,Image> _imageRegistry = new Hashtable<ImageDescriptor,Image>();

        static {
            _fgIconBaseURL = OWLModelPlugin.getDefault().getBundle().getEntry(PATH_SUFFIX);
        }
        public static final ImageDescriptor NEW_ONTOLOGY_PROJECT_WIZ = create("wizard", "newonto_wiz.gif"); //$NON-NLS-1$ //$NON-NLS-2$

        /*
         * OWL specific constants
         */
        public static final ImageDescriptor OWL_ONTOLOGY = create("onto", "owlontology.gif"); //$NON-NLS-1$ //$NON-NLS-2$
        public static final ImageDescriptor OWL_PROJECT = create("onto", "project.gif"); //$NON-NLS-1$ //$NON-NLS-2$    
        public static final ImageDescriptor PROJECT = create("onto", "project.gif"); //$NON-NLS-1$ //$NON-NLS-2$    
        public static final ImageDescriptor CLAZZ = create("onto", "clazz.gif"); //$NON-NLS-1$ //$NON-NLS-2$
        public static final ImageDescriptor INDIVIDUAL = create("onto", "individual.gif"); //$NON-NLS-1$ //$NON-NLS-2$    
        public static final ImageDescriptor OBJECT_PROPERTY = create("onto", "obj_property.gif"); //$NON-NLS-1$ //$NON-NLS-2$
        public static final ImageDescriptor DATA_PROPERTY = create("onto", "data_property.gif"); //$NON-NLS-1$ //$NON-NLS-2$
        public static final ImageDescriptor ANNOTATION_PROPERTY = create("onto", "annot_property.gif"); //$NON-NLS-1$ //$NON-NLS-2$
        public static final ImageDescriptor DATATYPE = create("onto", "datatype.gif"); //$NON-NLS-1$ //$NON-NLS-2$
        public static final ImageDescriptor FOLDER = create("onto", "folder.gif"); //$NON-NLS-1$ //$NON-NLS-2$
        public static final ImageDescriptor URI_ERROR = create("onto", "uri_error.gif"); //$NON-NLS-1$ //$NON-NLS-2$

        public static Image get(ImageDescriptor descr) {
            Image im = (Image) _imageRegistry.get(descr);
            if (im == null) {
                im = descr.createImage();
                _imageRegistry.put(descr, im);
            }
            return im;
        }

        private static ImageDescriptor create(String prefix, String name) {
            try {
                return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
            } catch (MalformedURLException e) {
                return ImageDescriptor.getMissingImageDescriptor();
            }
        }

        private static URL makeIconFileURL(String prefix, String name) throws MalformedURLException {
            if (_fgIconBaseURL == null) {
                throw new MalformedURLException();
            } else {
                StringBuffer buffer = new StringBuffer(prefix);
                buffer.append('/');
                buffer.append(name);
                return new URL(_fgIconBaseURL, buffer.toString());
            }
        }
    }
}
