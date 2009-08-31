/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.table.model;

public interface ITableContentModel {
	void addContentListener(ITableContentListener listener);
	void removeContentListener(ITableContentListener listener);
	ITableRow addRow();
	ITableRow getLastRow();
	ITableRow[] getTableRows();
	int getColumnCount();
	int getRowCount();
	ITableRow removeRow(ITableRow row);
	void dispose();
	void initializeModel(Object selection);
}
