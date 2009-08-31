/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 02.07.2009
 * Created by: krekeler
 ******************************************************************************/
package org.neontoolkit.core.util;

import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.IOntologyProjectFilter;

/**
 *
 */
public class OntologyProjectFilter implements IOntologyProjectFilter {
    private final String _ontologyLanguage;
    private final String _factoryId;

    /**
     * Create a new instance.
     * 
     * <p>
     * An {@link IOntologyProject} will pass the filter iff
     * <ul>
     *    <li><code>ontologyLanguage</code> is <code>null</code> or matches the give ontology project's ontology language and</li>
     *    <li><code>factoryId</code> is <code>null</code> or matches the give ontology project's factory id.</li>
     * </ul>
     * </p>
     * 
     * @param ontologyLanguage                  The ontology language which must match to pass the filter.
     * @param factoryId                         The factory id which must match to pass the filter.
     */
    public OntologyProjectFilter(String ontologyLanguage, String factoryId) {
        _ontologyLanguage = ontologyLanguage;
        _factoryId = factoryId;
    }
    
    @Override
    public boolean matches(IOntologyProject ontologyProject) {
        return (_ontologyLanguage == null || _ontologyLanguage.equals(ontologyProject.getOntologyLanguage())) && (_factoryId == null || _factoryId.equals(ontologyProject.getProjectFactoryId()));
    }

}
