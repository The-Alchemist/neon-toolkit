/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.ontoprise.ontostudio.owl.model.util.Filter;

public class OWLCollectionsExtension {

    /**
     * Get a subset of a set by applying a filter to it.
     * 
     * @param <SourceType> Type of the items in the source set.
     * @param <ResultType> Type of the itemsin the result set.
     * @param source The source items.
     * @param filter The filter to apply to source.
     * @return A <code>Set<ResultType></code> which contains all items from <code>source</code> which passes <code>filter</code>.
     */
    public static <SourceType, ResultType extends SourceType> Set<ResultType> subSet(Set<SourceType> source, Filter<SourceType> filter) {
        return (LinkedHashSet<ResultType>) OWLCollectionsExtension.addAll(new LinkedHashSet<ResultType>(), source, filter);
    }

    /**
     * Get a subset of a set by applying a filter to it.
     * 
     * @param <SourceType> Type of the items in the source set.
     * @param <ResultType> Type of the itemsin the result set.
     * @param resultingSetType The result of this method will be of type <code>resultingSetType</code>. Note that <code>resultingSetType</code> must provide a
     *            default empty constructor. Any exception while construction an instance of <code>resultSetType</code> by calling
     *            <code>resultingSetType.getConstructor().newInstance()</code> will be wrapped in an <code>IllegalArgumentException</code>.
     * @param source The source items.
     * @param filter The filter to apply to source.
     * @return An instance of type <code>resultingSetType</code> which contains all items from <code>source</code> which passes <code>filter</code>.
     * @throws <code>IllegalArgumentException</code> If <code>resultingSetType.getConstructor().newInstance()</code> fails, wrapping the thrown exception.
     */
    public static <SourceType, ResultType extends SourceType> Set<ResultType> subSet(Class<? extends Set<ResultType>> resultingSetType, Set<SourceType> source, Filter<SourceType> filter) {
        Set<ResultType> target = null;
        try {
            target = resultingSetType.getConstructor().newInstance();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(e);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
        return (Set<ResultType>) OWLCollectionsExtension.addAll(target, source, filter);
    }

    /**
     * Add all items from a source collection which pass a filter to a target collection.
     * 
     * @param <SourceType> Type of the source collection.
     * @param <TargetType> Type of the target collection.
     * @param target The collection to which items get added.
     * @param source The collection from which items are considered.
     * @param filter The filter which tells which items should be added to the target.
     * @return <code>target</code>
     */
    @SuppressWarnings("unchecked")
    public static <SourceType, TargetType extends SourceType> Collection<TargetType> addAll(Collection<TargetType> target, Collection<SourceType> source, Filter<SourceType> filter) {
        if (source != null) {
            for (SourceType item: source) {
                if (filter == null || filter.matches(item)) {
                    target.add((TargetType) item);
                }
            }
        }
        return target;
    }

}
