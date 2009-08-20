/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.move.clazz;

import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.neontoolkit.gui.navigator.TreeProviderManager;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;

/* 
 * Created on: 24.05.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Refactor
 */
/**
 * This class extends the ConceptSelectionPage by the options that can be applied to a concept move refactoring.
 */

public class MoveClazzPage extends ClazzSelectionPage {
    private MoveClazzProcessor _processor;

    public MoveClazzPage(MoveClazzProcessor processor) {
        super("MoveConceptInputPage"); //$NON-NLS-1$
        _processor = processor;
    }

    @Override
    public void createOptions(Composite parent) {
    }

    @Override
    protected void createHeader(Composite composite) {
        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.MoveClazzPage_0);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    }
    
    @Override
    public void verifyDestination(Object selected) {
        if (selected instanceof ClazzTreeElement) {
            RefactoringStatus status = _processor.setDestination((ClazzTreeElement) selected);
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

    private MoveClazzProcessor getMoveProcessor() {
        return (MoveClazzProcessor) getRefactoring().getAdapter(MoveClazzProcessor.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.refactor.move.ConceptSelectionPage#getModule()
     */
    @Override
    public OntologyTreeElement getOntology() {
        ClazzTreeElement[] elems = (ClazzTreeElement[]) ((MoveClazzProcessor) getRefactoring().getAdapter(MoveClazzProcessor.class)).getElements();
        OntologyTreeElement ontology = new OntologyTreeElement(elems[0].getProjectName(), elems[0].getOntologyUri(), TreeProviderManager.getDefault().getProvider(ClazzSelectionPage.PROVIDER_ID, OntologyProvider.class));
        return ontology;
    }
}
