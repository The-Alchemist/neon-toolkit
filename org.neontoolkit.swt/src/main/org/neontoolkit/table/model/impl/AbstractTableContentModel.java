/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.table.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.neontoolkit.table.model.ITableContentListener;
import org.neontoolkit.table.model.ITableContentModel;
import org.neontoolkit.table.model.ITableRow;
import org.neontoolkit.table.model.TableChangeEvent;


public abstract class AbstractTableContentModel implements ITableContentModel {
	private ListenerList _listeners = new ListenerList();
	protected List<ITableRow> _rows = new ArrayList<ITableRow>();

	void createRow(TableRow row) {
		_rows.add(row);
	}
	
	public void addContentListener(ITableContentListener listener) {
		_listeners.add(listener);
	}

	public void removeContentListener(ITableContentListener listener) {
		_listeners.remove(listener);
	}
	
	public void dispose() {
		_listeners.clear();
	}

	public void addRow(ITableRow row) {
		if (firePreEvent(new TableChangeEvent(row), TableChangeEvent.ROW_ADDED_TYPE)) {
			_rows.add(row);
			fireEvent(new TableChangeEvent(row), TableChangeEvent.ROW_ADDED_TYPE);
		}
	}

	public ITableRow removeRow(ITableRow row) {
		if (_rows.contains(row)) {
			if (firePreEvent(new TableChangeEvent(row), TableChangeEvent.ROW_REMOVED_TYPE)) {
				_rows.remove(row);
				fireEvent(new TableChangeEvent(row), TableChangeEvent.ROW_REMOVED_TYPE);
				return row;
			}
		}
		return null;
	}
	
	public ITableRow[] getTableRows() {
		return _rows.toArray(new ITableRow[0]);
	}

	public ITableRow getLastRow() {
		int count = getRowCount();
		if (count > 0) {
			return _rows.get(count-1);
		}
		else {
			return null;
		}
	}
	
	public int getColumnCount() {
		int max = 0;
		for (ITableRow row: _rows) {
			if (row.getCellCount() > max) {
				max = row.getCellCount();
			}
		}
		return max;
	}
	
	public int getRowCount() {
		return _rows.size();
	}

	boolean firePreEvent(TableChangeEvent event, int type) {
        Object[] listeners = _listeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            ITableContentListener listener = (ITableContentListener) listeners[i];
        	switch (type) {
        	case TableChangeEvent.ROW_ADDED_TYPE: 
        		if (!listener.acceptRowAddition(event)) {
        			return false;
        		}
        		break;
        	case TableChangeEvent.ROW_REMOVED_TYPE: 
        		if (!listener.acceptRowRemoval(event)) {
        			return false;
        		}
        		break;
	    	case TableChangeEvent.CELL_CHANGED_TYPE:
	    		if (!listener.acceptCellChange(event)) {
	    			return false;
	    		}
	    		break;
	    	}
        }
        return true;
	}
	
	void fireEvent(final TableChangeEvent event, final int type) {
        Object[] listeners = _listeners.getListeners();
        //iterating backwards over the listeners so that the first registered listener is notified 
        //the last => the first listener should be the viewer that will do updates, etc.
        for (int i = listeners.length-1; i >= 0; i--) {
            final ITableContentListener listener = (ITableContentListener) listeners[i];
            SafeRunnable.run(new SafeRunnable() {
                public void run() {
                	switch (type) {
                	case TableChangeEvent.ROW_ADDED_TYPE: 
                		listener.rowAdded(event);
                		break;
	            	case TableChangeEvent.ROW_REMOVED_TYPE: 
	            		listener.rowRemoved(event);
	            		break;
		        	case TableChangeEvent.CELL_CHANGED_TYPE: 
		        		listener.cellChanged(event);
		        		break;
                	}
	        	}
            });
        }
	}
}
