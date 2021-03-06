/*
 * Copyright (c) 2002-2019 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.driver.internal.messaging.request;

import java.time.Duration;
import java.util.Map;

import org.neo4j.driver.AccessMode;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.Bookmarks;
import org.neo4j.driver.internal.util.Iterables;

import static java.util.Collections.emptyMap;
import static org.neo4j.driver.Values.value;
import static org.neo4j.driver.internal.messaging.request.MultiDatabaseUtil.ABSENT_DB_NAME;

public class TransactionMetadataBuilder
{
    private static final String BOOKMARKS_METADATA_KEY = "bookmarks";
    private static final String DATABASE_NAME_KEY = "db";
    private static final String TX_TIMEOUT_METADATA_KEY = "tx_timeout";
    private static final String TX_METADATA_METADATA_KEY = "tx_metadata";
    private static final String MODE_KEY = "mode";
    private static final String MODE_READ_VALUE = "r";

    public static Map<String,Value> buildMetadata( Duration txTimeout, Map<String,Value> txMetadata, AccessMode mode, Bookmarks bookmarks )
    {
        return buildMetadata( txTimeout, txMetadata, ABSENT_DB_NAME, mode, bookmarks );
    }

    public static Map<String,Value> buildMetadata( Duration txTimeout, Map<String,Value> txMetadata, String databaseName, AccessMode mode, Bookmarks bookmarks )
    {
        boolean bookmarksPresent = bookmarks != null && !bookmarks.isEmpty();
        boolean txTimeoutPresent = txTimeout != null;
        boolean txMetadataPresent = txMetadata != null && !txMetadata.isEmpty();
        boolean accessModePresent = mode == AccessMode.READ;
        boolean databaseNamePresent = databaseName != null && !databaseName.equals( ABSENT_DB_NAME );

        if ( !bookmarksPresent && !txTimeoutPresent && !txMetadataPresent && !accessModePresent && !databaseNamePresent )
        {
            return emptyMap();
        }

        Map<String,Value> result = Iterables.newHashMapWithSize( 5 );

        if ( bookmarksPresent )
        {
            result.put( BOOKMARKS_METADATA_KEY, value( bookmarks.values() ) );
        }
        if ( txTimeoutPresent )
        {
            result.put( TX_TIMEOUT_METADATA_KEY, value( txTimeout.toMillis() ) );
        }
        if ( txMetadataPresent )
        {
            result.put( TX_METADATA_METADATA_KEY, value( txMetadata ) );
        }
        if( accessModePresent )
        {
            result.put( MODE_KEY, value( MODE_READ_VALUE ) );
        }
        if ( databaseNamePresent ) // only sent if the database name is different from absent
        {
            result.put( DATABASE_NAME_KEY, value( databaseName ) );
        }

        return result;
    }
}
