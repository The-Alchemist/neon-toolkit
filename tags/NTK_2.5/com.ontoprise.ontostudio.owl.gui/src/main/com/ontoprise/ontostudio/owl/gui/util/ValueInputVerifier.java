/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util;

import com.ontoprise.ontostudio.owl.gui.Messages;

/**
 * @author janiko
 * @author Nico Stieler
 *
 */
public class ValueInputVerifier extends InputVerifier{

    
    @Override
    public String verify(String... input) {
        if(input.length < 2 ||input[1] == null || input[1].length() <= 0) throw new IllegalArgumentException(Messages.InputVerifier_3);
        try{
            return DatatypeManager.INSTANCE.parseObject(input[0], input[1] ).toString();
        }catch (UnknownDatatypeException e){
//            throw e;
            return null;
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(Messages.InputVerifier_1 + input[1] + Messages.InputVerifier_2);
        }
    }

}
