/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 10.02.2009
 * Created by: Dirk Wenke
 ******************************************************************************/
package org.neontoolkit.table.cursor;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;

/**
 * @author Dirk Wenke
 *
 */
public class TableCursor extends AbstractColumnCursor {
    private TableViewerColumn[] _columns;

    public TableCursor(TableViewer viewer, TableViewerColumn[] columns) {
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
