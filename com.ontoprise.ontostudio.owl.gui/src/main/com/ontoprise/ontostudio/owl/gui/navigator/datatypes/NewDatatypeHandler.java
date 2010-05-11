/*****************************************************************************
 * Copyright (c) 2010 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.datatypes;

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
import org.semanticweb.owlapi.model.OWLDatatype;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;
import com.ontoprise.ontostudio.owl.model.commands.datatypes.CreateDatatype;
import com.ontoprise.ontostudio.owl.model.commands.datatypes.CreateSubDatatype;
import com.ontoprise.ontostudio.owl.perspectives.OWLPerspective;

/**
 * Action to create new datatype in the tree.
 */

public class NewDatatypeHandler extends AbstractNewHandler {

	private DatatypeTreeElement _parentDatatype = null;
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
            if (parent instanceof DatatypeTreeElement) {
                _parentDatatype = (DatatypeTreeElement) parent;
                owlModel = OWLModelFactory.getOWLModel(_parentDatatype.getOntologyUri(), _parentDatatype.getProjectName());
                provider = _parentDatatype.getProvider();
    
            } else if (parent instanceof DatatypeFolderTreeElement) {
                _parentDatatype = null;
                DatatypeFolderTreeElement folder = (DatatypeFolderTreeElement) parent;
                owlModel = OWLModelFactory.getOWLModel(folder.getOntologyUri(), folder.getProjectName());
                provider = _view.getExtensionHandler().getProvider("com.ontoprise.ontostudio.owl.gui.navigator.datatypes.DatatypeProvider"); //$NON-NLS-1$
                
            } else {
                _parentDatatype = null;
                return null;
            }
    
            String newUri = ""; //$NON-NLS-1$
            long timeStamp = System.currentTimeMillis() % 1000;
            String newId = Messages.NewDatatypeAction_0 + "datatype" + timeStamp; //$NON-NLS-1$
            try {
                if(owlModel.getDefaultNamespace() != null) {
                    newId = owlModel.getDefaultNamespace() + "datatype" + timeStamp; //$NON-NLS-1$
                }
                newUri = OWLPlugin.getDefault().getSyntaxManager().parseUri(newId, owlModel);
            } catch (NeOnCoreException e) {
                newUri = newId;
            }

            OWLDatatype datatype = OWLModelFactory.getOWLDataFactory(owlModel.getProjectId()).getOWLDatatype(OWLUtilities.toIRI(newUri));
            DatatypeTreeElement newElement = new DatatypeTreeElement(
                    datatype, owlModel.getOntologyURI(), owlModel.getProjectId(), provider);
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
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATATYPE);
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
        DatatypeTreeElement element = (DatatypeTreeElement) item.getData();
        String ontologyId = element.getOntologyUri();
        String projectId = element.getProjectName();
        String newUri = OWLGUIUtilities.getValidURI(newId, ontologyId, projectId);
        if (newUri == null) {
            return false;
        }
        
		OWLDatatype datatype = OWLModelFactory.getOWLDataFactory(projectId).getOWLDatatype(OWLUtilities.toIRI(newUri));
		DatatypeTreeElement data = new DatatypeTreeElement(
				datatype,
				element.getOntologyUri(), 
				element.getProjectName(), 
				element.getProvider());
		item.setData(data);
		item.setText(element.getProvider().getText(data));

		OWLModuleChangeCommand command = null;
        if (_parentDatatype != null) {
            // create subDataype
            command = new CreateSubDatatype(element.getProjectName(), element.getOntologyUri(), newUri, _parentDatatype.getId());

            // not supported yet, act as if no parent was present
            //command = new CreateDatatype(element.getProjectName(), element.getOntologyUri(), newUri);
        } else {
            // create new datatype
            command = new CreateDatatype(element.getProjectName(), element.getOntologyUri(), newUri);
        }
        command.run();
        
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
