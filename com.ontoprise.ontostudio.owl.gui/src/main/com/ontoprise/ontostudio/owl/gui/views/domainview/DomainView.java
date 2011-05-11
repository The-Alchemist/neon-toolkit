/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.views.domainview;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.neontoolkit.gui.IHelpContextIds;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;

import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;

/**
 * @author Michael Erdmann
 * @author Nico Stieler
 */
public class DomainView extends ViewPart implements ISelectionListener {

    public static final String ID = "com.ontoprise.ontostudio.owl.views.domainview"; //$NON-NLS-1$
    
    private DomainViewContentProvider _contentProvider;
    private TreeViewer _viewer;

    private String _projectId = null;
    private String _ontologyId;

    public DomainView() {
    }

    @Override
    public void createPartControl(Composite parent) {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IHelpContextIds.DOMAIN_VIEW);
        _viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        new DrillDownAdapter(_viewer);
        _contentProvider = new DomainViewContentProvider(_viewer);
        _viewer.setContentProvider(_contentProvider);
        _viewer.setLabelProvider(new DomainViewLabelProvider());
        _viewer.setInput(getViewSite());
        IWorkbenchWindow window = getViewSite().getWorkbenchWindow();
        window.getSelectionService().addSelectionListener(MTreeView.ID, this);

        getViewSite().setSelectionProvider(_viewer);
    }

    @Override
    public void dispose() {
        getViewSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(MTreeView.ID, this);
        super.dispose();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (selection instanceof StructuredSelection) {
            StructuredSelection s = (StructuredSelection) selection;
            Object o = s.getFirstElement();

            if (o instanceof IProjectElement) {
                _projectId = ((IProjectElement) o).getProjectName();
            }
            if (o instanceof IOntologyElement) {
                _ontologyId = ((IOntologyElement) o).getOntologyUri();
            }

            if (o instanceof ClazzTreeElement) {
                ClazzTreeElement treeElement = (ClazzTreeElement) o;
                _viewer.setInput(new Object[] {treeElement, _ontologyId, _projectId});
                
            }
            else {
                _viewer.setInput(null);
            }
        }
    }

    @Override
    public void setFocus() {
        _viewer.getControl().setFocus();
    }
}
