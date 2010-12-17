/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.properties;

import org.eclipse.jface.action.Action;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.SharedImages;

/* 
 * Created on: 20.06.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Action, EntityProperties
 */
/**
 * Action in the toolbar of the EntityProperties view which allows to pin the current
 * property page so that no other property page is shown even if such en element is 
 * selected.
 */

public class PinAction extends Action {
	private EntityPropertiesView _view;
	
	public PinAction(EntityPropertiesView view) {
		super(Messages.PinAction_0); 
		_view = view;
		setChecked(_view.isPinned());
		setImageDescriptor(NeOnUIPlugin.getDefault().getImageRegistry().getDescriptor(SharedImages.PIN_ACTION));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		_view.setPinned(isChecked());
	}
}
