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

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.ReadContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
* The StorageHandlerHelpTest class runs tests on the helper functions for the StorageHandler
*/
@RunWith(JUnit4.class)
public class StorageHandlerHelperTest {
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
  public void checkMembership_personInClub() throws Exception {
    StorageHandlerTestHelper.insertPerson("person");
    StorageHandlerTestHelper.insertClub("club");
    StorageHandlerTestHelper.insertMembership("person", "club");
    ReadContext readContext = dbClient.singleUse();
    Boolean actual = StorageHandlerHelper.checkMembership(readContext, "person", "club");
    assertTrue(actual);
  }

  @Test
  public void checkMembership_personNotInClub() throws Exception {
    StorageHandlerTestHelper.insertClub("club");
    ReadContext readContext = dbClient.singleUse();
    Boolean actual = StorageHandlerHelper.checkMembership(readContext, "personNotInClub", "club");
    assertFalse(actual);
  }

  @Test
  public void getMemberCount_oneMember() throws Exception {
    StorageHandlerTestHelper.insertPerson("person");
    StorageHandlerTestHelper.insertClub("club");
    StorageHandlerTestHelper.insertMembership("person", "club");
    ReadContext readContext = dbClient.singleUse();
    long actual = StorageHandlerHelper.getMemberCount(readContext, "club");
    assertEquals(1, actual);
  }

  @Test
  public void getMemberCount_noMember() throws Exception {
    StorageHandlerTestHelper.insertClub("club");
    ReadContext readContext = dbClient.singleUse();
    long actual = StorageHandlerHelper.getMemberCount(readContext, "club");
    assertEquals(0, actual);
  }
}