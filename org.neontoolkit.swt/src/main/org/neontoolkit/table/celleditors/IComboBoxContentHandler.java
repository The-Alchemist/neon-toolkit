/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.table.celleditors;

public interface IComboBoxContentHandler {
	Object[] getElements();
	String toString(Object object);
}
