/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties.complexclazz;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLClassExpression;

import com.ontoprise.ontostudio.owl.gui.navigator.complexclazz.ComplexClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.properties.clazz.ClazzPropertyPage2;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetEquivalentRestrictionHits;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetSuperRestrictionHits;

public class ComplexClazzPropertyPage2 extends ClazzPropertyPage2 {

    private OWLClassExpression _description;

    @Override
    public void setSelection(IWorkbenchPart part, IStructuredSelection selection) {
        super.setSelection(part, selection);
        if (selection.getFirstElement() instanceof ComplexClazzTreeElement) {
            _description = ((ComplexClazzTreeElement) selection.getFirstElement()).getDescription();
        }
        refresh();
    }

    @Override
    protected String[][] getSuperRestrictionHits() throws CommandException {
        if (_description == null) {
            return new String[0][0]; // in case of complex classes
        }
        try {
            return new GetSuperRestrictionHits(_project, _ontologyUri, OWLUtilities.toString(_description, _owlModel.getOntology())).getResults();
        } catch (NeOnCoreException e) {
            return new String[0][0]; // NICO how to handle this?
        }
    }
    
    @Override
    protected String[][] getEquivalentRestrictionHits() throws CommandException {
        if (_description == null) {
            return new String[0][0]; // in case of complex classes
        }
        try {
            return new GetEquivalentRestrictionHits(_project, _ontologyUri, OWLUtilities.toString(_description, _owlModel.getOntology())).getResults();
        } catch (NeOnCoreException e) {
            return new String[0][0]; // NICO how to handle this?
        }
    }
    
    @Override
    public void refreshIdArea() {
        try {
            if (_description != null) {
                _uriText.setText(OWLGUIUtilities.getEntityLabel(_description, _ontologyUri, _project));
            }
        } catch (NeOnCoreException e) {
            // logging
            _uriText.setText(_id);
        }
        _uriText.setEnabled(false);
    }

    public OWLClassExpression getDescription() {
        return _description;
    }
}
