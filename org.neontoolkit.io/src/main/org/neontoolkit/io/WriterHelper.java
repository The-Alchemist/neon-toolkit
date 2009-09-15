/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.io;

import java.io.IOException;
import java.io.Writer;

/* 
 * Created on 11.10.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: UTF8 Writer interface 
 */


public interface WriterHelper {
    Writer createWriter() throws IOException;
    Writer createWriterUTF8() throws IOException;
    
    void performFinish() throws Exception;
}
