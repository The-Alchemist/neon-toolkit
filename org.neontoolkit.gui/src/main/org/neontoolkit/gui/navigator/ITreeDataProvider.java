/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator;

import org.eclipse.swt.graphics.Image;

/* 
 * Created on: 23.12.2004
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Navigator, extendableTreeProvider
 */
/**
 * This interface has to be implemented by all extensions of the extenableTreeprovider
 * extension point. This interface provides methods to retrieve the elements provided
 * by the provider and their representation
 */

public interface ITreeDataProvider {
	/** 
	 * called when the tree based on this provider is closed.
	 * NOTE: not yet implemented
	 */
	public void dispose();

	/**
	 * Returns the number of children of the given element.
	 * @param parentElement
	 * @return
	 */
	public int getChildCount(ITreeElement parentElement);

	/** 
	 * Retrieves the children of the given element.
	 * 
	 * @param parentElement
	 * @param topIndex
	 * @param amount
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public ITreeElement[] getChildren(ITreeElement parentElement, int topIndex, int amount);

	/**
	 * Retrieves the root elements of this provider.
	 * 
	 * @param parentElement
	 * @param topIndex
	 * @param amount
	 * @return
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public ITreeElement[] getElements(ITreeElement parentElement, int topIndex, int amount);

	/**
	 * If this provider provides elements that are contained in th path between the given element
	 * and the root of the tree, this part of the path has to be returned. The ordering has to be
	 * from the parent to the children.
	 * As multiple inheritance is allowed, multiple paths may exists, so an array has to be 
	 * returned.
	 * @param element
	 * @return
	 */
	public ITreeElementPath[] getPathElements(ITreeElement element);

	/**
	 * Returns the id of this provider
	 * @return
	 */
	public String getId();
	/**
	 * Returns the image for the given element or null if none.
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(ITreeElement element);

	/**
	 * Returns the text to be displayed for the given element
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(ITreeElement element);

	/**
	 * This method is called by the framework to set the id of this provider.
	 * @param id
	 */
	public void setId(String id);
	
	/**
	 * Passes the used TreeExtensionHandler that manages the current 
	 * instance of this class.
	 * @param handler
	 */
	public void setExtensionHandler(ITreeExtensionHandler handler);
	
	/**
	 * Returns the TreeExtensionHandler for this provider
	 * @return
	 */
	public ITreeExtensionHandler getExtensionHandler();
	
	/**
	 * This method should return true, if elements from this provider can be used for a drag operation.
	 * If drag is allowed, the handling can be controlled by the DragSourceListener returned by
	 * getDragSourceListener().
	 * @return
	 */
	public boolean isDragSupported();
	
	/**
	 * This method should return true, if objects can be dropped on elements from this provider in a
	 * drag&drop operation.
	 * If drop is allowed, the handling can be controlled by the DropTargetListener returned by
	 * getDropTargetListener().
	 * @return
	 */
	public boolean isDropSupported();
}
