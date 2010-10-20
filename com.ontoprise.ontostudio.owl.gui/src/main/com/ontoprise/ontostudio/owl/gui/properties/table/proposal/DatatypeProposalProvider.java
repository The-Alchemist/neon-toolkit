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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLDatatype;

import com.ontoprise.ontostudio.owl.gui.control.AlphabeticalProposalComparator;
import com.ontoprise.ontostudio.owl.model.OWLConstants;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
/**
 * 
 * @author Nico Stieler
 */
public class DatatypeProposalProvider extends AbstractOwlProposalProvider {

    private static final String NULL_DATATYPE = "http://kaon2.semanticweb.org/internal#null"; //$NON-NLS-1$

    public DatatypeProposalProvider(OWLModel owlModel) {
        super(owlModel);
    }

    public DatatypeProposalProvider(OWLModel localOwlModel, OWLModel sourceOwlModel) {
        super(localOwlModel, sourceOwlModel);
    }
    
    public IContentProposal[] getProposals(String contents, int position) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();

        Set<String> datatypes = OWLConstants.OWL_DATATYPE_URIS;
        Set<OWLDatatype> datatypesFromModel;
        try {
            datatypesFromModel = _sourceOwlModel.getAllDatatypes();//NICO are you sure?
            for (OWLDatatype datatype: datatypesFromModel) {
                String[] array = (String[]) datatype.accept(_visitor);
                if (AbstractOwlProposalProvider.checkProposal(array, contents)) {
                    proposals.add(new DatatypeProposal(datatype, array, position, _localOwlModel));//NICO are you sure?
                }
            }
        } catch (NeOnCoreException e) {
            datatypesFromModel = new HashSet<OWLDatatype>();
        }

        for (String datatypeUri: datatypes) {
            if (!datatypeUri.equals(NULL_DATATYPE)) { // bugfix for #10099
                OWLDatatype datatype;
                try {
                    datatype = _sourceOwlModel.getOWLDataFactory().getOWLDatatype(OWLUtilities.toIRI(datatypeUri));//NICO are you sure?
                } catch (NeOnCoreException e) {
                    throw new RuntimeException(e);
                }
                if (!datatypesFromModel.contains(datatype)) {
                    String[] array = (String[]) datatype.accept(_visitor);
                    if (AbstractOwlProposalProvider.checkProposal(array, contents)) {
                        proposals.add(new DatatypeProposal(datatype, array, position, _localOwlModel));//NICO are you sure?
                    }
                }
            }
        }

        Collections.sort(proposals, new AlphabeticalProposalComparator<IContentProposal>());
        return proposals.toArray(new IContentProposal[proposals.size()]);
    }
}
