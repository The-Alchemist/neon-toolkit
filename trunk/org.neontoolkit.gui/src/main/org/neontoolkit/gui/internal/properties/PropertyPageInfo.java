/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.internal.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.neontoolkit.gui.properties.IEntityPropertyPage;
import org.neontoolkit.gui.properties.IMainPropertyPage;
import org.neontoolkit.gui.properties.IPropertyPage;
import org.neontoolkit.gui.properties.LegacyPropertyPageWrapper;
import org.neontoolkit.gui.properties.MainPropertyPageWrapper;


/* 
 * Created on: 04.10.2008
 * Created by: Dirk Wenke
 *
 * Keywords: UI, EntityPropertyPage, Properties
 */
/**
 * This is a helper class for the extensions of the entityProperties extension point.
 */
public class PropertyPageInfo {
    
    private IConfigurationElement _element;
    private String _activator;
    private String _id;
    private double _priority = 0;
    private String _name;
    public boolean _isMain = true;
    private IPropertyPage _propertyPage;
    private List<PropertyPageInfo> _subPages;
    
    public PropertyPageInfo(IConfigurationElement elem) {
        this(elem, 0);
    }
    
    public PropertyPageInfo(IConfigurationElement elem, int priority) {
    	_subPages = new ArrayList<PropertyPageInfo>();
        _id = elem.getAttribute("id"); //$NON-NLS-1$
        _name = elem.getAttribute("name"); //$NON-NLS-1$
        _element = elem;
        _activator = elem.getAttribute("activatorClass"); //$NON-NLS-1$
        _priority = priority;
    }
    
    /**
     * Returns true whether the passed object is of the type of the defined
     * activator class, false otherwise.
     * @param o
     * @return
     */
    public boolean matches(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass().getName().equals(_activator)) {
            return true;
        }
        return false;
    }
    
    public String getId() {
        return _id;
    }
    
    public String getName() {
        return _name;
    }
    
    public double getPriority() {
        return _priority;
    }
    
    public void addSubContributor(PropertyPageInfo subContributorInfo) {
    	subContributorInfo._isMain = false;
    	if (!_subPages.contains(subContributorInfo)) {
    		for (int i=0; i<_subPages.size(); i++) {
    			if (subContributorInfo.getPriority() > _subPages.get(i).getPriority()) {
    				_subPages.add(i, subContributorInfo);
    				return;
    			}
    		}
    		_subPages.add(subContributorInfo);
    	}
    }
    
    public PropertyPageInfo[] getSubContributors() {
        return _subPages.toArray(new PropertyPageInfo[0]);
    }
    
    public IPropertyPage getPropertyPage() throws CoreException {
        if (_propertyPage == null) {
        	Object page = _element.createExecutableExtension("class"); //$NON-NLS-1$
        	if (page instanceof IPropertyPage) {
        		_propertyPage = (IPropertyPage)page;
        	}
        	else if (page instanceof IEntityPropertyPage) {
        		_propertyPage = new LegacyPropertyPageWrapper((IEntityPropertyPage)page);
        	}
        	else {
        		return null;
        	}
        	
        	if (_isMain) {
        		if (!(_propertyPage instanceof IMainPropertyPage)) {
        			MainPropertyPageWrapper mainPage = new MainPropertyPageWrapper(_propertyPage); 
        			_propertyPage.setMainPage(mainPage);
        			_propertyPage = mainPage;
        		}
        		for (PropertyPageInfo subPage: _subPages) {
        			((IMainPropertyPage)_propertyPage).addSubPage(subPage.getPropertyPage());
        		}
        	}
        }
        return _propertyPage;
    }
}
