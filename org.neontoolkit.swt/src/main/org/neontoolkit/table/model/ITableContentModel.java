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
