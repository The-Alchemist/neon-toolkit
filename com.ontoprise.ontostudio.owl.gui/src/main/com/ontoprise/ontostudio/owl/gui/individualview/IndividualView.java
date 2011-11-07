/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.individualview;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.IHelpContextIds;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.SelectionTransfer;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzFolderTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
/**
 * 
 * @author Nico Stieler
 */
public class IndividualView extends ViewPart implements ISelectionListener {

    public static final String ID = "com.ontoprise.ontostudio.owl.views.individualview"; //$NON-NLS-1$
    public static final String CONTEXT_ID = "com.ontoprise.ontostudio.owl.views.individualContext"; //$NON-NLS-1$

    private IndividualViewContentProvider _contentProvider;
    private ClazzTreeElement _clazzTreeElement;
    private TreeViewer _viewer;
    private TreeEditor _treeEditor;
    private boolean _dontProcessFocusLost;

    private Text _text;
    private String _projectId = null;
    private String _ontologyId;

    public IndividualView() {
        
    }

    @Override
    public void createPartControl(Composite parent) {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IHelpContextIds.INDIVIDUALS_VIEW);
        _viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        new DrillDownAdapter(_viewer);
        _contentProvider = new IndividualViewContentProvider(_viewer);
        _viewer.setContentProvider(_contentProvider);
        _viewer.setLabelProvider(new IndividualViewLabelProvider());
        _treeEditor = new TreeEditor(_viewer.getTree());
        _viewer.setInput(getViewSite());
        IWorkbenchWindow window = getViewSite().getWorkbenchWindow();
        window.getSelectionService().addSelectionListener(MTreeView.ID, this);

        getViewSite().setSelectionProvider(_viewer);
        _viewer.getTree().getVerticalBar().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // ScrollBar bar = (ScrollBar) e.getSource();
//                if (_contentProvider.update()) {
//                    TreeItem elem = _viewer.getTree().getTopItem();
//                    _viewer.refresh();
//                    _viewer.getTree().setTopItem(elem);
//                }
            }

        });

        hookContextMenu();
        // register context
        IContextService service = (IContextService) getSite().getService(IContextService.class);
        service.activateContext(CONTEXT_ID);

        initDragAndDrop();
    }

    private void hookContextMenu() {
        MenuManager menuManager = new MenuManager("individuals#contextMenu"); //$NON-NLS-1$
        getSite().registerContextMenu(menuManager, _viewer);

        Menu menu = menuManager.createContextMenu(_viewer.getControl());

        // new and new_global groups
        GroupMarker newGroup = new GroupMarker("individuals.new"); //$NON-NLS-1$
        menuManager.add(newGroup);

        // cut/copy/paste/delete group
        GroupMarker ccpGroup = new GroupMarker("individuals.ccp"); //$NON-NLS-1$
        menuManager.add(ccpGroup);
        menuManager.add(new Separator());

        // refactoring group
        GroupMarker refactorGroup = new GroupMarker("individuals.refGroup"); //$NON-NLS-1$
        menuManager.add(refactorGroup);
        MenuManager refactorMenu = new MenuManager("Refactor", "individuals.refactor"); //$NON-NLS-1$ //$NON-NLS-2$
        menuManager.appendToGroup("individuals.refGroup", refactorMenu); //$NON-NLS-1$

        GroupMarker refGroup1 = new GroupMarker("individuals.refactorGroup1"); //$NON-NLS-1$
        refactorMenu.add(refGroup1);
        menuManager.add(new Separator());

        // additional
        menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

        _viewer.getControl().setMenu(menu);
        menuManager.update();
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
            if (o instanceof ClazzTreeElement) {
                if (o instanceof IProjectElement) {
                    _projectId = ((IProjectElement) o).getProjectName();
                }
                if (o instanceof IOntologyElement) {
                    _ontologyId = ((IOntologyElement) o).getOntologyUri();
                }
                _clazzTreeElement = (ClazzTreeElement) o;
                _viewer.setInput(new Object[] {_clazzTreeElement, _projectId});
            } else if (o instanceof ClazzFolderTreeElement) {
                if (o instanceof IProjectElement) {
                    _projectId = ((IProjectElement) o).getProjectName();
                }
                if (o instanceof IOntologyElement) {
                    _ontologyId = ((IOntologyElement) o).getOntologyUri();
                }
                _viewer.setInput(new Object[] {o, _projectId});
            } else {
                _viewer.setInput(null);
                _clazzTreeElement = null;
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public void prepareItemForEdit(IIndividualTreeElement item) {
        TreeItem treeItem = getTreeItem(item);
        _viewer.getTree().setFocus();
        _viewer.getTree().setSelection(new TreeItem[] {treeItem});
        _treeEditor.horizontalAlignment = SWT.LEFT;
        _treeEditor.grabHorizontal = true;
        _treeEditor.minimumWidth = 50;
        Control oldEditor = _treeEditor.getEditor();
        if (oldEditor != null) {
            oldEditor.dispose();
        }

        _text = getNewTextEditor();
        _text.setText(item.toString());
        _text.selectAll();
        _text.setFocus();
        _treeEditor.setEditor(_text, treeItem);
    }

    @SuppressWarnings("rawtypes")
    private TreeItem getTreeItem(IIndividualTreeElement individual) {
        TreeItem[] items = _viewer.getTree().getItems();
        for (int i = 0; i < items.length; i++) {
            if (items[i].getData() == individual) {
                return items[i];
            }
        }
        return null;
    }

    private Text getNewTextEditor() {
        final Text text = new Text(_viewer.getTree(), SWT.NONE);
        text.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                // when the user hits "ENTER"
                if (e.character == SWT.CR) {
                    try {
                        _dontProcessFocusLost = true;
                        stopEditing(true);
                        _dontProcessFocusLost = false;

                    } catch (CommandException e1) {
                        new NeonToolkitExceptionHandler().handleException(Messages.IndividualView_0, e1, _text.getShell());
                    } catch (NeOnCoreException e1) {
                        new NeonToolkitExceptionHandler().handleException(Messages.IndividualView_0, e1, _text.getShell());
                    }
                }
                // close the text editor when the user hits "ESC"
                if (e.character == SWT.ESC) {
                    try {
                        stopEditing(false);
                    } catch (CommandException e1) {
                        OWLPlugin.logError("", e1); //$NON-NLS-1$
                    } catch (NeOnCoreException e1) {
                        OWLPlugin.logError("", e1); //$NON-NLS-1$
                    }
                }
            }
        });
        text.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    if (!_dontProcessFocusLost) {
                        stopEditing(true);
                    }
                } catch (CommandException e1) {
                    OWLPlugin.logError("", e1); //$NON-NLS-1$
                } catch (NeOnCoreException e1) {
                    OWLPlugin.logError("", e1); //$NON-NLS-1$
                }
            }
        });
        return text;
    }

    @Override
    public void setFocus() {
        _viewer.getControl().setFocus();
    }

    public TreeViewer getTreeViewer() {
        return _viewer;
    }

    public IndividualViewContentProvider getContentProvider() {
        return _contentProvider;
    }

    public ClazzTreeElement getSelectedClazz() {
        return _clazzTreeElement;
    }

    @SuppressWarnings("rawtypes")
    public void stopEditing(boolean success) throws CommandException,NeOnCoreException {
        if (_treeEditor != null && _text != null && !_text.isDisposed()) {
            try {
                String newURI = OWLGUIUtilities.getValidURI(_text.getText(), _ontologyId, _projectId);
                if (newURI == null) {
                    success = false;
                } else {
                    ((IIndividualTreeElement) _treeEditor.getItem().getData()).setIndividualId(newURI);
                }
            } catch (NeOnCoreException e) {
                // Call is only needed to attach the entered text to the item
            }
            if (success) {
                _contentProvider.editingFinished();
            } else {
                _contentProvider.editingCancelled();
            }
            _treeEditor.setEditor(null, null);
            _text.dispose();
            _viewer.refresh();
            ISelection sel = _viewer.getSelection();
            _viewer.setSelection(sel, true);
        }

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

    private void initDragAndDrop() {
        // allowed operations
        int operations = DND.DROP_MOVE | DND.DROP_COPY;

        Transfer[] transferTypes = new Transfer[1];
        transferTypes[0] = SelectionTransfer.getInstance();
        DragSource source = new DragSource(_viewer.getControl(), operations);
        source.setTransfer(new Transfer[] {SelectionTransfer.getInstance(), TextTransfer.getInstance()});
        DragSourceListener dragSourceListener = new IndividualViewDragListener(_viewer);
        source.addDragListener(dragSourceListener);
    }

}
