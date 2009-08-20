/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.move.property;

import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.neontoolkit.gui.navigator.ComplexTreeViewer;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeExtensionHandler;
import org.neontoolkit.gui.navigator.TreeProviderManager;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyFolderProvider;

/* 
 * Created on: 24.05.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Refactor
 */
/**
 * This class extends the ConceptSelectionPage by the options that can be applied to a concept move refactoring.
 */

public class MoveDataPropertyPage extends PropertySelectionPage {

    private static final String PROVIDER_ID = "com.ontoprise.ontostudio.refactor.move.owl.dataproperty"; //$NON-NLS-1$

    private MoveDataPropertyProcessor _processor;

    public MoveDataPropertyPage(MoveDataPropertyProcessor processor) {
        super("MovePropertyInputPage"); //$NON-NLS-1$
        _processor = processor;
    }

    @Override
    public void createOptions(Composite parent) {
    }

    @Override
    protected void createHeader(Composite composite) {
        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.MoveDataPropertyPage_0);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    }
    
    @Override
    public void verifyDestination(Object selected) {
        if (selected instanceof PropertyTreeElement) {
            RefactoringStatus status = _processor.setDestination((PropertyTreeElement) selected);
            if (status.hasError()) {
                this.setErrorMessage(status.getEntryWithHighestSeverity().getMessage());
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.refactor.move.ConceptSelectionPage#isDestinationSet()
     */
    @Override
    public boolean isDestinationSet() {
        return getMoveProcessor().hasDestinationSet();
    }

    private MoveDataPropertyProcessor getMoveProcessor() {
        return (MoveDataPropertyProcessor) getRefactoring().getAdapter(MoveDataPropertyProcessor.class);
    }

    @Override
    protected ITreeExtensionHandler getExtensionHandler(ComplexTreeViewer treeViewer) {
        return TreeProviderManager.getDefault().createExtensionHandler(PROVIDER_ID, treeViewer);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.refactor.move.ConceptSelectionPage#getModule()
     */
    @Override
    public OntologyTreeElement getOntology() {
        PropertyTreeElement[] elems = (PropertyTreeElement[]) ((MoveDataPropertyProcessor) getRefactoring().getAdapter(MoveDataPropertyProcessor.class)).getElements();
        OntologyTreeElement ontology = new OntologyTreeElement(elems[0].getProjectName(), elems[0].getOntologyUri(), TreeProviderManager.getDefault().getProvider(PROVIDER_ID, OntologyProvider.class));
        return ontology;
    }

    @Override
    public void dispose() {
        TreeProviderManager.getDefault().disposeExtensionHandler(PROVIDER_ID);
    }

    @Override
    protected ITreeDataProvider getRootProvider(ITreeExtensionHandler handler) {
        return handler.getProvider(DataPropertyFolderProvider.class);
    }
}
