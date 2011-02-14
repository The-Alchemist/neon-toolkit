/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.table.cursor;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;

/**
 * @author Dirk Wenke
 *
 */
public class TreeTableCursor extends AbstractColumnCursor {
    private TreeViewerColumn[] _columns;

    public TreeTableCursor(TreeViewer viewer, TreeViewerColumn[] columns) {
        super(viewer);
        _columns = columns;
    }
    
    @Override
    public int getAlignment(int columnIndex) {
        return _columns[columnIndex].getColumn().getAlignment();
    }

    @Override
    public int getColumnCount() {
        return _columns.length;
    }

}
