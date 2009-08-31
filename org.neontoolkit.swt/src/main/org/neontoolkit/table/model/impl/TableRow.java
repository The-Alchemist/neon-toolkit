/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.table.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.neontoolkit.table.model.ITableCell;
import org.neontoolkit.table.model.ITableContentModel;
import org.neontoolkit.table.model.ITableRow;
import org.neontoolkit.table.model.TableChangeEvent;


/* 
 * Created on 12.07.2007
 * Created by Dirk Wenke 
 *
 * Keywords: 
 */

public class TableRow implements ITableRow {
	private AbstractTableContentModel _model;
	private List<ITableCell> _cells;
	private boolean _removeable = false;
	private Object _data;
	
	public TableRow(AbstractTableContentModel model, boolean removeable) {
		_model = model;
		_cells = new ArrayList<ITableCell>();
		_model.createRow(this);
		_removeable = removeable;
	}
	
	public TableRow(AbstractTableContentModel model, Object[] cellValues) {
		this(model, cellValues, false, true);
	}

	public TableRow(AbstractTableContentModel model, Object[] cellValues, boolean removeable) {
		this(model, cellValues, removeable, true);
	}

   public TableRow(AbstractTableContentModel model, Object[] cellValues, boolean removeable, boolean editable) {
        this(model, removeable);
        for (int i=0; i<cellValues.length; i++) {
            new TableCell(this, cellValues[i], editable);
        }
    }

	void createCell(TableCell cell) {
		_cells.add(cell);
	}

	public ITableCell getCellAt(int index) {
		return _cells.get(index);
	}

	public ITableCell[] getCells() {
		return _cells.toArray(new ITableCell[0]);
	}
	
	public int getCellCount() {
		return _cells.size();
	}
	
	public ITableContentModel getContentModel() {
		return _model;
	}

	@Override
	public int indexOf(ITableCell cell) {
	    return _cells.indexOf(cell);
	}
	
	public boolean isEmpty() {
		for (ITableCell cell: _cells) {
			if (!cell.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public boolean isRemoveable() {
		return _removeable;
	}
	
	public void fireCellChangeEvent(TableChangeEvent event) {
		_model.fireEvent(event, TableChangeEvent.CELL_CHANGED_TYPE);
	}

	public boolean firePreCellChangeEvent(TableChangeEvent event) {
		return _model.firePreEvent(event, TableChangeEvent.CELL_CHANGED_TYPE);
	}

    @Override
    public Object getData() {
        return _data;
    }

    @Override
    public void setData(Object data) {
        _data = data;
    }
}
