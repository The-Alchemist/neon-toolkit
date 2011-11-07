/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.fieldassist;

import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

public class DummyContentAdapter implements IControlContentAdapter {
	private TextContentAdapter _adapter = new TextContentAdapter();

	public void setTextContentAdapter(TextContentAdapter adapter) {
		_adapter = adapter;
	}

	public void insertControlContents(Control control, String text, int cursorPosition) {
		_adapter.insertControlContents(control, text, cursorPosition);
	}
	

	public String getControlContents(Control control) {
		return _adapter.getControlContents(control);
	}

	public int getCursorPosition(Control control) {
		return _adapter.getCursorPosition(control);
	}

	public Rectangle getInsertionBounds(Control control) {
		return _adapter.getInsertionBounds(control);
	}

	public void setControlContents(Control control, String contents, int cursorPosition) {
		_adapter.setControlContents(control, contents, cursorPosition);
		
	}

	public void setCursorPosition(Control control, int index) {
		_adapter.setCursorPosition(control, index);
	}
}
