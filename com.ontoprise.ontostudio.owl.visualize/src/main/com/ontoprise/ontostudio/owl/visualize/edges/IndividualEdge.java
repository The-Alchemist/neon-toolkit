/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package com.ontoprise.ontostudio.owl.visualize.edges;

import net.sourceforge.jpowergraph.Node;

import org.neontoolkit.ontovisualize.edges.OntoStudioDefaultEdge;

/*
 * Created by Werner Hihn
 */

public class IndividualEdge extends OntoStudioDefaultEdge {

    public IndividualEdge(Node from, Node to, String label) {
        super(from, to, label);
    }

    public IndividualEdge(Node from, Node to) {
        super(from, to);
    }

}
