/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;

/**
 * @author werner
 * 
 */
public class GetPropertyAttribute extends OWLOntologyRequestCommand {

    private boolean _trueOrFalse;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetPropertyAttribute(String project, String module, String propertyUri, String attributeType, boolean includeImports) {
        super(project, module, propertyUri, attributeType, includeImports);
    }

    @Override
    protected void perform() throws CommandException {
        String propertyUri = (String) getArgument(2);
        String attributeType = (String) getArgument(3);
        boolean includeImports = ((Boolean) getArgument(4)).booleanValue();

        try {
            if (attributeType.equals(OWLCommandUtils.FUNCTIONAL)) {
                _trueOrFalse = getOwlModel().isFunctional(propertyUri, includeImports);
            } else if (attributeType.equals(OWLCommandUtils.INVERSE_FUNCTIONAL)) {
                _trueOrFalse = getOwlModel().isInverseFunctional(propertyUri, includeImports);
            } else if (attributeType.equals(OWLCommandUtils.REFLEXIVE)) {
                _trueOrFalse = getOwlModel().isReflexive(propertyUri, includeImports);
            } else if (attributeType.equals(OWLCommandUtils.IRREFLEXIVE)) {
                _trueOrFalse = getOwlModel().isIrreflexive(propertyUri, includeImports);
            } else if (attributeType.equals(OWLCommandUtils.SYMMETRIC)) {
                _trueOrFalse = getOwlModel().isSymmetric(propertyUri, includeImports);
            } else if (attributeType.equals(OWLCommandUtils.ASYMMETRIC)) {
                _trueOrFalse = getOwlModel().isAsymmetric(propertyUri, includeImports);
            } else if (attributeType.equals(OWLCommandUtils.TRANSITIVE)) {
                _trueOrFalse = getOwlModel().isTransitive(propertyUri, includeImports);
            }
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }

    public boolean getAttributeValue() throws CommandException {
        perform();
        return _trueOrFalse;
    }
}
