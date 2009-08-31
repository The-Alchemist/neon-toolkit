/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.swt.dialogs;

/* version:			1.0
 * date:			25.01.2004
 * author:			Dirk Wenke
 * function:		
 * keywords:		
 * change history:
 */
/**
 * Class documentation.
 * 
 * @author Dirk Wenke
 */
public interface ListSelectionDialogModel {

    public Object[] getListItems(String s);

    public boolean isItem(Object item);

    public boolean needsUpdate(int index, int amount);
}
