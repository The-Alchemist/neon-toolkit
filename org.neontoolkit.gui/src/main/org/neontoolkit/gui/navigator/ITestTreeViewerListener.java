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

import org.eclipse.swt.widgets.Widget;

/**
 * This is a listener that can be attached to a ComplexTreeViewer.
 * These listeners are only intended to be used during plugin tests.
 * @author Dirk Wenke
 */
public interface ITestTreeViewerListener {
    /**
     * Called if a child has been added. The parent TreeItem is passed 
     * and the data objects for parent and child as well.
     * The itemCreated flag indicates whether a TreeItem has been created for
     * the child (which is not the case, if the items are not yet visible).
     */
    void childrenAdded(Widget parentItem, Object parent, Object child, boolean itemCreated);
    /**
     * Called if a child has been removed. The parent TreeItem is passed 
     * and the data objects for parent and child as well.
     * The itemDisposed flag indicates whether the TreeItem for the child
     * had to be disposed (which is not necessary if no TreeItems have been created
     * yet for the children).
     */
    void childRemoved(Widget item, Object parent, Object children, boolean itemDisposed);
    /**
     * Called if an item has been changed. The item is passed and the old and new
     * data objects as well.
     */
    void itemUpdated(Widget item, Object oldElement, Object newElement);
}
