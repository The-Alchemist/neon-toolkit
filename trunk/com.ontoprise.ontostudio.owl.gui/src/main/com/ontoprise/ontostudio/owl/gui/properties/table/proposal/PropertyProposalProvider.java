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
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import com.ontoprise.ontostudio.owl.gui.control.AlphabeticalProposalComparator;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.OntoStudioOWLConstants;

public class PropertyProposalProvider extends AbstractOwlProposalProvider {

    public static final int ALL_PROPERTY_STYLE = 7;
    public static final int OBJECT_PROPERTY_STYLE = 1;
    public static final int DATA_PROPERTY_STYLE = 2;
    public static final int ANNOTATION_PROPERTY_STYLE = 4;

    private int _style = ALL_PROPERTY_STYLE;

    public PropertyProposalProvider(OWLModel owlModel, int style) {
        super(owlModel);
        _style = style;
    }

    public IContentProposal[] getProposals(String contents, int position) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        try {
            List<IContentProposal> objectProposals = new ArrayList<IContentProposal>();
            List<IContentProposal> dataProposals = new ArrayList<IContentProposal>();
            List<IContentProposal> annotProposals = new ArrayList<IContentProposal>();

            Set<OWLObjectProperty> objectProps = new HashSet<OWLObjectProperty>();
            Set<OWLDataProperty> dataProps = new HashSet<OWLDataProperty>();
            Set<OWLAnnotationProperty> annotProps = new HashSet<OWLAnnotationProperty>();

            if ((_style & OBJECT_PROPERTY_STYLE) > 0) {
                objectProps = _owlModel.getAllObjectProperties(true);

                try {
                    objectProps.add(_owlModel.getOWLDataFactory().getOWLTopObjectProperty());
                    objectProps.add(_owlModel.getOWLDataFactory().getOWLBottomObjectProperty());
                } catch (Exception e) {
                    //ignore
                }

                for (OWLObjectProperty prop: objectProps) {
                    String[] array = (String[]) prop.accept(_visitor);
                    if (AbstractOwlProposalProvider.checkProposal(array, contents)) {
                        objectProposals.add(new ObjectPropertyProposal(prop, array, position, _owlModel));
                    }
                }

            }

            if ((_style & DATA_PROPERTY_STYLE) > 0) {
                dataProps = _owlModel.getAllDataProperties(true);
                
                try {
                    dataProps.add(_owlModel.getOWLDataFactory().getOWLTopDataProperty());
                    dataProps.add(_owlModel.getOWLDataFactory().getOWLBottomDataProperty());
                } catch (Exception e) {
                    //ignore
                }

                for (OWLDataProperty prop: dataProps) {
                    String[] array = (String[]) prop.accept(_visitor);
                    if (AbstractOwlProposalProvider.checkProposal(array, contents)) {
                        dataProposals.add(new DataPropertyProposal(prop, array, position, _owlModel));
                    }
                }
            }

            if ((_style & ANNOTATION_PROPERTY_STYLE) > 0) {
                // standard annotation properties
                List<OWLAnnotationProperty> standardAnnotationProperties = new ArrayList<OWLAnnotationProperty>();
                for (String prop: OntoStudioOWLConstants.OWL_STANDARD_ANNOTATION_PROPERTIES) {
                    standardAnnotationProperties.add(_owlModel.getOWLDataFactory().getOWLAnnotationProperty(OWLUtilities.toURI(prop)));
                }

                annotProps = _owlModel.getAllAnnotationProperties(true);
                annotProps.addAll(standardAnnotationProperties);

                for (OWLAnnotationProperty prop: annotProps) {
                    String[] array = (String[]) prop.accept(_visitor);
                    if (AbstractOwlProposalProvider.checkProposal(array, contents)) {
                        annotProposals.add(new AnnotationPropertyProposal(prop, array, position, _owlModel));
                    }
                }
            }

            Collections.sort(objectProposals, new AlphabeticalProposalComparator<IContentProposal>());
            Collections.sort(dataProposals, new AlphabeticalProposalComparator<IContentProposal>());
            Collections.sort(annotProposals, new AlphabeticalProposalComparator<IContentProposal>());
            proposals.addAll(objectProposals);
            proposals.addAll(dataProposals);
            proposals.addAll(annotProposals);
        } catch (NeOnCoreException e) {
            // nothing to do
        }

        return proposals.toArray(new IContentProposal[proposals.size()]);
    }
}