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

import org.eclipse.swt.graphics.Image;
import org.neontoolkit.gui.navigator.DefaultTreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.gui.navigator.elements.TreeElementPath;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;

/**
 * Provider for concept folders in the OntologyNavigator.
 */
public class DataPropertyFolderProvider extends DefaultTreeDataProvider {
    private DataPropertyFolderTreeElement _folder;

    /**
     * This class is implemented as a singleton. The default constructor is called by the plugin framework, but other classes should always use the getDefault()
     * method
     */
    public DataPropertyFolderProvider() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getChildCount(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    public int getChildCount(ITreeElement parentElement) {
        if (parentElement instanceof DataPropertyFolderTreeElement) {
            return 0;
        } else {
            return 1;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getChildren(com.ontoprise.ontostudio.gui.navigator.ITreeElement, int, int)
     */
    public ITreeElement[] getChildren(ITreeElement parentElement, int topIndex, int amount) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getElements(com.ontoprise.ontostudio.gui.navigator.ITreeElement, int, int)
     */
    public ITreeElement[] getElements(ITreeElement parentElement, int topIndex, int amount) {
        assert parentElement instanceof AbstractOntologyTreeElement;
        AbstractOntologyTreeElement el = (AbstractOntologyTreeElement) parentElement;
        _folder = new DataPropertyFolderTreeElement(el.getProjectName(), el.getOntologyUri(), this);
        return new ITreeElement[] {_folder};
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#isDragSupported()
     */
    public boolean isDragSupported() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#isDropSupported()
     */
    public boolean isDropSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.DefaultTreeDataProvider#getImage(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    @Override
    public Image getImage(ITreeElement element) {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.FOLDER);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getPathElements(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    public TreeElementPath[] getPathElements(ITreeElement element) {
        if (element instanceof DataPropertyTreeElement) {
            TreeElementPath path = new TreeElementPath();
            DataPropertyTreeElement conceptTreeElement = ((DataPropertyTreeElement) element);
            String projectName = conceptTreeElement.getProjectName();
            String ontologyUri = conceptTreeElement.getOntologyUri();
            DataPropertyFolderTreeElement conceptFolderTreeElement = new DataPropertyFolderTreeElement(projectName, ontologyUri, this);
            path.append(conceptFolderTreeElement);
            return new TreeElementPath[] {path};
        } else if (element instanceof DataPropertyFolderTreeElement) {
            TreeElementPath path = new TreeElementPath();
            path.append(element);
            return new TreeElementPath[] {path};
        }
        return new TreeElementPath[0];
    }

}
