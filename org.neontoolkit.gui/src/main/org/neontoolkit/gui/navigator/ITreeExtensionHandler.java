/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.navigator;



/* 
 * Created on: 14.11.2007
 * Created by: Michael Erdmann
 *
 * Keywords: UI, extendableTreeProvider, Tree extension, interface
 */
/**
 * This is an interface for extensions of the extendableTreeProvider
 * extension point.
 */
public interface ITreeExtensionHandler {

	/**
	 * Returns the viewer this element belongs to
	 * @return
	 */
	public AbstractComplexTreeViewer getViewer();
	
	/**
	 * Returns the provider for the given id. If no provider is defined for 
	 * this id, null is returned.
	 * @param id
	 * @return
	 */
	public ITreeDataProvider getProvider(String id);
	
	/**
	 * Returns the subproviders of the provider with the given id.
	 * @param id
	 * @return
	 */
	public ITreeDataProvider[] getSubProviders(String id);
	
	/**
	 * Returns the provider which is the parent of the provider with the given id.
	 * @param id
	 * @return
	 */
	public ITreeDataProvider getSuperProvider(String id);
	
	/**
	 * Returns all providers that are not subprovider of another provider.
	 * @return
	 */
	public ITreeDataProvider[] getRootProviders();
	
	/**
	 * Computes the path from the given element to the root depending on 
	 * the configuration of the different providers.
	 * @param element
	 * @return
	 */
	public ITreeElementPath[] computePathsToRoot(ITreeElement element);
	
	/**
	 * Returns the ITreeDataProvider instance of the given class or
	 * null if no such instance exists.
	 * @param classType
	 * @return
	 */
	public ITreeDataProvider getProvider(Class<?> classType);

	/**
	 * Disposes the different ITreeDataProviders
	 */
	public void dispose();
}
