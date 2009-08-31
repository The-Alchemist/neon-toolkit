/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 25.03.2009
 * Created by: diwe
 ******************************************************************************/
package org.neontoolkit.gui.progress;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

/**
 * Abstract implementation of IRunnableWithProgress w
 * @author Dirk Wenke
 *
 */
public abstract class NotForkedRunnableWithProgress implements IRunnableWithProgress {
    protected Display _display;
    
    public NotForkedRunnableWithProgress(Display display) {
        _display = display;
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        runNotForked(new EventDispatchProgressMonitor(monitor, _display));
    }
    
    public abstract void runNotForked(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException;

}
