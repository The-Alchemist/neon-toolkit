/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.event;

import java.util.EventListener;

/* 
 * Created on: 11.04.2008
 * Created by: Werner Hihn
 *
 */

/**
 * An implementation of this listener class is able to receive events from the OWLModel if Axioms/Entities are added to or removed from the model or identifiers
 * change. The modelChanged method may be called with multiple changes if more than one change of the model has occured during a transaction.
 * 
 */
public interface OWLModelListener extends EventListener {

    /**
     * This method is called, if the model has changed.
     */
    public void modelChanged(OWLChangeEvent event);
}
