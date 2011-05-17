/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/
package org.neontoolkit.gui.navigator.elements;

import org.neontoolkit.gui.navigator.ITreeElement;
import org.semanticweb.owlapi.model.OWLIndividual;

/**Interface for all OWL individuals. 
 * @author janiko
 * @author Nico Stieler
 * Created on: 05.10.2009
 */
public interface IIndividualTreeElement<T extends OWLIndividual> extends IQualifiedIDElement, ITreeElement, IProjectElement, IOntologyElement{
   
    public void setIndividualId(String s);
    
    public boolean isDirect();

    public String[] getClazzUris();
    
    public String getCurrentClazz();
    
    public T getIndividual();

}
