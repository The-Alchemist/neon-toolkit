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
import java.util.List;
import java.util.Set;

import org.eclipse.search.ui.text.Match;
import org.neontoolkit.core.EntityType;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.TreeProviderManager;
import org.neontoolkit.search.SearchPlugin;
import org.neontoolkit.search.command.AbstractSearchCommand;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAxiom;
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

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.individualview.IndividualViewItem;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
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
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
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
 *
 */
public class OwlSearchCommand extends AbstractSearchCommand{


    private int _searchFlags;

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
    /**
     * @param project
     * @param expression
     * @param caseSensitive
     */
    public OwlSearchCommand(String project, String expression, boolean caseSensitive) {
        super(project, expression, caseSensitive);
    }

    /**
     * @param project
     * @param _expression
     * @param flags
     * @param sensitive
     */
    public OwlSearchCommand(String project, String expression, int searchFlags, boolean caseSensitive) {
        super(project, expression, caseSensitive);
        this._searchFlags = searchFlags;
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
            _results = getOWLResults(getProject(), getFieldTypes());
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
              
    }
    

    public Match[] getOWLResults(String project, Set<FieldTypes> types) throws NeOnCoreException {

        List<Match> searchResults = new ArrayList<Match>();
        for (OWLModel owlModel: OWLModelFactory.getOWLModels(project)) {
            
            //XXX Temporary simple solution until the owl lucene indexer is mirgrated 
            SimpleOwlSearchHelper searchHelper = new SimpleOwlSearchHelper(owlModel);
//            OwlSearchHelper searchHelper = new OwlSearchHelper(owlModel.getConnection());
            
            String searchExpression = getExpression();
            
            SearchResults results;
            results = searchHelper.search(owlModel.getOntologyURI(), types, searchExpression, true, isCaseSensitive(), 0, 9999);
            
            List<SearchElement> resultElements = results.getResults();
            for (SearchElement result: resultElements) {
                addSearchMatches(project, owlModel.getOntologyURI(), result, searchResults);
            }

        }
        if (searchResults.size() > 0) {
            OWLGUIUtilities.removeDuplicate(searchResults);
        }

        return searchResults.toArray(new Match[0]);
    }

    private void addSearchMatches(String project, String ontology, SearchElement element, List<Match> resultList) {
        Set<FieldTypes> types = getFieldTypes();
        for (FieldTypes type: types) {
            FieldTypes elementType = element.getType();

            if (type.equals(elementType)) {
                AbstractOwlEntityTreeElement elem = null;
                OWLModel owlModel = null;
                try {
                    owlModel = OWLModelFactory.getOWLModel(ontology, project);
                    OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(project);
                    switch (type) {
                        case CLASSES:
                            OWLClass clazz = factory.getOWLClass(OWLUtilities.toURI(element.getEntityUri()));
                            elem = new ClazzTreeElement(clazz, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class));
                            add(new Match(new ClassSearchMatch((ClazzTreeElement) elem), 0, getExpression().length()), resultList);
                            break;
                            
                        case ANNOTATION_PROPERTIES:
                            OWLAnnotationProperty annotProp = factory.getOWLAnnotationProperty(OWLUtilities.toURI(element.getEntityUri()));
                            elem = new AnnotationPropertyTreeElement(annotProp, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, AnnotationPropertyHierarchyProvider.class));
                            add(new Match(new AnnotationPropertySearchMatch((AnnotationPropertyTreeElement) elem), 0, getExpression().length()), resultList);
                            break;
                            
                        case DATA_PROPERTIES:
                            OWLDataProperty dataProp = factory.getOWLDataProperty(OWLUtilities.toURI(element.getEntityUri()));
                            elem = new DataPropertyTreeElement(dataProp, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, DataPropertyHierarchyProvider.class));
                            add(new Match(new DataPropertySearchMatch((DataPropertyTreeElement) elem), 0, getExpression().length()), resultList);
                            break;
                            
                        case OBJECT_PROPERTIES:
                            OWLObjectProperty objectProp = factory.getOWLObjectProperty(OWLUtilities.toURI(element.getEntityUri()));
                            elem = new ObjectPropertyTreeElement(objectProp, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ObjectPropertyHierarchyProvider.class));
                            add(new Match(new ObjectPropertySearchMatch((ObjectPropertyTreeElement) elem), 0, getExpression().length()), resultList);
                            break;
                            
                        case INDIVIDUALS:
                            OWLIndividual indi = factory.getOWLNamedIndividual(OWLUtilities.toURI(element.getEntityUri()));
                            Set<OWLClass> classes;
                            try {
                                classes = OWLModelFactory.getOWLModel(ontology, project).getClasses(element.getEntityUri());
                                for (OWLClass cl: classes) {
                                    elem = new IndividualViewItem(indi, cl.getURI().toString(), ontology, project);
                                    classes = OWLModelFactory.getOWLModel(ontology, project).getClasses(OWLUtilities.toString(indi));
                                    List<ClassSearchMatch> classMatchesList = new ArrayList<ClassSearchMatch>();
                                    for (OWLClass c: classes) {
                                        ClassSearchMatch classSearchMatch = new ClassSearchMatch(new ClazzTreeElement(c, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class)));
                                        classMatchesList.add(classSearchMatch);
                                    }
                                    for (OWLClass c: classes) {
                                        elem = new IndividualViewItem(indi, c.getURI().toString(), ontology, project);
                                        add(new Match(new IndividualSearchMatch((IndividualViewItem) elem, classMatchesList.toArray(new ClassSearchMatch[classMatchesList.size()])), 0, getExpression().length()), resultList);
                                    }
                                }
                            } catch (NeOnCoreException e) {
                                SearchPlugin.logError(e.getMessage(), e);
                            }
                            break;
                            
                        case DATA_PROPERTY_VALUES:
                            String axiomString = element.getAxiom();
                            OWLAxiom axiom = OWLUtilities.axiom(axiomString, owlModel.getNamespaces(), owlModel.getOWLDataFactory());
                            if (axiom instanceof OWLDataPropertyAssertionAxiom) {
                                OWLDataPropertyAssertionAxiom member = (OWLDataPropertyAssertionAxiom) axiom;
                                OWLIndividual individual = member.getSubject();
                                OWLDataPropertyExpression prop = member.getProperty();
                                int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
                                OWLObjectVisitorEx visitor = OWLPlugin.getDefault().getSyntaxManager().getVisitor(owlModel, idDisplayStyle);
                                String value = OWLGUIUtilities.getEntityLabel((String[]) member.getObject().accept(visitor));
                                Set<OWLClass> clazzes;
                                try {
                                    clazzes = owlModel.getClasses(OWLUtilities.toString(individual));
                                    List<ClassSearchMatch> classMatchesList = new ArrayList<ClassSearchMatch>();
                                    for (OWLClass c: clazzes) {
                                        ClassSearchMatch classSearchMatch = new ClassSearchMatch(new ClazzTreeElement(c, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class)));
                                        classMatchesList.add(classSearchMatch);
                                    }
                                    for (OWLClass c: clazzes) {
                                        elem = new IndividualViewItem(individual, c.getURI().toString(), ontology, project);
                                        add(new Match(new DataPropertyValuesSearchMatch((IndividualViewItem) elem, classMatchesList.toArray(new ClassSearchMatch[classMatchesList.size()]), getExpression(), value, prop), 0, getExpression().length()), resultList);
                                    }
                                } catch (NeOnCoreException e) {
                                    SearchPlugin.logError(e.getMessage(), e);
                                }
                            }
                            break;
                            
                        case ANNOTATION_VALUES:
                            String axiomStr = element.getAxiom();
                            OWLAxiom a = OWLUtilities.axiom(axiomStr, owlModel.getNamespaces(), owlModel.getOWLDataFactory());
                            if (a instanceof OWLAnnotationAssertionAxiom) {
                                OWLAnnotationAssertionAxiom annot = (OWLAnnotationAssertionAxiom) a;
                                OWLAnnotationSubject entity = annot.getSubject();
                                if (!(entity instanceof OWLEntity)) {
                                    // TODO: migration
                                    throw new UnsupportedOperationException("TODO: migration"); //$NON-NLS-1$
                                }
                                OWLAnnotationProperty prop = annot.getAnnotation().getProperty();
                                OWLObject annotationValue = annot.getAnnotation().getValue();
                                StringBuffer buffer = new StringBuffer();
                                OWLUtilities.toString(annotationValue, buffer, owlModel.getNamespaces());
                                addSearchResults(prop, (OWLEntity)entity, buffer.toString(), getExpression(), ontology, project, resultList);
                            }
                            break;

                        case DATATYPES:
                            OWLDatatype datatype = factory.getOWLDatatype(OWLUtilities.toURI(element.getEntityUri()));
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
                }
            }
        }

    }

    private void addSearchResults(OWLAnnotationProperty annotProperty, OWLEntity entity, String annotationValue, String searchString, String ontology, String project, List<Match> resultList) throws NeOnCoreException {
        AbstractOwlEntityTreeElement elem = null;
        if (entity instanceof OWLClass) {
            elem = new ClazzTreeElement(entity, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class));
            add(new Match(new AnnotationValuesSearchMatch((ClazzTreeElement) elem, annotProperty, annotationValue, searchString), 0, getExpression().length()), resultList);

        } else if (entity instanceof OWLObjectProperty) {
            elem = new ObjectPropertyTreeElement(entity, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ObjectPropertyHierarchyProvider.class));
            add(new Match(new AnnotationValuesSearchMatch((ObjectPropertyTreeElement) elem, annotProperty, annotationValue, searchString), 0, getExpression().length()), resultList);
        
        } else if (entity instanceof OWLDataProperty) {
            elem = new DataPropertyTreeElement(entity, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, DataPropertyHierarchyProvider.class));
            add(new Match(new AnnotationValuesSearchMatch((DataPropertyTreeElement) elem, annotProperty, annotationValue, searchString), 0, getExpression().length()), resultList);
        
        } else if (entity instanceof OWLAnnotationProperty) {
            elem = new AnnotationPropertyTreeElement(entity, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, AnnotationPropertyHierarchyProvider.class));
            add(new Match(new AnnotationValuesSearchMatch((AnnotationPropertyTreeElement) elem, annotProperty, annotationValue, searchString), 0, getExpression().length()), resultList);
        
        } else if (entity instanceof OWLIndividual) {
            Set<OWLClass> clazzes = OWLModelFactory.getOWLModel(ontology, project).getClasses(entity.getURI().toString());

            List<ClassSearchMatch> classMatchesList = new ArrayList<ClassSearchMatch>();
            for (OWLClass c: clazzes) {
                ClassSearchMatch classSearchMatch = new ClassSearchMatch(new ClazzTreeElement(c, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class)));
                classMatchesList.add(classSearchMatch);
            }
            for (OWLClass c: clazzes) {
                elem = new IndividualViewItem((OWLIndividual) entity, c.getURI().toString(), ontology, project);
                add(new Match(new AnnotationValuesSearchMatch((IndividualViewItem) elem, annotProperty, annotationValue, searchString), 0, getExpression().length()), resultList);
            }
        }
    }

    private void add(Match match, List<Match> resultList) {
        if (!resultList.contains(match)) {
            resultList.add(match);
        }
    }

//    @Override
//    public ISearchResult getSearchResult() {
//        if (_searchResult == null) {
//            _searchResult = new SearchResult(this);
//        }
//        return _searchResult;
//    }

//    @Override
//    public IStatus run(IProgressMonitor monitor) {
//        final SearchResult textResult = (SearchResult) getSearchResult();
//        textResult.removeAll();
//        // Don't need to pass in working copies in 3.0 here
//
//        Set<FieldTypes> transformedTypes = getFieldTypes();
//
//        int work = _projects.length;
//        monitor.beginTask(com.ontoprise.ontostudio.owl.gui.Messages.OwlSearchQuery_0, work); 
//        for (String project: _projects) {
//            List<Object> addedMatches = new ArrayList<Object>();
//            try {
//                Match[] matches = getOWLResults(project, transformedTypes);
//                for (Match match: matches) {
//                    Object element = match.getElement();
//                    if (!addedMatches.contains(element)) {
//                        textResult.addMatch(match);
//                        addedMatches.add(element);
//                    }
//                }
//            } catch (Exception e) {
//                SearchPlugin.logError("", e); //$NON-NLS-1$
//            } finally {
//                monitor.worked(1);
//            }
//        }
//        String message = Messages.AbstractSearchQuery_3;
//        MessageFormat.format(message, new Object[] {new Integer(textResult.getMatchCount())});
//        return new Status(IStatus.OK, SearchPlugin.getDefault().getBundle().getSymbolicName(), 0, message, null);
//    }
}
