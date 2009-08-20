/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.complexclazz;

import org.eclipse.swt.graphics.Image;
import org.neontoolkit.gui.navigator.DefaultTreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.navigator.elements.TreeElementPath;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;

/**
 * Provider for complex class folders in the OntologyNavigator.
 */
public class ComplexClazzFolderProvider extends DefaultTreeDataProvider {
    private ComplexClazzFolderTreeElement _folder;

    /**
     * This class is implemented as a singleton. The default constructor is called by the plugin framework, but other classes should always use the getDefault()
     * method
     */
    public ComplexClazzFolderProvider() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getChildCount(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    public int getChildCount(ITreeElement parentElement) {
        if (parentElement instanceof ComplexClazzFolderTreeElement) {
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
        assert parentElement instanceof IOntologyElement;
        assert parentElement instanceof IProjectElement;
        _folder = new ComplexClazzFolderTreeElement(((IProjectElement) parentElement).getProjectName(), ((IOntologyElement) parentElement).getOntologyUri(), this);
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
        if (element instanceof ComplexClazzTreeElement) {
            TreeElementPath path = new TreeElementPath();
            ComplexClazzTreeElement classTreeElement = ((ComplexClazzTreeElement) element);
            ComplexClazzFolderTreeElement classFolderTreeElement = new ComplexClazzFolderTreeElement(classTreeElement.getProjectName(), classTreeElement.getOntologyUri(), this);
            path.append(classFolderTreeElement);
            return new TreeElementPath[] {path};
        } else if (element instanceof ComplexClazzFolderTreeElement) {
            TreeElementPath path = new TreeElementPath();
            path.append(element);
            return new TreeElementPath[] {path};
        }
        return new TreeElementPath[0];
    }

}
