/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/* 
 * Created on: 20.07.2004
 * Created by: Dirk Wenke
 *
 * Keywords: Datamodel, Util, QuickSort
 */
/**
 * This utility class is used for sorting of elements.
 */

public class ItemSorter {

    @SuppressWarnings("unchecked")
	public static void quickSort(Object[] items) {
        if (items != null) {
            if (items.length > 1) {
                Object splitItem = items[java.lang.Math.round(items.length / 2)];
                Object split = splitItem;
                if (!(split instanceof Comparable)) {
                    split = split.toString();
                }
                List<Object> rightVector = new ArrayList<Object>();
                List<Object> leftVector = new ArrayList<Object>();
                List<Object> middleVector = new ArrayList<Object>();
                for (int i = 0; i < items.length; i++) {
                    Object o = items[i];
                    if (!(o instanceof Comparable)) {
                        o = o.toString();
                    }
                    if (((Comparable) o).compareTo(split) < 0) {
                        leftVector.add(items[i]);
                    } else if (((Comparable) o).compareTo(split) == 0) {
                        middleVector.add(items[i]);
                    } else {
                        rightVector.add(items[i]);
                    }
                }
                Object[] sortedLeftItems = leftVector.toArray();
                Object[] sortedRightItems = rightVector.toArray();
                quickSort(sortedLeftItems);
                quickSort(sortedRightItems);
                for (int i = 0; i < items.length; i++) {
                    if (i < sortedLeftItems.length) {
                        items[i] = sortedLeftItems[i];
                    } else if (i < sortedLeftItems.length + middleVector.size()) {
                        items[i] = middleVector.get(i - sortedLeftItems.length);
                    } else {
                        items[i] = sortedRightItems[i - sortedLeftItems.length - middleVector.size()];
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
	public static void quickSort(Vector items) {
        if (items != null) {
            Object[] o = items.toArray();
            quickSort(o);
            for (int i = 0; i < o.length; i++) {
                items.setElementAt(o[i], i);
            }
        }
    }

    @SuppressWarnings("unchecked")
	public static Vector quickSort(Vector items, int sortIndex) {
        //This method sorts a Vector of Vectors so that the column sortIndex is
        // sorted//
        if (items != null) {
            if (items.size() > 1) {
                Object splitItem = ((Vector) items.elementAt(java.lang.Math.round(items.size() / 2))).elementAt(sortIndex);
                Vector rightVector = new Vector();
                Vector leftVector = new Vector();
                Vector middleVector = new Vector();

                for (int i = 0; i < items.size(); i++) {
                    if (((Vector) items.elementAt(i)).elementAt(sortIndex).toString().compareTo(splitItem.toString()) < 0) {
                        leftVector.addElement(items.elementAt(i));
                    } else if (((Vector) items.elementAt(i)).elementAt(sortIndex).toString().compareTo(splitItem.toString()) == 0) {
                        middleVector.addElement(items.elementAt(i));
                    } else {
                        rightVector.addElement(items.elementAt(i));
                    }
                }
                Vector sortedLeftItems = quickSort(leftVector, sortIndex);
                Vector sortedRightItems = quickSort(rightVector, sortIndex);
                for (int i = 0; i < items.size(); i++) {
                    if (i < sortedLeftItems.size()) {
                        items.setElementAt(sortedLeftItems.elementAt(i), i);
                    } else if (i < sortedLeftItems.size() + middleVector.size()) {
                        items.setElementAt(middleVector.elementAt(i - sortedLeftItems.size()), i);
                    } else {
                        items.setElementAt(sortedRightItems.elementAt(i - sortedLeftItems.size() - middleVector.size()), i);
                    }
                }
            }
        }
        return items;
    }

}
