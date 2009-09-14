/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties.table.proposal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLClass;

import com.ontoprise.ontostudio.owl.gui.control.AlphabeticalProposalComparator;
import com.ontoprise.ontostudio.owl.model.OWLModel;

public class ClazzProposalProvider extends AbstractOwlProposalProvider {

    public ClazzProposalProvider(OWLModel owlModel) {
        super(owlModel);
    }

    public IContentProposal[] getProposals(String contents, int position) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();

        try {
            Set<OWLClass> allClasses = _owlModel.getAllClasses(true);

            for (OWLClass clazz: allClasses) {
                String[] array = (String[]) clazz.accept(_visitor);
                if (AbstractOwlProposalProvider.checkProposal(array, contents)) {
                    proposals.add(new ClazzProposal(clazz, array, position, _owlModel));
                }
            }
        } catch (NeOnCoreException e) {
            // ignore
        }

        Collections.sort(proposals, new AlphabeticalProposalComparator<IContentProposal>());
        return proposals.toArray(new IContentProposal[proposals.size()]);
    }

}
