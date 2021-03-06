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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ControlAwareTextCellEditor extends TextCellEditor {

    private ColumnViewerEditorActivationEvent _activationEvent;

    public ControlAwareTextCellEditor() {
        super();
    }

    public ControlAwareTextCellEditor(Composite parent, int style) {
        super(parent, style);
    }

    public ControlAwareTextCellEditor(Composite parent) {
        super(parent);
    }

    public Text getTextControl() {
        return (Text) getControl();
    }
    
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
            if (_activationEvent.keyCode != SWT.CR) {
                getTextControl().setText(""+_activationEvent.character); //$NON-NLS-1$
                getTextControl().setSelection(1,1);
            }
        }
    }
}
