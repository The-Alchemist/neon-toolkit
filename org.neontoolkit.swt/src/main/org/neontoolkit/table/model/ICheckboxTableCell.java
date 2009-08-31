/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.table.model;

public interface ICheckboxTableCell extends ITableCell {
	boolean isChecked();
	void setChecked(boolean checked);
}
