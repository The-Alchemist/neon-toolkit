/*****************************************************************************
 * based on com.ontoprise.ontostudio.owl.gui.domainview.DomainView developed by ontoprise GmbH
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.views.rangeView;

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

import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;

/**
 * 
 * @author Nico Stieler
 * @author Michael Erdmann
 * Created on: 08.10.2010
 */
public class RangeView extends ViewPart implements ISelectionListener {

    public static final String ID = "com.ontoprise.ontostudio.owl.views.rangeview"; //$NON-NLS-1$

    private RangeViewContentProvider _contentProvider;
    private TreeViewer _viewer;

    private String _projectId = null;
    private String _ontologyId;

    public RangeView() {
    }

    @Override
    public void createPartControl(Composite parent) {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IHelpContextIds.RANGE_VIEW);
        _viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        new DrillDownAdapter(_viewer);
        _contentProvider = new RangeViewContentProvider(_viewer);
        _viewer.setContentProvider(_contentProvider);
        _viewer.setLabelProvider(new RangeViewLabelProvider());
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

            if (o instanceof AbstractOwlEntityTreeElement) {
                AbstractOwlEntityTreeElement treeElement = (AbstractOwlEntityTreeElement) o;
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
