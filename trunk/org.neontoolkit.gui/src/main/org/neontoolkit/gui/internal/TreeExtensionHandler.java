/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.internal;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.AbstractComplexTreeViewer;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.ITreeElementPath;
import org.neontoolkit.gui.navigator.ITreeExtensionHandler;
import org.neontoolkit.gui.navigator.elements.TreeElementPath;

/* 
 * Created on: 13.03.2007
 * Created by: Dirk Wenke
 *
 * Keywords: UI, extendableTreeProvider, Tree extension
 */
/**
 * This is a helper class for the extensions of the extendableTreeProvider
 * extension point.
 */
public class TreeExtensionHandler implements ITreeExtensionHandler {
	//The id to get all root providers;
	private static final String ROOT_ID = "_root_providers_"; //$NON-NLS-1$

	private IConfigurationElement[] _providerConfigurations;
	//Stores the subproviders in a list as the value of the provider itself
	private Hashtable<String, List<String>> _providerChildren;
	
	private Hashtable<String, ITreeDataProvider> _providers;
	
	private AbstractComplexTreeViewer _viewer;

	public TreeExtensionHandler(AbstractComplexTreeViewer viewer, IConfigurationElement[] providerConfigs) {
		_viewer = viewer;
		_providerConfigurations = providerConfigs;
		initialize();
	}
	
	/**
	 * initializes the hierarchy of the providers
	 *
	 */
	private void initialize() {
		//first store the providers with their identifiers
		_providers = new Hashtable<String, ITreeDataProvider>();
		_providerChildren = new Hashtable<String, List<String>>();
		for (IConfigurationElement element:_providerConfigurations) {
			String id = element.getAttribute(NeOnUIPlugin.EXTENDABLE_TREE_PROVIDER_ID);
			if (id != null) {
				_providerChildren.put(id, new ArrayList<String>());
			}
		}
		_providerChildren.put(ROOT_ID, new ArrayList<String>());
		
		//now retrieve the children for the single elements.
		//if a provider is defined to be a subprovider which cannot be
		//determined, it will be shown as root element.
		for (IConfigurationElement element:_providerConfigurations) {
			String id = element.getAttribute(NeOnUIPlugin.EXTENDABLE_TREE_PROVIDER_ID);
			String superId = element.getAttribute(NeOnUIPlugin.EXTENDABLE_TREE_PROVIDER_SUBPROVIDER_OF);
			if (superId == null || _providerChildren.get(superId) == null) {
				superId = ROOT_ID;
			}
			if (id != null) {
				List<String> list = _providerChildren.get(superId);
				list.add(id);
			}
		}
	}
	
	/**
	 * Returns the viewer this element belongs to
	 * @return
	 */
	public AbstractComplexTreeViewer getViewer() {
		return _viewer;
	}
	
	/**
	 * Sets the viewer this element belongs to
	 * @return
	 */
	public void setViewer(AbstractComplexTreeViewer viewer) {
		_viewer = viewer;
	}

	/**
	 * Returns the provider for the given id. If no provider is defined for 
	 * this id, null is returned.
	 * @param id
	 * @return
	 */
	@Override
    public ITreeDataProvider getProvider(String id) {
		ITreeDataProvider prov = _providers.get(id);
		if (prov == null) {
			//prov is not defined or not yet instantiated
			for (IConfigurationElement confElem:_providerConfigurations) {
				if (id.equals(confElem.getAttribute(NeOnUIPlugin.EXTENDABLE_TREE_PROVIDER_ID))) {
					//configuration element found, create real object
					try {
						prov = (ITreeDataProvider)confElem.createExecutableExtension(NeOnUIPlugin.EXTENDABLE_TREE_PROVIDER_CLASS);
						prov.setExtensionHandler(this);
						prov.setId(id);
						_providers.put(id, prov);
						break;
					} catch (CoreException ce) {
						NeOnUIPlugin.getDefault().logError(Messages.TreeExtensionHandler_1+id, ce);
					}
				}
			}
		}
		return prov;
	}
	
	/**
	 * Returns the subproviders of the provider with the given id.
	 * @param id
	 * @return
	 */
	public ITreeDataProvider[] getSubProviders(String id) {
		List<String> children = _providerChildren.get(id);
		List<ITreeDataProvider> subProviders = new ArrayList<ITreeDataProvider>();
		for (String childId:children) {
			ITreeDataProvider prov = getProvider(childId);
			if (prov != null) {
				subProviders.add(prov);
			}
		}
		return subProviders.toArray(new ITreeDataProvider[0]);
	}
	
	/**
	 * Returns the provider which is the parent of the provider with the given id.
	 * @param id
	 * @return
	 */
	public ITreeDataProvider getSuperProvider(String id) {
		for (Iterator<String> i = _providerChildren.keySet().iterator(); i.hasNext();) {
			String parentId = (String) i.next();
			List<String> children = _providerChildren.get(parentId);
			for (int j = 0; j < children.size(); j++) {
				if (id.equals(children.get(j))) {
					return getProvider(parentId);
				}
			}
		}
		return null;
	}
	/**
	 * Returns all providers that are not subprovider of another provider.
	 * @return
	 */
	public ITreeDataProvider[] getRootProviders() {
		return getSubProviders(ROOT_ID);
	}
	
	/**
	 * Computes the path from the given element to the root depending on 
	 * the configuration of the different providers.
	 * @param element
	 * @return
	 */
	public ITreeElementPath[] computePathsToRoot(ITreeElement element) {
		ITreeDataProvider provider = element.getProvider();
		ITreeElementPath [] paths = provider.getPathElements(element);
		ITreeDataProvider superProvider = getSuperProvider(provider.getId());
		while (superProvider != null) {
			ITreeElementPath[] superPaths = superProvider.getPathElements(element);
			List<ITreeElementPath> allPaths = new ArrayList<ITreeElementPath>();
			for (int i = 0; i < superPaths.length; i++) {
				ITreeElementPath currentPath = superPaths[i];
				if (paths.length == 0) {
					try {
						allPaths.add((ITreeElementPath)((TreeElementPath)currentPath).clone());
					} catch (CloneNotSupportedException e) {
						NeOnUIPlugin.getDefault().logError(Messages.TreeExtensionHandler_2, e);
					} catch (ClassCastException e) {
						NeOnUIPlugin.getDefault().logError(Messages.TreeExtensionHandler_3, e);
					}
				}
				for (int j = 0; j < paths.length; j++) {
					try {
						ITreeElementPath path = ((ITreeElementPath)((TreeElementPath)currentPath).clone());
						path.append(paths[j]);
						allPaths.add(path);
					} catch (CloneNotSupportedException e) {
						NeOnUIPlugin.getDefault().logError(Messages.TreeExtensionHandler_4, e);
					} catch (ClassCastException e) {
						NeOnUIPlugin.getDefault().logError(Messages.TreeExtensionHandler_5, e);
					}
				}
			}
			paths = allPaths.toArray(new ITreeElementPath[0]);
			superProvider = getSuperProvider(superProvider.getId());
		}
		return paths;
	}
	
	/**
	 * Returns the ITreeDataProvider instance of the given class or
	 * null if no such instance exists.
	 * @param classType
	 * @return
	 */
	public ITreeDataProvider getProvider(Class<?> classType) {
		for (ITreeDataProvider rootProvider:getRootProviders()) {
			ITreeDataProvider prov = getProvider(classType, rootProvider);
			if (prov != null) {
				return prov;
			}
		}
		return null;
	}
	
	/**
	 * Internal recuirsivly called methdo to retrieve the ITreeDataProvider 
	 * instance of the given class.
	 * @param classType
	 * @param currentProvider
	 * @return
	 */
	private ITreeDataProvider getProvider(Class<?> classType, ITreeDataProvider currentProvider) {
		if (currentProvider.getClass() == classType) {
			return currentProvider;
		}
		for (ITreeDataProvider subProvider:getSubProviders(currentProvider.getId())) {
			ITreeDataProvider prov = getProvider(classType, subProvider);
			if (prov != null) {
				return prov;
			}
		}
		return null;
	}
	
	/**
	 * Disposes the different ITreeDataProviders
	 */
	public void dispose() {
		for (Enumeration<ITreeDataProvider> e=_providers.elements(); e.hasMoreElements();) {
			e.nextElement().dispose();
		}
        _providers.clear();
	}
}
