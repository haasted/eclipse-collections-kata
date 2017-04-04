/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.companykata;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.impl.bag.sorted.mutable.TreeBag;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

import static org.eclipse.collections.impl.block.factory.Comparators.byDoubleFunction;
import static org.eclipse.collections.impl.block.factory.Predicates.attributeEqual;

public class Exercise8Test extends CompanyDomainForKata
{
    /**
     * Extra credit. Aggregate the total order values by city.
     *
     * @see RichIterable#aggregateBy(Function, Function0, Function2)
     */
    @Test
    public void totalOrderValuesByCity()
    {
        Function0<Double> zeroValueFactory = () -> 0.0;
        Function2<Double, Customer, Double> aggregator = (result, customer) -> result + customer.getTotalOrderValue();

        MutableMap<String, Double> map = company.getCustomers().aggregateBy(Customer::getCity, zeroValueFactory, aggregator);

        Assert.assertEquals(2, map.size());
        Assert.assertEquals(446.25, map.get("London"), 0.0);
        Assert.assertEquals(857.0, map.get("Liphook"), 0.0);
    }

    /**
     * Extra credit. Aggregate the total order values by item.
     * Hint: Look at {@link RichIterable#aggregateBy(Function, Function0, Function2)} and remember
     * how to use {@link RichIterable#flatCollect(Function)} to get an iterable of all items.
     */
    @Test
    public void totalOrderValuesByItem()
    {
        Function0<Double> zeroValueFactory = () -> 0.0;
        Function2<Double, LineItem, Double> aggregator = (result, lineItem) -> result + lineItem.getValue();

        MutableMap<String, Double> map = company.getOrders().flatCollect(Order::getLineItems).aggregateBy(LineItem::getName, zeroValueFactory, aggregator);

        Verify.assertSize(12, map);
        Assert.assertEquals(100.0, map.get("shed"), 0.0);
        Assert.assertEquals(10.5, map.get("cup"), 0.0);
    }

    /**
     * Extra credit. Find all customers' line item values greater than 7.5 and sort them by highest to lowest price.
     */
    @Test
    public void sortedOrders()
    {
        MutableSortedBag<Double> orderedPrices =
                company.getOrders().flatCollect(Order::getLineItems)
                .select(li -> li.getValue() > 7.5)
                .sortThisBy(LineItem::getValue)
                .collect(LineItem::getValue)
                .into(TreeBag.newBag(Comparators.reverseNaturalOrder()));


        MutableSortedBag<Double> expectedPrices = TreeBag.newBagWith(
                Collections.reverseOrder(), 500.0, 150.0, 120.0, 75.0, 50.0, 50.0, 12.5);
        Verify.assertSortedBagsEqual(expectedPrices, orderedPrices);
    }

    /**
     * Extra credit. Figure out which customers ordered saucers (in any of their orders).
     */
    @Test
    public void whoOrderedSaucers()
    {
        MutableList<Customer> customersWithSaucers =
                company.getCustomers().
                    select(c ->
                            c.getOrders()
                                .flatCollect(Order::getLineItems)
                                .anySatisfy(attributeEqual(LineItem::getName, "saucer")));

        Verify.assertSize("customers with saucers", 2, customersWithSaucers);
    }

    /**
     * Extra credit. Look into the {@link MutableList#toMap(Function, Function)} method.
     */
    @Test
    public void ordersByCustomerUsingAsMap()
    {
        MutableMap<String, MutableList<Order>> customerNameToOrders =
                this.company.getCustomers().toMap(Customer::getName, Customer::getOrders);

        Assert.assertNotNull("customer name to orders", customerNameToOrders);
        Verify.assertSize("customer names", 3, customerNameToOrders);
        MutableList<Order> ordersForBill = customerNameToOrders.get("Bill");
        Verify.assertSize("Bill orders", 3, ordersForBill);
    }

    /**
     * Extra credit. Create a multimap where the values are customers and the key is the price of
     * the most expensive item that the customer ordered.
     */
    @Test
    public void mostExpensiveItem()
    {

        MutableListMultimap<Double, Customer> multimap
                = company.getCustomers()
                .groupBy(c -> c.getOrders().flatCollect(Order::getLineItems).maxBy(LineItem::getValue).getValue());

        Assert.assertEquals(3, multimap.size());
        Assert.assertEquals(2, multimap.keysView().size());
        Assert.assertEquals(
                FastList.newListWith(
                        this.company.getCustomerNamed("Fred"),
                        this.company.getCustomerNamed("Bill")),
                multimap.get(50.0));
    }
}
