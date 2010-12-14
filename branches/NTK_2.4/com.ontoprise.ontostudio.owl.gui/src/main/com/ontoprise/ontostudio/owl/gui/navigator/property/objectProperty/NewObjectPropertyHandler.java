/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty;

import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.actions.AbstractNewHandler;
import org.neontoolkit.gui.util.PerspectiveChangeHandler;
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
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectProperty;
import com.ontoprise.ontostudio.owl.perspectives.OWLPerspective;

/**
 * Action to create new concepts in the tree.
 */

public class NewObjectPropertyHandler extends AbstractNewHandler {

    private AbstractOwlEntityTreeElement _parentProperty = null;
    private IWorkbenchWindow _window = null;

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractNewAction#createNewItem(java.lang.Object)
     */
    @Override
    public Object createNewItem(Object parent) {
        try {
            OWLModel owlModel;
            ITreeDataProvider provider;
            if (parent instanceof ObjectPropertyTreeElement) {
                _parentProperty = (ObjectPropertyTreeElement) parent;
                owlModel = OWLModelFactory.getOWLModel(_parentProperty.getOntologyUri(), _parentProperty.getProjectName());
                provider = _parentProperty.getProvider();
    
            } else if (parent instanceof ObjectPropertyFolderTreeElement) {
                _parentProperty = null;
                ObjectPropertyFolderTreeElement folder = (ObjectPropertyFolderTreeElement) parent;
                owlModel = OWLModelFactory.getOWLModel(folder.getOntologyUri(), folder.getProjectName());
                provider = _view.getExtensionHandler().getProvider("com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyHierarchyProvider"); //$NON-NLS-1$
    
            } else {
                _parentProperty = null;
                return null;
            }
    
            String newUri = ""; //$NON-NLS-1$
            long timeStamp = System.currentTimeMillis() % 1000;
            String newId = Messages.NewObjectPropertyAction_0 + "prop" + timeStamp; //$NON-NLS-1$
            try {
                if(owlModel.getDefaultNamespace() != null) {
                    newId = owlModel.getDefaultNamespace() + "prop" + timeStamp; //$NON-NLS-1$
                }
                newUri = OWLPlugin.getDefault().getSyntaxManager().parseUri(newId, owlModel);
            } catch (NeOnCoreException e) {
                newUri = newId;
            }

            OWLObjectProperty prop = OWLModelFactory.getOWLDataFactory(owlModel.getProjectId()).getOWLObjectProperty(OWLUtilities.toIRI(newUri));
            ObjectPropertyTreeElement newElement = new ObjectPropertyTreeElement(prop, owlModel.getOntologyURI(), owlModel.getProjectId(), provider);
            return newElement;
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractNewAction#getImage()
     */
    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.OBJECT_PROPERTY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractNewAction#finishEditing(org.eclipse.swt.widgets.TreeItem, java.lang.String)
     */
    @Override
    public boolean finishEditing(TreeItem item, String newId) throws Exception {
        ObjectPropertyTreeElement element = (ObjectPropertyTreeElement) item.getData();
        String ontologyId = element.getOntologyUri();
        String projectId = element.getProjectName();
        String newURI = OWLGUIUtilities.getValidURI(newId, ontologyId, projectId);
        if (newURI == null) {
            return false;
        }

        OWLObjectProperty prop = OWLModelFactory.getOWLDataFactory(projectId).getOWLObjectProperty(OWLUtilities.toIRI(newURI));
        Set<OWLEntity> entities = OWLModelFactory.getOWLModel(ontologyId, projectId).getEntity(newURI);
        for (OWLEntity entity: entities) {
            if (entity.getIRI().toString().equals(newURI)) {
                if(entity instanceof OWLDataProperty) {
                    MessageDialog.openInformation(_view.getSite().getShell(), Messages.NewPropertyHandler_0, Messages.NewPropertyHandler_1);
                    return false;
                }
            }
        }

        ObjectPropertyTreeElement data = new ObjectPropertyTreeElement(prop, ontologyId, projectId, element.getProvider());
        item.setData(data);
        item.setText(element.getProvider().getText(data));

        if (item.getParentItem().getData() instanceof ObjectPropertyFolderTreeElement) {
            // root property
            new CreateObjectProperty(element.getProjectName(), element.getOntologyUri(), newURI, null).run();
        } else {
            new CreateObjectProperty(element.getProjectName(), element.getOntologyUri(), newURI, _parentProperty.getId()).run();
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

    /*
     * (non-Javadoc)
     * 
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
