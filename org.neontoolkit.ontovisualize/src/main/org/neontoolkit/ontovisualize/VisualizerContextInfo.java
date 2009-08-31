/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 08.12.2008
 * Created by: werner
 ******************************************************************************/
package org.neontoolkit.ontovisualize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.ontology.IOntologyTreeElement;
import org.neontoolkit.ontovisualize.nodes.LabelImageNode;


/**
 * @author werner
 * 
 */
public class VisualizerContextInfo {

    private String _ontologyLanguage;
    private IConfigurationElement[] _nodeContentProviderDefinitions;
    private List<LabelImageNode> _nodeClasses;
    private Map<String,NodeContentProviderDefinition> _nodeContentProviderDefinitionMap;
    private IVisualizerConfiguration _visualizerConfigurator;

    public VisualizerContextInfo(IConfigurationElement elem) {
        _ontologyLanguage = elem.getAttribute(OntovisualizePlugin.ONTOLOGY_LANGUAGE);
        _nodeContentProviderDefinitions = elem.getChildren(OntovisualizePlugin.NODE_CONTENT_PROVIDER_DEFINITION);
        _nodeContentProviderDefinitionMap = new HashMap<String,NodeContentProviderDefinition>();

        try {
            initNodeContentProviderDefinitionObjects();
            initNodeClasses();
        } catch (CoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
    }

    public IVisualizerConfiguration getVisualizerConfigurator() {
        return _visualizerConfigurator;
    }

    private void initNodeContentProviderDefinitionObjects() throws CoreException {
        for (IConfigurationElement element: _nodeContentProviderDefinitions) {
            String id = element.getAttribute(OntovisualizePlugin.DEF_ID);
            INodeContentProvider provider = (INodeContentProvider) element.createExecutableExtension(OntovisualizePlugin.CONTENT_PROVIDER);

            LabelImageNode nodeClass = (LabelImageNode) element.createExecutableExtension(OntovisualizePlugin.NODE_CLASS);
            NodeContentProviderDefinition definition = new NodeContentProviderDefinition(id, null, provider, nodeClass);
            _nodeContentProviderDefinitionMap.put(id, definition);
        }
    }

    public Collection<NodeContentProviderDefinition> getNodeContentProviderDefinitionObjects() {
        return _nodeContentProviderDefinitionMap.values();
    }

    private void initNodeClasses() throws CoreException {
        _nodeClasses = new ArrayList<LabelImageNode>();
        for (IConfigurationElement element: _nodeContentProviderDefinitions) {
            _nodeClasses.add((LabelImageNode) element.createExecutableExtension(OntovisualizePlugin.NODE_CLASS));
        }
    }

    public List<INodeContentProvider> getContentProviders() {
        List<INodeContentProvider> list = new ArrayList<INodeContentProvider>();
        Collection<NodeContentProviderDefinition> c = _nodeContentProviderDefinitionMap.values();
        for (NodeContentProviderDefinition definition: c) {
            list.add(definition.getContentProvider());
        }
        return list;
    }

    public String getOntologyLanguage() {
        return _ontologyLanguage;
    }
    
    public void setVisualizerConfigurator(IVisualizerConfiguration configurator) {
        _visualizerConfigurator = configurator;
    }

    public class NodeContentProviderDefinition {

        private String _id;
        private IOntologyTreeElement _treeElement;
        private INodeContentProvider _contentProvider;
        private LabelImageNode _nodeClass;

        NodeContentProviderDefinition(String id, IOntologyTreeElement treeElement, INodeContentProvider contentProvider, LabelImageNode nodeClass) {
            _id = id;
            _treeElement = treeElement;
            _contentProvider = contentProvider;
            _nodeClass = nodeClass;
        }

        public String getId() {
            return _id;
        }

        /**
         * @return the _treeElement
         */
        public IOntologyTreeElement getTreeElement() {
            return _treeElement;
        }

        /**
         * @return the _contentProvider
         */
        public INodeContentProvider getContentProvider() {
            return _contentProvider;
        }

        /**
         * @return the _nodeClass
         */
        public LabelImageNode getNodeClass() {
            return _nodeClass;
        }

    }
}
