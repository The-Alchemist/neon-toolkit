/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.table.celleditors;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;

public class ObjectComboBoxCellEditor extends ComboBoxCellEditor {
	private Object[] _objects;
	private IComboBoxContentHandler _handler;

	public ObjectComboBoxCellEditor(Composite composite, IComboBoxContentHandler handler) {
		super(composite, new String[0]);
		_handler = handler;
	}

	public Object[] getObjectItems() {
		if (_objects == null) {
			_objects = _handler.getElements();
			String[] items = new String[_objects.length];
			for (int i=0; i<_objects.length; i++) {
				items[i] = _handler.toString(_objects[i]);
			}
			setItems(items);
		}
		return _objects;
	}
	
	@Override
	protected void doSetValue(Object value) {
		Object[] objectItems = getObjectItems();
        if (value instanceof String[]) {
            for (int i = 0; i < objectItems.length; i++) {
                if (objectItems[i] == value) {
                    super.doSetValue(new Integer(i));
                }
            }
        } else {
            super.doSetValue(value);
        }
	}
	
	public void clearObjectItems() {
		_objects = null;
	}
	
	@Override
	public String[] getItems() {
		getObjectItems();
		return super.getItems();
	}
}
