/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package org.neontoolkit.search.workingset;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IWorkingSetPage;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.SharedImages;
import org.neontoolkit.search.Messages;


/* 
 * Created on 26.04.2005
 * Created by: Dirk Wenke
 *
 * Function:
 * Keywords:
 * 
 * Copyright (c) 2005 ontoprise GmbH.
 */
/**
 * @author Dirk Wenke
 */

public class OntologyWorkingSetPage extends WizardPage implements IWorkingSetPage {

	private static final String PAGE_ID = "ontologyWorkingSetPage";  //$NON-NLS-1$
	private static final String PAGE_TITLE = "Title";  //$NON-NLS-1$
	private CheckboxTreeViewer _tree;
	private Text _nameText;
	private ITreeContentProvider _contentProvider;
	private OntologyWorkingSetLabelProvider _labelProvider;
	private IWorkingSet _workingSet;
	private boolean _firstCheck;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public OntologyWorkingSetPage() {
		super(PAGE_ID, PAGE_TITLE, NeOnUIPlugin.getDefault().getImageRegistry().getDescriptor(SharedImages.PROJECT));
		setDescription("Description"); //$NON-NLS-1$
		_firstCheck = true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.IWorkingSetPage#finish()
	 */
	public void finish() {
		String workingSetName = _nameText.getText();
		List<Object> elements = new ArrayList<Object>(10);
		findCheckedElements(elements, _tree.getInput());
		if (_workingSet == null) {
			IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
			_workingSet = workingSetManager.createWorkingSet(workingSetName, (IAdaptable[]) elements.toArray(new IAdaptable[elements.size()]));
		} else {
			// Add inaccessible resources
			IAdaptable[] oldItems = _workingSet.getElements();
			List<IProject> closedWithChildren = new ArrayList<IProject>(elements.size());
			for (int i = 0; i < oldItems.length; i++) {
				IResource oldResource = null;
				if (oldItems[i] instanceof IResource) {
					oldResource = (IResource) oldItems[i];
				} else {
					oldResource = (IResource) oldItems[i].getAdapter(IResource.class);
				}
				if (oldResource != null && !oldResource.isAccessible()) {
					IProject project = oldResource.getProject();
					if (elements.contains(project) || closedWithChildren.contains(project)) {
						elements.add(oldItems[i]);
						elements.remove(project);
						closedWithChildren.add(project);
					}
				}
			}
			_workingSet.setName(workingSetName);
			_workingSet.setElements((IAdaptable[]) elements.toArray(new IAdaptable[elements.size()]));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.IWorkingSetPage#getSelection()
	 */
	public IWorkingSet getSelection() {
		return _workingSet;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.IWorkingSetPage#setSelection(org.eclipse.ui.IWorkingSet)
	 */
	public void setSelection(IWorkingSet workingSet) {
		Assert.isNotNull(workingSet, "Working set must not be null"); //$NON-NLS-1$
		_workingSet = workingSet;
		if (getContainer() != null && getShell() != null && _nameText != null) {
			_firstCheck = false;
			_nameText.setText(_workingSet.getName());
			initializeCheckedState();
			validateInput();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		setControl(composite);

		Label label = new Label(composite, SWT.WRAP);
		label.setText(Messages.OntologyWorkingSetPage_2); 
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		label.setLayoutData(gd);

		_nameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		_nameText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		_nameText.addModifyListener(
			new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					validateInput();
				}
			}
		);
		_nameText.setFocus();
		
		label = new Label(composite, SWT.WRAP);
		label.setText("Working set content:"); //$NON-NLS-1$
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		label.setLayoutData(gd);

		_tree = new CheckboxTreeViewer(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL);
		gd.heightHint = convertHeightInCharsToPixels(15);
		_tree.getControl().setLayoutData(gd);
		
		_contentProvider = new OntologyWorkingSetContentProvider();
		_tree.setContentProvider(_contentProvider);
		

		
		_labelProvider = new OntologyWorkingSetLabelProvider();
		_tree.setLabelProvider(_labelProvider);
		_tree.setUseHashlookup(true);
		
		_tree.setInput(ResourcesPlugin.getWorkspace().getRoot());

		_tree.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				handleCheckStateChange(event);
			}
		});

		_tree.addTreeListener(new ITreeViewerListener() {
			public void treeCollapsed(TreeExpansionEvent event) {
			}
			public void treeExpanded(TreeExpansionEvent event) {
				final Object element = event.getElement();
				if (!_tree.getGrayed(element)) {
					BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
						public void run() {
							setSubtreeChecked(element, _tree.getChecked(element), false);
						}
					});
				}
			}
		});

		if (_workingSet != null) {
			_nameText.setText(_workingSet.getName());
		}
		initializeCheckedState();
		validateInput();

		Dialog.applyDialogFont(composite);
		// Set help for the page 
//		JavaUIHelp.setHelp(_tree, IJavaHelpContextIds.JAVA_WORKING_SET_PAGE);
	}

	private void validateInput() {
		String errorMessage = null; 
		String newText = _nameText.getText();

		if (!newText.equals(newText.trim())) {
			errorMessage = "Working set names must not contain whitespaces!"; //WorkingSetMessages.getOldString("JavaWorkingSetPage.warning.nameWhitespace"); //$NON-NLS-1$
		}
		if (newText.equals("")) { //$NON-NLS-1$
			if (_firstCheck) {
				setPageComplete(false);
				_firstCheck = false;
				return;
			} else {				
				errorMessage = "An empty name for the working set is not allowed!"; //WorkingSetMessages.getOldString("JavaWorkingSetPage.warning.nameMustNotBeEmpty"); //$NON-NLS-1$
			}
		}

		_firstCheck = false;

		if (errorMessage == null && !(_workingSet == null || newText.equals(_workingSet.getName()))) {
			IWorkingSet[] workingSets = PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSets();
			for (int i = 0; i < workingSets.length; i++) {
				if (newText.equals(workingSets[i].getName())) {
					errorMessage = "A working set with this name already exists!"; //WorkingSetMessages.getOldString("JavaWorkingSetPage.warning.workingSetExists"); //$NON-NLS-1$
				}
			}
		}
		if (errorMessage == null && !hasCheckedElement()) {
			errorMessage = "At least one element must be checked!"; //WorkingSetMessages.getOldString("JavaWorkingSetPage.warning.resourceMustBeChecked"); //$NON-NLS-1$
		}
		setErrorMessage(errorMessage);
		setPageComplete(errorMessage == null);
	}
	
	private boolean hasCheckedElement() {
		TreeItem[] items = _tree.getTree().getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getChecked()) {
				return true;
			}
		}
		return false;
	}

	void handleCheckStateChange(final CheckStateChangedEvent event) {
		BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			public void run() {
				IAdaptable element = (IAdaptable) event.getElement();
				boolean state = event.getChecked();		
				_tree.setGrayed(element, false);
				if (_contentProvider.hasChildren(element)) {
					setSubtreeChecked(element, state, state); // only check subtree if state is set to true
				}
				updateParentState(element, state);
				validateInput();
			}
		});
	}

	private void setSubtreeChecked(Object parent, boolean state, boolean checkExpandedState) {
		if (!(parent instanceof IAdaptable)) {
			return;
		}
		IContainer container = (IContainer) ((IAdaptable) parent).getAdapter(IContainer.class);
		if ((!_tree.getExpandedState(parent) && checkExpandedState) || (container != null && !container.isAccessible())) {
			return;
		}
		Object[] children = _contentProvider.getChildren(parent);
		for (int i = children.length - 1; i >= 0; i--) {
			Object element = children[i];
			if (state) {
				_tree.setChecked(element, true);
				_tree.setGrayed(element, false);
			} else {
				_tree.setGrayChecked(element, false);
			}
			if (_contentProvider.hasChildren(element)) {
				setSubtreeChecked(element, state, true);
			}
		}
	}

	private void updateParentState(Object child, boolean baseChildState) {
		if (child == null) {
			return;
		}
		if (child instanceof IAdaptable) {
			IResource resource = (IResource) ((IAdaptable) child).getAdapter(IResource.class);
			if (resource != null && !resource.isAccessible()) {
				return;
			}
		}
		Object parent = _contentProvider.getParent(child);
		if (parent == null) {
			return;
		}

		boolean allSameState = true;
		Object[] children = null;
		children = _contentProvider.getChildren(parent);

		for (int i = children.length - 1; i >= 0; i--) {
			if (_tree.getChecked(children[i]) != baseChildState || _tree.getGrayed(children[i])) {
				allSameState = false;
				break;
			}
		}
	
		_tree.setGrayed(parent, !allSameState);
		_tree.setChecked(parent, !allSameState || baseChildState);
		
		updateParentState(parent, baseChildState);
	}

	private void findCheckedElements(List<Object> checkedResources, Object parent) {
		Object[] children = _contentProvider.getChildren(parent);
		for (int i = 0; i < children.length; i++) {
			if (_tree.getGrayed(children[i])) {
				findCheckedElements(checkedResources, children[i]);
			} else if (_tree.getChecked(children[i])) {
				checkedResources.add(children[i]);
			}
		}
	}

	private void initializeCheckedState() {

		BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			public void run() {
				Object[] elements;
				if (_workingSet == null) {
					return;
/*
					// Use current part's selection for initialization
					IWorkbenchPage page= JavaPlugin.getActivePage();
					if (page == null)
						return;
					
					IWorkbenchPart part= JavaPlugin.getActivePage().getActivePart();
					if (part == null)
						return;
					
					try {
						elements= SelectionConverter.getStructuredSelection(part).toArray();
						for (int i= 0; i < elements.length; i++) {
							if (elements[i] instanceof IResource) {
								IJavaElement je= (IJavaElement)((IResource)elements[i]).getAdapter(IJavaElement.class);
								if (je != null && je.exists() &&  je.getJavaProject().isOnClasspath((IResource)elements[i]))
									elements[i]= je;
							}
						}
					} catch (JavaModelException e) {
						return;
					}
*/
				} else {
					elements = _workingSet.getElements();
				}
				// Use closed project for elements in closed project
				for (int i = 0; i < elements.length; i++) {
					Object element = elements[i];
					if (element instanceof IResource) {
						IProject project = ((IResource) element).getProject();
						if (!project.isAccessible()) {
							elements[i] = project;
						}
					}
/*					if (element instanceof IJavaElement) {
						IJavaProject jProject= ((IJavaElement)element).getJavaProject();
						if (jProject != null && !jProject.getProject().isAccessible()) 
							elements[i]= jProject.getProject();
					}
*/				}

				_tree.setCheckedElements(elements);
				for (int i = 0; i < elements.length; i++) {
					Object element = elements[i];
					if (_contentProvider.hasChildren(element)) {
						setSubtreeChecked(element, true, true);
					}
						
					updateParentState(element, true);
				}
			}
		});
	}
}
