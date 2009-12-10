/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.clazz;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.actions.AbstractNewHandler;
import org.neontoolkit.gui.util.PerspectiveChangeHandler;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.clazz.CreateRootClazz;
import com.ontoprise.ontostudio.owl.model.commands.clazz.CreateSubClazz;
import com.ontoprise.ontostudio.owl.perspectives.OWLPerspective;

/**
 * Action to create new classes in the tree.
 */

public class NewClazzHandler extends AbstractNewHandler {

	private ClazzTreeElement _parentClazz = null;
	private IWorkbenchWindow _window = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractNewAction#createNewItem(java.lang.Object)
	 */
	@Override
    public Object createNewItem(Object parent) {
		if (parent instanceof ClazzTreeElement) {
			_parentClazz = (ClazzTreeElement) parent;
			String newId = Messages.NewClazzAction_0 + System.currentTimeMillis(); 
			String newUri = ""; //$NON-NLS-1$
			try {
				newUri = OWLPlugin.getDefault().getSyntaxManager().parseUri(newId, OWLModelFactory.getOWLModel(_parentClazz.getOntologyUri(), _parentClazz.getProjectName()));
			} catch (NeOnCoreException e) {
				newUri = newId;
			}
			OWLEntity clazz;
            try {
                clazz = OWLModelFactory.getOWLDataFactory(_parentClazz.getProjectName()).getOWLClass(OWLUtilities.toURI(newUri));
            } catch (NeOnCoreException e) {
                throw new RuntimeException(e);
            }
			ClazzTreeElement newElement = new ClazzTreeElement(
					clazz, 
					_parentClazz.getOntologyUri(), 
					_parentClazz.getProjectName(), 
					_parentClazz.getProvider());
			return newElement;
		} else if (parent instanceof ClazzFolderTreeElement) {
			ClazzFolderTreeElement folder = (ClazzFolderTreeElement) parent;
			_parentClazz = null;
			String newId = Messages.NewClazzAction_0 + System.currentTimeMillis(); 
			String newUri = ""; //$NON-NLS-1$
			try {
				newUri = OWLPlugin.getDefault().getSyntaxManager().parseUri(newId, OWLModelFactory.getOWLModel(folder.getOntologyUri(), folder.getProjectName()));
			} catch (NeOnCoreException e) {
				newUri = newId;
			}
			OWLEntity clazz;
            try {
                clazz = OWLModelFactory.getOWLDataFactory(folder.getProjectName()).getOWLClass(OWLUtilities.toURI(newUri));
            } catch (NeOnCoreException e) {
                throw new RuntimeException(e);
            }
			ClazzTreeElement newElement = new ClazzTreeElement(
					clazz,
					folder.getOntologyUri(),
					folder.getProjectName(),
					_view.getExtensionHandler().getProvider(
									"com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzHierarchyProvider")); //$NON-NLS-1$
			return newElement;
		} else {
			_parentClazz = null;
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractNewAction#getImage()
	 */
	@Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CLAZZ);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractNewAction#finishEditing(org.eclipse.swt.widgets.TreeItem,
	 *      java.lang.String)
	 */
	@Override
    public boolean finishEditing(TreeItem item, String newId) throws Exception {
        if (item.isDisposed()) {
            return true;
        }
		ClazzTreeElement element = (ClazzTreeElement) item.getData();
        String ontologyId = element.getOntologyUri();
        String projectId = element.getProjectName();
        String newUri = OWLGUIUtilities.getValidURI(newId, ontologyId, projectId);
        if (newUri == null) {
            return false;
        }
        
		OWLEntity clazz = OWLModelFactory.getOWLDataFactory(projectId).getOWLClass(OWLUtilities.toURI(newUri));
		ClazzTreeElement data = new ClazzTreeElement(
				clazz,
				element.getOntologyUri(), 
				element.getProjectName(), 
				element.getProvider());
		item.setData(data);
		item.setText(element.getProvider().getText(data));

        if (_parentClazz != null) {
            // create subClass
            new CreateSubClazz(element.getProjectName(), element.getOntologyUri(), newUri, _parentClazz.getId()).run();
        } else {
            // create root class
            new CreateRootClazz(element.getProjectName(), element.getOntologyUri(), newUri).run();
        }
        MTreeView navigator = (MTreeView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MTreeView.ID);
        OWLGUIUtilities.doJumpToEntity(data, navigator);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	@Override
    public void dispose() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		_window = window;
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.navigator.actions.AbstractNewHandler#runWithArgumentsSet()
	 */
	@Override
	protected Object runWithArgumentsSet() {
		if (_window == null) {
			_window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		}
    	PerspectiveChangeHandler.switchPerspective(OWLPerspective.ID, OWLPlugin.getDefault(), NeOnUIPlugin.ASK_FOR_PRESPECTIVE_SWITCH);
    	if (_view != null) {
    	    _view.getSite().getPage().activate(_view);
    	}
		return super.runWithArgumentsSet();
	}
}
