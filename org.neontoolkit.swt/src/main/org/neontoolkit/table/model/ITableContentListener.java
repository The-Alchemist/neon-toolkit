/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.table.model;

public interface ITableContentListener {
	boolean acceptRowRemoval(TableChangeEvent event);
	boolean acceptRowAddition(TableChangeEvent event);
	boolean acceptCellAddition(TableChangeEvent event);
	boolean acceptCellChange(TableChangeEvent event);
	void cellChanged(TableChangeEvent event);
	void cellAdded(TableChangeEvent event);
	void rowAdded(TableChangeEvent event);
	void rowRemoved(TableChangeEvent event);
}
