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

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class Exercise7Test extends CompanyDomainForKata
{
    /**
     * Create a multimap where the keys are the names of cities and the values are the customers from those cities.
     */
    @Test
    public void customersByCity()
    {
        // Notice that the second generic type is Customer, not List<Customer>
        MutableListMultimap<String, Customer> multimap = company.getCustomers().groupBy(Customer::getCity);

        Assert.assertEquals(FastList.newListWith(this.company.getCustomerNamed("Mary")), multimap.get("Liphook"));
        Assert.assertEquals(
                FastList.newListWith(
                        this.company.getCustomerNamed("Fred"),
                        this.company.getCustomerNamed("Bill")),
                multimap.get("London"));
    }

    @Test
    public void mapOfItemsToSuppliers()
    {
        /**
         * Change itemsToSuppliers to a MutableMultimap<String, Supplier>
         */
        MutableMultimap<String, Supplier> itemsToSuppliers = Multimaps.mutable.list.empty();

        // TODO There must be a more elegant approach ...
        ArrayIterate.forEach(company.getSuppliers(), s -> {
            ArrayIterate.forEach(s.getItemNames(), i -> itemsToSuppliers.put(i, s));
        });

        Verify.assertIterableSize("should be 2 suppliers for sofa", 2, itemsToSuppliers.get("sofa"));
    }
}
