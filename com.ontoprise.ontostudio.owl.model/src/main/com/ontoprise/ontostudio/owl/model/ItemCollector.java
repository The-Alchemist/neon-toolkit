package com.ontoprise.ontostudio.owl.model;

import java.util.Set;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAxiom;


/**
 * An <code>ItemCollector</code> 'collects' items from a set of axioms.<br/>
 * <br/>
 * Example: Let A be a set of <code>SubClassOf</code> axioms. Then you may like to know all sub descriptions within this axiom set, i.e. you like to get the
 * set { D | a in A with a.getSubDescription() == D}.<br/>
 * <br/>
 * Further more, one may not only like to know all sub descriptions, but also would like to 'tag' the sub descriptions with the axioms they come from. <br/>
 * This is what an <code>ItemCollector</code> is used for. Its major method is
 * <code>getItemOccurrences(OccurrenceGrouping groupBy, Set<LocatedItem<AxiomType>> source)</code>, all other methods are just for comfort.
 * 
 * @author krekeler
 * 
 * @param <ItemType>
 * @param <AxiomType>
 */
public interface ItemCollector<ItemType, AxiomType extends OWLAxiom> {
    /**
     * Get the items without informations about there occurrences.
     * 
     * @param groupBy
     * @param source
     * @return The items without any tagged information about their occurrences.
     */
    public Set<ItemType> getItems(OWLModel.ItemHitsGrouping groupBy, Set<LocatedItem<AxiomType>> source, Object... parameters);

    /**
     * This method evaluates the given <code>AxiomRequest</code> <code>source</code> with the given parameters <code>parameters</code> and uses the
     * resulting set of axioms as input set.<br/>
     * <br/>
     * The <code>ItemHitsGrouping</code> to use is detected from the associated <code>OWLModel</code>.<br/>
     * <br/>
     * No information about the occurrences of the items is included within the result.
     * 
     * @param source
     * @param parameters
     * @return The items without any tagged information about their occurrences.
     * @throws NeOnCoreException
     */
    public Set<ItemType> getItems(AxiomRequest<AxiomType> source, Object[] sourceParameters, Object... parameters) throws NeOnCoreException;

    /**
     * Get all occurrences of items within a set of given axioms.<br/>
     * <br/>
     * For each occurrence of an item within one of the given axioms there will be an <code>ItemHits</code> instance in the resulting set which holds the
     * information about the item itself and the axiom where it comes from.
     * 
     * @param filter A filter to apply on the resulting set.
     * @param groupBy An item can occure several items within the set of given axioms.<br/>
     * <br/>
     *            Note that an <code>ItemHits</code> instance can hold several of such occurrences for a single item.<br/>
     * <br/>
     *            If an item occurs several times, there may be several <code>ItemHits</code> instances which are related to the same item.<br/>
     * <br/>
     *            This parameter controls how many <code>ItemHits</code> instances for the item will be included within the resulting set:
     *            <dl>
     *            <dt><code>ItemHitsGrouping.OnePerAxiom</code></dt>
     *            <dd>There will be one <code>ItemHits</code> instance for each occurrence within the axiom set of the item. Each <code>ItemHits</code>
     *            instance holds just one single (located) axiom.</dd>
     *            <dt><code>ItemHitsGrouping.OnePerOntology</code></dt>
     *            <dd>If an <code>ItemHits</code> instance from the result contains more than one (located) axioms, they will all come from the same
     *            ontology.<br/>
     *            <br/>
     *            Conversely all axioms from a given ontology for a given item will be found in one single <code>ItemHits</code> instance.
     *            <dd>
     *            <dt><code>ItemHitsGrouping.OneForThisOneForOtherOntologies</code></dt>
     *            <dd>For each item there will be at most two <code>ItemHits</code> instance in the resulting set: (at most) one which holds all item
     *            occurrences for this ontology and (at most) one for all occurrences in any of the imported ontologies.</dd>
     *            <dt><code>ItemHitsGrouping.OneAtAll</code></dt>
     *            <dd>For each item there will be at most one <code>ItemHits</code> instance in the resulting set and it will include all (located) axioms
     *            in which the item occurs (regardless of the ontology).</dd>
     *            </dl>
     * @param source The set of axioms.
     * @param parameters Parameters needed by the concrete implementor of this interface.
     * @return A set items tagged with the information about their occurrences within <code>source</code>.
     */
    public Set<ItemHits<ItemType,AxiomType>> getItemHits(OWLModel.ItemHitsGrouping groupBy, Set<LocatedItem<AxiomType>> source, Object... parameters);

    /**
     * This method evaluates the given <code>AxiomRequest</code> <code>source</code> with the given parameters <code>parameters</code> and uses the
     * resulting set of axioms as input set.<br/>
     * <br/>
     * The <code>ItemHitsGrouping</code> to use is detected from the associated <code>OWLModel</code>.
     * 
     * @param filter
     * @param source
     * @param parameters
     * @return
     * @throws NeOnCoreException
     */
    public Set<ItemHits<ItemType,AxiomType>> getItemHits(AxiomRequest<AxiomType> source, Object[] sourceParameters, Object... parameters) throws NeOnCoreException;
}