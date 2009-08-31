/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/
package org.neontoolkit.table.model;

/* 
 * Created on 12.07.2007
 * Created by Dirk Wenke 
 *
 * Keywords: 
 */

public interface ITableCell {
	ITableRow getRow();
	boolean isChangeable();
	boolean isEnabled();
	boolean isEmpty();
	String getString();
	void setString(String value) throws CellNotChangeableException;
	void setValue(Object value) throws CellNotChangeableException;
	void setData(Object data);
	Object getData();
	Object getValue();
}
