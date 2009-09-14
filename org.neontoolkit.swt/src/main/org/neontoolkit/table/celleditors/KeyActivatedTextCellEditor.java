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

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

public class KeyActivatedTextCellEditor extends TextCellEditor {
	private ColumnViewerEditorActivationEvent _activationEvent;

	public KeyActivatedTextCellEditor(Composite parent, int style) {
		super(parent, style);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#activate(org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent)
	 */
	@Override
	public void activate(ColumnViewerEditorActivationEvent activationEvent) {
		super.activate(activationEvent);
		_activationEvent = activationEvent;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.TextCellEditor#doSetFocus()
	 */
	@Override
	protected void doSetFocus() {
		super.doSetFocus();
		if (_activationEvent != null && _activationEvent.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
			text.setText(""+_activationEvent.character); //$NON-NLS-1$
			text.setSelection(1,1);
		}
	}
}
