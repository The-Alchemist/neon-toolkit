/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.complexclazz;

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.navigator.DefaultTreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.navigator.elements.TreeElementPath;
import org.semanticweb.owlapi.model.OWLClassExpression;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;

/**
 * Provider for classes and their hierarchy in the OntologyNavigator.
 */
public class ComplexClazzHierarchyProvider extends DefaultTreeDataProvider {

    protected static Logger _log = Logger.getLogger(ComplexClazzHierarchyProvider.class);

    private static final String FOLDER_NAME = ""; //$NON-NLS-1$

    public ComplexClazzHierarchyProvider() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.DefaultTreeDataProvider#dispose()
     */
    @Override
    public void dispose() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getChildren(com.ontoprise.ontostudio.gui.navigator.ITreeElement, int, int)
     */
    public ITreeElement[] getChildren(ITreeElement parentElement, int topIndex, int amount) {
        assert (parentElement instanceof IOntologyElement);
        assert (parentElement instanceof IProjectElement);

        return new ITreeElement[] {};
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getChildCount(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    public int getChildCount(ITreeElement parentElement) {
//        long x = System.currentTimeMillis();
        assert (parentElement instanceof IOntologyElement);
        assert (parentElement instanceof IProjectElement);

        String projectId = ((IProjectElement) parentElement).getProjectName();
        String ontologyId = ((IOntologyElement) parentElement).getOntologyUri();

        if (parentElement instanceof ComplexClazzFolderTreeElement) {
            try {
                int result = OWLModelFactory.getOWLModel(ontologyId, projectId).getComplexClasses().size();
//                long y = System.currentTimeMillis();
//                _log.debug("###PERFORMANCE### - Time for getChildCount() of complex classes: " + (y-x)); //$NON-NLS-1$ 
                return result; 
            } catch (NeOnCoreException e) {
                OWLPlugin.logError(Messages.ComplexClazzHierarchyProvider_0, e); 
            }
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getElements(com.ontoprise.ontostudio.gui.navigator.ITreeElement, int, int)
     */
    public ITreeElement[] getElements(ITreeElement parentElement, int topIndex, int amount) {
//        _log.debug("###PERFORMANCE### - Entering ComplexClazzHierarchyProvider.getElements()..."); //$NON-NLS-1$ 
        assert (parentElement instanceof IOntologyElement);
        assert (parentElement instanceof IProjectElement);

        String projectId = ((IProjectElement) parentElement).getProjectName();
        String ontologyId = ((IOntologyElement) parentElement).getOntologyUri();

        try {
//            long x = System.currentTimeMillis();
            Set<OWLClassExpression> complexClasses = OWLModelFactory.getOWLModel(ontologyId, projectId).getComplexClasses();
//            long y = System.currentTimeMillis();
//            _log.debug("###PERFORMANCE### - Time for fetching complex classes from model: " + (y-x)); //$NON-NLS-1$ 
            ComplexClazzTreeElement[] nodes = new ComplexClazzTreeElement[complexClasses.size()];

            int i = 0;
//            x = System.currentTimeMillis();
            for (OWLClassExpression root: complexClasses) {
                nodes[i] = new ComplexClazzTreeElement(root, ontologyId, projectId, this);
                i++;
            }
//            y = System.currentTimeMillis();
//            _log.debug("###PERFORMANCE### - Time for creating tree elements (visitor is used): " + (y-x)); //$NON-NLS-1$ 
//            _log.debug("###PERFORMANCE### - Leaving ComplexClazzHierarchyProvider.getElements()"); //$NON-NLS-1$ 
            
            return nodes;
        } catch (NeOnCoreException e) {
            OWLPlugin.logError(Messages.ComplexClazzHierarchyProvider_1, e); 
        }
        return new ITreeElement[] {};
    }

    public ComplexClazzFolderTreeElement getFolder(String ontologyUri, String projectName) {
        ComplexClazzFolderProvider prov = (ComplexClazzFolderProvider) getExtensionHandler().getProvider(ComplexClazzFolderProvider.class);
        ComplexClazzFolderTreeElement el = new ComplexClazzFolderTreeElement(projectName, ontologyUri, prov);
        return el;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getImage(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    @Override
    public Image getImage(ITreeElement element) {
        if (FOLDER_NAME.equals(element.toString())) {
            return getFolderImage();
        } else {
            return getClazzImage();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getText(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    @Override
    public String getText(ITreeElement element) {
        return element.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#isDragSupported()
     */
    public boolean isDragSupported() {
        return true;
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
     * @see com.ontoprise.ontostudio.gui.navigator.ITreeDataProvider#getPathElements(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
     */
    public TreeElementPath[] getPathElements(ITreeElement element) {
        return new TreeElementPath[0];
    }

    private Image getFolderImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.FOLDER);
    }

    private Image getClazzImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CLAZZ);
    }
}
