/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui;

/**
 * See: file: <code>com.ontoprise.ontostudio.help/plugin.xml</code> <br>
 * See: file: <code>com.ontoprise.ontostudio.help/help_contexts.xml</code> <br>
 * <p>
 */
public interface IHelpContextIds {
  /**
   * See: /com.ontoprise.ontostudio.help/plugin.xml:extension point"org.eclipse.help.contexts".plugin entry
   */
  public static final String PREFIX = "com.ontoprise.ontostudio.help."; //$NON-NLS-1$
  public static final String PREFIX_OWL = "com.ontoprise.ontostudio.owl.help."; //$NON-NLS-1$
  /**
   * See: /com.ontoprise.ontostudio.help/help_contexts.xml
   */
  public static final String INSTANCES_VIEW = PREFIX + "INSTANCES_VIEW"; //$NON-NLS-1$
  public static final String MTREE_VIEW = PREFIX + "MTREE_VIEW";  //$NON-NLS-1$
  public static final String FILE_SYSTEM_IMPORT = PREFIX + "FILE_SYSTEM_IMPORT";  //$NON-NLS-1$
  public static final String FLOGIC_ENTITY_PROPERTIES_VIEW = PREFIX + "FLOGIC_ENTITY_PROPERTIES_VIEW";  //$NON-NLS-1$
  public static final String VISUALIZER_VIEW = PREFIX + "VISUALIZER_VIEW";  //$NON-NLS-1$
  public static final String GRAPH_VIEW = PREFIX + "GRAPH_VIEW";  //$NON-NLS-1$
  public static final String FLOGIC_EDITOR_VIEW = PREFIX + "FLOGIC_EDITOR_VIEW";  //$NON-NLS-1$
  
  public static final String QUERY_ENTITY_PROPERTIES_VIEW = PREFIX + "QUERY_ENTITY_PROPERTIES_VIEW";  //$NON-NLS-1$
  public static final String EXPLANATION_EDITOR_VIEW = PREFIX + "EXPLANATION_EDITOR_VIEW";  //$NON-NLS-1$
  public static final String QUERY_EXPLANATION_VIEW = PREFIX + "QUERY_EXPLANATION_VIEW";  //$NON-NLS-1$
  public static final String QUERY_RESULT_VIEW = PREFIX + "QUERY_RESULT_VIEW";  //$NON-NLS-1$
  public static final String GRAPHICAL_RULE_EDITOR_VIEW = PREFIX + "GRAPHICAL_RULE_EDITOR_VIEW";  //$NON-NLS-1$
  public static final String STYLIZED_ENGLISH_VIEW = PREFIX + "STYLIZED_ENGLISH_VIEW";  //$NON-NLS-1$
  public static final String TEXTUAL_QUERIES_VIEW = PREFIX + "TEXTUAL_QUERIES_VIEW";  //$NON-NLS-1$
  public static final String REGRESSION_TEST_RUNNER_VIEW = PREFIX + "REGRESSION_TEST_RUNNER_VIEW";  //$NON-NLS-1$
    
  public static final String MAPPING_VIEW = PREFIX + "MAPPING_VIEW";  //$NON-NLS-1$
  public static final String XLS_ANNOTATION_VIEW = PREFIX + "XLS_ANNOTATION_VIEW";  //$NON-NLS-1$
  
  public static final String DEBUG_MONITOR_VIEW = PREFIX + "DEBUG_MONITOR_VIEW";  //$NON-NLS-1$
  public static final String REFERENCE_EXPLORER_VIEW = PREFIX + "REFERENCE_EXPLORER_VIEW";  //$NON-NLS-1$
  public static final String RULE_GRAPH_VIEW = PREFIX + "RULE_GRAPH_VIEW";  //$NON-NLS-1$
  public static final String RULE_LITERALS_VIEW = PREFIX + "RULE_LITERALS_VIEW";  //$NON-NLS-1$
  public static final String TUPLES_VIEW = PREFIX + "TUPLES_VIEW";  //$NON-NLS-1$
  
  public static final String RDF_ENTITY_PROPERTIES_VIEW = PREFIX + "RDF_ENTITY_PROPERTIES_VIEW";  //$NON-NLS-1$
  public static final String RDF_INSTANCES_VIEW = PREFIX + "RDF_INSTANCES_VIEW";  //$NON-NLS-1$
  
  public static final String INDIVIDUALS_VIEW = PREFIX_OWL + "INDIVIDUALS_VIEW";  //$NON-NLS-1$
  public static final String OWL_SEARCH = PREFIX_OWL + "OWL_SEARCH";  //$NON-NLS-1$
  public static final String OWL_ONTOLOGY_VIEW = PREFIX_OWL + "OWL_ONTOLOGY_VIEW";  //$NON-NLS-1$
  public static final String OWL_CLASSES_VIEW = PREFIX_OWL + "OWL_CLASSES_VIEW";  //$NON-NLS-1$
  public static final String OWL_OBJECT_PROPERTIES_VIEW = PREFIX_OWL + "OWL_OBJECT_PROPERTIES_VIEW";  //$NON-NLS-1$
  public static final String OWL_DATA_PROPERTIES_VIEW = PREFIX_OWL + "OWL_DATA_PROPERTIES_VIEW";  //$NON-NLS-1$
  public static final String OWL_INDIVIDUAL_VIEW = PREFIX_OWL + "OWL_INDIVIDUAL_VIEW";  //$NON-NLS-1$
  public static final String OWL_ANNOTATION_PROPERTIES_VIEW = PREFIX_OWL + "OWL_ANNOTATION_PROPERTIES_VIEW";  //$NON-NLS-1$
  
  public static final String OWL_CREATE_ONTOLOGY_PROJECT = PREFIX_OWL + "OWL_CREATE_ONTOLOGY_PROJECT"; //$NON-NLS-1$
  public static final String OWL_CREATE_ONTOLOGY = PREFIX_OWL + "OWL_CREATE_ONTOLOGY"; //$NON-NLS-1$
  public static final String OWL_IMPORT_ONTOLOGY = PREFIX_OWL + "OWL_IMPORT_ONTOLOGY"; //$NON-NLS-1$
  public static final String OWL_EXPORT_ONTOLOGY = PREFIX_OWL + "OWL_EXPORT_ONTOLOGY"; //$NON-NLS-1$
  public static final String OWL_DELETE_AN_ENTITY = PREFIX_OWL + "OWL_DELETE_AN_ENTITY"; //$NON-NLS-1$

}
