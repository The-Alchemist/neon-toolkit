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
 * Created on 04.04.2008
 * @author Dirk Wenke
 *
 * Function:
 * Keywords:
 */
/**
 * Type comment
 */
public class SearchPageOption {

	private String _optionName;
	private int _optionBit;
	
	public SearchPageOption(String optionName, int styleBit) {
		_optionName = optionName;
		_optionBit = styleBit;
	}
	
	public String getName() {
		return _optionName;
	}
	
	public int getOptionBit() {
		return _optionBit;
	}
}
