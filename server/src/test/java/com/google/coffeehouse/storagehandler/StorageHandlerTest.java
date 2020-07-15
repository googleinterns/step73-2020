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

  @Before
  public void setUp() {
    dbClient = StorageHandlerTestHelper.setUpHelper();
    StorageHandlerTestHelper.setUpClearDb();
  }

  public void insertPersonWithPronouns() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Persons")
        .set("userId")
        .to("hasPronouns")
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

  public void insertBookWithAuthorAndIsbn() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("bookWithAuthorAndIsbn")
        .set("author")
        .to("bell hooks")
        .set("isbn")
        .to("9780060959470")
        .set("title")
        .to("all about love: new visions")
        .build());
    dbClient.write(mutations);
  }

  public void insertBookWithNullAuthor() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("bookWithNullAuthor")
        .set("isbn")
        .to("9780060959470")
        .set("title")
        .to("all about love: new visions")
        .build());
    dbClient.write(mutations);
  }

  public void insertBookWithNullIsbn() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("bookWithNullIsbn")
        .set("author")
        .to("bell hooks")
        .set("title")
        .to("all about love: new visions")
        .build());
    dbClient.write(mutations);
  }

  public void insertBookWithNullAuthorAndNullIsbn() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("bookWithNullAuthorAndNullIsbn")
        .set("title")
        .to("anonymous book about love")
        .build());
    dbClient.write(mutations);
  }

  public void insertBookWithEmptyAuthor() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("bookWithEmptyAuthor")
        .set("author")
        .to("")
        .set("isbn")
        .to("9780060959470")
        .set("title")
        .to("all about love: new visions")
        .build());
    dbClient.write(mutations);
  }

  public void insertBookWithEmptyIsbn() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("bookWithEmptyIsbn")
        .set("author")
        .to("bell hooks")
        .set("isbn")
        .to("")
        .set("title")
        .to("all about love: new visions")
        .build());
    dbClient.write(mutations);
  }

  public void insertBookWithEmptyAuthorAndEmptyIsbn() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("bookWithEmptyAuthorAndEmptyIsbn")
        .set("author")
        .to("")
        .set("isbn")
        .to("")
        .set("title")
        .to("anonymous book about love")
        .build());
    dbClient.write(mutations);
  }

  public void insertBookWithEmptyAuthorAndNullIsbn() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("bookWithEmptyAuthorAndNullIsbn")
        .set("author")
        .to("")
        .set("title")
        .to("anonymous book about love")
        .build());
    dbClient.write(mutations);
  }

  public void insertBookWithNullAuthorAndEmptyIsbn() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("bookWithNullAuthorAndEmptyIsbn")
        .set("isbn")
        .to("")
        .set("title")
        .to("anonymous book about love")
        .build());
    dbClient.write(mutations);
  }

  @Test
  public void addPersonClubMembershipMutation() throws Exception {
    StorageHandlerTestHelper.insertPerson();
    StorageHandler.getAddMembershipTransaction(dbClient, "person", "club");
    ReadContext readContext = dbClient.singleUse();
    Boolean actual = StorageHandlerHelper.checkMembership(readContext, "person", "club");
    assertEquals(true, actual);
  }

  @Test
  public void deletePersonClubMembership() throws Exception {
    StorageHandlerTestHelper.insertPerson();
    StorageHandlerTestHelper.insertMembership();
    StorageHandler.getDeleteMembershipTransaction(dbClient, "person", "club");
    ReadContext readContext = dbClient.singleUse();
    Boolean actual = StorageHandlerHelper.checkMembership(readContext, "person", "club");
    assertEquals(false, actual);
  }
}
