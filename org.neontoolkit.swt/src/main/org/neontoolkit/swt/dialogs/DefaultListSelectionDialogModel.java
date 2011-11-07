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

import java.util.ArrayList;
import java.util.List;

/*
 * (c) Copyright 2003 ontoprise GmbH.
 * All Rights Reserved.
 *
 * Created on 25.01.2004
 * 
 * author:			Dirk Wenke
 * 
 */
public class DefaultListSelectionDialogModel implements ListSelectionDialogModel {

    private Object[] _allItems;
    private Object[] _itemReminder;
    private String _stringReminder = null;
    private boolean _cache;

    public DefaultListSelectionDialogModel(Object[] items, boolean cacheOn) {
        _allItems = items;
        _cache = cacheOn;
    }

    public Object[] getListItems(String s) {
        if (_cache && _stringReminder != null && s.startsWith(_stringReminder)) {
            //a letter has been appended at the end, so the items in the list
            // have only to be refined.
            _itemReminder = filter(_itemReminder, s);
        } else {
            //calculate the items of the list
            _itemReminder = filter(_allItems, s);
        }
        _stringReminder = s;
        return _itemReminder;
    }

    public boolean isItem(Object item) {
        return true;
    }

    private Object[] filter(Object[] elements, String startString) {
        List<Object> itemList = new ArrayList<Object>();
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].toString().startsWith(startString)) {
                itemList.add(elements[i]);
            }
        }
        return itemList.toArray();
    }

    public boolean needsUpdate(int index, int amount) {
        return false;
    }
}
