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

import org.eclipse.swt.widgets.TreeItem;

/* 
 * Created on: 24.03.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Event, Editor
 */
/**
 * Event sent to the listeners of type IEditorListener.
 * This event contains insformation about the modified element in the tree and the new entered
 * data during modification.
 */
public class EditorEvent {
    /**
     * the item taht is modified during the editing process
     */
    private TreeItem _item;
    /**
     * the modified text
     */
    private String _text;
    
    private boolean _regularFinish = true;

    /**
     * Constructor for the Event given the TreeItem that is modified.
     * @param item the item in the tree
     */
    public EditorEvent(TreeItem item) {
        _item = item;
    }
    /**
     * Constructor for the Event given the TreeItem that is modified and the entered text
     * during the modification
     * @param item the item in the tree
     * @param text the new entered text
     * @param regularFinish whether the editing was finished regularly 
     * by pressing enter
     */
    public EditorEvent(TreeItem item, String text, boolean regularFinish) {
        this(item);
        _text = text;
        _regularFinish = regularFinish;
    }
    
    /**
     * Returns the TreeItem that has been modified
     * @return
     */
    public TreeItem getItem() {
        return _item;
    }
    
    /**
     * Returns the modified text
     * @return
     */
    public String getText() {
        return _text;
    }
    
    /**
     * Returns whether the editing process was finished regularly by
     * pressing enter.
     * @return
     */
    public boolean isFinishedRegularly() {
    	return _regularFinish;
    }
}
