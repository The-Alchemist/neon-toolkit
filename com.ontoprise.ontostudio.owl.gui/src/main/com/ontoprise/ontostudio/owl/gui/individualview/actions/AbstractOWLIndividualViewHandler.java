/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.individualview.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ontoprise.ontostudio.owl.gui.individualview.IndividualView;

/*
 * Created on 25.11.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public abstract class AbstractOWLIndividualViewHandler extends AbstractHandler {
	protected IndividualView _view;

    /* (non-Javadoc)
     * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        _view = HandlerUtil.getActivePart(arg0) instanceof IndividualView ? 
                (IndividualView)HandlerUtil.getActivePart(arg0) : null;
        return runWithArgumentsSet();
    }

    protected abstract Object runWithArgumentsSet();

}
