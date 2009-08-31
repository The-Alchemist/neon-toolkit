/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 05.02.2009
 * Created by: werner
 ******************************************************************************/
package org.neontoolkit.table;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.swt.SWT;

/**
 * @author werner
 *
 */
public class StandardViewer {

    protected boolean isValidEditingKey(ColumnViewerEditorActivationEvent event) {
        int keyCode = event.keyCode;
        if ((event.keyCode & SWT.MODIFIER_MASK) == event.keyCode) {
            //only modifier pressed, nothing to do
            return false;
        }
        else if (keyCode == SWT.ARROW_DOWN ||
                keyCode == SWT.ARROW_UP ||
                keyCode == SWT.ARROW_LEFT ||
                keyCode == SWT.ARROW_RIGHT) {
            return false;
        }
        else if (keyCode == SWT.ESC) {
            return false;
        }
        return true;
    }
    
}
