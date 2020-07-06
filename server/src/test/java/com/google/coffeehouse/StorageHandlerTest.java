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

package com.google.coffeehouse;

import static com.google.cloud.spanner.TransactionRunner.TransactionCallable;

import com.google.cloud.spanner.Database;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Instance;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.TransactionContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
* The StorageHandlerTest class encompasses functions to set up the spanner service
* and database client, insert data needed to run each specific test, and then deletes
* the data in tearDown().
*/
@RunWith(JUnit4.class)
public class StorageHandlerTest {

  static DatabaseClient dbClient;
  private static final String INSTANCE_ID = "coffeehouse-instance-test";
  private static final String DATABASE_ID = "coffeehouse-db-test";

  @Before
  public void setUp() {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), INSTANCE_ID, DATABASE_ID);
    dbClient = spanner.getDatabaseClient(db);
    System.out.println("Setting up!");
  }

  @After
  public void tearDown() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(Mutation.delete("Persons", KeySet.all()));
    dbClient.write(mutations);
    System.out.println("Deleting data and tearing down!");
  }

  public void insertPersonWithPronouns() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Persons")
        .set("userId")
        .to("pronouns")
        .set("email")
        .to("pronouns@test.com")
        .set("nickname")
        .to("Pro-Test")
        .set("pronouns")
        .to("she/he/they")
        .build());
    dbClient.write(mutations);
  }

  public void insertPersonWithNullPronouns() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Persons")
        .set("userId")
        .to("nullPronouns")
        .set("email")
        .to("null@test.com")
        .set("nickname")
        .to("Null-Test")
        .build());
    dbClient.write(mutations);
  }

  public void insertPersonWithEmptyPronouns() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Persons")
        .set("userId")
        .to("emptyPronouns")
        .set("email")
        .to("empty@test.com")
        .set("nickname")
        .to("Empty-Test")
        .set("pronouns")
        .to("")
        .build());
    dbClient.write(mutations);
  }

  @Test
  public void testGetPersonWithPronounsQuery() throws Exception {
    insertPersonWithPronouns();
    String actual = StorageHandler.getPersonQuery(dbClient, "pronouns");
    String expected = "User ID: pronouns || Email: pronouns@test.com || Nickname: Pro-Test || Pronouns: she/he/they\n";
    Assert.assertEquals(expected, actual);
    tearDown();
  }

  @Test
  public void testGetPersonWithNullPronounsQuery() throws Exception {
    insertPersonWithNullPronouns();
    String actual = StorageHandler.getPersonQuery(dbClient, "nullPronouns");
    String expected = "User ID: nullPronouns || Email: null@test.com || Nickname: Null-Test || No pronouns\n";
    Assert.assertEquals(expected, actual);
    tearDown();
  }

  @Test
  public void testGetPersonWithEmptyPronounsQuery() throws Exception {
    insertPersonWithEmptyPronouns();
    String actual = StorageHandler.getPersonQuery(dbClient, "emptyPronouns");
    String expected = "User ID: emptyPronouns || Email: empty@test.com || Nickname: Empty-Test || No pronouns\n";
    Assert.assertEquals(expected, actual);
    tearDown();
  }
}
