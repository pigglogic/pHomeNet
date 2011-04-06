package com.pigglogic.phomenet.service.impl;

import java.util.Date;
import java.util.List;
import org.apache.cassandra.cli.CliParser.rowKey_return;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wyki.cassandra.pelops.GeneralPolicy;
import org.wyki.cassandra.pelops.Mutator;
import org.wyki.cassandra.pelops.Pelops;
import org.wyki.cassandra.pelops.Selector;
import org.wyki.cassandra.pelops.ThriftPoolComplex;
import org.wyki.cassandra.pelops.UuidHelper;

/**
 *
 * @author michaelpigg
 */
@Test(enabled=false)
public class CassandraObservationServiceTest
{
    private static final String PHOMENET_KEYSPACE = "com.pigglogic.phomenet";
    private static final String PHOMENET_POOL = "phomenet";


    public void testInsert() throws Exception
    {
        final String location = "Location1";
        final Double value = 28.235;
        final Date observationDateTime = new Date();
        final String type = "temperature";
        
        Pelops.addPool("phomenet", new String[] {"localhost"}, 9160, false, null, new GeneralPolicy(), new ThriftPoolComplex.Policy());
        final String key = UuidHelper.newTimeUuid().toString();
        Mutator mutator = Pelops.createMutator(PHOMENET_POOL, PHOMENET_KEYSPACE);
        mutator.writeSubColumns(key, "Observations", type,
            mutator.newColumnList(
                mutator.newColumn("location", location),
                mutator.newColumn("value", value.toString()),
                mutator.newColumn("date", Long.toString(observationDateTime.getTime())),
                mutator.newColumn("type", type.toString())
            )
        );
        mutator.execute(ConsistencyLevel.ONE);

        Selector selector = Pelops.createSelector(PHOMENET_POOL, PHOMENET_KEYSPACE);
        List<Column> columnsFromRow = selector.getSubColumnsFromRow(key, "Observations", type, Selector.newColumnsPredicateAll(true, 500), ConsistencyLevel.ONE);
        Assert.assertEquals(columnsFromRow.size(), 4);
        Selector selector2 = Pelops.createSelector(PHOMENET_POOL, PHOMENET_KEYSPACE);
        

    }
}
