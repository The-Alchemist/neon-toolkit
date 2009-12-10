/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty;

import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.actions.AbstractNewHandler;
import org.neontoolkit.gui.util.PerspectiveChangeHandler;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.CreateAnnotationProperty;
import com.ontoprise.ontostudio.owl.perspectives.OWLPerspective;


/**
 * Action to create new concepts in the tree. 
 */

public class NewAnnotationPropertyHandler extends AbstractNewHandler {

	private AbstractOwlEntityTreeElement _parentProperty = null;
	private IWorkbenchWindow _window = null;

	/*
	 *  (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractNewAction#createNewItem(java.lang.Object)
	 */
	@Override
    public Object createNewItem(Object parent) {
		if (parent instanceof AnnotationPropertyTreeElement) {
			_parentProperty = (AbstractOwlEntityTreeElement) parent;
			String newId = Messages.NewAnnotationPropertyAction_0 + System.currentTimeMillis(); 
			String newUri = ""; //$NON-NLS-1$
            OWLModel model;
            try {
                model = OWLModelFactory.getOWLModel(_parentProperty.getOntologyUri(), _parentProperty.getProjectName());
            } catch (NeOnCoreException e1) {
                throw new RuntimeException(e1);
            }
            OWLDataFactory factory;
            try {
                factory = model.getOWLDataFactory();
            } catch (NeOnCoreException e1) {
                throw new RuntimeException(e1);
            }
			try {
                newUri = OWLPlugin.getDefault().getSyntaxManager().parseUri(newId, model);
			} catch (NeOnCoreException e) {
				newUri = newId;
			}
			OWLAnnotationProperty prop = factory.getOWLAnnotationProperty(OWLUtilities.toURI(newUri));
			AnnotationPropertyTreeElement newElement = new AnnotationPropertyTreeElement(
					prop,
					_parentProperty.getOntologyUri(),
					_parentProperty.getProjectName(),
					_parentProperty.getProvider());
			return newElement;
		} else if (parent instanceof AnnotationPropertyFolderTreeElement) {
			AnnotationPropertyFolderTreeElement folder = (AnnotationPropertyFolderTreeElement) parent;
			_parentProperty = null;
			String newId = Messages.NewAnnotationPropertyAction_0 + System.currentTimeMillis(); 
			String newUri = ""; //$NON-NLS-1$
            OWLModel model;
            OWLDataFactory factory;
            try {
                model = OWLModelFactory.getOWLModel(folder.getOntologyUri(), folder.getProjectName());
                factory = model.getOWLDataFactory();
            } catch (NeOnCoreException e1) {
                throw new RuntimeException(e1);
            }
			try {
				newUri = OWLPlugin.getDefault().getSyntaxManager().parseUri(newId, OWLModelFactory.getOWLModel(folder.getOntologyUri(), folder.getProjectName()));
			} catch (NeOnCoreException e) {
				newUri = newId;
			}
			OWLAnnotationProperty prop = factory.getOWLAnnotationProperty(OWLUtilities.toURI(newUri));
			AnnotationPropertyTreeElement newElement = new AnnotationPropertyTreeElement(
					prop,
					folder.getOntologyUri(),
                    folder.getProjectName(),
					_view.getExtensionHandler().getProvider("com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyHierarchyProvider")); //$NON-NLS-1$
			return newElement;
		} else {
			_parentProperty = null;
			return null;
		}
	}


	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractNewAction#getImage()
	 */
	@Override
    public Image getImage() {
		return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ANNOTATION_PROPERTY);
	}

	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractNewAction#finishEditing(org.eclipse.swt.widgets.TreeItem, java.lang.String)
	 */
	@Override
    public boolean finishEditing(TreeItem item, String newText) throws Exception {
		AnnotationPropertyTreeElement element = (AnnotationPropertyTreeElement) item.getData();
        String ontologyId = element.getOntologyUri();
        String projectId = element.getProjectName();
        String newURI = OWLGUIUtilities.getValidURI(newText, ontologyId, projectId);
        if (newURI == null) {
            return false;
        }
        
		OWLAnnotationProperty prop = OWLModelFactory.getOWLDataFactory(projectId).getOWLAnnotationProperty(OWLUtilities.toURI(newURI));
        Set<OWLEntity> entities = OWLModelFactory.getOWLModel(ontologyId, projectId).getEntity(newURI);
        for (OWLEntity entity: entities) {
            if (entity.getURI().toString().equals(newURI)) {
                if(entity instanceof OWLDataProperty || entity instanceof OWLObjectProperty) {
                    MessageDialog.openInformation(_view.getSite().getShell(), Messages.NewPropertyHandler_0, Messages.NewPropertyHandler_1);
                    return false;
                }
            }
        }
		
		AnnotationPropertyTreeElement data = new AnnotationPropertyTreeElement( 
				prop, 
				element.getOntologyUri(), 
				element.getProjectName(), 
				element.getProvider());
		item.setData(data);
		item.setText(element.getProvider().getText(data));

        if (item.getParentItem().getData() instanceof AnnotationPropertyFolderTreeElement) {
            // root property
            new CreateAnnotationProperty(projectId, ontologyId, newURI, "").run(); //$NON-NLS-1$
        } else {
            new CreateAnnotationProperty(projectId, ontologyId, newURI, item.getParentItem().getText()).run();
        }
        MTreeView navigator = (MTreeView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MTreeView.ID);
        OWLGUIUtilities.doJumpToEntity(data, navigator);
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	@Override
    public void dispose() {
	}


	/* (non-Javadoc)
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
