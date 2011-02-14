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
import com.ontoprise.ontostudio.owl.model.util.OWLAxiomUtils;
/**
 * 
 * @author Nico Stieler
 */
public class DatatypeAndIndividualProposalProvider extends AbstractOwlProposalProvider {

    private static final String NULL_DATATYPE = "http://kaon2.semanticweb.org/internal#null"; //$NON-NLS-1$

    public DatatypeAndIndividualProposalProvider(OWLModel owlModel) {
        super(owlModel);
    }

    public DatatypeAndIndividualProposalProvider(OWLModel localOwlModel, OWLModel sourceOwlModel) {
        super(localOwlModel, sourceOwlModel);
    }
    
    public IContentProposal[] getProposals(String contents, int position) {
        List<IContentProposal> allProposals = new ArrayList<IContentProposal>();
        List<IContentProposal> datatypeProposals = new ArrayList<IContentProposal>();

        Set<String> datatypes = OWLConstants.OWL_DATATYPE_URIS;
        Set<OWLDatatype> datatypesFromModel;
        try {
            datatypesFromModel = _sourceOwlModel.getAllDatatypes();
            for (OWLDatatype datatype: datatypesFromModel) {
                String[] array = (String[]) datatype.accept(_visitor);
                if (AbstractOwlProposalProvider.checkProposal(array, contents)) {
                    datatypeProposals.add(new DatatypeProposal(datatype, array, position, _localOwlModel));
                }
            }
        } catch (NeOnCoreException e) {
            datatypesFromModel = new HashSet<OWLDatatype>();
        }

        for (String datatypeUri: datatypes) {
            if (!datatypeUri.equals(NULL_DATATYPE)) { // bugfix for #10099
                OWLDatatype datatype;
                try {
                    datatype = _sourceOwlModel.getOWLDataFactory().getOWLDatatype(OWLUtilities.toIRI(datatypeUri));
                } catch (NeOnCoreException e) {
                    throw new RuntimeException(e);
                }
                if (!datatypesFromModel.contains(datatype)) {
                    String[] array = (String[]) datatype.accept(_visitor);
                    if (AbstractOwlProposalProvider.checkProposal(array, contents)) {
                        datatypeProposals.add(new DatatypeProposal(datatype, array, position, _localOwlModel));
                    }
                }
            }
        }
        Collections.sort(datatypeProposals, new AlphabeticalProposalComparator<IContentProposal>());
        allProposals.addAll(datatypeProposals);

        allProposals.add(new OWLTextProposal("<" + OWLAxiomUtils.OWL_INDIVIDUAL + ">", position));  //$NON-NLS-1$//$NON-NLS-2$
        return allProposals.toArray(new IContentProposal[allProposals.size()]);
    }
}
