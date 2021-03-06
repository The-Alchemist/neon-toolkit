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
