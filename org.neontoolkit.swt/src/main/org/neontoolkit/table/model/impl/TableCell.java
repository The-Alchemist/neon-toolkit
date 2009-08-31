/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.table.model.impl;

import org.neontoolkit.table.model.CellNotChangeableException;
import org.neontoolkit.table.model.ITableCell;
import org.neontoolkit.table.model.ITableRow;
import org.neontoolkit.table.model.TableChangeEvent;

/* 
 * Created on 12.07.2007
 * Created by Dirk Wenke 
 *
 * Keywords: 
 */
/**
 * 
 */
public class TableCell implements ITableCell {
	private TableRow _row;
	protected Object _value;
	private Object _data;
	private boolean _changeable = true;
	private boolean _enabled = true;
	
	public TableCell(TableRow row, Object value) {
		_row = row;
		_value = value;
		if (row != null) {
			_row.createCell(this);
		}
	}

	public TableCell(TableRow row, Object value, boolean enabled) {
		this(row, value, enabled, true);
	}
	public TableCell(TableRow row, Object value, boolean enabled, boolean changeable) {
		this(row, value);
		if (!enabled) {
			changeable = false;
		}
		_changeable = changeable;
		_enabled = enabled;
	}
	
	public ITableRow getRow() {
		return _row;
	}

	public String getString() {
		return _value != null ? _value.toString() : ""; //$NON-NLS-1$
	}

	public Object getValue() {
		return _value;
	}

	public boolean isChangeable() {
		return _changeable;
	}
	
	public void setValue(Object value) {
		if (!isChangeable()) {
			throw new CellNotChangeableException();
		}
		else {
			//if the value is equal to the current value, do nothing 
			if (_value == null ? value == null : _value.equals(value)) {
				return;
			}
			TableChangeEvent event = new TableChangeEvent(this, _value, value);
			if (_row.firePreCellChangeEvent(event)) {
				_value = value;
				_row.fireCellChangeEvent(event);
			}
		}
	}
	
	public void setData(Object data) {
	    _data = data;
	}
	
	public Object getData() {
	    return _data;
	}

	public void setString(String value) {
        if (value != null && value.length() == 0) {
            value = null;
        }
		setValue(value);
	}

	public boolean isEmpty() {
		return getValue() == null;
	}
	
	@Override
	public String toString() {
		return _value != null ? _value.toString() : ""; //$NON-NLS-1$
	}
	
	public void setValueNoEvent(Object value) {
		if (!isChangeable()) {
			if (!value.equals(_value)) {
				throw new CellNotChangeableException();
			}
		}
		else {
			_value = value;
		}
		
	}

	/* (non-Javadoc)
	 * @see com.ontoprise.table.model.ITableCell#isEnabled()
	 */
	public boolean isEnabled() {
		return _enabled;
	}
}
