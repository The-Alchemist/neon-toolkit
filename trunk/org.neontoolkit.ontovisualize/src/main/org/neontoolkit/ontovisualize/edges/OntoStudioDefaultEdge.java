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

import net.sourceforge.jpowergraph.DefaultEdge;
import net.sourceforge.jpowergraph.Node;

/*
 * Created by Werner Hihn
 */

public class OntoStudioDefaultEdge extends DefaultEdge {

    private String _label;

    public OntoStudioDefaultEdge(Node from, Node to, String label) {
        super(from, to);
        _label = label;
    }

    public OntoStudioDefaultEdge(Node from, Node to) {
        super(from, to);
    }

    @Override
    public String toString() {
        String l = (_label == null || _label.equals("")) ? "" : "[" + _label + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        return l + getFrom().getLabel() + "_" + getTo().getLabel(); //$NON-NLS-1$
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OntoStudioDefaultEdge)) {
            return false;
        }
        return getFrom().equals(((OntoStudioDefaultEdge) obj).getFrom()) && getTo().equals(((OntoStudioDefaultEdge) obj).getTo());
    }

}
