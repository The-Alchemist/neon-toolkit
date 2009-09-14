/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty;

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
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataProperty;
import com.ontoprise.ontostudio.owl.perspectives.OWLPerspective;

/**
 * Action to create new concepts in the tree.
 */

public class NewDataPropertyHandler extends AbstractNewHandler {

    private AbstractOwlEntityTreeElement _parentProperty = null;
    private IWorkbenchWindow _window = null;

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractNewAction#createNewItem(java.lang.Object)
     */
    @Override
    public Object createNewItem(Object parent) {
        if (parent instanceof DataPropertyTreeElement) {
            _parentProperty = (AbstractOwlEntityTreeElement) parent;
            String newId = Messages.NewDataPropertyAction_0 + System.currentTimeMillis();
            String newUri = ""; //$NON-NLS-1$
            try {
                newUri = OWLPlugin.getDefault().getSyntaxManager().parseUri(newId, OWLModelFactory.getOWLModel(_parentProperty.getOntologyUri(), _parentProperty.getProjectName()));
            } catch (NeOnCoreException e) {
                newUri = newId;
            }
            OWLDataProperty prop;
            try {
                prop = OWLModelFactory.getOWLDataFactory(_parentProperty.getProjectName()).getOWLDataProperty(OWLUtilities.toURI(newUri));
            } catch (NeOnCoreException e) {
                throw new RuntimeException(e);
            }
            DataPropertyTreeElement newElement = new DataPropertyTreeElement(prop, _parentProperty.getOntologyUri(), _parentProperty.getProjectName(), _parentProperty.getProvider());
            return newElement;
        } else if (parent instanceof DataPropertyFolderTreeElement) {
            DataPropertyFolderTreeElement folder = (DataPropertyFolderTreeElement) parent;
            _parentProperty = null;
            String newId = Messages.NewDataPropertyAction_0 + System.currentTimeMillis();
            String newUri = ""; //$NON-NLS-1$
            try {
                newUri = OWLPlugin.getDefault().getSyntaxManager().parseUri(newId, OWLModelFactory.getOWLModel(folder.getOntologyUri(), folder.getProjectName()));
            } catch (NeOnCoreException e) {
                newUri = newId;
            }
            OWLDataProperty prop;
            try {
                prop = OWLModelFactory.getOWLDataFactory(folder.getProjectName()).getOWLDataProperty(OWLUtilities.toURI(newUri));
            } catch (NeOnCoreException e) {
                throw new RuntimeException(e);
            }
            DataPropertyTreeElement newElement = new DataPropertyTreeElement(prop, folder.getOntologyUri(), folder.getProjectName(), _view.getExtensionHandler().getProvider("com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyHierarchyProvider")); //$NON-NLS-1$
            return newElement;
        } else {
            _parentProperty = null;
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
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATA_PROPERTY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractNewAction#finishEditing(org.eclipse.swt.widgets.TreeItem, java.lang.String)
     */
    @Override
    public boolean finishEditing(TreeItem item, String newText) throws Exception {
        DataPropertyTreeElement element = (DataPropertyTreeElement) item.getData();
        String ontologyId = element.getOntologyUri();
        String projectId = element.getProjectName();
        String newURI = OWLGUIUtilities.getValidURI(newText, ontologyId, projectId);
        if (newURI == null) {
            return false;
        }

        OWLDataProperty prop = OWLModelFactory.getOWLDataFactory(projectId).getOWLDataProperty(OWLUtilities.toURI(newURI));
        Set<OWLEntity> entities = OWLModelFactory.getOWLModel(ontologyId, projectId).getEntity(newURI);
        for (OWLEntity entity: entities) {
            if (entity.getURI().equals(newURI)) {
                MessageDialog.openInformation(_view.getSite().getShell(), Messages.NewClazzHandler_0, Messages.NewClazzHandler_1);
                return false;
            }
        }
        
        DataPropertyTreeElement data = new DataPropertyTreeElement(prop, element.getOntologyUri(), element.getProjectName(), element.getProvider());
        item.setData(data);
        item.setText(element.getProvider().getText(data));

        if (item.getParentItem().getData() instanceof DataPropertyFolderTreeElement) {
            // root property
            new CreateDataProperty(element.getProjectName(), element.getOntologyUri(), newURI, null).run();
        } else {
            new CreateDataProperty(element.getProjectName(), element.getOntologyUri(), newURI, _parentProperty.getId()).run();
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
