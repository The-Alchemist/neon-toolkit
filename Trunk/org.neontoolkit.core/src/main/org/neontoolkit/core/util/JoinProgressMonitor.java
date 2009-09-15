/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core.util;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Adopts another {@link IProgressMonitor} and provides the method {#join} 
 * which will block the calling thread until the {@link IProgressMonitor#done} method has been called on this object.
 *
 */
public class JoinProgressMonitor implements IProgressMonitor {
    private final IProgressMonitor _progressMonitor;
    private final AtomicBoolean _done;

    public JoinProgressMonitor() {
        this(null);
    }
    
    public JoinProgressMonitor(IProgressMonitor progressMonitor) {
        _progressMonitor = (progressMonitor == null) ? new NullProgressMonitor() : progressMonitor;
        _done = new AtomicBoolean();
    }

    @Override
    public void done() {
        synchronized (_done) {
            _done.set(true);
            _done.notifyAll();
        }
    }
    
    public void join(long timeout) throws TimeoutException {
        if (timeout < 0) {
            throw new IllegalArgumentException();
        }
        long start = System.currentTimeMillis();
        synchronized (_done) {
            long timeToWait = timeout; 
            while (!_done.get() && timeToWait >= 0) {
                try {
                    _done.wait(timeout);
                } catch (InterruptedException e) {
                }
                if (!_done.get() && timeout != 0) {
                    timeToWait = timeout - (System.currentTimeMillis() - start);
                }
            }
            if (!_done.get()) {
                throw new TimeoutException();
            }
        }
    }
    
    @Override
    public void beginTask(String name, int totalWork) {
        _progressMonitor.beginTask(name, totalWork);
    }

    @Override
    public void internalWorked(double work) {
        _progressMonitor.internalWorked(work);
    }

    @Override
    public boolean isCanceled() {
        return _progressMonitor.isCanceled();
    }

    @Override
    public void setCanceled(boolean value) {
        _progressMonitor.setCanceled(value);
    }

    @Override
    public void setTaskName(String name) {
        _progressMonitor.setTaskName(name);
    }

    @Override
    public void subTask(String name) {
        _progressMonitor.subTask(name);
    }

    @Override
    public void worked(int work) {
        _progressMonitor.worked(work);
    }

}
