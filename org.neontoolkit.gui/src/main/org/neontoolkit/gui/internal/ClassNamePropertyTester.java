package org.neontoolkit.gui.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.expressions.PropertyTester;

public class ClassNamePropertyTester extends PropertyTester {
    public static final String CLASS_NAME_PROPERTY = "getClassName"; //$NON-NLS-1$
    public static final String BOOLEAN_METHOD_PROPERTY = "booleanMethod"; //$NON-NLS-1$;
    public static final String METHOD_PROPERTY = "method"; //$NON-NLS-1$;
    
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        try {
            Object[] methodArgs = (args != null && args.length > 1) ? new Object[args.length-1] : null;
            if (methodArgs != null) {
                System.arraycopy(args, 1, methodArgs, 0, methodArgs.length);
            }
            if (property.equals(CLASS_NAME_PROPERTY)) {
                return receiver.getClass().getName().equals(expectedValue);
            }
            else if (args != null) {
                if (property.equals(BOOLEAN_METHOD_PROPERTY) && args.length == 1) {
                    return Boolean.TRUE.equals(
                            getMethodValue(receiver, args[0].toString(), methodArgs));
                }
                else if (property.equals(METHOD_PROPERTY) && args.length >= 1) {
                    return expectedValue.equals(
                            getMethodValue(receiver, args[0].toString(), methodArgs));
                }
            }
        } catch (Exception e) {}
        return false;
    }
    
    private Object getMethodValue(Object object, String methodName, Object[] arguments) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Class<?>[] argTypes = arguments != null ? new Class[arguments.length] : null;
        if (arguments != null) {
            for (int i=0; i<arguments.length; i++) {
                argTypes[i] = arguments[i].getClass();
            }
        }
        Method method = object.getClass().getMethod(methodName, argTypes);
        return method.invoke(object, arguments);
    }

}
