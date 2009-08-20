package com.ontoprise.ontostudio.owl.gui.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import sun.io.ByteToCharConverter;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;

/**
 * This is the registry of all datatypes. Applications with custom datatypes should register
 * appropriate datatype handlers with this class.
 */
public class BuiltInDatatypes {
    
    public static void registerBuiltInDatatypes() {
        regusterHandlers();
    }
    protected static void regusterHandlers() {
        DatatypeManager.INSTANCE.registerDatatypeHandler(new NullHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new BooleanHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new StringHandler(OWLNamespaces.XSD_NS+"string"));
        DatatypeManager.INSTANCE.registerDatatypeHandler(new StringHandler(OWLNamespaces.XSD_NS+"normalizedString"));
        DatatypeManager.INSTANCE.registerDatatypeHandler(new StringHandler(OWLNamespaces.RDFS_NS+"Literal"));
        DatatypeManager.INSTANCE.registerDatatypeHandler(new StringHandler(OWLNamespaces.RDF_NS+"XMLLiteral"));
        DatatypeManager.INSTANCE.registerDatatypeHandler(new StringHandler(OWLNamespaces.RDF_NS+"text"));
        DatatypeManager.INSTANCE.registerDatatypeHandler(new UnsignedByteHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new ByteHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new UnsignedShortHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new ShortHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new UnsignedIntHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new IntHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new LongHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new UnsignedLongHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new FloatHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new DoubleHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new IntegerHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new NonPositiveIntegerHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new PositiveIntegerHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new NonNegativeIntegerHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new NegativeIntegerHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new DecimalHandler());
        DatatypeManager.INSTANCE.registerDatatypeHandler(new URIHandler());
    }
    
//    private static class NumberFormatter extends NumberFormat{
//
//        @Override
//        public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        @Override
//        public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        @Override
//        public Number parse(String source, ParsePosition parsePosition) {
//            // TODO Auto-generated method stub
//            return null;
//        }
//        
//    }

    private static class NullHandler implements DatatypeHandler {


        public String getDatatypeURI() {
            return OWLNamespaces.KAON2_NS+"null";
        }

        public Object parseObject(String objectValue) throws IllegalArgumentException {
            if ("<null>".equalsIgnoreCase(objectValue))
                return null;
            else
                throw new IllegalArgumentException("Invalid syntax of the null value '"+objectValue+"'.");
        }
    }

    private static class BooleanHandler implements DatatypeHandler {


        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"boolean";
        }

        public Object parseObject(String objectValue) {
            return Boolean.parseBoolean(objectValue) ? Boolean.TRUE : Boolean.FALSE;
        }
    }

    private static class StringHandler implements DatatypeHandler {
        protected final String m_datatypeURI;
        
        public StringHandler(String datatypeURI) {
            m_datatypeURI=datatypeURI;
        }

        public String getDatatypeURI() {
            return m_datatypeURI;
        }

        public Object parseObject(String objectValue) {
            return objectValue;
        }
    }

    private static class ByteHandler implements DatatypeHandler {


        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"byte";
        }

        public Object parseObject(String objectValue) throws IllegalArgumentException {
            try {
                return Byte.valueOf(objectValue);
            }
            catch (NumberFormatException error) {
                throw new IllegalArgumentException("Invalid number format.",error);
            }
        }
    }

    /**[Definition:]   unsignedByte is ·derived· from unsignedShort by setting the value of 
     * ·maxInclusive· to be 255. The ·base type· of unsignedByte is unsignedShort. 
     * @author janiko
     *
     */
    private static class UnsignedByteHandler extends ShortHandler {
        private static short MAX_VALUE = 255;

        @Override
        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"unsignedByte";
        }
        
        @Override
        public Object parseObject(String arg0) throws IllegalArgumentException {
            return checkUnsignedRange((Short)super.parseObject(arg0), (short)0, MAX_VALUE);
        }
    }

    private static class ShortHandler implements DatatypeHandler {


        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"short";
        }

        public Object parseObject(String objectValue) throws IllegalArgumentException {
            try {
                return Short.valueOf(objectValue);
            }
            catch (NumberFormatException error) {
                throw new IllegalArgumentException("Invalid number format.",error);
            }
        }
    }

    /**[Definition:]   unsignedShort is ·derived· from unsignedInt by setting the value 
     * of ·maxInclusive· to be 65535. The ·base type· of unsignedShort is unsignedInt. 
     * @author janiko
     *
     */
    private static class UnsignedShortHandler extends IntegerHandler {
        private static int MAX_VALUE = 65535; 

        @Override
        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"unsignedShort";
        }
        
        @Override
        public Object parseObject(String arg0) throws IllegalArgumentException {
            return checkUnsignedRange((Integer)super.parseObject(arg0), 0, MAX_VALUE);
        }
    }

    private static class IntHandler implements DatatypeHandler {
       

        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"int";
        }

        public Object parseObject(String objectValue) throws IllegalArgumentException {
            try {
                return Integer.valueOf(objectValue);
            }
            catch (NumberFormatException error) {
                throw new IllegalArgumentException("Invalid number format.",error);
            }
        }
    }
    
    /**[Definition:]   unsignedInt is ·derived· from unsignedLong by setting the value 
     * of ·maxInclusive· to be 4294967295. The ·base type· of unsignedInt is unsignedLong. 
     * @author janiko
     *
     */
    private static class UnsignedIntHandler extends LongHandler {
        private static long MAX_VALUE = 4294967295L;
        
        @Override
        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"unsignedInt";
        }
        
        @Override
        public Object parseObject(String arg0) throws IllegalArgumentException {
            return checkUnsignedRange((Long)super.parseObject(arg0), 0L, MAX_VALUE);
        }

    }
    

    private static <A extends Comparable<A>> A checkUnsignedRange(A value, A minValue, A maxValue){
        if(value.compareTo(maxValue) > 0 || value.compareTo(minValue) < 0) throw new IllegalArgumentException("Invalid number format. [0..."+maxValue+"]");
        return value;
    }
    private static class LongHandler implements DatatypeHandler {

        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"long";
        }

        public Object parseObject(String objectValue) throws IllegalArgumentException {
            try {
                return Long.valueOf(objectValue);
            }
            catch (NumberFormatException error) {
                throw new IllegalArgumentException("Invalid number format.",error);
            }
        }
    }

    
    /**[Definition:]   unsignedLong is ·derived· from nonNegativeInteger by setting the value 
     * of ·maxInclusive· to be 18446744073709551615. The ·base type· of unsignedLong is nonNegativeInteger. 
     * @author janiko
     *
     */
    private static class UnsignedLongHandler extends FloatHandler{
        private static float MAX_VALUE = 18446744073709551615F;
        
        @Override
        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"unsignedLong";
        }
        
        @Override
        public Object parseObject(String arg0) throws IllegalArgumentException {
            return checkUnsignedRange((Float)super.parseObject(arg0), 0F, MAX_VALUE);
        }
    }
    
    private static class FloatHandler implements DatatypeHandler {

 
        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"float";
        }
 
        public Object parseObject(String objectValue) throws IllegalArgumentException {
            try {
                return Float.valueOf(objectValue);
            }
            catch (NumberFormatException error) {
                throw new IllegalArgumentException("Invalid number format.",error);
            }
        }
    }

    private static class DoubleHandler implements DatatypeHandler {


        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"double";
        }

        public Object parseObject(String objectValue) throws IllegalArgumentException {
            try {
                return Double.valueOf(objectValue);
            }
            catch (NumberFormatException error) {
                throw new IllegalArgumentException("Invalid number format.",error);
            }
        }
    }

    private static class IntegerHandler implements DatatypeHandler {
        protected static final BigInteger ZERO=BigInteger.valueOf(0);

        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"integer";
        }

        public Object parseObject(String objectValue) throws IllegalArgumentException {
            try {
                return Integer.parseInt(objectValue);
            }
            catch (NumberFormatException error) {
            }
            try {
                return new BigInteger(objectValue);
            }
            catch (NumberFormatException error) {
                throw new IllegalArgumentException("Invalid number format.",error);
            }
        }
    }

    private static class NonPositiveIntegerHandler extends IntegerHandler {

        @Override
        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"nonPositiveInteger";
        }
        
        
    }

    private static class PositiveIntegerHandler extends IntegerHandler {

        @Override
        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"positiveInteger";
        }
        
        @Override
        public Object parseObject(String arg0) throws IllegalArgumentException {
            return checkUnsignedRange((Integer)super.parseObject(arg0), new Integer(1), Integer.MAX_VALUE);
        }
    }
    
    private static class NonNegativeIntegerHandler extends IntegerHandler {

        @Override
        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"nonNegativeInteger";
        }
        
        @Override
        public Object parseObject(String arg0) throws IllegalArgumentException {
            return checkUnsignedRange((Integer)super.parseObject(arg0), 0, Integer.MAX_VALUE);
        }
    }
    
    private static class NegativeIntegerHandler extends IntegerHandler {

        @Override
        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"negativeInteger";
        }
        
        @Override
        public Object parseObject(String arg0) throws IllegalArgumentException {
            return checkUnsignedRange((Integer)super.parseObject(arg0), Integer.MIN_VALUE, new Integer(-1));
        }
    }
    
    private static class DecimalHandler implements DatatypeHandler {

        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"decimal";
        }

        public Object parseObject(String objectValue) throws IllegalArgumentException {
            try {
                return Double.parseDouble(objectValue);
            }
            catch (NumberFormatException error) {
            }
            try {
                return new BigDecimal(objectValue);
            }
            catch (NumberFormatException error) {
                throw new IllegalArgumentException("Invalid number format.",error);
            }
        }
    }

    
    private static class URIHandler implements DatatypeHandler {

        public String getDatatypeURI() {
            return OWLNamespaces.XSD_NS+"anyURI";
        }

        public Object parseObject(String objectValue) throws IllegalArgumentException {
            try {
                return new URI(objectValue);
            }
            catch (URISyntaxException error) {
                throw new IllegalArgumentException("Invalid URI format.",error);
            }
        }
    }
}
