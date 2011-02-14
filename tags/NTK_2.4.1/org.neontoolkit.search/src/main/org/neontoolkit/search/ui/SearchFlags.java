/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.search.ui;

/* 
 * Created on: 22.03.2006
 * Created by: Dirk Wenke
 *
 * Function:
 * Keywords:
 *
 */
/**
 * @author Dirk Wenke
 */

public class SearchFlags {
	public static final int CONCEPT_SEARCH_FLAG = 1 << 1;
	public static final int ATTRIBUTE_SEARCH_FLAG = 1 << 2;
	public static final int RELATION_SEARCH_FLAG = 1 << 3;
	public static final int INSTANCE_SEARCH_FLAG = 1 << 4;
	
	public static final int CLASS_SEARCH_FLAG = 1 << 5;
	public static final int PROPERTY_SEARCH_FLAG = 1 << 6;
	public static final int INDIVIDUAL_SEARCH_FLAG = 1 << 7;
}
