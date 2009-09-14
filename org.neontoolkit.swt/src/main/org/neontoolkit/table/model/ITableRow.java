/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

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
