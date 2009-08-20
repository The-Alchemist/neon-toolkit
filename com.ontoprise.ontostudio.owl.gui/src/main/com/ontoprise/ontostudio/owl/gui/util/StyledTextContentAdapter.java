/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util;

import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

public class StyledTextContentAdapter implements IControlContentAdapter {

    public String getControlContents(Control control) {
        return ((StyledText) control).getText();
    }

    public int getCursorPosition(Control control) {
        return ((StyledText) control).getCaretOffset();
    }

    public Rectangle getInsertionBounds(Control control) {
        StyledText text = (StyledText) control;
        Point caretOrigin = text.getCaret().getLocation();
        return new Rectangle(caretOrigin.x, caretOrigin.y, 1, text.getLineHeight());
    }

    public void insertControlContents(Control control, String contents, int cursorPosition) {
        Point selection = ((StyledText) control).getSelection();
        ((StyledText) control).insert(contents);
        // Insert will leave the cursor at the end of the inserted text. If this
        // is not what we wanted, reset the selection.
        if (cursorPosition < contents.length()) {
            ((StyledText) control).setSelection(selection.x + cursorPosition, selection.x + cursorPosition);
        }
    }

    public void setControlContents(Control control, String contents, int cursorPosition) {
        String text = ((StyledText) control).getText();
        int offset = ((StyledText) control).getSelection().x;
        int lastPosition = getLastPosition(text, offset);
        ((StyledText) control).replaceTextRange(lastPosition, offset - lastPosition, contents);

        ((StyledText) control).setSelection(cursorPosition);
    }

    public void setCursorPosition(Control control, int index) {
        ((StyledText) control).setSelection(new Point(index, index));
    }

    private int getLastPosition(String haystack, int fromIndex) {
        int blankIndex = haystack.lastIndexOf(" ", fromIndex); //$NON-NLS-1$
        int bracketIndex = haystack.lastIndexOf("(", fromIndex); //$NON-NLS-1$
        if (blankIndex < 0 && bracketIndex < 0) {
            return 0;
        }
        if (blankIndex > bracketIndex) {
            return blankIndex + 1;
        } else {
            return bracketIndex + 1;
        }
    }
}
