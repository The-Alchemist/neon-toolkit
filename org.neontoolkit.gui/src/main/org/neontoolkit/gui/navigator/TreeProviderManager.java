/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.navigator;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.neontoolkit.core.EntityType;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.internal.TreeExtensionHandler;

/* 
 * Created on: 03.04.2007
 * Created by: Dirk Wenke
 *
 * Keywords: UI, extendableTreeProvider, Tree extensions
 */
/**
 * This class manages the extensions of the extenableTreeProvider extension
 * point and the different instantiations of the providers. Trees with a 
 * MainTreeDataProvider as content provider can retrieve the TreeExtensions here.
 */
public class TreeProviderManager {
    private static TreeProviderManager _singleton;
	private Hashtable<String, TreeExtensionHandler> _handlers;
	private Map<EntityType, ITreeElementFactory> _elementFactories;
	
	
	private TreeProviderManager() {
		_handlers = new Hashtable<String, TreeExtensionHandler>();
		_elementFactories = new HashMap<EntityType, ITreeElementFactory>();
	}
	
	public static TreeProviderManager getDefault() {
		if (_singleton == null) {
			_singleton = new TreeProviderManager();
		}
		return _singleton;
	}
	
	/**
	 * Returns an TreeExtensionHandler instance registered for the tree
	 * component with the given id. If none has already been registered, 
	 * a dummy handler is returned, containing the extensions defined in
	 * the plugin.xml files.
	 * @param viewId
	 * @return
	 */
	public TreeExtensionHandler getExtensionHandler(String viewId) {
		TreeExtensionHandler handler = _handlers.get(viewId);
		if (handler == null) {
			IConfigurationElement[] configElems = NeOnUIPlugin.getDefault().getProviders(viewId);
			handler = new TreeExtensionHandler(null, configElems);
			_handlers.put(viewId, handler);
		}
		return handler;
	}
	
	/**
	 * Creates an TreeExtensionHandler instance for the tree component
	 * with the given id. If there already exists one, this instance is 
	 * returned. 
	 * @param viewId
	 * @param viewer
	 * @return
	 */
	public ITreeExtensionHandler createExtensionHandler(String viewId, AbstractComplexTreeViewer viewer) {
		TreeExtensionHandler handler = getExtensionHandler(viewId);
		if (handler == null) {
			IConfigurationElement[] configElems = NeOnUIPlugin.getDefault().getProviders(viewId);
			handler = new TreeExtensionHandler(viewer, configElems);
			_handlers.put(viewId, handler);
		}
		else if (handler.getViewer() == null) {
			handler.setViewer(viewer);
		}
		return handler;
	}
	
	/**
	 * 
	 * @param viewId
	 * @return
	 */
	public ITreeDataProvider getProvider(String viewId, Class<?> providerClass) {
		TreeExtensionHandler handler = getExtensionHandler(viewId);
		return handler.getProvider(providerClass);
	}
	
	public void disposeExtensionHandler(String viewId) {
		TreeExtensionHandler handler = _handlers.remove(viewId);
		if (handler != null) {
			handler.dispose();
		}
	}

	public void setTreeElementFactory(EntityType type, ITreeElementFactory factory) {
		_elementFactories.put(type, factory);
	}
	
	public ITreeElementFactory getTreeElementFactory(EntityType type) {
		return _elementFactories.get(type);
	}
}
