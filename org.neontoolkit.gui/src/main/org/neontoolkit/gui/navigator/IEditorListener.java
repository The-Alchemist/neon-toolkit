/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

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
