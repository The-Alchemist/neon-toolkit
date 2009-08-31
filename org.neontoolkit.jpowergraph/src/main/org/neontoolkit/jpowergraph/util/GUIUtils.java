/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package org.neontoolkit.jpowergraph.util;

import java.util.Iterator;

import org.eclipse.swt.graphics.GC;
import org.neontoolkit.jpowergraph.nodes.DefaultImageNode;


/*
 * Created by Werner Hihn
 */

public class GUIUtils {

    /**
     * Calculate the width of a node including the label.
     * 
     * @param imageWidth
     * @param name
     * @param g
     * @return
     */
    public static int nodeExtent(int imageWidth, String name, GC g) {
        int x = imageWidth;
        x += 10;
        x += g.stringExtent(name).x;
        x += 10;
        x += 25;
        return x;
    }

    /**
     * Calculate the width of a list of nodes including the labels.
     * 
     * @param nodeIterator
     * @param g
     * @return
     */
    public static int nodeListExtent(Iterator<?> nodeIterator, GC g) {
        int x = 20;
        for (Iterator<?> iter = nodeIterator; iter.hasNext();) {
            DefaultImageNode node = (DefaultImageNode) iter.next();
            x += nodeExtent(node.getImage().getImageData().width, node.getLabel(), g);
        }
        return x;
    }

}
