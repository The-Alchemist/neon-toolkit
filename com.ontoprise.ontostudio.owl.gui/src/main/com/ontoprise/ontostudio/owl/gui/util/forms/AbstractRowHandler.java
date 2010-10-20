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

public abstract class AbstractRowHandler {

    protected IOWLPropertyPage _propertyPage;
    protected OWLModel _localOwlModel;
    protected OWLModel _sourceOwlModel;

    public AbstractRowHandler(IOWLPropertyPage propertyPage, OWLModel localOwlModel, OWLModel sourceOwlModel) {
        _propertyPage = propertyPage;
        _localOwlModel = localOwlModel;
        _sourceOwlModel = sourceOwlModel;
    }

    public void layoutSections() {
        _propertyPage.layoutSections();
    }

    public void reflow() {
        _propertyPage.getForm().reflow(true);
    }

    /**
     * Refreshes the complete property page
     */
    public void cancelPressed() {
        _propertyPage.refresh();
    }

    public abstract void addPressed();

    public abstract void savePressed();

    public abstract void removePressed() throws NeOnCoreException, CommandException;

    public abstract void ensureQName();

}
