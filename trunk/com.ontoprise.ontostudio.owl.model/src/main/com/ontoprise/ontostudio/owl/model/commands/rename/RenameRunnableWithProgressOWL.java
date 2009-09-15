/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.rename;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.InformationAlreadyExistsException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.progress.NotForkedRunnableWithProgress;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.model.Messages;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;

/* 
 * Created on: 24.06.2007
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Progress, Rename
 */
/**
 * Progress runnable for rename operations
 */
public class RenameRunnableWithProgressOWL extends NotForkedRunnableWithProgress {

    public static final int CLAZZ_TYPE = 0;
    public static final int INDIVIDUAL_TYPE = 1;
    public static final int OBJECT_PROPERTY_TYPE = 2;
    public static final int DATA_PROPERTY_TYPE = 3;
    public static final int ANNOTATION_PROPERTY_TYPE = 4;
    public static final int DATATYPE_TYPE = 5;

    protected String _oldId;
    protected String _newId;
    protected List<String> _ontologyUris;
    protected String _project;

    protected int _type;

    public RenameRunnableWithProgressOWL(Display display, String oldId, String newId, List<String> ontologyUris, String project, int type) {
        super(display);
        _oldId = oldId;
        _newId = newId;
        _ontologyUris = ontologyUris;
        _project = project;
        _type = type;
    }

    @Override
    public void runNotForked(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            RenameOWLEntity command = null;
            // first check if an entity with the new URI exists in one of the relevant ontologies
            for (String ontologyUri: _ontologyUris) {
                Set<OWLEntity> entities = OWLModelFactory.getOWLModel(ontologyUri, _project).getEntity(_newId);
                for (OWLEntity e: entities) {
                    if (e.getURI().equals(_newId)) {
                        throw new InformationAlreadyExistsException(Messages.getString("RenameRunnableWithProgressOWL.2"));  //$NON-NLS-1$
                    }
                }
                
            }
            for (String ontologyUri: _ontologyUris) {
                if (_type == CLAZZ_TYPE) {
                    command = new RenameOWLClazz(_project, ontologyUri, _oldId, _newId);
                } else if (_type == INDIVIDUAL_TYPE) {
                    command = new RenameOWLIndividual(_project, ontologyUri, _oldId, _newId);
                } else if (_type == OBJECT_PROPERTY_TYPE) {
                    command = new RenameOWLObjectProperty(_project, ontologyUri, _oldId, _newId);
                } else if (_type == DATA_PROPERTY_TYPE) {
                    command = new RenameOWLDataProperty(_project, ontologyUri, _oldId, _newId);
                } else if (_type == ANNOTATION_PROPERTY_TYPE) {
                    command = new RenameOWLAnnotationProperty(_project, ontologyUri, _oldId, _newId);
                } else if (_type == DATATYPE_TYPE) {
                    command = new RenameOWLDatatype(_project, ontologyUri, _oldId, _newId);
                }
                if (command != null) {
                    try {
                        command.run();
                    } catch (InformationAlreadyExistsException iaee) {
                        // nothing to do in this case
                    }
                } else {
                    throw new IllegalArgumentException(Messages.getString("RenameRunnableWithProgressOWL.0") + _type); //$NON-NLS-1$
                }
            }
        } catch (CommandException e) {
            throw new InvocationTargetException(e);
        } catch (NeOnCoreException e) {
            throw new InvocationTargetException(e);
        }
    }
}
