/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.views.propertymember;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.semanticweb.owlapi.model.OWLNamedObject;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;

/**
 * A view that displays all property members of a selected property (data, object, or annotation)
 * in a 3-column tables.
 * 
 * @author Michael Erdmann
 * @author Nico Stieler
 */
public class PropertyMemberView extends ViewPart implements ISelectionListener {

    public static final String ID = "com.ontoprise.ontostudio.owl.views.propertymemberview"; //$NON-NLS-1$

    private Text _textBox;
    private TableViewer _viewer;

    public PropertyMemberView() {
    }

    @Override
    public void createPartControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.BORDER);

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.makeColumnsEqualWidth = false;
        composite.setLayout(layout);

        createTextBox(composite);
        createViewer(composite);

        _viewer.setContentProvider(new PropertyMemberViewContentProvider(_viewer));
        _viewer.setLabelProvider(new PropertyMemberViewLabelProvider());
        
        IWorkbenchWindow window = getViewSite().getWorkbenchWindow();
        window.getSelectionService().addSelectionListener(MTreeView.ID, this);
    }

    private void createTextBox(Composite parent) {
        _textBox = new Text(parent, SWT.LEFT);
        _textBox.setText(Messages.PropertyMemberView_0);
        _textBox.setVisible(true);
        _textBox.setEditable(false);

        GridData gd = new GridData();
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        _textBox.setLayoutData(gd);
    }

    private void createViewer(Composite parent) {
        final String[] titles = {Messages.PropertyMemberView_1, Messages.PropertyMemberView_3, Messages.PropertyMemberView_2};
        
        int w = Math.max((parent.getParent().getBounds().x-10)/3, 100);       
        _viewer = new TableViewer(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        for (String title: titles) {
            TableViewerColumn column = new TableViewerColumn(_viewer, SWT.NONE);
            column.getColumn().setText(title);
            column.getColumn().setResizable(true);
            column.getColumn().setMoveable(true);
            column.getColumn().setWidth(w);
        }
    
        Table table = _viewer.getTable();
        table.setHeaderVisible(true);
        table.setVisible(true);

        GridData gd = new GridData();
        gd = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        table.setLayoutData(gd);
    }

    @Override
    public void dispose() {
        getViewSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(MTreeView.ID, this);
        super.dispose();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (selection instanceof StructuredSelection) {
            StructuredSelection sSelection = (StructuredSelection) selection;
            Object selectedProperty = sSelection.getFirstElement();
            
            String projectId = null;
            String ontologyId = null;
            

            if (selectedProperty instanceof IOntologyElement) {
                ontologyId = ((IOntologyElement) selectedProperty).getOntologyUri();
            }
            if (selectedProperty instanceof IProjectElement) {
                projectId = ((IProjectElement) selectedProperty).getProjectName();
            }

            _viewer.setInput(new Object[] {selectedProperty, ontologyId, projectId, _textBox});
        }
    }

    @Override
    public void setFocus() {
        _viewer.getControl().setFocus();
    }

}
