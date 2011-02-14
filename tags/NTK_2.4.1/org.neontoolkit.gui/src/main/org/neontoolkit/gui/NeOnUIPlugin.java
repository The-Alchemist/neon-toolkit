/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.gui.internal.properties.PropertyPageInfo;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.properties.EntityPropertiesView;
import org.neontoolkit.gui.properties.project.IDatamodelConfigurationGroup;
import org.neontoolkit.gui.result.IResultPage;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class NeOnUIPlugin extends LoggingUIPlugin {
    // The plug-in ID
    public static final String PLUGIN_ID = "org.neontoolkit.gui"; //$NON-NLS-1$

    public static final String LANGUAGE_PREFERENCE = "org.neontoolkit.gui.language"; //$NON-NLS-1$
    public static final String TOOLBAR_ACTIONS = "org.neontoolkit.gui.toolbaractions"; //$NON-NLS-1$

    /**
     * Preference key that is used to store how identifiers are displayed in the UI. Supported values are: <li>DISPLAY_URI</li> <li>DISPLAY_LOCAL</li> <li>
     * DISPLAY_QNAME</li>
     */
    public static final String ID_DISPLAY_PREFERENCE = "org.neontoolkit.gui.displayId"; //$NON-NLS-1$
    /**
     * Preference key for the show/hide namespace switch
     * 
     * @deprecated in OntoStudio 2.3. Please use ID_DISPLAY_PREFERENCE instead. Supported values are: <li>DISPLAY_URI</li> <li>DISPLAY_LOCAL</li> <li>
     *             DISPLAY_QNAME</li>
     */
    public final static String NAMESPACE_PREFERENCE = "org.neontoolkit.gui.namespace"; //$NON-NLS-1$

    // view identifiers
    public static final String ID_ONTOLOGY_NAVIGATOR = MTreeView.ID;
    public static final String ID_ENTITY_PROPERTIES = EntityPropertiesView.ID;

    // extension point identifiers
    public static final String EXT_POINT_EXTENDABLE_DROP_HANDLER = PLUGIN_ID + ".extendableDropHandler"; //$NON-NLS-1$
    public static final String EXT_POINT_EXTENDABLE_TREE_PROVIDER = PLUGIN_ID + ".extendableTreeProvider"; //$NON-NLS-1$
    public static final String EXT_POINT_ENTITY_PROPERTIES = PLUGIN_ID + ".entityProperties"; //$NON-NLS-1$
    public static final String EXT_POINT_RESULT_PAGE = PLUGIN_ID + ".resultPage"; //$NON-NLS-1$
    public static final String EXT_POINT_DATAMODEL_PROPERTIES = PLUGIN_ID + ".datamodelProperties"; //$NON-NLS-1$

    // extension point tags
    public static final String EXTENDABLE_TREE_PROVIDER_ID = "id"; //$NON-NLS-1$
    public static final String EXTENDABLE_TREE_PROVIDER_CLASS = "class"; //$NON-NLS-1$
    public static final String EXTENDABLE_TREE_PROVIDER_SUBPROVIDER_OF = "subproviderOf"; //$NON-NLS-1$
    public static final String EXTENDABLE_TREE_PROVIDER_PRIORITY = "priority"; //$NON-NLS-1$
    public static final String EXTENDABLE_TREE_PROVIDER_VIEW = "viewContribution"; //$NON-NLS-1$

    // values for the ID_DISPLAY_PREFERENCE
    public static final int DISPLAY_URI = 0;
    public static final int DISPLAY_QNAME = 1;
    public static final int DISPLAY_LOCAL = 2;

    // The ConfigurationElement of the extensions of TreeDataProvider extension point
    private ArrayList<IConfigurationElement> _providerList;
    // The dropHandlers stored in a hashtable. The configuration element is the key and the real class
    // instance implementing ITreeDropHandler is stored as value or if not yet used the IConfigurationElement
    private Hashtable<IConfigurationElement,Object> _dropHandlers;
    // The transferHandlers stored by their name of the transfer classes. The key is the class name,
    // the value is the real class instance implementing ITreeTransferHandler or if not yet used the
    // IConfigurationElement
    private Hashtable<Class<?>,Object> _transferHandlers;
    private Transfer[] _transfers;
    //
    private Map<String,IConfigurationElement> _resultPageConigurations;
    private Map<String,IResultPage> _lastPages;
    private Set<IConfigurationElement> _configurationGroups;

    private Map<String,PropertyPageInfo> _propertyPageInfos;

    // The shared instance
    private static NeOnUIPlugin plugin;

    /**
     * The constructor
     */
    public NeOnUIPlugin() {
        _dropHandlers = new Hashtable<IConfigurationElement,Object>();
        _transferHandlers = new Hashtable<Class<?>,Object>();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        // ---------------------------------------------------------------------
        // josp 2009-03-16:
        // 'PlatformUI.getWorkbench()'
        // WILL BREAKS PLUGIN TESTS!
        // USE 'if (PluginTest.isPluginTest()) return' to avoid it
        // ---------------------------------------------------------------------
        super.start(context);
        initTreeProviderExtPoint();
        initDropHandlerExtPoint();
        initEntityPropertiesExtPoint();
        initDatamodelPropertiesExtensionPoint();
        initEarlyStartup();
        plugin = this;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static NeOnUIPlugin getDefault() {
        return plugin;
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        SharedImages.register(reg);
    }

    /**
     * Returns all available languages that can be displayed.
     */
    public String[] getLanguages() {
        List<String> langs = new ArrayList<String>();
        String languageString = getPreferenceStore().getString(NeOnUIPlugin.LANGUAGE_PREFERENCE);
        StringTokenizer tokenizer = new StringTokenizer(languageString, ";"); //$NON-NLS-1$
        while (tokenizer.hasMoreTokens()) {
            String lang = tokenizer.nextToken();
            if (!lang.equals("")) { //$NON-NLS-1$
                langs.add(lang);
            }
        }
        return langs.toArray(new String[langs.size()]);
    }

    /**
     * Returns the current selected language for displaying the ontology entities.
     */
    public int getIdDisplayStyle() {
        return getPreferenceStore().getInt(ID_DISPLAY_PREFERENCE);
    }

    /**
     * Returns the drop handler that is registered for drop operation for given source and target elements
     * 
     * @param dragSource
     * @param dropTarget
     * @return
     */
    public DropTargetListener getDropHandler(Object dragSource, Object dropTarget) {
        for (Iterator<IConfigurationElement> i = _dropHandlers.keySet().iterator(); i.hasNext();) {
            IConfigurationElement next = (IConfigurationElement) i.next();
            boolean addDropHandler = false;
            String acceptDropSubclasses = next.getAttribute("acceptDropSubclasses"); //$NON-NLS-1$
            String acceptDragSubclasses = next.getAttribute("acceptDragSubclasses"); //$NON-NLS-1$

            if (next.getAttribute("dragClass").equals(dragSource.getClass().getName())) { //$NON-NLS-1$
                addDropHandler = true;
            } else if (acceptDragSubclasses != null && Boolean.parseBoolean(acceptDragSubclasses)) {
                if (checkInterfaces(next.getAttribute("dragClass"), dragSource.getClass())) { //$NON-NLS-1$
                    addDropHandler = true;
                }
                // check for dragSource being a subclass of dragClass
                Class<?> dragSuperClass = dragSource.getClass().getSuperclass();
                while (dragSuperClass != null && !addDropHandler) {
                    if (next.getAttribute("dragClass").equals(dragSuperClass.getName())) { //$NON-NLS-1$
                        addDropHandler = true;
                    } else {
                        if (checkInterfaces(next.getAttribute("dragClass"), dragSuperClass.getClass())) { //$NON-NLS-1$
                            addDropHandler = true;
                        }
                        dragSuperClass = dragSuperClass.getSuperclass();
                    }
                }
            }
            if (addDropHandler && next.getAttribute("dropClass").equals(dropTarget.getClass().getName())) { //$NON-NLS-1$
                addDropHandler = true;
            } else if (addDropHandler && acceptDropSubclasses != null && Boolean.parseBoolean(acceptDropSubclasses)) {
                // check for dropTarget being a subclass of dropClass
                addDropHandler = checkInterfaces(next.getAttribute("dropClass"), dragSource.getClass()); //$NON-NLS-1$
                Class<?> dropSuperClass = dropTarget.getClass().getSuperclass();
                while (dropSuperClass != null && !addDropHandler) {
                    if (next.getAttribute("dropClass").equals(dropSuperClass.getName())) { //$NON-NLS-1$
                        addDropHandler = true;
                    } else {
                        if (checkInterfaces(next.getAttribute("dropClass"), dropSuperClass.getClass())) { //$NON-NLS-1$
                            addDropHandler = true;
                        } else {
                            dropSuperClass = dropSuperClass.getSuperclass();
                        }
                    }
                }
            } else {
                addDropHandler = false;
            }
            if (addDropHandler) {
                Object handler = _dropHandlers.get(next);
                if (!(handler instanceof DropTargetListener)) {
                    try {
                        handler = next.createExecutableExtension("class"); //$NON-NLS-1$
                        _dropHandlers.put(next, handler);
                    } catch (CoreException e) {
                        getLog().log(new Status(IStatus.ERROR, getBundle().getSymbolicName(), IStatus.OK, Messages.NeOnUIPlugin_3 + next.getAttribute("class"), //$NON-NLS-1$ 
                                e));
                        return null;
                    }
                }
                return (DropTargetListener) handler;
            }
        }
        return null;
    }

    private boolean checkInterfaces(String className, Class<?> object) {
        Class<?>[] interfaces = object.getInterfaces();
        for (Class<?> class1: interfaces) {
            if (className.equals(class1.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the handler managing transfers of the given class type
     * 
     * @param transferClass
     * @return
     */
    public DropTargetListener getTransferHandler(Class<?> transferClass) {
        Object handler = _transferHandlers.get(transferClass);
        if (handler instanceof DropTargetListener) {
            return (DropTargetListener) handler;
        } else if (handler instanceof IConfigurationElement) {
            IConfigurationElement conf = (IConfigurationElement) handler;
            try {
                handler = conf.createExecutableExtension("class"); //$NON-NLS-1$
            } catch (CoreException e) {
                logError("", e); //$NON-NLS-1$
                return null;
            }
            _transferHandlers.put(transferClass, handler);
            return (DropTargetListener) handler;
        }
        return null;
    }

    /**
     * Returns the transfer types that are registered
     * 
     * @return
     */
    public Transfer[] getTransfers() {
        return _transfers;
    }

    public PropertyPageInfo[] getPropertyPageInfos() {
        List<PropertyPageInfo> rootPages = new ArrayList<PropertyPageInfo>();
        for (PropertyPageInfo info: _propertyPageInfos.values()) {
            if (info._isMain) {
                rootPages.add(info);
            }
        }
        return rootPages.toArray(new PropertyPageInfo[0]);
    }

    public IResultPage getResultPage(String pageId) {
        if (_resultPageConigurations == null) {
            initResultPageExtensionPoint();
        }
        IConfigurationElement confElement = _resultPageConigurations.get(pageId);
        if (confElement == null) {
            return null;
        }

        if (Boolean.parseBoolean(confElement.getAttribute("singleton"))) { //$NON-NLS-1$
            // singleton flag is set, if page has already been created, just return the page
            IResultPage page = _lastPages.get(pageId);
            if (page != null) {
                return page;
            }
        }

        try {
            IResultPage page = (IResultPage) confElement.createExecutableExtension("class"); //$NON-NLS-1$
            _lastPages.put(pageId, page);
            return page;
        } catch (CoreException e) {
            logError(Messages.NeOnUIPlugin_0, e);
            return null;
        }
    }

    /**
     * Returns the tree providers for the view with the given id. Providers with no specific defined id are added to the default view, the OntologyNavigator
     * 
     * @param viewId
     * @return
     */
    public IConfigurationElement[] getProviders(String viewId) {
        List<IConfigurationElement> result = new ArrayList<IConfigurationElement>();
        for (IConfigurationElement element: _providerList) {
            IConfigurationElement[] viewElements = element.getChildren(EXTENDABLE_TREE_PROVIDER_VIEW);
            if (viewElements == null || viewElements.length == 0) {
                // if no specific view id defined, add it only to the default view
                if (viewId.equals(MTreeView.ID)) {
                    result.add(element);
                }
            } else {
                for (IConfigurationElement viewElement: viewElements) {
                    if (viewId.equals(viewElement.getAttribute(EXTENDABLE_TREE_PROVIDER_ID))) {
                        result.add(element);
                    }
                }
            }
        }
        return result.toArray(new IConfigurationElement[0]);
    }

    /**
     * initializes the extensions of the entityProperties extension point.
     * 
     */
    private void initEntityPropertiesExtPoint() {
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IExtensionPoint extPoint = reg.getExtensionPoint(EXT_POINT_ENTITY_PROPERTIES);
        IExtension[] extension = extPoint.getExtensions();

        _propertyPageInfos = new HashMap<String,PropertyPageInfo>();

        Map<PropertyPageInfo,IConfigurationElement> confMap = new HashMap<PropertyPageInfo,IConfigurationElement>();
        for (int i = 0; i < extension.length; i++) {
            IConfigurationElement[] confElems = extension[i].getConfigurationElements();
            for (int j = 0; j < confElems.length; j++) {
                if (confElems[j].getName().equals("entityPropertyContributor")) { //$NON-NLS-1$
                    int prio = 0;
                    IConfigurationElement[] children = confElems[j].getChildren("subContributorOf"); //$NON-NLS-1$ 
                    if (children.length > 0) {
                        try {
                            prio = Integer.parseInt(children[0].getAttribute("priority")); //$NON-NLS-1$
                        } catch (Exception e) {
                        }
                    }
                    PropertyPageInfo info = new PropertyPageInfo(confElems[j], prio);
                    _propertyPageInfos.put(confElems[j].getAttribute("id"), info); //$NON-NLS-1$
                    confMap.put(info, confElems[j]);
                }
            }
        }

        for (PropertyPageInfo info: confMap.keySet()) {
            IConfigurationElement confElement = confMap.get(info);
            IConfigurationElement[] children = confElement.getChildren("subContributorOf"); //$NON-NLS-1$ 
            if (children.length > 0) {
                PropertyPageInfo superContributor = _propertyPageInfos.get(children[0].getAttribute("superContributorId")); //$NON-NLS-1$
                if (superContributor != null) {
                    superContributor.addSubContributor(info);
                }
            }
        }
    }

    /**
     * Initializes the extensions of the dropHandler extension-point. The handlers are not created, just stored and will be created when accessed.
     */
    private void initDropHandlerExtPoint() {
        // Initialize the extensions of the drop handler extension point
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IExtensionPoint extPoint = reg.getExtensionPoint(EXT_POINT_EXTENDABLE_DROP_HANDLER);
        IExtension[] extension = extPoint.getExtensions();
        List<Object> transfersTemp = new ArrayList<Object>();
        for (int i = 0; i < extension.length; i++) {
            IConfigurationElement[] confElems = extension[i].getConfigurationElements();
            IConfigurationElement confElem = null;
            for (int j = 0; j < confElems.length; j++) {
                confElem = confElems[j];
                if (confElems[j].getName().equals("dropHandler")) { //$NON-NLS-1$
                    _dropHandlers.put(confElem, confElem);
                } else if (confElems[j].getName().equals("transferHandler")) { //$NON-NLS-1$
                    try {
                        Object transferClass = confElem.createExecutableExtension("transferClass"); //$NON-NLS-1$
                        transfersTemp.add(transferClass);
                        _transferHandlers.put(transferClass.getClass(), confElem);
                    } catch (CoreException e) {
                        // Check if the transfer class is one of the
                        // standard eclipse
                        // transfer types with a private constructor
                        try {
                            Class<?> c = getBundle().loadClass(confElem.getAttribute("transferClass")); //$NON-NLS-1$
                            Method getInstance = c.getDeclaredMethod("getInstance", new Class[0]); //$NON-NLS-1$
                            getInstance.setAccessible(true);
                            if (getInstance != null && getInstance.isAccessible() && Transfer.class.isAssignableFrom(c)) {
                                Object o = getInstance.invoke(c, new Object[0]);
                                if (o instanceof Transfer) {
                                    transfersTemp.add(o);
                                }
                            }
                            _transferHandlers.put(c, confElem);
                        } catch (Exception e1) {
                            logError("", e1); //$NON-NLS-1$
                        }
                    }
                }
            }
        }
        _transfers = (Transfer[]) transfersTemp.toArray(new Transfer[0]);
    }

    /**
     * Initializes the extensions of the tree providers
     */
    private void initTreeProviderExtPoint() {
        _providerList = new ArrayList<IConfigurationElement>();
        // Initialize the extensions of the tree provider extension point
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IExtensionPoint extPoint = reg.getExtensionPoint(EXT_POINT_EXTENDABLE_TREE_PROVIDER);
        IExtension[] extension = extPoint.getExtensions();
        for (int i = 0; i < extension.length; i++) {
            IConfigurationElement[] confElems = extension[i].getConfigurationElements();
            for (int j = 0; j < confElems.length; j++) {
                if (confElems[j].getName().equals("provider")) { //$NON-NLS-1$
                    // Provider found, add it to the list
                    _providerList.add(confElems[j]);
                }
            }
        }

        // sort the providers according to the priority
        Collections.sort(_providerList, new Comparator<IConfigurationElement>() {
            @Override
            public int compare(IConfigurationElement o1, IConfigurationElement o2) {
                int prio1 = getPriority(o1);
                int prio2 = getPriority(o2);
                return prio2 - prio1;
            }
        });
    }

    private void initResultPageExtensionPoint() {
        _resultPageConigurations = new HashMap<String,IConfigurationElement>();
        _lastPages = new HashMap<String,IResultPage>();

        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IExtensionPoint extPoint = reg.getExtensionPoint(EXT_POINT_RESULT_PAGE);
        IExtension[] extension = extPoint.getExtensions();
        for (int i = 0; i < extension.length; i++) {
            IConfigurationElement[] confElems = extension[i].getConfigurationElements();
            for (int j = 0; j < confElems.length; j++) {
                if (confElems[j].getName().equals("page")) { //$NON-NLS-1$
                    // Provider found, add it to the list
                    _resultPageConigurations.put(confElems[j].getAttribute("id"), confElems[j]); //$NON-NLS-1$
                }
            }
        }
    }
    
    private void initDatamodelPropertiesExtensionPoint() {
        _configurationGroups = new HashSet<IConfigurationElement>();

        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IExtensionPoint extPoint = reg.getExtensionPoint(EXT_POINT_DATAMODEL_PROPERTIES);
        IExtension[] extension = extPoint.getExtensions();
        for (int i = 0; i < extension.length; i++) {
            IConfigurationElement[] confElems = extension[i].getConfigurationElements();
            for (int j = 0; j < confElems.length; j++) {
                if (confElems[j].getName().equals("group")) { //$NON-NLS-1$
                    // Group found, add it to the list
                    _configurationGroups.add(confElems[j]);
                }
            }
        }
    }
    
    private void initEarlyStartup() {
        // josp 2009.01.20: Plugin tests (headless Eclipse) dont have a Workbench
        if (PlatformUI.isWorkbenchRunning()) {
            PlatformUI.getWorkbench().addWorkbenchListener(new PreShutdownHandler());
        }       

    }

    /**
     * Returns the priority for the given extendable tree provider extension. If none is defined, null is returned.
     * 
     * @param element
     * @return
     */
    private int getPriority(IConfigurationElement element) {
        String prio1String = element.getAttribute(EXTENDABLE_TREE_PROVIDER_PRIORITY);
        int prio = 0;
        if (prio1String != null) {
            try {
                prio = new Integer(prio1String).intValue();
            } catch (NumberFormatException nfe) {
                logError(Messages.NeOnUIPlugin_1, nfe);
            }
        }
        return prio;
    }
    
    public IDatamodelConfigurationGroup[] getDatamodelConfigurationGroups() {
        if (_configurationGroups == null) {
            _configurationGroups = new HashSet<IConfigurationElement>();
            return new IDatamodelConfigurationGroup[0];
        }
        else {
            List<IDatamodelConfigurationGroup> res = new ArrayList<IDatamodelConfigurationGroup>();
            for (IConfigurationElement elem: _configurationGroups) {
                try {
                    res.add((IDatamodelConfigurationGroup)elem.createExecutableExtension("class"));
                } catch (CoreException e) {
                    NeOnUIPlugin.getDefault().logError("DatamodelConfigurationGroup could not be created: "+elem.getAttribute("class"), e);
                }
            }
            return res.toArray(new IDatamodelConfigurationGroup[0]);
        }
    }
}
