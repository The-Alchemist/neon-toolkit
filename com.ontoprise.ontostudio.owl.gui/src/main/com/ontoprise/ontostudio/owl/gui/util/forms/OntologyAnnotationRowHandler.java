/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.forms;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.gui.properties.IOWLPropertyPage;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.commands.annotations.RemoveOntologyAnnotation;

/**
 * @author Nico Stieler
 */
public abstract class OntologyAnnotationRowHandler extends AbstractRowHandler {

    private String _annotationValueText;
    private String _annotationPropertyText;
    private String _language;
    private String _datatype;

    public OntologyAnnotationRowHandler(IOWLPropertyPage page, OWLModel owlModel, String prop, String annotationValueText, String language, String datatype) {
        super(page, owlModel, owlModel);
        _annotationValueText = annotationValueText;
        _annotationPropertyText = prop;
        _language = language;
        _datatype = datatype;
    }

    @Override
    public void addPressed() {
        // empty implementation
    }

    @Override
    public abstract void savePressed();

    @Override
    public void removePressed() throws NeOnCoreException, CommandException {
        removeOldAnnotation();
        _propertyPage.refresh();
    }
    
    protected void removeOldAnnotation() throws NeOnCoreException, CommandException {
        new RemoveOntologyAnnotation(_sourceOwlModel.getProjectId(), _sourceOwlModel.getOntologyURI(), _annotationPropertyText, _annotationValueText, _language, _datatype).run();
    }

}
