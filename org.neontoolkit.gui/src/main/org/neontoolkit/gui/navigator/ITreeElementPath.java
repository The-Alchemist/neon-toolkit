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
