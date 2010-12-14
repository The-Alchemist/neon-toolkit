/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

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
