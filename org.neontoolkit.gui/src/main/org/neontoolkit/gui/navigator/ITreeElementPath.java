/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.navigator;


/**
 * An interface for paths of ITreeElements in a tree.
 */

public interface ITreeElementPath extends Cloneable {

	public void append(ITreeElement element);
	
	public void append(ITreeElementPath path);
	
	public ITreeElement get(int index);

	public void insert(int index, ITreeElement element);

	public ITreeElement[] toArray();
	
	public int length();
	
}
