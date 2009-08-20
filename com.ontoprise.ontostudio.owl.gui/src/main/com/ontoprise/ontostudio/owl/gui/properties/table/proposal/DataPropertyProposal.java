/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties.table.proposal;

import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.model.OWLModel;

public class DataPropertyProposal extends OwlURIProposal {

    public DataPropertyProposal(OWLEntity entity, String[] array, int offset, OWLModel owlModel) {
        super(entity, array, offset, owlModel);
    }
}
