/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.table;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.neontoolkit.table.model.ITableRow;


/*
 * Created on 29.10.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class TableViewerComparator extends ViewerComparator {
	private int _sortColumn;
	private boolean _ascendingOrder; 
	
	public TableViewerComparator(int sortColumn, boolean ascendingOrder) {
		super();
		_sortColumn = sortColumn;
		_ascendingOrder = ascendingOrder;
	}

	@Override
	public int category(Object element) {
	    if (element instanceof ITableRow) {
	        if (((ITableRow)element).isEmpty()) {
	            return 1;
	        }
	    }
	    return super.category(element);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
        int cat1 = category(e1);
        int cat2 = category(e2);

        if (cat1 != cat2) {
            return cat1 - cat2;
        }
        
		if (viewer instanceof TableViewer) {
			TableViewer _viewer = (TableViewer) viewer;
			IBaseLabelProvider labelProvider = _viewer.getLabelProvider();
			if (labelProvider instanceof ITableLabelProvider) {
				ITableLabelProvider tableLabel = (ITableLabelProvider) labelProvider;
				String e1Label = tableLabel.getColumnText(e1, _sortColumn);
				String e2Label = tableLabel.getColumnText(e2, _sortColumn);
				if (_ascendingOrder) {
					return getComparator().compare(e1Label, e2Label);
				}
				else {
					return getComparator().compare(e2Label, e1Label);
				}
			}
		}
		return super.compare(viewer, e1, e2);
	}
	
	public void setSortColumn(int sortColumn) {
		_sortColumn = sortColumn;
	}
	
	public int getSortColumn() {
		return _sortColumn;
	}
	
	public void setAscendingOrder(boolean isAscending) {
		_ascendingOrder = isAscending;
	}
	
	public boolean isAscendingOrder() {
		return _ascendingOrder;
	}
}
