/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.table;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.neontoolkit.table.model.ITableContentModel;


/*
 * (c) Copyright 2003 ontoprise GmbH.
 * All Rights Reserved.
 *
 * Created on 04.02.2004
 * date:			04.02.2004
 * author:			Dirk Wenke
 * function:		
 * keywords:		
 * change history:
 */
/**
 * @author Dirk Wenke
 */
public class DefaultContentProvider implements IStructuredContentProvider {

    ITableContentModel _model;

    public DefaultContentProvider(ITableContentModel model) {
        this._model = model;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        return _model.getTableRows();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
    	_model.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    	if (newInput != null) {
    		_model.initializeModel(newInput);
    	}
    }

    public ITableContentModel getModel() {
        return _model;
    }
}
