/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.table.model;

public interface ITableRow {
	ITableCell[] getCells();
	ITableCell getCellAt(int index);
	int getCellCount();
	ITableContentModel getContentModel();
	int indexOf(ITableCell cell);
	boolean isRemoveable();
	boolean isEmpty();
	
	/**
	 * This method can be used to bind a data object to this row
	 * @param data
	 */
	public void setData(Object data);
	
	/**
	 * Returns the data object that is bound to this row.
	 * @return
	 */
	public Object getData();
}
