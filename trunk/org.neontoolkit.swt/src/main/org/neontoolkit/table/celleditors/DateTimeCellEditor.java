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

public class DateTimeCellEditor extends ControlAwareTextCellEditor {

	public DateTimeCellEditor() {
	}

	public DateTimeCellEditor(Composite parent) {
		super(parent);
	}

	public DateTimeCellEditor(Composite parent, int style) {
		super(parent, style);
	}

}
