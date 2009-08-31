package com.ontoprise.ontostudio.owl.model.util;

/**
 * This class localizes the unsafe casts to one place, so that unsafe cast
 * warnings are located to one place only.
 */
public class Cast {
    @SuppressWarnings("unchecked")
    public static final <T> T cast(Object o) {
        return (T)o;
    }
}
