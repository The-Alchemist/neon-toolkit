/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.table.cursor;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;

/*
 * Created on 20.05.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class ColumnViewerEditorActivationAdapter extends
		ColumnViewerEditorActivationListener {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnViewerEditorActivationListener#afterEditorActivated(org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent)
	 */
	@Override
	public void afterEditorActivated(ColumnViewerEditorActivationEvent event) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnViewerEditorActivationListener#afterEditorDeactivated(org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent)
	 */
	@Override
	public void afterEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnViewerEditorActivationListener#beforeEditorActivated(org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent)
	 */
	@Override
	public void beforeEditorActivated(ColumnViewerEditorActivationEvent event) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnViewerEditorActivationListener#beforeEditorDeactivated(org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent)
	 */
	@Override
	public void beforeEditorDeactivated(
			ColumnViewerEditorDeactivationEvent event) {
	}
}
