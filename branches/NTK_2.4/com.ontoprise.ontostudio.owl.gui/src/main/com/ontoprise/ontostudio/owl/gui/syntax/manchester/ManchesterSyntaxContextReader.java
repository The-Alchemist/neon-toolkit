/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.syntax.manchester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.model.OWLModel;

public class ManchesterSyntaxContextReader {

    private OWLModel _owlModel;

    public static final int EXPECTDATATYPE = 1;
    public static final int EXPECTCLASS = 2;
    public static final int EXPECTINDIVIDUAL = 4;
    public static final int EXPECTPROPERTY = 8;
    public static final int EXPECTRESTRICTIONKEYWORD = 16;
    public static final int EXPECTCLASSCONSTRUCTORKEYWORD = 32;
    public static final int EXPECTFACETKEYWORD = 64;
    public static final int EXPECTFACET = 128;

    public int _result = 0;

    private String _lastToken;
    private String _lastFullToken;

    /**
     * Parses an input string and stores the keyword, and all participants of the recognized context.
     * 
     * Example input: "a or b and hasParent min 1 Person"
     * 
     * @param input
     */
    public ManchesterSyntaxContextReader(String input, OWLModel owlModel) {
        _owlModel = owlModel;

        if (input.equals("")) { //$NON-NLS-1$
            _lastToken = ""; //$NON-NLS-1$

        } else {
            char lastChar = input.charAt(input.length() - 1);

            if (Character.isWhitespace(lastChar)) {
                _lastToken = ""; //$NON-NLS-1$
                input = input.trim();

            } else {
                if (lastChar == '}' || lastChar == ')' || lastChar == ']' || lastChar == '>' || lastChar == '^' || lastChar == '"' || lastChar == '{' || lastChar == '(' || lastChar == '[' || lastChar == '<' || lastChar == '@') {
                    _lastToken = ""; //$NON-NLS-1$

                } else {
                    _lastToken = getLastPartialToken(input);
                    input = input.substring(0, input.length() - _lastToken.length()).trim();
                }
            }
        }

        _lastFullToken = getLastFullToken(input);
        if (_lastFullToken.equals("^^")) { //$NON-NLS-1$
            _result = EXPECTDATATYPE;
            return;

        } else if (ManchesterSyntaxConstants.isRestrictionKeyword(_lastFullToken)) {
            _result = EXPECTCLASS | EXPECTPROPERTY;
            return;

        } else if (ManchesterSyntaxConstants.isClassConstructorKeyword(_lastFullToken)) {
            _result = EXPECTCLASS | EXPECTPROPERTY;
            return;

        } else if (ManchesterSyntaxConstants.isPropertyChainKeyword(_lastFullToken)) {
            _result = EXPECTPROPERTY;
            return;

        } else if (ManchesterSyntaxConstants.isFacetKeyword(_lastFullToken)) {
            _result = EXPECTFACET;
            return;

        } else if (ManchesterSyntaxConstants.isFacet(_lastFullToken)) {
            return;

        } else if (isProperty(_lastFullToken)) {
            _result = EXPECTRESTRICTIONKEYWORD;
            return;

        } else if (isClass(_lastFullToken)) {
            _result = EXPECTCLASSCONSTRUCTORKEYWORD | EXPECTPROPERTY;
            return;
        }

        char bracket = getLastOpenBracket(input);
        if (bracket == 0) {

        } else if (bracket == '[') {
            _result = EXPECTFACETKEYWORD;
            return;

        } else if (bracket == '{') {
            _result = EXPECTINDIVIDUAL;
            return;

        } else if (bracket == '<') {
            return;
        }

        _result = EXPECTCLASS | EXPECTPROPERTY;
        return;

        // /////////////////////////////////////////////////////////////
        //		
        // int ignoredLength = 0;
        //
        // _input = input.trim();
        // _participantsStack = new LinkedList<String>();
        // int sepCharOffset = _input.lastIndexOf(_sepCharBegin);
        // // if there is a closing paranthesis after this offset, we have to ignore
        // // the section between these parantheses.
        // String s = sepCharOffset > 0 ? _input.substring(sepCharOffset) : _input;
        // // ignore all sections completely enclosed by parantheses
        // while(containsClosingParanthesis(s)) {
        // ignoredLength += s.length();
        // String newString = _input.substring(0, sepCharOffset) + s.substring(s.indexOf(_sepCharEnd)+1);
        // _input = newString;
        // sepCharOffset = _input.lastIndexOf(_sepCharBegin);
        // s = sepCharOffset > 0 ? _input.substring(sepCharOffset) : _input;
        // }
        //		
        // if (sepCharOffset < 0) {
        // // In this case there is no paranthesis, so this must be a restriction.
        // // For restrictions the participant at position 0 is always a propertyId
        // // and the participant at position 1 is the restriction keyword.
        // extractParticipants(0);
        // if (_participantsStack.size() > 1) {
        // _contextKeyword = _participantsStack.get(1);
        // }
        // return;
        // }
        // int keywordOffset = getLastSeparatorPosition(_input.substring(0, sepCharOffset-1));
        // if (keywordOffset != 0) {
        // keywordOffset += 1;
        // }
        // _contextKeyword = _input.substring(keywordOffset, sepCharOffset).trim();
        // extractParticipants(keywordOffset);
    }

    private String getLastPartialToken(String input) {
        String delims = "\t\n\r\f ({[]})\"<>=@^"; //$NON-NLS-1$
        int index = -1;
        for (int i = 0; i < delims.length(); i++) {
            char c = delims.charAt(i);
            index = Math.max(index, input.lastIndexOf(c));
        }
        return input.substring(index + 1, input.length());
    }

    private String getLastFullToken(String input) {
        input = input.trim();
        if (input.equals("")) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }
        String[] otherTokens = {"@", "^^", ","}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        List<String> fullTokens = new ArrayList<String>();
        fullTokens.addAll(Arrays.asList(otherTokens));
        fullTokens.addAll(Arrays.asList(ManchesterSyntaxConstants.getBrackets()));
        fullTokens.addAll(Arrays.asList(ManchesterSyntaxConstants.getClassConstructorKeywords()));
        fullTokens.addAll(Arrays.asList(ManchesterSyntaxConstants.getPropertyChainKeyword()));
        fullTokens.addAll(Arrays.asList(ManchesterSyntaxConstants.getRestrictionKeywords()));
        fullTokens.addAll(Arrays.asList(ManchesterSyntaxConstants.getFacetKeywords()));
        fullTokens.addAll(Arrays.asList(ManchesterSyntaxConstants.getFacets()));

        String token = "\u0000"; //$NON-NLS-1$

        int index = -1;
        for (String string: fullTokens) {
            int tempIndex = input.lastIndexOf(string);
            if (tempIndex > index) {
                if (tempIndex > 0) {
                    // prevent parsing "or" in hasAuthor
                    String delims = "\t\n\r\f ({[]})\"<>=@^"; //$NON-NLS-1$
                    if (delims.indexOf(input.charAt(tempIndex - 1)) == -1) {
                        continue; // ignore this
                    }
                }
                index = input.lastIndexOf(string);
                token = string;
            }
        }

        if (input.endsWith(token)) {
            return token;
        }

        // String delims = "\t\n\r\f ";
        // for (int i = 0; i < delims.length(); i++) {
        // char c = delims.charAt(i);
        // index = Math.max(index, input.lastIndexOf(c));
        // }

        return input.substring(index + token.length());
    }

    private char getLastOpenBracket(String input) {
        Stack<Character> stack = new Stack<Character>();

        for (int i = input.length() - 1; i > 0; i--) {
            char c = input.charAt(i);
            if (c == '}' || c == ')' || c == ']' || c == '>') {
                stack.push(c);
            } else if (c == '{') {
                if (stack.isEmpty()) {
                    return c;
                } else if (stack.peek().equals("}")) { //$NON-NLS-1$
                    stack.pop();
                } else {
                    return c;
                }
            } else if (c == '(') {
                if (stack.isEmpty()) {
                    return c;
                } else if (stack.peek().equals(")")) { //$NON-NLS-1$
                    stack.pop();
                } else {
                    return c;
                }
            } else if (c == '[') {
                if (stack.isEmpty()) {
                    return c;
                } else if (stack.peek().equals("]")) { //$NON-NLS-1$
                    stack.pop();
                } else {
                    return c;
                }
            } else if (c == '<') {
                if (stack.isEmpty()) {
                    return c;
                } else if (stack.peek().equals(">")) { //$NON-NLS-1$
                    stack.pop();
                } else {
                    return c;
                }
            }

        }

        return 0;
    }

    private boolean isProperty(String input) {
        if (input.trim().length() == 0) {
            return false;
        }
        try {
            OWLDataProperty inputDP = OWLPlugin.getDefault().getSyntaxManager().parseDataProperty(input, _owlModel);
            if (_owlModel.getAllDataProperties().contains(inputDP)) {
                return true;
            }

            OWLObjectPropertyExpression inputOP = OWLPlugin.getDefault().getSyntaxManager().parseObjectProperty(input, _owlModel);
            if (_owlModel.getAllObjectProperties().contains(inputOP)) {
                return true;
            }
        } catch (NeOnCoreException e) {
            // ignore
        }

        return false;
    }

    private boolean isClass(String input) {
        if (input.trim().length() == 0) {
            return false;
        }
        try {
            OWLClassExpression clazz = OWLPlugin.getDefault().getSyntaxManager().parseDescription(input, _owlModel);
            if (_owlModel.getAllClasses(true).contains(clazz)) {
                return true;
            }
        } catch (NeOnCoreException e) {
            // ignore
        }
        return false;
    }

    public int getResult() {
        return _result;
    }

    public String getLastToken() {
        return _lastToken;
    }

    public String getLastFullToken() {
        return _lastFullToken;
    }
}
