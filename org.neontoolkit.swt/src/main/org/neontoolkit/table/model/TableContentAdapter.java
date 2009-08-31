/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.table.model;

public class TableContentAdapter implements ITableContentListener {

	public boolean acceptCellChange(TableChangeEvent event) {
		return true;
	}

	public boolean acceptCellAddition(TableChangeEvent event) {
		return true;
	}

	public boolean acceptRowAddition(TableChangeEvent event) {
		return true;
	}

	public boolean acceptRowRemoval(TableChangeEvent event) {
		return true;
	}

	public void cellAdded(TableChangeEvent event) {
	}

	public void cellChanged(TableChangeEvent event) {
	}

	public void rowAdded(TableChangeEvent event) {
	}

	public void rowRemoved(TableChangeEvent event) {
	}
}
