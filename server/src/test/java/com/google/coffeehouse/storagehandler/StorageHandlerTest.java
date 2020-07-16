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

import static org.junit.Assert.*;

import com.google.cloud.spanner.Database;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.ReadContext;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.ArrayList;
import java.util.List;

/**
* The StorageHandlerTest class encompasses functions to set up the spanner service
* and database client, insert data for each specific test, and delete the data
* inserted from a previous test.
*/
@RunWith(JUnit4.class)
public class StorageHandlerTest {

  static DatabaseClient dbClient;
  private static final String INSTANCE_ID = "coffeehouse-instance-test";
  private static final String DATABASE_ID = "coffeehouse-db-test";

  @After
  public void tearDown() {
    StorageHandlerTestHelper.setUpClearDb();
  }

  @Before
  public void setUp() {
    dbClient = StorageHandlerTestHelper.setUpHelper();
    StorageHandlerTestHelper.setUpClearDb();
  }

  @Test
  public void getAddMembershipTransaction() throws Exception {
    StorageHandlerTestHelper.insertPerson("person");
    StorageHandler.runAddMembershipTransaction(dbClient, "person", "club");
    ReadContext readContext = dbClient.singleUse();
    Boolean actual = StorageHandlerHelper.checkMembership(readContext, "person", "club");
    assertTrue(actual);
  }

  @Test
  public void getDeleteMembershipTransaction() throws Exception {
    StorageHandlerTestHelper.insertPerson("person");
    StorageHandlerTestHelper.insertMembership("person", "club");
    StorageHandler.runDeleteMembershipTransaction(dbClient, "person", "club");
    ReadContext readContext = dbClient.singleUse();
    Boolean actual = StorageHandlerHelper.checkMembership(readContext, "person", "club");
    assertFalse(actual);
  }
}
