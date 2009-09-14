/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;

/* 
 * Created on: 23.07.2004
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Images
 */
/**
 * The keys for the shared images of this plugin
 */
public final class OWLSharedImages {

    public static final String DELETE_INDIVIDUAL = "delete_individual"; //$NON-NLS-1$
    public static final String ONTOLOGY = "ontology"; //$NON-NLS-1$
    public static final String CLAZZ = "clazz"; //$NON-NLS-1$
    public static final String OBJECT_PROPERTY = "object_property"; //$NON-NLS-1$
    public static final String DATA_PROPERTY = "data_property"; //$NON-NLS-1$
    public static final String ANNOTATION_PROPERTY = "annotation_property"; //$NON-NLS-1$
    public static final String DATATYPE = "datatype"; //$NON-NLS-1$
    public static final String INDIVIDUAL = "individual"; //$NON-NLS-1$
    public static final String PROJECT = "ontoprj_obj"; //$NON-NLS-1$
    public static final String PROJECT_ERROR = "ontoprj_obj_error"; //$NON-NLS-1$
    public static final String FOLDER = "folder"; //$NON-NLS-1$
    public static final String EMPTY = "empty"; //$NON-NLS-1$

    /*
     * Icons for Complex Class Editor
     */
    public static final String INTERSECTION_OF = "intersection_of"; //$NON-NLS-1$ 
    public static final String UNION_OF = "union_of"; //$NON-NLS-1$ 
    public static final String COMPLEMENT_OF = "complement_of"; //$NON-NLS-1$
    public static final String ONE_OF = "one_of"; //$NON-NLS-1$
    public static final String MIN = "min"; //$NON-NLS-1$
    public static final String MAX = "max"; //$NON-NLS-1$
    public static final String CARDINALITY = "cardinality"; //$NON-NLS-1$
    public static final String SOME_VALUES_FROM = "some_values_from"; //$NON-NLS-1$
    public static final String ALL_VALUES_FROM = "all_values_from"; //$NON-NLS-1$
    public static final String HAS_VALUE = "has_value"; //$NON-NLS-1$

    static void register(ImageRegistry registry) {
        registry.put(INDIVIDUAL, create("onto", "individual.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(DELETE_INDIVIDUAL, create("onto", "delete_individual.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(ONTOLOGY, create("onto", "ontology.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(CLAZZ, create("onto", "clazz.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(OBJECT_PROPERTY, create("onto", "object_property.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(ANNOTATION_PROPERTY, create("onto", "annotation_property.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(DATA_PROPERTY, create("onto", "data_property.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(PROJECT, create("onto", "ontoprj_obj.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(PROJECT_ERROR, create("onto", "ontoprj_obj_error.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(FOLDER, create("onto", "folder.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(DATATYPE, create("onto", "datatype.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(EMPTY, create("common", "empty.gif")); //$NON-NLS-1$ //$NON-NLS-2$

        registry.put(INTERSECTION_OF, create("complexclass", "intersection_of.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(UNION_OF, create("complexclass", "union_of.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(COMPLEMENT_OF, create("complexclass", "complement_of.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(ONE_OF, create("complexclass", "one_of.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(MIN, create("complexclass", "min.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(MAX, create("complexclass", "max.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(CARDINALITY, create("complexclass", "cardinality.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(SOME_VALUES_FROM, create("complexclass", "some_values_from.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(ALL_VALUES_FROM, create("complexclass", "all_values_from.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        registry.put(HAS_VALUE, create("complexclass", "has_value.gif")); //$NON-NLS-1$ //$NON-NLS-2$

    }

    private static ImageDescriptor create(String path, String name) {
        Bundle bundle = OWLPlugin.getDefault().getBundle();
        return ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/" + path + "/" + name), null)); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
