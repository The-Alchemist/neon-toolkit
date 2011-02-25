/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.nio.CharBuffer;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;


/**
 * @author krekeler
 * @author Nico Stieler
 *
 */
public class InternalParserLite {
    private OWLNamespaces _namespaces;
    private StreamTokenizer _tokenizer;
    private OWLDataFactory _f;

    public InternalParserLite(String object, OWLNamespaces namespaces, OWLDataFactory factory) throws InternalParserException  {
        _namespaces = namespaces;
        _tokenizer = createStreamTokenizer(new StringReader(object));
        nextToken();
        _f = factory;
    }
    public <E extends OWLLiteral> E parseOWLConstant() throws InternalParserException  {
        return Cast.cast(parseConstant());
    }
    private InternalParserException createException(String message) {
        return new InternalParserException(message);
    }
    private InternalParserException createException(String message,Throwable cause) {
        return new InternalParserException(message,cause);
    } 
    private OWLLiteral parseConstant() throws InternalParserException {
        try {
            if (isNumberToken()) {
                double result=getNumber();
                nextToken();
                return _f.getOWLLiteral(result);
            }
            else if (_tokenizer.ttype==StreamTokenizer.TT_WORD) {
                if ("<null>".equalsIgnoreCase(_tokenizer.sval)) { //$NON-NLS-1$
                    nextToken();
                    return null;
                }
                throw createException("Invalid token '"+_tokenizer.sval+"'."); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else if (_tokenizer.ttype=='"') {
                PositionReader reader=((StreamTokenizerWithSpecialMinusCharacterHandling)_tokenizer).getReader();
                reader.mark(10000000);
                nextToken();
                while (_tokenizer.ttype!='"') {
                    switch(_tokenizer.ttype) {
                    case StreamTokenizer.TT_EOF: {
                        throw createException("End of input stream while parsing literal value."); //$NON-NLS-1$
                    }
                    case StreamTokenizer.TT_EOL: {
                        // do nothing
                        break;
                    }
                    case StreamTokenizer.TT_NUMBER: {
                        // do nothing
                        break;
                    }
                    case StreamTokenizer.TT_WORD: {
                        // do nothing
                        break;
                    }
                    default: {
                        if (_tokenizer.ttype=='\\') {
                            // escaped quotes must not end the while loop
                            nextToken();
                        }
                    }
                    }
                    nextToken();
                }
                long end=reader.getPosition();
                // go back to the marked position
                reader.reset();
                StringBuffer buffer=new StringBuffer();
                for (long i=reader.getMark()+1;i<end;) {
                    char c=(char)reader.read();
                    i++;
                    if (c=='\\') {
                        char sequence=(char)reader.read();
                        i++;
                        if (sequence=='\\')
                            buffer.append('\\');
                        else if (sequence=='"')
                            buffer.append('"');
                        else if (sequence=='n')
                            buffer.append('\n');
                        else if (sequence=='r')
                            buffer.append('\r');
                        else if (sequence=='t')
                            buffer.append('\t');
                        else
                            throw createException("Unrecognized escape sequence at position "+reader.getPosition()+" while parsing literal value."); //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        buffer.append(c);
                    }
                }
                reader.read(); // skip the end quote
                String stringValue=buffer.toString();
                nextToken();
                if (_tokenizer.ttype==StreamTokenizer.TT_WORD && _tokenizer.sval.startsWith("^^")) { //$NON-NLS-1$
                    String datatypeURI=_tokenizer.sval.substring(2);
                    if (datatypeURI.startsWith("<")) { //$NON-NLS-1$
                        if (!datatypeURI.endsWith(">")) //$NON-NLS-1$
                            throw createException("Invalid datatype URI '"+datatypeURI+"'."); //$NON-NLS-1$ //$NON-NLS-2$
                        datatypeURI=datatypeURI.substring(1,datatypeURI.length()-1);
                    }
                    nextToken();
                    datatypeURI=_namespaces.expandString(datatypeURI);
                    return _f.getOWLLiteral(stringValue, _f.getOWLDatatype(OWLUtilities.toIRI(datatypeURI)));
                }
                else if (_tokenizer.ttype==StreamTokenizer.TT_WORD && _tokenizer.sval.startsWith("@")) { //$NON-NLS-1$
                    String language=_tokenizer.sval.substring(1);
                    nextToken();
                    return _f.getOWLLiteral((stringValue.equals("null"))?null:stringValue, (language.equals("null"))?null:language); //$NON-NLS-1$ //$NON-NLS-2$
                }
                else
                    return _f.getOWLLiteral((stringValue.equals("null"))?null:stringValue, (String) null); //$NON-NLS-1$
            }
            else
                throw createException("Invalid token '"+Character.valueOf((char)_tokenizer.ttype)+"'."); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (IOException e) {
            throw createException("Error reading input stream.",e); //$NON-NLS-1$
        }
    }
    private boolean isNumberToken() {
        // TODO 2008-07-08 (tkr): this is a pretty inefficient implementation, do it better...
        if (_tokenizer.ttype != StreamTokenizer.TT_WORD || _tokenizer.sval == null) {
            return false;
        }
        try {
            Double.parseDouble(_tokenizer.sval);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    /**
     * Get the current word token as number.<br/>
     * <br/>
     * <em>Precondition</em>: <code>isNumber()</code> must hold.
     * @param _tokenizer
     * @return <code>tokenizer.sval</code> as number.
     */
    private double getNumber() {
        assert(isNumberToken());
        return Double.parseDouble(_tokenizer.sval);
    }
    private void nextToken() throws InternalParserException {
        try {
            _tokenizer.nextToken();
        }
        catch (IOException error) {
            throw createException("Error reading input stream.",error); //$NON-NLS-1$
        }
    }
    /**
     * Wraps a <code>Reader</code> and provides a position info.
     * 
     * @author krekeler
     */
    private static class PositionReader extends Reader {
        protected Reader m_source;
        protected long m_position;
        protected long m_mark;
        public PositionReader(Reader source) {
            m_source=source;
            m_position=0;
            m_mark=-1;
        }
        public long getPosition() {
            return m_position;
        }
        public long getMark() {
            return m_mark;
        }
        @Override
        public void close() throws IOException {
            m_source.close();
        }
        @Override
        public void mark(int readAheadLimit) throws IOException {
            m_source.mark(readAheadLimit);
            m_mark=m_position;
        }
        @Override
        public boolean markSupported() {
            return m_source.markSupported();
        }
        @Override
        public int read() throws IOException {
            int c=m_source.read();
            m_position++;
            return c;
        }
        @Override
        public int read(char[] cbuf) throws IOException {
            int count=m_source.read(cbuf);
            if (count!=-1)
                m_position+=count;
            return count;
        }
        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int count=m_source.read(cbuf, off, len);
            if (count!=-1)
                m_position+=count;
            return count;
        }
        @Override
        public int read(CharBuffer target) throws IOException {
            int count=m_source.read(target);
            if (count!=-1)
                m_position+=count;
            return count;
        }
        @Override
        public boolean ready() throws IOException {
            return m_source.ready();
        }
        @Override
        public void reset() throws IOException {
            m_source.reset();
            if (m_mark!=-1) {
                m_position=m_mark;
            }
        }
        @Override
        public long skip(long n) throws IOException {
            long count=m_source.skip(n);
            if (count!=-1)
                m_position+=count;
            return count;
        }
    }
    /**
     * This is a helper class which handles an issue with the original <code>StreamTokenizer</code> implementation,
     * which behaves strange on the character '-':
     * if parsing "-" and <code>parseNumber()</code> is on, <code>ttype</code> equals '-', but if <code>parseNumber()</code> is off,
     * <code>ttype</code> equals -3 and <code>sval</code> contains "-".<br/>
     * <br/>
     * 
     * @author krekeler
     */
    private static class StreamTokenizerWithSpecialMinusCharacterHandling extends StreamTokenizer {
        protected PositionReader m_reader;
        private static StreamTokenizerWithSpecialMinusCharacterHandling createInstance(Reader r) {
            if (!r.markSupported())
                r=new BufferedReader(r);
            return new StreamTokenizerWithSpecialMinusCharacterHandling(new PositionReader(r));
        }
        protected StreamTokenizerWithSpecialMinusCharacterHandling(PositionReader r) {
            super(r);
            m_reader=r;
        }
        @Override
        public int nextToken() throws IOException {
            super.nextToken();
            if (ttype==TT_WORD && "-".equals(sval)) { //$NON-NLS-1$
                ttype='-';
            }
            return ttype;
        }
        public PositionReader getReader() {
            return m_reader;
        }
    }
    private static StreamTokenizer createStreamTokenizer(Reader reader) {
        // 2008-07-08 (tkr): Because parsing numbers is now done "manually" we need a special handling for the '-' character
        StreamTokenizer tokenizer=StreamTokenizerWithSpecialMinusCharacterHandling.createInstance(reader);
        tokenizer.resetSyntax();
        // 2008-08-09 (tkr): Parsing of literal values is done "manually"
        //tokenizer.quoteChar('"');
        tokenizer.whitespaceChars(' ',' ');
        tokenizer.whitespaceChars('\t','\t');
        tokenizer.eolIsSignificant(false);
        // 2008-07-08 (tkr): Parsing numbers is now done "manually"
        //tokenizer.parseNumbers();

        // 2008-11-13 (tkr): Allow any non special characters
        boolean allowAll = true;
        if (allowAll) {
            // Note that 
            //    - minus '-' (negation of literals) and 
            //    - ':', '<', >', '_', and '=' (fmolecule)
            //    - '@' (StringWithLanguage)
            // are special chars which are also word chars, but already were word chars in the original implementation.
            // Note that \r and \n are non special chars which are no word chars and have not been before.
            // Note that '*' is a special char (fmolecule) and is now included as word char, but was not a word char before.
            
            // zero
            tokenizer.wordChars(1, 8);
            // tab, cariage return
            tokenizer.wordChars(11, 12);
            // line feed
            tokenizer.wordChars(14, 31);
            // space
            tokenizer.wordChars(33, 33);
            // quote
            tokenizer.wordChars(35, 90);
            // '[', '\', ']'
            tokenizer.wordChars(94, 122);
            // '{', '|', '}'
            tokenizer.wordChars(126, Integer.MAX_VALUE);
            return tokenizer;
        }
        
        // 2008-11-13 (tkr): old word chars
        tokenizer.wordChars('A','Z');
        tokenizer.wordChars('a','z');
        tokenizer.wordChars('0','9');
        tokenizer.wordChars('_','_');
        tokenizer.wordChars('-','-');
        tokenizer.wordChars('!','!');
        tokenizer.wordChars('.','.');
        tokenizer.wordChars('~','~');
        tokenizer.wordChars('\'','\'');
        tokenizer.wordChars('(','(');
        tokenizer.wordChars(')',')');
        tokenizer.wordChars('*','*');
        tokenizer.wordChars(',',',');
        tokenizer.wordChars(';',';');
        tokenizer.wordChars(':',':');
        tokenizer.wordChars('$','$');
        tokenizer.wordChars('&','&');
        tokenizer.wordChars('+','+');
        tokenizer.wordChars('=','=');
        tokenizer.wordChars('?','?');
        tokenizer.wordChars('/','/');
        tokenizer.wordChars('@','@');
        tokenizer.wordChars('%','%');
        tokenizer.wordChars('<','<');
        tokenizer.wordChars('>','>');
        tokenizer.wordChars('#','#');
        tokenizer.wordChars('^','^');
        return tokenizer;
    }
}
