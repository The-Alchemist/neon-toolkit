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
