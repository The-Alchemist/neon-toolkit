/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.syntax.manchester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

import com.ontoprise.ontostudio.owl.gui.properties.table.proposal.AbstractOwlProposalProvider;
import com.ontoprise.ontostudio.owl.gui.properties.table.proposal.ClazzProposalProvider;
import com.ontoprise.ontostudio.owl.gui.properties.table.proposal.DatatypeProposalProvider;
import com.ontoprise.ontostudio.owl.gui.properties.table.proposal.IndividualProposalProvider;
import com.ontoprise.ontostudio.owl.gui.properties.table.proposal.OWLTextProposal;
import com.ontoprise.ontostudio.owl.gui.properties.table.proposal.PropertyProposalProvider;
import com.ontoprise.ontostudio.owl.model.OWLModel;

public class ManchesterSyntaxProposalProvider implements IContentProposalProvider {

    private OWLModel _owlModel;

    public void setOwlModel(OWLModel owlModel) {
        _owlModel = owlModel;
    }

    public IContentProposal[] getProposals(String contents, int position) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();

        ManchesterSyntaxContextReader contextReader = new ManchesterSyntaxContextReader(contents.substring(0, position), _owlModel);
        String current = contextReader.getLastToken();
        int result = contextReader.getResult();

        if ((result & ManchesterSyntaxContextReader.EXPECTCLASSCONSTRUCTORKEYWORD) > 0) {
            proposals.addAll(addClassConstructorKeywords(current, position));
        }

        if ((result & ManchesterSyntaxContextReader.EXPECTRESTRICTIONKEYWORD) > 0) {
            proposals.addAll(addRestrictionKeywords(current, position));
        }

        if ((result & ManchesterSyntaxContextReader.EXPECTFACET) > 0) {
            proposals.addAll(addFacet(current, position));
        }

        if ((result & ManchesterSyntaxContextReader.EXPECTFACETKEYWORD) > 0) {
            proposals.addAll(addFacetKeywords(current, position));
        }

        if ((result & ManchesterSyntaxContextReader.EXPECTCLASS) > 0) {
            proposals.addAll(addClasses(current, position));
        }

        if ((result & ManchesterSyntaxContextReader.EXPECTPROPERTY) > 0) {
            proposals.addAll(addProperties(current, position));
        }

        if ((result & ManchesterSyntaxContextReader.EXPECTDATATYPE) > 0) {
            proposals.addAll(addDatatypes(current, position));
        }

        if ((result & ManchesterSyntaxContextReader.EXPECTINDIVIDUAL) > 0) {
            proposals.addAll(addIndividuals(current, position));
        }

        return proposals.toArray(new IContentProposal[proposals.size()]);
    }

    private List<IContentProposal> addFacetKeywords(String currentToken, int position) {
        String[] keywords = ManchesterSyntaxConstants.getFacetKeywords();
        return filterProposalsStartingWith(currentToken, position, keywords);
    }

    private List<IContentProposal> addFacet(String currentToken, int position) {
        String[] keywords = ManchesterSyntaxConstants.getFacets();
        return filterProposalsStartingWith(currentToken, position, keywords);
    }

    private List<IContentProposal> addRestrictionKeywords(String currentToken, int position) {
        String[] keywords = ManchesterSyntaxConstants.getRestrictionKeywords();
        return filterProposalsStartingWith(currentToken, position, keywords);
    }

    private List<IContentProposal> addClassConstructorKeywords(String currentToken, int position) {
        String[] keywords = ManchesterSyntaxConstants.getClassConstructorKeywords();
        return filterProposalsStartingWith(currentToken, position, keywords);
    }

    private List<IContentProposal> addClasses(String currentToken, int position) {
        ClazzProposalProvider provider = new ClazzProposalProvider(_owlModel);
        return Arrays.asList(provider.getProposals(currentToken, position));
    }

    private List<IContentProposal> addProperties(String currentToken, int position) {
        AbstractOwlProposalProvider provider = new PropertyProposalProvider(_owlModel, PropertyProposalProvider.DATA_PROPERTY_STYLE | PropertyProposalProvider.OBJECT_PROPERTY_STYLE);
        return Arrays.asList(provider.getProposals(currentToken, position));
    }

    private List<IContentProposal> addDatatypes(String currentToken, int position) {
        DatatypeProposalProvider provider = new DatatypeProposalProvider(_owlModel);
        return Arrays.asList(provider.getProposals(currentToken, position));
    }

    private List<IContentProposal> addIndividuals(String currentToken, int position) {
        IndividualProposalProvider provider = new IndividualProposalProvider(_owlModel);
        return Arrays.asList(provider.getProposals(currentToken, position));
    }

    private List<IContentProposal> filterProposalsStartingWith(String needle, int position, String[] restrictionKeywords) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        for (int i = 0; i < restrictionKeywords.length; i++) {
            String keyword = restrictionKeywords[i];
            if (keyword.startsWith(needle)) {
                int pos = position - needle.length() + keyword.length() + 1;
                proposals.add(new OWLTextProposal(keyword, pos));
            }
        }
        return proposals;
    }

}
