/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.search.owl.ui;

/* 
 * Created on: 22.03.2006
 * Created by: Werner Hihn
 *
 * Function:
 * Keywords:
 *
 */
/**
 * @author Werner Hihn
 */

public class OWLSearchFlags {

    public static final int OWL_CLASS_SEARCH_FLAG = 1 << 7;
    public static final int OWL_OBJECT_PROPERTY_SEARCH_FLAG = 1 << 8;
    public static final int OWL_DATA_PROPERTY_SEARCH_FLAG = 1 << 9;
    public static final int OWL_ANNOTATION_PROPERTY_SEARCH_FLAG = 1 << 10;
    public static final int OWL_DATATYPE_SEARCH_FLAG = 1 << 11;
    public static final int OWL_INDIVIDUAL_SEARCH_FLAG = 1 << 12;
    public static final int OWL_ANNOTATION_VALUES_SEARCH_FLAG = 1 << 13;
    public static final int OWL_DATA_PROPERTY_VALUES_SEARCH_FLAG = 1 << 14;

}
