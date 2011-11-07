/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

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
