/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.search.owl.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.search.ui.text.Match;
import org.neontoolkit.core.EntityType;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.TreeProviderManager;
import org.neontoolkit.search.SearchPlugin;
import org.neontoolkit.search.command.AbstractSearchCommand;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.individualview.IIndividualTreeElement;
import com.ontoprise.ontostudio.owl.gui.individualview.IndividualItem;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.datatypes.DatatypeProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.datatypes.DatatypeTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.util.InternalParser;
import com.ontoprise.ontostudio.owl.model.util.InternalParserException;
import com.ontoprise.ontostudio.search.owl.match.AnnotationPropertySearchMatch;
import com.ontoprise.ontostudio.search.owl.match.AnnotationValuesSearchMatch;
import com.ontoprise.ontostudio.search.owl.match.ClassSearchMatch;
import com.ontoprise.ontostudio.search.owl.match.DataPropertySearchMatch;
import com.ontoprise.ontostudio.search.owl.match.DataPropertyValuesSearchMatch;
import com.ontoprise.ontostudio.search.owl.match.DatatypeSearchMatch;
import com.ontoprise.ontostudio.search.owl.match.IndividualSearchMatch;
import com.ontoprise.ontostudio.search.owl.match.ObjectPropertySearchMatch;

/**
 * @author janiko
 * @author Nico Stieler
 *
 */
public class OwlSearchCommand extends AbstractSearchCommand {


    private int _searchFlags;
    private SearchScope _searchScope;
    private String _ontologyId;

    //Reimplementation of EntityTypes which is used in the generic class AbstractSearchCommand, EntityTypes has only FLogic types.
    public enum FieldTypes{
        ONTOLOGY("ontology"), //$NON-NLS-1$
        DATATYPES("datatypes"), //$NON-NLS-1$
        ANNOTATION_PROPERTIES("annotationProperties"), //$NON-NLS-1$
        INDIVIDUALS("individuals"), //$NON-NLS-1$
        ANNOTATION_VALUES("annotationValues"), //$NON-NLS-1$
        DATA_PROPERTY_VALUES("dataPropertyValues"), //$NON-NLS-1$
        DATA_PROPERTIES("dataProperties"), //$NON-NLS-1$
        OBJECT_PROPERTIES("objectProperties"), //$NON-NLS-1$
        CLASSES("classes"); //$NON-NLS-1$
        
        private String _type;
        private FieldTypes(String type) {
            _type = type;
        }
        
        @Override
        public String toString() {
            return _type;
        }
    }

    public enum SearchScope{ ONTOLOGY, PROJECT }
    
    /**
     * @param project_or_onotlogy
     * @param expression
     * @param caseSensitive
     */
    public OwlSearchCommand(String project, String onotlogyId, String expression, boolean caseSensitive, SearchScope searchArea) {
        super(project, expression, caseSensitive);
        this._searchScope = searchArea;
        this._ontologyId = onotlogyId;
    }

    /**
     * @param project_or_onotlogy
     * @param _expression
     * @param flags
     * @param sensitive
     */
    public OwlSearchCommand(String project, String onotlogyId, String expression, int searchFlags, boolean caseSensitive, SearchScope searchArea) {
        super(project, expression, caseSensitive);
        this._searchFlags = searchFlags;
        this._searchScope = searchArea;
        this._ontologyId = onotlogyId;
    }

    @Override 
    protected Set<EntityType> getTypes() {
        // XXX not implemented: look FieldTypes
        return null;
    }
    
    private Set<FieldTypes> getFieldTypes() {
        Set<FieldTypes> types = new HashSet<FieldTypes>();

        if ((_searchFlags & OWLSearchFlags.OWL_CLASS_SEARCH_FLAG) > 0) {
            types.add(FieldTypes.CLASSES);
        }
        if ((_searchFlags & OWLSearchFlags.OWL_OBJECT_PROPERTY_SEARCH_FLAG) > 0) {
            types.add(FieldTypes.OBJECT_PROPERTIES);
        }
        if ((_searchFlags & OWLSearchFlags.OWL_DATA_PROPERTY_SEARCH_FLAG) > 0) {
            types.add(FieldTypes.DATA_PROPERTIES);
        }
        if ((_searchFlags & OWLSearchFlags.OWL_ANNOTATION_PROPERTY_SEARCH_FLAG) > 0) {
            types.add(FieldTypes.ANNOTATION_PROPERTIES);
        }
        if ((_searchFlags & OWLSearchFlags.OWL_INDIVIDUAL_SEARCH_FLAG) > 0) {
            types.add(FieldTypes.INDIVIDUALS);
        }
        if ((_searchFlags & OWLSearchFlags.OWL_ANNOTATION_VALUES_SEARCH_FLAG) > 0) {
            types.add(FieldTypes.ANNOTATION_VALUES);
        }
        if ((_searchFlags & OWLSearchFlags.OWL_DATA_PROPERTY_VALUES_SEARCH_FLAG) > 0) {
            types.add(FieldTypes.DATA_PROPERTY_VALUES);
        }
        if ((_searchFlags & OWLSearchFlags.OWL_DATATYPE_SEARCH_FLAG) > 0) {
            types.add(FieldTypes.DATATYPES);
        }
        return types;
    }
    
    
    
    @Override
    protected void perform() throws CommandException {
        try {
            _results = getOWLResults(getProject(),getFieldTypes());
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }         
    }
    

    protected Match[] getOWLResults(String project_or_onotlogy, Set<FieldTypes> types) throws NeOnCoreException {
        List<Match> matches = new ArrayList<Match>();

        switch(_searchScope){
            case ONTOLOGY:
                matches = getOWLResultsForOntology(types,OWLModelFactory.getOWLModel(_ontologyId,getProject()));
                break;
            case PROJECT:
                matches = getOWLResultsForProject(getProject(), types);
                break;
            default:
                String message = "This should never happen";//$NON-NLS-1$
                throw new InternalNeOnException(message);
        }
        return matches.toArray(new Match[matches.size()]);
    }
    

    protected List<Match> getOWLResultsForOntology(Set<FieldTypes> types, OWLModel owlModel){
        List<Match> matches = new ArrayList<Match>();
        
        SimpleOwlSearchHelper searchHelper = new SimpleOwlSearchHelper(owlModel);
        String searchExpression = getExpression();
        
        SearchResults results;
        try {
            results = searchHelper.search(owlModel.getOntologyURI(), types, searchExpression, false, isCaseSensitive(), 0, 999);
            for (SearchElement result: results.getResults()) {
                addSearchMatches(owlModel.getProjectId(), result, matches);
            }
        } catch (NeOnCoreException e) {
            e.printStackTrace();
        }
        return matches; //it was null, now it is an empty set
    }
    protected List<Match> getOWLResultsForOntologies(Set<OWLModel> ontologies, Set<FieldTypes> types) {
        List<Match> matches = new ArrayList<Match>();
        
        for (OWLModel owlModel: ontologies) {
            matches.addAll(getOWLResultsForOntology(types, owlModel));
        }
        return matches;
    }
    protected List<Match> getOWLResultsForProject( String project, Set<FieldTypes> types) throws NeOnCoreException {
        Set<OWLModel> ontologies = OWLModelFactory.getOWLModels(project);
        return getOWLResultsForOntologies(ontologies, types);
    }
    protected List<Match> getOWLResultsForProjects(Set<String> projects, Set<FieldTypes> types) throws NeOnCoreException {
        Set<OWLModel> ontologies = new HashSet<OWLModel>();
        for(String project : projects)
            ontologies.addAll(OWLModelFactory.getOWLModels(project));
        return getOWLResultsForOntologies(ontologies, types);
    }

    @SuppressWarnings("unchecked")
    private void addSearchMatches(String project, SearchElement element, List<Match> resultList) {
        FieldTypes elementType = element.getType();
        String ontology = element.getOntologyUri(); 
        OWLModel owlModel = null;
        
        try {
            owlModel = OWLModelFactory.getOWLModel(ontology, project);
            OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(project);

            ITreeElement elem = null;
            switch (elementType) {
                case CLASSES:
                    OWLClass clazz = factory.getOWLClass(OWLUtilities.toIRI(element.getEntityUri()));
                    elem = new ClazzTreeElement(clazz, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class));
                    add(new Match(new ClassSearchMatch((ClazzTreeElement) elem), 0, getExpression().length()), resultList);
                    break;
                    
                case ANNOTATION_PROPERTIES:
                    OWLAnnotationProperty annotProp = factory.getOWLAnnotationProperty(OWLUtilities.toIRI(element.getEntityUri()));
                    elem = new AnnotationPropertyTreeElement(annotProp, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, AnnotationPropertyHierarchyProvider.class));
                    add(new Match(new AnnotationPropertySearchMatch((AnnotationPropertyTreeElement) elem), 0, getExpression().length()), resultList);
                    break;
                    
                case DATA_PROPERTIES:
                    OWLDataProperty dataProp = factory.getOWLDataProperty(OWLUtilities.toIRI(element.getEntityUri()));
                    elem = new DataPropertyTreeElement(dataProp, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, DataPropertyHierarchyProvider.class));
                    add(new Match(new DataPropertySearchMatch((DataPropertyTreeElement) elem), 0, getExpression().length()), resultList);
                    break;
                    
                case OBJECT_PROPERTIES:
                    OWLObjectProperty objectProp = factory.getOWLObjectProperty(OWLUtilities.toIRI(element.getEntityUri()));
                    elem = new ObjectPropertyTreeElement(objectProp, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ObjectPropertyHierarchyProvider.class));
                    add(new Match(new ObjectPropertySearchMatch((ObjectPropertyTreeElement) elem), 0, getExpression().length()), resultList);
                    break;
                    
                case INDIVIDUALS:
                    OWLIndividual indi = new InternalParser(element.getEntityUri(), OWLNamespaces.EMPTY_INSTANCE, factory).parseOWLIndividual();// factory.getOWLNamedIndividual(OWLUtilities.toIRI(element.getEntityUri()));
                    Set<OWLClass> classes;
                    try {
                        classes = OWLModelFactory.getOWLModel(ontology, project).getClasses(OWLUtilities.toString(indi));
                        ClassSearchMatch classMatch = null;
                        for (OWLClass c: classes) {
                            classMatch = new ClassSearchMatch(new ClazzTreeElement(c, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class)));
                            elem = IndividualItem.createNewInstance(indi, c.getIRI().toString(), ontology, project);
                            add(new Match(new IndividualSearchMatch((IIndividualTreeElement) elem, classMatch), 0, getExpression().length()), resultList);
                            break; // only add the match once
                        }
                    } catch (NeOnCoreException e) {
                        SearchPlugin.logError(e.getMessage(), e);
                    }
                    break;
                    
                case DATA_PROPERTY_VALUES:
                    {
                        OWLDataPropertyAssertionAxiom axiom = (OWLDataPropertyAssertionAxiom)element.getAxiom();
                        OWLIndividual subject = axiom.getSubject();
                        OWLDataPropertyExpression prop = axiom.getProperty();
    
                        classes = OWLModelFactory.getOWLModel(ontology, project).getClasses(OWLUtilities.toString(subject));
                        ClassSearchMatch classMatch = null;
                        for (OWLClass c: classes) {
                            classMatch = new ClassSearchMatch(new ClazzTreeElement(c, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class)));
                            elem = IndividualItem.createNewInstance(subject, c.getIRI().toString(), ontology, project);
    
                            int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
                            OWLObjectVisitorEx visitor = OWLPlugin.getDefault().getSyntaxManager().getVisitor(owlModel, idDisplayStyle);
                            String value = OWLGUIUtilities.getEntityLabel((String[]) axiom.getObject().accept(visitor));
    
                            add(new Match(
                                    new DataPropertyValuesSearchMatch((IIndividualTreeElement) elem, classMatch, getExpression(), value, prop), 
                                    0, getExpression().length()), 
                                resultList);
    
                            break; // only add the match once
                        }
                        break;
                    }
                    
                case ANNOTATION_VALUES: 
                    {
                        OWLAnnotationAssertionAxiom axiom = (OWLAnnotationAssertionAxiom)element.getAxiom();
                        OWLObject subject;
                        Iterator<OWLEntity> possibleSubjects = owlModel.getEntity(axiom.getSubject().toString()).iterator();
                        if(possibleSubjects.hasNext()) {
                            subject = possibleSubjects.next();
                        } else {
                            subject = owlModel.getOntology(); //subject is the ontology by default
                        }
                        OWLAnnotationProperty prop = axiom.getProperty();
    
                        elem = findTypeForEntity(subject, ontology, project);
    
                        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
                        OWLObjectVisitorEx visitor = OWLPlugin.getDefault().getSyntaxManager().getVisitor(owlModel, idDisplayStyle);
                        String value = OWLGUIUtilities.getEntityLabel((String[]) axiom.getValue().accept(visitor));
                        
                        add(new Match(
                                new AnnotationValuesSearchMatch(elem, prop, value, getExpression()), 
                                0, getExpression().length()), 
                            resultList);
                        break;
                    }
                case DATATYPES:
                    OWLDatatype datatype = factory.getOWLDatatype(OWLUtilities.toIRI(element.getEntityUri()));
                    elem = new DatatypeTreeElement(datatype, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, DatatypeProvider.class));
                    add(new Match(new DatatypeSearchMatch((DatatypeTreeElement) elem), 0, getExpression().length()), resultList);
                    break;

                case ONTOLOGY:
                    break;
                    
                default:
                    break;
            }
        } catch (NeOnCoreException e1) {
            SearchPlugin.logError(e1.getMessage(), e1);
        } catch (InternalParserException e) {
            SearchPlugin.logError(e.getMessage(), e);
        }
    }

    private void add(Match match, List<Match> matches) {
        if (!matches.contains(match)) {
            matches.add(match);
        }
    }

    /**
     * @param entity
     * @return
     */
    private ITreeElement findTypeForEntity(OWLObject entity, String ontology, String project) {
        if(entity instanceof OWLClass) {
            return new ClazzTreeElement((OWLClass)entity, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class));
            
        } else if(entity instanceof OWLDataProperty) {
            return new DataPropertyTreeElement((OWLDataProperty)entity, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, DataPropertyHierarchyProvider.class));

        } else if(entity instanceof OWLObjectProperty) {
            return new ObjectPropertyTreeElement((OWLObjectProperty)entity, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ObjectPropertyHierarchyProvider.class));
        
        } else if(entity instanceof OWLAnnotationProperty) {
            return new AnnotationPropertyTreeElement((OWLAnnotationProperty)entity, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, AnnotationPropertyHierarchyProvider.class));
        
        } else if(entity instanceof OWLIndividual) {

            Set<OWLClass> classes;
            try {
                classes = OWLModelFactory.getOWLModel(ontology, project).getClasses(OWLUtilities.toString(entity));
//                ClassSearchMatch classMatch = null;
                for (OWLClass c: classes) {
//                    classMatch = new ClassSearchMatch(new ClazzTreeElement(c, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class)));
                    return IndividualItem.createNewInstance((OWLIndividual)entity, c.getIRI().toString(), ontology, project);
                }
            } catch (NeOnCoreException e) {
                SearchPlugin.logError(e.getMessage(), e);
            }
        
        } else if(entity instanceof OWLDatatype) {
            return new DatatypeTreeElement((OWLDatatype)entity, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, DatatypeProvider.class));
        
        } else if(entity instanceof OWLOntology) {
            return new OntologyTreeElement(project, ontology, TreeProviderManager.getDefault().getProvider(MTreeView.ID, DatatypeProvider.class));
        } 
        
        return new OntologyTreeElement(project, ontology, TreeProviderManager.getDefault().getProvider(MTreeView.ID, DatatypeProvider.class));
    }
}
