/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.table.model;

import java.util.EventObject;

public class TableChangeEvent extends EventObject {
	public static final int ROW_ADDED_TYPE = 0;
	public static final int ROW_REMOVED_TYPE = 1;
	public static final int CELL_CHANGED_TYPE = 3;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3432445730021945617L;
	
	private Object _oldValue;
	private Object _newValue;
	
	public TableChangeEvent(ITableCell cell, Object oldValue, Object newValue) {
		super(cell);
		_oldValue = oldValue;
		_newValue = newValue;
	}
	
	public TableChangeEvent(ITableRow row) {
		super(row);
	}
	
	public Object getOldValue() {
		return _oldValue;
	}
	
	public Object getNewValue() {
		return _newValue;
	}
}
