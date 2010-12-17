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
