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


/* 
 * Created on: 24.03.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Editor, Listener
 */
/**
 * Listener for the editing process in the MTreeView 
 */
public interface IEditorListener {

    /**
     * This method is called if the editing process is finished 
     */
    public void editingFinished(EditorEvent event);
    /**
     * This method is called if the editing process is aborted
     */
    public void editingCancelled(EditorEvent event);
}
