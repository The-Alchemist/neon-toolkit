/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.swt.dialog.filesystem;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/* 
 * Created on 12.01.2007
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 * 
 * Copyright (c) 2007 ontoprise technologies GmbH.
 */
public class FileSystemLabelProvider extends DecoratingLabelProvider {
	public FileSystemLabelProvider() {
		super(new WorkbenchLabelProvider(), PlatformUI.getWorkbench()
                .getDecoratorManager().getLabelDecorator());
	}
}
