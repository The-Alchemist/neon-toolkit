/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.search.owl.references;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.search.ui.text.Match;
import org.neontoolkit.core.EntityType;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.TreeProviderManager;
import org.neontoolkit.search.SearchPlugin;
import org.neontoolkit.search.command.AbstractSearchCommand;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import com.ontoprise.ontostudio.owl.gui.individualview.IndividualItem;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.datatypes.DatatypeProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.datatypes.DatatypeTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.search.owl.match.AxiomSearchMatch;
import com.ontoprise.ontostudio.search.owl.ui.SearchElement;
import com.ontoprise.ontostudio.search.owl.ui.SearchResults;
import com.ontoprise.ontostudio.search.owl.ui.OwlSearchCommand.FieldTypes;

/**
 * @author Nico Stieler
 */
public class FindReferencesCommand extends AbstractSearchCommand{

    private OWLEntity _entity;

    /**
     * @param owlEntity that should be retrieved
     * @param project
     */
    public FindReferencesCommand(OWLEntity entity, String project) {
        super(project,  entity.getIRI().toString(), false);
        _entity = entity;
    }

    @Override
    protected void perform() throws CommandException {
        try {
            _results = getReferences();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }
    
    private Match[] getReferences() throws NeOnCoreException {
        List<Match> matches = new ArrayList<Match>();
        
        SearchResults results = FindReferencesHelper.search(_entity, getProject());
        for (SearchElement result: results.getResults()) {
            addSearchMatches(result, matches);
        }

        return matches.toArray(new Match[matches.size()]);
    }


    private void addSearchMatches(SearchElement element, List<Match> resultList) {
        String ontology = element.getOntologyUri(); 
        String project = getProject();
        OWLAxiom axiom = element.getAxiom();
        OWLEntity entity = FindReferencesHelper.findSubject(axiom);
        FieldTypes elementType = FindReferencesHelper.findType(entity);
        IRI entityUri = entity.getIRI();
        
        try {
            OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(project);

            ITreeElement elem = null;
            switch (elementType) {
                case CLASSES:
                    OWLClass clazz = factory.getOWLClass(entityUri);
                    elem = new ClazzTreeElement(clazz, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class));
                    break;
                    
                case ANNOTATION_PROPERTIES:
                    OWLAnnotationProperty annotProp = factory.getOWLAnnotationProperty(entityUri);
                    elem = new AnnotationPropertyTreeElement(annotProp, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, AnnotationPropertyHierarchyProvider.class));
                    break;
                    
                case DATA_PROPERTIES:
                    OWLDataProperty dataProp = factory.getOWLDataProperty(entityUri);
                    elem = new DataPropertyTreeElement(dataProp, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, DataPropertyHierarchyProvider.class));
                    break;
                    
                case OBJECT_PROPERTIES:
                    OWLObjectProperty objectProp = factory.getOWLObjectProperty(entityUri);
                    elem = new ObjectPropertyTreeElement(objectProp, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ObjectPropertyHierarchyProvider.class));
                    break;
                    
                case INDIVIDUALS:
                    OWLIndividual indi = factory.getOWLNamedIndividual(entityUri);
                    Set<OWLClass> classes;
                    try {
                        classes = OWLModelFactory.getOWLModel(ontology, project).getClasses(OWLUtilities.toString(indi));
                        for (OWLClass c: classes) {
                            elem = IndividualItem.createNewInstance(indi, c.getIRI().toString(), ontology, project);
                            break; // only add the match once
                        }
                    } catch (NeOnCoreException e) {
                        SearchPlugin.logError(e.getMessage(), e);
                    }
                    break;
                    
                case DATATYPES:
                    OWLDatatype datatype = factory.getOWLDatatype(entityUri);
                    elem = new DatatypeTreeElement(datatype, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, DatatypeProvider.class));
                    break;
                    
                default:
                    System.out.println("!!! found strange element "+element); //$NON-NLS-1$
                    //ignore
                    break;
            }
            
            add(new Match(new AxiomSearchMatch(elem, axiom), 0,0), resultList);
            
        } catch (NeOnCoreException e1) {
            SearchPlugin.logError(e1.getMessage(), e1);
        }
    }

    private void add(Match match, List<Match> matches) {
        if (!matches.contains(match)) {
            matches.add(match);
        }
    }

    @Override
    protected Set<EntityType> getTypes() {
        return null;
    }
}
