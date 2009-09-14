/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.swt.decorator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ILabelDecorator;

/* 
 * Created on 12.02.2007
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 * 
 * Copyright (c) 2007 ontoprise technologies GmbH.
 */
public class MarkerDecorator extends ProblemDecorator implements ILabelDecorator {
	
	@Override
	protected int getDecorationFlags(Object element) throws CoreException {
		if (!(element instanceof IResource)) {
			return 0;
		}
		
		IResource res = (IResource)element;
		if (res == null || !res.isAccessible()) {
			return 0;
		}
		int info= 0;
		
		IMarker[] markers= res.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		if (markers != null) {
			for (int i= 0; i < markers.length && (info != DECORATE_ERROR); i++) {
				IMarker curr= markers[i];
				int priority= curr.getAttribute(IMarker.SEVERITY, -1);
				if (priority == IMarker.SEVERITY_WARNING) {
					info= DECORATE_WARNING;
				} else if (priority == IMarker.SEVERITY_ERROR) {
					info= DECORATE_ERROR;
				}
			}			
		}
		return info;
	}

    @Override
    protected int getDecorationFlags(Object element, String markerId) throws CoreException {
        // TODO Auto-generated method stub
        return 0;
    }

}
