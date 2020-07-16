// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.coffeehouse.storagehandler;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;

/**
* The StorageHandlerSetup class creates and sets up the Spanner service and database
* client needed in order to run transactions in the database.
*/
public class StorageHandlerSetup {

  private static final String INSTANCE_ID = "coffeehouse-instance";
  private static final String DATABASE_ID = "coffeehouse-db";

  /**
  * Returns a spanner service.
  * This method creates a spanner service which is then returned and passed into
  * the creation and instantiation of a database client
  *
  * @return   the spanner service
  */
  public static Spanner createSpannerService() {
    // Instantiates a client
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    return spanner;
  }

  /**
  * Returns a database client that is used to query information.
  * This method creates a database client, which is then returned in order to
  * run transacations in the database.
  *
  * @param spanner    the spanner service
  * @return           the database client used to query the database
  */
  public static DatabaseClient createDbClient(Spanner spanner) {
    SpannerOptions options = spanner.getOptions();
    DatabaseId db = DatabaseId.of(options.getProjectId(), INSTANCE_ID, DATABASE_ID);
    DatabaseClient dbClient = spanner.getDatabaseClient(db);
    return dbClient;
  }
}
