/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

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
