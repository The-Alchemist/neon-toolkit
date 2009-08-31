/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

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
