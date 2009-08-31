/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package org.neontoolkit.jpowergraph.nodes;

import net.sourceforge.jpowergraph.DefaultNode;

/*
 * Created by Werner Hihn
 */

public class OntoPowerGraphDefaultNode extends DefaultNode implements IOntoPowerGraphNode {

    public OntoPowerGraphDefaultNode() {
        super();
    }
    
    public boolean isRoot() {
        return false;
    }

}
