/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyEntity;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.TreeElement;

/* 
 * Created on: 23.12.2004
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Navigator, extendableTreeProvider
 *
 */
/**
 * This class is the content and label provider for the treeviewer in the MTreeView. This content provider can be used by any TreeViewer to display content as
 * modular as in the MTreeView.
 */

public class MainTreeDataProvider extends LabelProvider implements ITreeContentProvider, ILabelProvider, IColorProvider {

    private static MainTreeDataProvider _singleton;
    private Color _grayColor;

    /*
     * The top index shown in the view NOT YET USED!!
     */
    private int _topIndex;
    /*
     * The number of visible rows currently shown NET YET USED
     */
    private int _visibleRows;

    /*
     * Default element sent to the root TreeDataProviders when the getElements(TreeElement) method is called.
     */
    private final TreeElement _rootElement = new TreeElement(null) {
        @Override
        public ITreeDataProvider getProvider() {
            return null;
        }
    };

    private ITreeDataProvider _rootProvider;

    private ITreeExtensionHandler _extensionHandler;

    /**
     * Default constructor
     */
    public MainTreeDataProvider(ITreeExtensionHandler extensions) {
        _singleton = this;
        _extensionHandler = extensions;
            _grayColor = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
    }

    public static MainTreeDataProvider getDefault() {
        return _singleton;
    }

    public TreeElement getRoot() {
        return _rootElement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage(Object element) {
        assert element instanceof ITreeElement;

        ITreeDataProvider provider = ((ITreeElement) element).getProvider();
        Image image = provider.getImage((ITreeElement) element);
        if (element instanceof IOntologyElement && ((IOntologyElement) element).isImported()) {
            ImageDescriptor id = ImageDescriptor.createFromImage(image);
            image = ImageDescriptor.createWithFlags(id, SWT.IMAGE_GRAY).createImage();
        }
        return image;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object element) {
        assert element instanceof ITreeElement;

        try {
            ITreeDataProvider provider = ((ITreeElement) element).getProvider();
            return provider.getText((ITreeElement) element);
        } catch (Exception e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
     */
    public Color getBackground(Object element) {
        assert element instanceof ITreeElement;

        ITreeDataProvider provider = ((ITreeElement) element).getProvider();
        if (provider instanceof IColorProvider) {
            return ((IColorProvider) provider).getBackground(element);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
     */
    public Color getForeground(Object element) {
        assert element instanceof ITreeElement;

        if (element instanceof AbstractOntologyEntity) {
            if (((AbstractOntologyEntity) element).isImported()) {
                return _grayColor;
            }
        }

        ITreeDataProvider provider = ((ITreeElement) element).getProvider();
        if (provider instanceof IColorProvider) {
            return ((IColorProvider) provider).getForeground(element);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        if (_rootProvider == null) {
            ITreeDataProvider[] provs = _extensionHandler.getRootProviders();
            List<ITreeElement> rootElems = new ArrayList<ITreeElement>();
            for (int i = 0; i < provs.length; i++) {
                if (provs[i] == null) {
                    continue;
                }
                ITreeElement[] children = provs[i].getElements(_rootElement, _topIndex, _visibleRows);
                for (int j = 0; j < children.length; j++) {
                    rootElems.add(children[j]);
                }
            }
            return rootElems.toArray();
        } else {
            return _rootProvider.getElements(inputElement instanceof ITreeElement ? (ITreeElement) inputElement : _rootElement, _topIndex, _visibleRows);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object parentElement) {
        assert parentElement instanceof TreeElement;

        TreeElement parent = (TreeElement) parentElement;
        ITreeDataProvider provider = parent.getProvider();

        List<ITreeElement> children = new ArrayList<ITreeElement>();
        ITreeDataProvider[] provs = _extensionHandler.getSubProviders(provider.getId());
        if (provider.getChildCount(parent) > 0) {
            ITreeElement[] elements = provider.getChildren(parent, _topIndex, _visibleRows);
            if (elements != null) {
                for (int j = 0; j < elements.length; j++) {
                    children.add(elements[j]);
                }
            }
        }
        for (int i = 0; i < provs.length; i++) {
            ITreeElement[] elements = provs[i].getElements(parent, _topIndex, _visibleRows);
            for (int j = 0; j < elements.length; j++) {
                children.add(elements[j]);
            }
        }
        return children.toArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element) {
        assert element instanceof ITreeElement;
        ITreeElement elem = (ITreeElement) element;

        ITreeDataProvider provider = elem.getProvider();

        boolean hasChildren = (provider.getChildCount(elem) > 0);
        if (!hasChildren) {
            // TODO check the subproviders for children. Might be that there are still no children.
            ITreeDataProvider[] sub = _extensionHandler.getSubProviders(elem.getProvider().getId());
            if (sub != null && sub.length > 0) {
                for (int i = 0; i < sub.length && !hasChildren; i++) {
                    hasChildren = sub[i].getChildCount(elem) > 0;
                }
            }
        }
        return hasChildren;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // TODO Auto-generated method stub

    }

    public void setTopIndex(int index) {
        _topIndex = index;
    }

    public void setVisibleRowCount(int visibleRows) {
        _visibleRows = visibleRows;
    }

    /**
     * Sets the root provider ignoring the roots from the configuration files.
     */
    public void setRootProvider(ITreeDataProvider prov) {
        _rootProvider = prov;
    }

    public ITreeExtensionHandler getExtensionHandler() {
        return _extensionHandler;
    }
}
