/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.OntologyProjectManager;

public class RestoreProjects implements IStartup {

    private static Set<NeOnCoreException> startupExceptions = new HashSet<NeOnCoreException>();

    @Override
    public void earlyStartup() {

        // always perform events asynchronously
        try {
            
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    try {
                        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                        ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
                        pmd.setCancelable(false);
                        pmd.create();
                        
                        pmd.run(false, false, new IRunnableWithProgress() {
                            @Override
                            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                                try {
                                    String[] ontologyProjects = OntologyProjectManager.getDefault().getOntologyProjects();
                                    int totalWork = ontologyProjects.length + 1; 
                                    monitor.beginTask("Loading projects...", totalWork); //$NON-NLS-1$
                                    monitor.worked(1);
                                    int count = 1;
                                    for (String ontologyProject: ontologyProjects) {
                                        try {
                                            monitor.setTaskName("Loading ontologies for project: "+ontologyProject+"  ("+count+"/"+ontologyProjects.length+")");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                                            NeOnCorePlugin.getDefault().getOntologyProject(ontologyProject).restoreProject(monitor);
                                            monitor.worked(1);
                                            ++count;
                                        } catch (NeOnCoreException e) {
                                            startupExceptions.add(e);
                                            // TODO: handle exception
                                        }
                                    }
                                } catch (Exception e) {
                                    // TODO: handle exception
                                } finally {
                                    monitor.done();
                                }
                            }
                        });

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            });
            
        } catch (Exception e) {
         // TODO: handle exception
        }
    }

    public static Set<NeOnCoreException> getStartupExceptions() {
        return startupExceptions;
    }

}
