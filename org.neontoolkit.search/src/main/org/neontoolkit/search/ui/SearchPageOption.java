/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 * All rights reserved. 
 *****************************************************************************/
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
