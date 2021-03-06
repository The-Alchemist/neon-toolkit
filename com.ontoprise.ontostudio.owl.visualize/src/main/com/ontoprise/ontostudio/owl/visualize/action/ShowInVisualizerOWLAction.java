/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.visualize.action;

import org.neontoolkit.ontovisualize.actions.AbstractShowInVisualizerAction;

import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;


public class ShowInVisualizerOWLAction extends AbstractShowInVisualizerAction {

    @Override
    public String getOntologyLanguage() {
        return OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE;
    }

}
