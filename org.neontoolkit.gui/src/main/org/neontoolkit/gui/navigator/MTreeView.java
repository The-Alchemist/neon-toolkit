/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.navigator;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ViewPart;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.OntologyProjectAdapter;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.neontoolkit.gui.IHelpContextIds;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.internal.navigator.MTreeDragListener;
import org.neontoolkit.gui.internal.navigator.MTreeDropListener;
import org.neontoolkit.gui.navigator.elements.IFolderElement;

/* 
 * Created on: 21.12.2004
 * Created by: Dirk Wenke
 *
 * Keywords: UI, OntologyNavigator, Navigator
 */
/**
 * This class represents a view to display content in a tree. This tree can dynamically 
 * be declared by use of the extenableTreeProvider extension point.
 */
public class MTreeView extends ViewPart implements ISetSelectionTarget, ISaveablePart {
	public static final String ID = "org.neontoolkit.gui.views.navigator"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "org.neontoolkit.gui.views.navigatorContext"; //$NON-NLS-1$
	/**
	 * The tree of the view
	 */
	protected Tree _tree;
	/**
	 * The viewer of the tree
	 */
	protected AbstractComplexTreeViewer _viewer;
	
	/**
	 * The editor used to edit the tree
	 */	
	protected TreeEditor _editor;
	/**
	 * The standard text widget used to edit texts of nodes
	 */
	protected Text _text;
	/**
	 * The current edited item, or null if not in edit mode
	 */
	protected TreeItem _editingItem;
	/**
	 * The root of all providers
	 */
	protected MainTreeDataProvider _rootProvider;

	/**
	 * Listeners listening to edit events.
	 */
	private ListenerList _listeners;

	/**
	 * Listener reacting on the namespace on/off switch
	 */
	private IPropertyChangeListener _refreshListener = new IPropertyChangeListener() {
		/* (non-Javadoc)
		 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(NeOnUIPlugin.ID_DISPLAY_PREFERENCE)) {
				BusyIndicator.showWhile(getTreeViewer().getTree().getDisplay(), new Runnable(){
					public void run() {
						getTreeViewer().updateLabels();
					}
				});
			}
		}
	};
	
	private boolean _isDirty = false;
	/**
	 * Listener that gets notified if modules change and need to be marked for a future save
	 * operation.
	 */
	private class OntologyProjectListener extends OntologyProjectAdapter {

	    @Override
		public void ontologyModified(String projectName, String ontologyUri,
				boolean modified) {
			if(modified && !_isDirty) {
			    try {
			        IOntologyProject ontoProject = NeOnCorePlugin.getDefault().getOntologyProject(projectName);
			        if (ontoProject != null && !ontoProject.isPersistent()) {
        				_isDirty = true;
        				_tree.getDisplay().asyncExec(new Runnable() {
        					public void run() {
        						firePropertyChange(ISaveablePart.PROP_DIRTY);
        					}
        				});
			        }
			        return;
			    } catch (NeOnCoreException ke) {
                    NeOnUIPlugin.getDefault().logError(Messages.MTreeView_3, ke);
			    }
			}
			String[] projects = OntologyProjectManager.getDefault().getOntologyProjects();
            for (String project : projects) {
                try {
            		IOntologyProject ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(project);
            		if(ontologyProject.getDirtyOntologies().length > 0) {
            			if(!_isDirty) {
            				_isDirty = true;
            				_tree.getDisplay().syncExec(new Runnable() {
            					public void run() {
            						firePropertyChange(ISaveablePart.PROP_DIRTY);
            					}
            				});
            				return;
            			} else {
            				return;
            			}
            		}
                } catch (NeOnCoreException nce) {
                    NeOnUIPlugin.getDefault().logError(Messages.MTreeView_3, nce);
            	}			
            }
            if(_isDirty) {
            	_isDirty = false;
            	//put the property change event sending in the UI thread, to avoid exception in
            	//eclipse components. See bug #7125 
            	_tree.getDisplay().asyncExec(new Runnable() {
            		public void run() {
            			firePropertyChange(ISaveablePart.PROP_DIRTY);
            		}
            	});
            }
		}
	}
	
	private OntologyProjectListener _ontologyProjectListener;


	public String getID()
	{		
		String identifier = getSite().getId();
		if (identifier == null) {
			identifier = ID;
		}
		return identifier;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
    PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IHelpContextIds.MTREE_VIEW);
		FillLayout layout = new FillLayout();
		layout.type = SWT.VERTICAL;
		parent.setLayout(layout);
		
		// initialize main controls
		_viewer = new ComplexTreeViewer(parent, SWT.MULTI);
		_tree = _viewer.getTree();
		// josp 2009-04-16: fix for Issue 8365
        ViewerComparator comparator = new ViewerComparator() {
            @SuppressWarnings("unchecked")
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                if (!(e1 instanceof IFolderElement) && e1.getClass() == e2.getClass()) {
                    // josp 2009-04-21 fix: Issue 9780 - Change sorting in OntoStudio
                    String s1 = (e1 == null) ? "" : String.valueOf(e1); //$NON-NLS-1$
                    String s2 = (e2 == null) ? "" : String.valueOf(e2); //$NON-NLS-1$
                    return getComparator().compare(s1, s2);
                }
                return 0;
            }
        };
        _viewer.setComparator(comparator);
		
		// initialize viewer
		String identifier = getSite().getId();
		if (identifier == null) {
			identifier = ID;
		}
		
		ITreeExtensionHandler extensions = TreeProviderManager.getDefault().createExtensionHandler(identifier, _viewer);
		_rootProvider = new MainTreeDataProvider(extensions);
		_viewer.setContentProvider(_rootProvider);
		_viewer.setLabelProvider(_rootProvider);
		_viewer.setInput(_rootProvider.getRoot());
        getSite().setSelectionProvider(_viewer);
        
        IContextService service = (IContextService)getSite().getService(IContextService.class);
        service.activateContext(CONTEXT_ID);

		// create tree editor
		_editor = new TreeEditor(_tree);
		
		// do other initilizing stuff
		NeOnUIPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(_refreshListener);
		
        _ontologyProjectListener = new OntologyProjectListener();
		NeOnCorePlugin.getDefault().addOntologyProjectListener(_ontologyProjectListener);

		createContextMenu();
		initDragAndDrop();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		getTreeViewer().getControl().setFocus();
	}
	
	/**
	 * initializes the drag and drop functionalities.
	 */
	private void initDragAndDrop() {
		//allowed operations
		int operations = DND.DROP_MOVE | DND.DROP_COPY;

		Transfer[] transferExtensions = NeOnUIPlugin.getDefault().getTransfers();
		Transfer[] transferTypes = new Transfer[transferExtensions.length + 1];
		transferTypes[0] = SelectionTransfer.getInstance(); 
		System.arraycopy(transferExtensions, 0, transferTypes, 1, transferExtensions.length);
		DragSource source =
			new DragSource(_viewer.getControl(), operations);
		source.setTransfer(
			new Transfer[] {SelectionTransfer.getInstance(), TextTransfer.getInstance()});
		DragSourceListener dragSourceListener =
			new MTreeDragListener(_viewer);
		source.addDragListener(dragSourceListener);
		//Drop
		DropTarget target =
			new DropTarget(_viewer.getControl(), operations);
		target.setTransfer(transferTypes);
		target.addDropListener(new MTreeDropListener(_viewer));
	}
	
	/**
	 * Creates the context menu of this view. Other plugins can contribute to this context menu by defining
	 * menu items in the plugin.xml
	 */
	private void createContextMenu() {
		MenuManager menuManager = new MenuManager("navigator#contextMenu"); //$NON-NLS-1$
		getSite().registerContextMenu(menuManager, _viewer); 
		IWorkbenchWindow window = getSite().getWorkbenchWindow();

		Menu menu =
			menuManager.createContextMenu(_viewer.getControl());
		
		// new and new_global groups
		GroupMarker newGroup = new GroupMarker("navigator.new"); //$NON-NLS-1$
        menuManager.add(newGroup);
		GroupMarker newGlobalGroup = new GroupMarker("navigator.new_global"); //$NON-NLS-1$
        menuManager.add(newGlobalGroup);
        menuManager.add(new Separator());
        
        //cut/copy/paste/delete group
        GroupMarker ccpGroup = new GroupMarker("navigator.ccp"); //$NON-NLS-1$
        menuManager.add(ccpGroup);
        menuManager.add(new Separator());

        //refactoring group
        GroupMarker refactorGroup = new GroupMarker("navigator.refGroup"); //$NON-NLS-1$
        menuManager.add(refactorGroup);
        MenuManager refactorMenu = new MenuManager("Refactor", "navigator.refactor"); //$NON-NLS-1$ //$NON-NLS-2$
        menuManager.appendToGroup("navigator.refGroup", refactorMenu); //$NON-NLS-1$
        menuManager.add(new Separator());

        //io group
        GroupMarker ioGroup = new GroupMarker("navigator.io"); //$NON-NLS-1$
        menuManager.add(ioGroup); 
        menuManager.appendToGroup("navigator.io", ActionFactory.IMPORT.create(window)); //$NON-NLS-1$
        menuManager.appendToGroup("navigator.io", ActionFactory.EXPORT.create(window)); //$NON-NLS-1$
        menuManager.add(new Separator());

        //refresh group
        GroupMarker refreshGroup = new GroupMarker("navigator.refresh"); //$NON-NLS-1$
        menuManager.add(refreshGroup);
        menuManager.add(new Separator());

        //run/debug group
        GroupMarker runGroup = new GroupMarker("navigator.runDebug"); //$NON-NLS-1$
        menuManager.add(runGroup);
        menuManager.add(new Separator());

        //properties group
        GroupMarker propertiesGroup = new GroupMarker("navigator.properties"); //$NON-NLS-1$
        menuManager.add(propertiesGroup);

        //additional
        menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        _tree.setMenu(menu);
        menuManager.update();
	}
	
	/**
	 * Returns the TreeViewer contained in this view.
	 * @return
	 */
	public AbstractComplexTreeViewer getTreeViewer() {
		return _viewer;
	}
	
	public void startEditing(TreeItem item) {
		startEditing(item, item.getText());
	}
	
	public void startEditing(TreeItem item, String initialText) {
		_tree.showItem(item);
		_editingItem = item;
		_editor.horizontalAlignment = SWT.LEFT;
		_editor.grabHorizontal = true;
		_editor.minimumWidth = 50;
		_text = new Text(_tree, SWT.SINGLE);
		_text.setText(initialText);
		_text.selectAll();
		_editor.setEditor(_text, item);
		_text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				finishEditing(false);
			}
		});
		_text.addKeyListener(new KeyAdapter() {
            @Override
			public void keyReleased(KeyEvent e) {
                if (e.character == SWT.CR) {
                    //Return pressed, accept value and finish editing
                    finishEditing(true);
                } else if (e.character == SWT.ESC) {
                    cancelEditing();
                }
            }
		});
		_text.setFocus();
		_text.setSelection(0, _text.getText().length());

	}
	
	/**
	 * Called when the editing process is finished.
	 * This can happen either if the user presses enter or if the cell
	 * loses the focus. In the first case regularFinish is true, in the
	 * latter case false.
	 * @param regularFinish
	 */
	public void finishEditing(boolean regularFinish) {
		_editor.setEditor(null, null);
		String text = _text.getText();
		_text.dispose();
		TreeItem item = _editingItem;
		_editingItem = null;
		fireEditingEvent(new EditorEvent(item, text, regularFinish));
	}
	
	public void cancelEditing() {
		_editor.setEditor(null, null);
		_text.dispose();
		_editingItem.dispose();
		EditorEvent event = new EditorEvent(_editingItem);
		_editingItem = null;
		fireEditingEvent(event);
	}
	
	public boolean isEditing() {
		return (_editor.getEditor() != null);
	}
	
	public Text getEditor() {
		return _text;
	}
	public void addEditorListener(IEditorListener listener) {
	    if (_listeners == null) {
	        _listeners = new ListenerList();
	    }
	    _listeners.add(listener);
	}
	
	public void removeEditorListener(IEditorListener listener) {
	    if (_listeners == null) {
	        _listeners = new ListenerList();
	    }
	    _listeners.remove(listener);
	}
	
	private void fireEditingEvent(EditorEvent event) {
	    Object[] listeners = _listeners.getListeners();
	    if (event.getText() == null) {
	        // no new entered text, must be cancel event
		    for (int i = 0; i < listeners.length; i++) {
		        ((IEditorListener) listeners[i]).editingCancelled(event);
		    }
	    } else {
	        // editing finished event
		    for (int i = 0; i < listeners.length; i++) {
		        ((IEditorListener) listeners[i]).editingFinished(event);
		    }
	    }
	}
	
	
	public TreeItem[] expandToNode(ITreeElement node) {
		ITreeElementPath[] paths = _rootProvider.getExtensionHandler().computePathsToRoot(node);
		ArrayList<TreeItem> items = new ArrayList<TreeItem>();
		for (int i = 0; i < paths.length; i++) {
			TreeItem item = _viewer.setPathExpanded(paths[i]);
			if (item != null) {
				items.add(item);
			}
		}
		return (TreeItem[]) items.toArray(new TreeItem[0]);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		TreeProviderManager.getDefault().disposeExtensionHandler(ID);
		NeOnUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(_refreshListener);
		NeOnCorePlugin.getDefault().removeOntologyProjectListener(_ontologyProjectListener);
		
		super.dispose();
	}

	/**
	 * @param name the text to be set in the editor
	 */
	public void setText(String name) {
		_text.setText(name);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ISetSelectionTarget#selectReveal(org.eclipse.jface.viewers.ISelection)
	 */
	public void selectReveal(ISelection selection) {
		this._viewer.setSelection(selection, true);
	}
	
	public ITreeExtensionHandler getExtensionHandler() {
		return _rootProvider.getExtensionHandler();
	}		
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		ISelection selection = _viewer.getSelection();
		IStructuredSelection sel = (IStructuredSelection) selection;
		for (Iterator<?> selIterator = sel.iterator(); selIterator.hasNext();) {
			Object element = selIterator.next();
			assert element instanceof ITreeElement;
			
			ITreeDataProvider provider = ((ITreeElement)element).getProvider();
			if (provider instanceof ISaveableProvider) {
				((ISaveableProvider)provider).doSave(monitor, (ITreeElement)element);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
		ISelection selection = _viewer.getSelection();
		IStructuredSelection sel = (IStructuredSelection) selection;
		for (Iterator<?> selIterator = sel.iterator(); selIterator.hasNext();) {
			Object element = selIterator.next();
			assert element instanceof ITreeElement;
			
			ITreeDataProvider provider = ((ITreeElement)element).getProvider();
			if (provider instanceof ISaveableProvider) {
				((ISaveableProvider)provider).doSaveAs((ITreeElement)element);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isDirty()
	 */
	public boolean isDirty() {
		return _isDirty;
//		ISelection selection = _viewer.getSelection();
//		IStructuredSelection sel = (IStructuredSelection) selection;
//		boolean dirty = false;
//		for (Iterator<?> selIterator = sel.iterator(); selIterator.hasNext();) {
//			Object element = selIterator.next();
//			assert element instanceof ITreeElement;
//			
//			ITreeDataProvider provider = ((ITreeElement)element).getProvider();
//			if (provider instanceof ISaveableProvider) {
//				dirty |= ((ISaveableProvider)provider).isDirty((ITreeElement)element);
//				if (dirty) {
//					break;
//				}
//			}
//		}
//		return dirty;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		ISelection selection = _viewer.getSelection();
		IStructuredSelection sel = (IStructuredSelection) selection;
		Object element = sel.getFirstElement();
		if (element != null) {
			assert element instanceof ITreeElement;
			
			ITreeDataProvider provider = ((ITreeElement)element).getProvider();
			if (provider instanceof ISaveableProvider) {
				return ((ISaveableProvider)provider).isSaveAsAllowed((ITreeElement)element);
			}
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isSaveOnCloseNeeded()
	 */
	public boolean isSaveOnCloseNeeded() {
		return false;
	}
	
}
