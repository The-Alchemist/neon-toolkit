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

import org.neontoolkit.search.command.AbstractSearchCommand;
import org.neontoolkit.search.ui.AbstractSearchQuery;
import org.neontoolkit.search.ui.SearchResult;

/* 
 * Created on 04.04.2008
 * @author Dirk Wenke
 *
 * Function:
 * Keywords:
 */
/**
 * Type comment
 */
public class OwlSearchQuery extends AbstractSearchQuery {

    private boolean _caseSensitive;
    private SearchResult _searchResult;

    /**
     * @param searchString
     * @param string
     * @param searchFlags
     * @param projects
     */
    public OwlSearchQuery(String searchString, boolean caseSensitive, int searchFlags, String[] projects) {
        super(searchString, searchFlags, projects);
        _caseSensitive = caseSensitive;
    }

//    /*
//     * (non-Javadoc)
//     * 
//     * @see com.ontoprise.ontostudio.search.ui.AbstractSearchQuery#addSearchMatches(java.lang.String, java.lang.String,
//     * com.ontoprise.indexer.SearchHelper.SearchElement, java.util.List)
//     */
//    protected void addSearchMatches(String project, String ontology, SearchElement element, List<Match> resultList) {
//        Set<FieldTypes> types = getFieldTypes();
//        for (FieldTypes type: types) {
//            FieldTypes elementType = element.getType();
//
//            if (type.equals(elementType)) {
//                AbstractOwlEntityTreeElement elem = null;
//                OWLModel owlModel = null;
//                try {
//                    owlModel = OWLModelFactory.getOWLModel(ontology, project);
//                    OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(project);
//                    switch (type) {
//                        case CLASSES:
//                            OWLClass clazz = factory.getOWLClass(OWLUtilities.toURI(element.getEntityUri()));
//                            elem = new ClazzTreeElement(clazz, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class));
//                            add(new Match(new ClassSearchMatch((ClazzTreeElement) elem), 0, _expression.length()), resultList);
//                            break;
//                            
//                        case ANNOTATION_PROPERTIES:
//                            OWLAnnotationProperty annotProp = factory.getOWLAnnotationProperty(OWLUtilities.toURI(element.getEntityUri()));
//                            elem = new AnnotationPropertyTreeElement(annotProp, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, AnnotationPropertyHierarchyProvider.class));
//                            add(new Match(new AnnotationPropertySearchMatch((AnnotationPropertyTreeElement) elem), 0, _expression.length()), resultList);
//                            break;
//                            
//                        case DATA_PROPERTIES:
//                            OWLDataProperty dataProp = factory.getOWLDataProperty(OWLUtilities.toURI(element.getEntityUri()));
//                            elem = new DataPropertyTreeElement(dataProp, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, DataPropertyHierarchyProvider.class));
//                            add(new Match(new DataPropertySearchMatch((DataPropertyTreeElement) elem), 0, _expression.length()), resultList);
//                            break;
//                            
//                        case OBJECT_PROPERTIES:
//                            OWLObjectProperty objectProp = factory.getOWLObjectProperty(OWLUtilities.toURI(element.getEntityUri()));
//                            elem = new ObjectPropertyTreeElement(objectProp, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ObjectPropertyHierarchyProvider.class));
//                            add(new Match(new ObjectPropertySearchMatch((ObjectPropertyTreeElement) elem), 0, _expression.length()), resultList);
//                            break;
//                            
//                        case INDIVIDUALS:
//                            OWLIndividual indi = factory.getOWLNamedIndividual(OWLUtilities.toURI(element.getEntityUri()));
//                            Set<OWLClass> classes;
//                            try {
//                                classes = OWLModelFactory.getOWLModel(ontology, project).getClasses(element.getEntityUri());
//                                for (OWLClass cl: classes) {
//                                    elem = new IndividualViewItem(indi, cl.getURI().toString(), ontology, project);
//                                    classes = OWLModelFactory.getOWLModel(ontology, project).getClasses(OWLUtilities.toString(indi));
//                                    List<ClassSearchMatch> classMatchesList = new ArrayList<ClassSearchMatch>();
//                                    for (OWLClass c: classes) {
//                                        ClassSearchMatch classSearchMatch = new ClassSearchMatch(new ClazzTreeElement(c, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class)));
//                                        classMatchesList.add(classSearchMatch);
//                                    }
//                                    for (OWLClass c: classes) {
//                                        elem = new IndividualViewItem(indi, c.getURI().toString(), ontology, project);
//                                        add(new Match(new IndividualSearchMatch((IndividualViewItem) elem, classMatchesList.toArray(new ClassSearchMatch[classMatchesList.size()])), 0, _expression.length()), resultList);
//                                    }
//                                }
//                            } catch (NeOnCoreException e) {
//                                SearchPlugin.logError(e.getMessage(), e);
//                            }
//                            break;
//                            
//                        case DATA_PROPERTY_VALUES:
//                            String axiomString = element.getAxiom();
//                            OWLAxiom axiom = OWLUtilities.axiom(axiomString, owlModel.getNamespaces(), owlModel.getOWLDataFactory());
//                            if (axiom instanceof OWLDataPropertyAssertionAxiom) {
//                                OWLDataPropertyAssertionAxiom member = (OWLDataPropertyAssertionAxiom) axiom;
//                                OWLIndividual individual = member.getSubject();
//                                OWLDataPropertyExpression prop = member.getProperty();
//                                int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
//                                OWLObjectVisitorEx visitor = OWLPlugin.getDefault().getSyntaxManager().getVisitor(owlModel, idDisplayStyle);
//                                String value = OWLGUIUtilities.getEntityLabel((String[]) member.getObject().accept(visitor));
//                                Set<OWLClass> clazzes;
//                                try {
//                                    clazzes = owlModel.getClasses(OWLUtilities.toString(individual));
//                                    List<ClassSearchMatch> classMatchesList = new ArrayList<ClassSearchMatch>();
//                                    for (OWLClass c: clazzes) {
//                                        ClassSearchMatch classSearchMatch = new ClassSearchMatch(new ClazzTreeElement(c, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class)));
//                                        classMatchesList.add(classSearchMatch);
//                                    }
//                                    for (OWLClass c: clazzes) {
//                                        elem = new IndividualViewItem(individual, c.getURI().toString(), ontology, project);
//                                        add(new Match(new DataPropertyValuesSearchMatch((IndividualViewItem) elem, classMatchesList.toArray(new ClassSearchMatch[classMatchesList.size()]), _expression, value, prop), 0, _expression.length()), resultList);
//                                    }
//                                } catch (NeOnCoreException e) {
//                                    SearchPlugin.logError(e.getMessage(), e);
//                                }
//                            }
//                            break;
//                            
//                        case ANNOTATION_VALUES:
//                            String axiomStr = element.getAxiom();
//                            OWLAxiom a = OWLUtilities.axiom(axiomStr, owlModel.getNamespaces(), owlModel.getOWLDataFactory());
//                            if (a instanceof OWLAnnotationAssertionAxiom) {
//                                OWLAnnotationAssertionAxiom annot = (OWLAnnotationAssertionAxiom) a;
//                                OWLAnnotationSubject entity = annot.getSubject();
//                                if (!(entity instanceof OWLEntity)) {
//                                    // TODO: migration
//                                    throw new UnsupportedOperationException("TODO: migration");
//                                }
//                                OWLAnnotationProperty prop = annot.getAnnotation().getProperty();
//                                OWLObject annotationValue = annot.getAnnotation().getValue();
//                                StringBuffer buffer = new StringBuffer();
//                                OWLUtilities.toString(annotationValue, buffer, owlModel.getNamespaces());
//                                addSearchResults(prop, (OWLEntity)entity, buffer.toString(), _expression, ontology, project, resultList);
//                            }
//                            break;
//
//                        case DATATYPES:
//                            OWLDatatype datatype = factory.getOWLDatatype(OWLUtilities.toURI(element.getEntityUri()));
//                            elem = new DatatypeTreeElement(datatype, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, DatatypeProvider.class));
//                            add(new Match(new DatatypeSearchMatch((DatatypeTreeElement) elem), 0, _expression.length()), resultList);
//                            break;
//                            
//                        case ONTOLOGY:
//                            break;
//                            
//                        default:
//                            break;
//                    }
//                } catch (NeOnCoreException e1) {
//                    SearchPlugin.logError(e1.getMessage(), e1);
//                }
//            }
//        }
//
//    }
//
//    private void addSearchResults(OWLAnnotationProperty annotProperty, OWLEntity entity, String annotationValue, String searchString, String ontology, String project, List<Match> resultList) throws NeOnCoreException {
//        AbstractOwlEntityTreeElement elem = null;
//        if (entity instanceof OWLClass) {
//            elem = new ClazzTreeElement(entity, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class));
//            add(new Match(new AnnotationValuesSearchMatch((ClazzTreeElement) elem, annotProperty, annotationValue, searchString), 0, _expression.length()), resultList);
//
//        } else if (entity instanceof OWLObjectProperty) {
//            elem = new ObjectPropertyTreeElement(entity, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ObjectPropertyHierarchyProvider.class));
//            add(new Match(new AnnotationValuesSearchMatch((ObjectPropertyTreeElement) elem, annotProperty, annotationValue, searchString), 0, _expression.length()), resultList);
//        
//        } else if (entity instanceof OWLDataProperty) {
//            elem = new DataPropertyTreeElement(entity, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, DataPropertyHierarchyProvider.class));
//            add(new Match(new AnnotationValuesSearchMatch((DataPropertyTreeElement) elem, annotProperty, annotationValue, searchString), 0, _expression.length()), resultList);
//        
//        } else if (entity instanceof OWLAnnotationProperty) {
//            elem = new AnnotationPropertyTreeElement(entity, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, AnnotationPropertyHierarchyProvider.class));
//            add(new Match(new AnnotationValuesSearchMatch((AnnotationPropertyTreeElement) elem, annotProperty, annotationValue, searchString), 0, _expression.length()), resultList);
//        
//        } else if (entity instanceof OWLIndividual) {
//            Set<OWLClass> clazzes = OWLModelFactory.getOWLModel(ontology, project).getClasses(entity.getURI().toString());
//
//            List<ClassSearchMatch> classMatchesList = new ArrayList<ClassSearchMatch>();
//            for (OWLClass c: clazzes) {
//                ClassSearchMatch classSearchMatch = new ClassSearchMatch(new ClazzTreeElement(c, ontology, project, TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class)));
//                classMatchesList.add(classSearchMatch);
//            }
//            for (OWLClass c: clazzes) {
//                elem = new IndividualViewItem((OWLIndividual) entity, c.getURI().toString(), ontology, project);
//                add(new Match(new AnnotationValuesSearchMatch((IndividualViewItem) elem, annotProperty, annotationValue, searchString), 0, _expression.length()), resultList);
//            }
//        }
//    }
//
//    private void add(Match match, List<Match> resultList) {
//        if (!resultList.contains(match)) {
//            resultList.add(match);
//        }
//    }
//
//    @Override
//    public ISearchResult getSearchResult() {
//        if (_searchResult == null) {
//            _searchResult = new SearchResult(this);
//        }
//        return _searchResult;
//    }
//
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
//
//    private Set<FieldTypes> getFieldTypes() {
//        Set<FieldTypes> types = new HashSet<FieldTypes>();
//
//        if ((_searchFlags & OWLSearchFlags.OWL_CLASS_SEARCH_FLAG) > 0) {
//            types.add(FieldTypes.CLASSES);
//        }
//        if ((_searchFlags & OWLSearchFlags.OWL_OBJECT_PROPERTY_SEARCH_FLAG) > 0) {
//            types.add(FieldTypes.OBJECT_PROPERTIES);
//        }
//        if ((_searchFlags & OWLSearchFlags.OWL_DATA_PROPERTY_SEARCH_FLAG) > 0) {
//            types.add(FieldTypes.DATA_PROPERTIES);
//        }
//        if ((_searchFlags & OWLSearchFlags.OWL_ANNOTATION_PROPERTY_SEARCH_FLAG) > 0) {
//            types.add(FieldTypes.ANNOTATION_PROPERTIES);
//        }
//        if ((_searchFlags & OWLSearchFlags.OWL_INDIVIDUAL_SEARCH_FLAG) > 0) {
//            types.add(FieldTypes.INDIVIDUALS);
//        }
//        if ((_searchFlags & OWLSearchFlags.OWL_ANNOTATION_VALUES_SEARCH_FLAG) > 0) {
//            types.add(FieldTypes.ANNOTATION_VALUES);
//        }
//        if ((_searchFlags & OWLSearchFlags.OWL_DATA_PROPERTY_VALUES_SEARCH_FLAG) > 0) {
//            types.add(FieldTypes.DATA_PROPERTY_VALUES);
//        }
//        if ((_searchFlags & OWLSearchFlags.OWL_DATATYPE_SEARCH_FLAG) > 0) {
//            types.add(FieldTypes.DATATYPES);
//        }
//        return types;
//    }
//
//    public Match[] getOWLResults(String project, Set<FieldTypes> types) throws InterruptedException,NeOnCoreException {
//
//        List<Match> searchResults = new ArrayList<Match>();
//        for (OWLModel owlModel: OWLModelFactory.getOntologies(project)) {
//            // TODO: migration
////            if (true) throw new UnsupportedOperationException("TODO: migration");
//            
//            //XXX Temporary simple solution until the owl lucene indexer is mirgrated 
//            SimpleOwlSearchHelper searchHelper = new SimpleOwlSearchHelper(owlModel);
////            OwlSearchHelper searchHelper = new OwlSearchHelper(owlModel.getConnection());
//            
//
//            String searchExpression = getExpression();
//            SearchResults results;
////            try {
//                results = searchHelper.search(owlModel.getOntologyURI(), types, searchExpression, true, _caseSensitive, 0, 9999);
////            } catch (KAON2Exception e) {
////                throw new NeOnCoreException(e);
////            }
//            List<SearchElement> resultElements = results.getResults();
//            for (SearchElement result: resultElements) {
//                addSearchMatches(project, owlModel.getOntologyURI(), result, searchResults);
//            }
//
//        }
//        if (searchResults.size() > 0) {
//            OWLGUIUtilities.removeDuplicate(searchResults);
//        }
//
//        return searchResults.toArray(new Match[0]);
//    }

    @Override
    protected AbstractSearchCommand getSearchCommand(String project) {
        return new OwlSearchCommand(project, _expression, _searchFlags, _caseSensitive);
    }
}
