/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.table.cursor;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.FocusCellHighlighter;
import org.eclipse.jface.viewers.ViewerCell;

/*
 * Created on 20.05.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class TableCursorCellHighlighter extends FocusCellHighlighter {
	private ColumnViewer _viewer;
	private AbstractColumnCursor _cursor;

	public TableCursorCellHighlighter(ColumnViewer viewer, AbstractColumnCursor cursor) {
		super(viewer);
		_viewer = viewer;
		_cursor = cursor;
	}
	
	public ColumnViewer getViewer() {
		return _viewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.FocusCellHighlighter#focusCellChanged(org.eclipse.jface.viewers.ViewerCell)
	 */
	@Override
	protected void focusCellChanged(ViewerCell cell) {
		super.focusCellChanged(cell);
		if( ! getViewer().isCellEditorActive() && cell != null) {
			_cursor.selectCell(cell);
			_cursor.setVisible(true);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.FocusCellHighlighter#init()
	 */
	@Override
	protected void init() {
		ColumnViewerEditorActivationAdapter activationListener = new ColumnViewerEditorActivationAdapter() {
			@Override
			public void afterEditorDeactivated(
					ColumnViewerEditorDeactivationEvent event) {
				_cursor.setVisible(true);
				_cursor.selectCell(getFocusCell());
			}

			@Override
			public void beforeEditorActivated(
					ColumnViewerEditorActivationEvent event) {
				_cursor.setVisible(false);
			}
		};
		
		_viewer.getColumnViewerEditor().addEditorActivationListener(activationListener);
	}
}
