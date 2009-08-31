/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

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
