/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.ontovisualize.edges;

import net.sourceforge.jpowergraph.Node;

/*
 * Created by Werner Hihn
 */

public class DashedEdge extends OntoStudioDefaultEdge {

	public DashedEdge(Node from, Node to, String label) {
		super(from, to, label);
	}

	public DashedEdge(Node from, Node to) {
		super(from, to);
	}

}
