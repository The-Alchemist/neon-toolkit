/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.control;

import java.util.Comparator;

import org.neontoolkit.gui.NeOnUIPlugin;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.syntax.ISyntaxManager;
import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.util.Cast;

public class AlphabeticalLocatedItemComparer<A extends LocatedItem<?>> implements Comparator<LocatedItem<?>> {

    private OWLObjectVisitorEx<?> _visitor;

    /**
	 * 
	 */
    public AlphabeticalLocatedItemComparer(OWLModel owlModel) {
        ISyntaxManager manager = OWLPlugin.getDefault().getSyntaxManager();
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        _visitor = manager.getVisitor(owlModel, idDisplayStyle);
    }

    @Override
    public int compare(LocatedItem<?> o1, LocatedItem<?> o2) {
        OWLAxiom axiom1 = Cast.cast(o1.getItem());
        String[] result1 = (String[]) axiom1.accept(_visitor);
        OWLAxiom axiom2 = Cast.cast(o2.getItem());
        String[] result2 = (String[]) axiom2.accept(_visitor);
        return result1[1].compareTo(result2[1]);
    }

}
