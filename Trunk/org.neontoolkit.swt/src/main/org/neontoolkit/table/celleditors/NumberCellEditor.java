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

import org.eclipse.swt.widgets.Composite;

public class NumberCellEditor extends ControlAwareTextCellEditor {

	public NumberCellEditor() {
	}

	public NumberCellEditor(Composite parent) {
		super(parent);
	}

	public NumberCellEditor(Composite parent, int style) {
		super(parent, style);
	}

}
