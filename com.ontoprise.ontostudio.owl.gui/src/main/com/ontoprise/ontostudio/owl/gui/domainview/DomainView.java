/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.domainview;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.IHelpContextIds;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.semanticweb.owlapi.model.OWLClass;

import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzFolderTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;

public class DomainView extends ViewPart implements ISelectionListener {

    public static final String ID = "com.ontoprise.ontostudio.owl.views.domainview"; //$NON-NLS-1$
    public static final String CONTEXT_ID = "com.ontoprise.ontostudio.owl.views.domainContext"; //$NON-NLS-1$

    private DomainViewContentProvider _contentProvider;
    private TreeViewer _viewer;

    private String _projectId = null;
    private String _ontologyId;
    private OWLClass _clazz;

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

        // register context
        IContextService service = (IContextService) getSite().getService(IContextService.class);
        service.activateContext(CONTEXT_ID);
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
                _clazz = (OWLClass)((ClazzTreeElement) o).getEntity();
                _viewer.setInput(new Object[] {_clazz, _ontologyId, _projectId});
            } else if (o instanceof ClazzFolderTreeElement) {
                try {
                    _clazz = OWLModelFactory.getOWLDataFactory(_projectId).getOWLThing();
                    _viewer.setInput(new Object[] {_clazz, _ontologyId, _projectId});
                } catch (NeOnCoreException e) {
                    _viewer.setInput(null);
                    _clazz = null;
                }
            } else {
                _viewer.setInput(null);
                _clazz = null;
            }
        }
    }

    @Override
    public void setFocus() {
        _viewer.getControl().setFocus();
    }

    public TreeViewer getTreeViewer() {
        return _viewer;
    }

    public DomainViewContentProvider getContentProvider() {
        return _contentProvider;
    }

    public OWLClass getSelectedClazz() {
        return _clazz;
    }

    public void refreshView() {
        ISelection sel = _viewer.getSelection();
        _contentProvider.forceUpdate();
        _viewer.setInput(_viewer.getInput());
        _viewer.setSelection(sel, true);
        IWorkbenchWindow window = getViewSite().getWorkbenchWindow();
        IWorkbenchPart part = window.getActivePage().getActivePart();
        window.getActivePage().activate(this);
        window.getActivePage().activate(part);
    }
}
