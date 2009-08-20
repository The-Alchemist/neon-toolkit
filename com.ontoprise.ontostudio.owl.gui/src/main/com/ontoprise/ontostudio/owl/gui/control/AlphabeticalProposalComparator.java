/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.control;

import java.util.Comparator;

import org.eclipse.jface.fieldassist.IContentProposal;

public class AlphabeticalProposalComparator<A extends IContentProposal> implements Comparator<IContentProposal> {

    public int compare(IContentProposal o1, IContentProposal o2) {
        return o1.getLabel().toLowerCase().compareTo(o2.getLabel().toLowerCase());
    }

}
