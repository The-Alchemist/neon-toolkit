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

import org.eclipse.core.runtime.IProgressMonitor;

/*
 * Created on 04.06.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
/**
 * TreeDataProviders that implement this interface may support save operations for
 * elements in the tree.
 */
public interface ISaveableProvider {
	/**
	 * Returns true if the given element has changed so that a save is possible.
	 * Dirty elements in the tree are marked with a leading '&gt;'.
	 * @param element
	 * @return
	 */
	boolean isDirty(ITreeElement element);
	
	/**
	 * Returns true if the save as operation is supported for the given element.
	 * @param element
	 * @return
	 */
	boolean isSaveAsAllowed(ITreeElement element);
	
	/**
	 * This method is called if the normal save operation has been started.
	 * @param progress
	 * @param element
	 */
	void doSave(IProgressMonitor progress, ITreeElement element);
	
	/**
	 * This method is called, if the save as operation is started for the given element.
	 * @param element
	 */
	void doSaveAs(ITreeElement element);
}
