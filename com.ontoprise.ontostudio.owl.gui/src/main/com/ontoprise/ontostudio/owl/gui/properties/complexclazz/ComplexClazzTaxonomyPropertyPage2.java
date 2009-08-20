/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties.complexclazz;

import org.neontoolkit.gui.properties.IMainPropertyPage;
import org.semanticweb.owlapi.model.OWLClassExpression;

import com.ontoprise.ontostudio.owl.gui.properties.clazz.ClazzTaxonomyPropertyPage2;

public final class ComplexClazzTaxonomyPropertyPage2 extends ClazzTaxonomyPropertyPage2 {

    @Override
    protected OWLClassExpression getClazzDescription() {
        IMainPropertyPage mainPage = getMainPage();
        if (mainPage instanceof ComplexClazzPropertyPage2) {
            return ((ComplexClazzPropertyPage2)mainPage).getDescription();
        }
        return null;
    }

}
