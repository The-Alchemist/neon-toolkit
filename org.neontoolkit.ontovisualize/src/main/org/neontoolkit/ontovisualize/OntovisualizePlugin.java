/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.ontovisualize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/*
 * Created by Werner Hihn
 */

public class OntovisualizePlugin extends AbstractUIPlugin {

    public static final String ID = "org.neontoolkit.ontovisualize"; //$NON-NLS-1$

    public static final String VISUALIZER_CONTEXT = "visualizerContext"; //$NON-NLS-1$
    public static final String ONTOLOGY_LANGUAGE = "ontologyLanguage"; //$NON-NLS-1$
    public static final String CONFIGURATOR_CLASS = "configuratorClass"; //$NON-NLS-1$ 
    public static final String DEF_ID = "id"; //$NON-NLS-1$
    public static final String NODE_CLASS = "nodeClass"; //$NON-NLS-1$
    public static final String CONTENT_PROVIDER = "contentProvider"; //$NON-NLS-1$
    public static final String NODE_CONTENT_PROVIDER_DEFINITION = "nodeContentProviderDefinition"; //$NON-NLS-1$
    public static final String NODE_PAINTER_DEFINITION = "nodePainterDefinition"; //$NON-NLS-1$

    public static final String EXT_POINT_VISUALIZER_CONTEXT = ID + ".visualizerContext"; //$NON-NLS-1$

    // The shared instance.
    private static OntovisualizePlugin _plugin;
    private Map<String,VisualizerContextInfo> _visualizerContextInfos;

    public OntovisualizePlugin() {
        super();
        _plugin = this;
    }

    /**
     * This method is called upon plug-in activation
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        initVisualizerContextExtensionPoint();
    }

    /**
     * initializes the extensions of the entityProperties extension point.
     * @throws CoreException 
     * 
     */
    private void initVisualizerContextExtensionPoint() throws CoreException {
        _visualizerContextInfos = new HashMap<String,VisualizerContextInfo>();
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IExtensionPoint extPoint = reg.getExtensionPoint(EXT_POINT_VISUALIZER_CONTEXT);
        IExtension[] extension = extPoint.getExtensions();
        for (int i = 0; i < extension.length; i++) {
            IConfigurationElement[] confElems = extension[i].getConfigurationElements();
            for (int j = 0; j < confElems.length; j++) {
                if (confElems[j].getName().equals(VISUALIZER_CONTEXT)) {
                    VisualizerContextInfo info = new VisualizerContextInfo(confElems[j]);
                    IVisualizerConfiguration configurator = (IVisualizerConfiguration) confElems[j].createExecutableExtension(OntovisualizePlugin.CONFIGURATOR_CLASS);
                    info.setVisualizerConfigurator(configurator);
                    _visualizerContextInfos.put(confElems[j].getAttribute(ONTOLOGY_LANGUAGE), info);
                }
            }
        }
    }

    public VisualizerContextInfo getVisualizerContextInfo(String ontologyLanguage) {
        return _visualizerContextInfos.get(ontologyLanguage);
    }

    public List<INodeContentProvider> getContentProviders(String ontologyLanguage) {
        return _visualizerContextInfos.get(ontologyLanguage).getContentProviders();
    }
    
    public IVisualizerConfiguration getVisualizerConfigurator(String ontologyLanguage) {
        return _visualizerContextInfos.get(ontologyLanguage).getVisualizerConfigurator();
    }

    /**
     * This method is called when the plug-in is stopped
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     */
    public static OntovisualizePlugin getDefault() {
        return _plugin;
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        VisualizerSharedImages.register(reg);
    }

    public static void logError(String message, Exception e) {
        ILog log = getDefault().getLog();
        log.log(new Status(IStatus.ERROR, ID, 1, message, e));
    }

    public static void logWarning(String message, Exception e) {
        ILog log = getDefault().getLog();
        log.log(new Status(IStatus.WARNING, ID, 1, message, e));
    }

    public static void logInfo(String message, Exception e) {
        ILog log = getDefault().getLog();
        log.log(new Status(IStatus.INFO, ID, 1, message, e));
    }
}
