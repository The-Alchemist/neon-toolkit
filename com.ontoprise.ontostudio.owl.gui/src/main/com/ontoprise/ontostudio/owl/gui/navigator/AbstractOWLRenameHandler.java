/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * Created on: 08.01.2009
 * Created by: werner
 *****************************************************************************/
package com.ontoprise.ontostudio.owl.gui.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.navigator.actions.AbstractRenameEntityDialog;
import org.neontoolkit.gui.navigator.actions.AbstractRenameHandler;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyEntity;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.individualview.IndividualViewItem;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.datatypes.DatatypeTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.commands.rename.RenameRunnableWithProgressOWL;

/**
 * @author werner
 * 
 */
public abstract class AbstractOWLRenameHandler extends AbstractRenameHandler {

    @Override
    protected IRunnableWithProgress getRenameRunnable(Shell shell, String newId, AbstractOntologyEntity entity) {
        String parsedNewId;
        String ontologyUri = entity.getOntologyUri();
        String projectName = entity.getProjectName();
        List<String> selectedOntos = null;
        int type = -1;
        try {
            parsedNewId = OWLGUIUtilities.parseUri(newId, ontologyUri, projectName);
            if (entity instanceof ClazzTreeElement) {
                type = RenameRunnableWithProgressOWL.CLAZZ_TYPE;
            } else if (entity instanceof IndividualViewItem) {
                type = RenameRunnableWithProgressOWL.INDIVIDUAL_TYPE;
            } else if (entity instanceof ObjectPropertyTreeElement) {
                type = RenameRunnableWithProgressOWL.OBJECT_PROPERTY_TYPE;
            } else if (entity instanceof DataPropertyTreeElement) {
                type = RenameRunnableWithProgressOWL.DATA_PROPERTY_TYPE;
            } else if (entity instanceof ObjectPropertyTreeElement) {
                type = RenameRunnableWithProgressOWL.OBJECT_PROPERTY_TYPE;
            } else if (entity instanceof AnnotationPropertyTreeElement) {
                type = RenameRunnableWithProgressOWL.ANNOTATION_PROPERTY_TYPE;
            } else if (entity instanceof DatatypeTreeElement) {
                type = RenameRunnableWithProgressOWL.DATATYPE_TYPE;
            }

            // first check if the ontology is imported by others, or imports other ontologies itself
            Set<OWLModel> importedOntos = OWLModelFactory.getOWLModel(ontologyUri, projectName).getAllImportedOntologies();
            Set<OWLModel> importingOntos = OWLModelFactory.getOWLModel(ontologyUri, projectName).getAllImportingOntologies();
            if (importedOntos.size() > 0 || importingOntos.size() > 0) {
                RenameOwlEntityOntologySelectionDialog dialog = new RenameOwlEntityOntologySelectionDialog(shell, entity.getId(), ontologyUri, projectName);
                dialog.open();
                selectedOntos = dialog.getSelectedOntologyUris();
            } else {
                selectedOntos = new ArrayList<String>();
                selectedOntos.add(ontologyUri);
            }

        } catch (NeOnCoreException e) {
            parsedNewId = newId;
            selectedOntos = new ArrayList<String>();
            selectedOntos.add(ontologyUri);
        }
        return new RenameRunnableWithProgressOWL(_display, entity.getId(), parsedNewId, selectedOntos, entity.getProjectName(), type);
    }
    
    @Override
    protected AbstractRenameEntityDialog getRenameEntityDialog(Shell shell, AbstractOntologyEntity entity) {
        OWLEntity owlEntity = ((AbstractOwlEntityTreeElement) entity).getEntity();
        return new RenameOWLEntityDialog(shell, getTitle(), entity.getProjectName(), entity.getOntologyUri(), entity.getId(), owlEntity);
    }

}
