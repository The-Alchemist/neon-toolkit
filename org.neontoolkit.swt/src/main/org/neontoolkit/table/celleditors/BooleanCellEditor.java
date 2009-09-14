/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.table.celleditors;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;

/*
	this class reflects the OS boolean ComboBoxCellEditor
	with possible values "", "true", "false"

	dropdown handling is not done via integer values but strings

	Author: Jörg
*/

public class BooleanCellEditor extends ComboBoxCellEditor {
		
	private static final String[] possibleValues = new String[]{"", "true", "false"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	public BooleanCellEditor(Composite parent) {
		super(parent, possibleValues);
	}
	
	public BooleanCellEditor(Composite parent, int style) {
		super(parent, possibleValues, style);
	}

	// returns the selected value as a string
	// "", "true" or "false"	
    @Override
	protected Object doGetValue() {
        return possibleValues[(Integer) super.doGetValue()];
    }
    
    @Override
	protected void doSetValue(Object value) {
    	// set value - if nothing matches set to default. Here: "0"
    	int index = -1;
    	for (int x=0; x<possibleValues.length; x++) {
    		if (value.toString().equals(possibleValues[x])) {
    			index = x;
    		}
    	}
    	if (index != -1) {
    		super.doSetValue(index);
    	} else {
    		super.doSetValue(0);
    	}
    }

    
//	public BooleanCellEditor(Composite parent, int style) {
//		super(parent, handler, style);
//	}
	
}
