/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 15.07.2009
 * Created by: krekeler
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.util.file;

import java.io.IOException;
import java.io.Reader;

/**
 * @author krekeler
 *
 */
public class ResetReader extends Reader {
    private final Reader _reader;
    private int _position;
    private int _bufferEnd;
    private int _mark;
    private char[] _buffer;

    public ResetReader(Reader reader) {
        _reader = reader;
        _position = 0;
        _bufferEnd = 0;
        _mark = 0;
        _buffer = new char[64 * 1024];
    }
    
    @Override
    public void close() throws IOException {
        _reader.close();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        // read as many chars from the buffer as possible
        int bufferEnd = _bufferEnd;
        int position = _position;
        int bufferedChars = bufferEnd - position;
        int charsFromBuffer = Math.min(len, bufferedChars);
        for (int i = 0; i < charsFromBuffer; i++, position++) {
            cbuf[off + i] = _buffer[position];
        }
        // reduce remaining count of characters to read
        len -= charsFromBuffer;
        off += charsFromBuffer;
        // enlarge the buffer if it may exceed
        if (bufferEnd + len > _buffer.length) {
            char[] newBuffer = new char[_buffer.length * 2];
            System.arraycopy(_buffer, 0, newBuffer, 0, _buffer.length);
            _buffer = newBuffer;
        }
        // read at most as many chars as the buffer can hold 
        len = Math.min(len, _buffer.length - bufferEnd);
        // fill the buffer
        int newCharCount = _reader.read(_buffer, position, len);
        if (newCharCount != -1) {
            // copy to target
            for (int i = 0; i < newCharCount; i++, bufferEnd++, position++) {
                cbuf[off + i] = _buffer[bufferEnd];
            }
        }
        _bufferEnd = bufferEnd;
        _position = position;
        return charsFromBuffer + newCharCount;
    }
    
    public void resetToStart() {
        _position = 0;
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        _mark = _position;
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public void reset() throws IOException {
        _position = _mark;
    }
}
