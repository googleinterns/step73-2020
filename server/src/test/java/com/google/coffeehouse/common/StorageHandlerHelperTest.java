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

package com.google.coffeehouse.common;

import static org.junit.Assert.*;

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
import com.google.cloud.spanner.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* The StorageHandlerHelpTest class encompasses functions to set up the spanner service
* and database client, insert data for each specific test, and then deletes the data in tearDown().
*/
@RunWith(JUnit4.class)
public class StorageHandlerHelperTest {
  static DatabaseClient dbClient;
  private static final String INSTANCE_ID = "coffeehouse-instance-test";
  private static final String DATABASE_ID = "coffeehouse-db-test";

  @Before
  public void setUp() {
    // Create spanner service and database client
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), INSTANCE_ID, DATABASE_ID);
    dbClient = spanner.getDatabaseClient(db);

    // Delete all the data while keeping the existing database
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(Mutation.delete("Persons", KeySet.all()));
    mutations.add(Mutation.delete("Books", KeySet.all()));
    mutations.add(Mutation.delete("Clubs", KeySet.all()));
    dbClient.write(mutations);
  }

  public void insertPerson() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Persons")
        .set("userId")
        .to("person")
        .set("email")
        .to("person@test.com")
        .set("nickname")
        .to("person")
        .set("pronouns")
        .to("she/he/they")
        .build());
    dbClient.write(mutations);
  }

  public void insertClub() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Clubs")
        .set("clubId")
        .to("club")
        .set("bookId")
        .to("book")
        .set("description")
        .to("A club")
        .set("name")
        .to("Club")
        .set("ownerId")
        .to("owner")
        .set("timestamp")
        .to(Value.COMMIT_TIMESTAMP)
        .build());
    dbClient.write(mutations);
  }

  public void insertMembership() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertBuilder("Memberships")
        .set("userId")
        .to("person")
        .set("clubId")
        .to("club")
        .set("membershipType")
        .to(MembershipConstants.MEMBER)
        .set("timestamp")
        .to(Value.COMMIT_TIMESTAMP)
        .build());
    dbClient.write(mutations);
  }

  public void insertBook() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("book")
        .set("author")
        .to("book")
        .set("isbn")
        .to("97800609459470")
        .set("title")
        .to("book")
        .build());
    dbClient.write(mutations);
  }

  @Test
  public void checkPersonInClubQuery_personInClub() throws Exception {
    insertPerson();
    insertClub();
    insertMembership();
    Boolean actual = StorageHandlerHelper.checkPersonInClubQuery(dbClient, "person", "club");
    Boolean expected = true;
    assertEquals(expected, actual);
  }

  @Test
  public void checkPersonInClubQuery_personNotInClub() throws Exception {
    insertClub();
    Boolean actual = StorageHandlerHelper.checkPersonInClubQuery(dbClient, "personNotInClub", "club");
    Boolean expected = false;
    assertEquals(expected, actual);
  }

  @Test
  public void getMemberCountQuery_oneMembers() throws Exception {
    insertPerson();
    insertClub();
    insertMembership();
    long actual = StorageHandlerHelper.getMemberCountQuery(dbClient, "club");
    long expected = 1;
    assertEquals(expected, actual);
  }

  @Test
  public void getMemberCountQuery_noMember() throws Exception {
    insertClub();
    long actual = StorageHandlerHelper.getMemberCountQuery(dbClient, "club");
    long expected = 0;
    assertEquals(expected, actual);
  }

  @Test
  public void getPersonCountQuery_onePersonExists() throws Exception {
    insertPerson();
    long actual = StorageHandlerHelper.getPersonCountQuery(dbClient, "person");
    long expected = 1;
    assertEquals(expected, actual);
  }

  @Test
  public void getPersonCountQuery_noPersonExists() throws Exception {
    long actual = StorageHandlerHelper.getPersonCountQuery(dbClient, "personThatDoesNotExist");
    long expected = 0;
    assertEquals(expected, actual);
  }

   @Test
  public void getClubCountQuery_oneClubExists() throws Exception {
    insertClub();
    long actual = StorageHandlerHelper.getClubCountQuery(dbClient, "club");
    long expected = 1;
    assertEquals(expected, actual);
  }

  @Test
  public void getClubCountQuery_noClubExists() throws Exception {
    long actual = StorageHandlerHelper.getClubCountQuery(dbClient, "clubThatDoesNotExist");
    long expected = 0;
    assertEquals(expected, actual);
  }

 @Test
  public void getBookCountQuery_oneBookExists() throws Exception {
    insertBook();
    long actual = StorageHandlerHelper.getBookCountQuery(dbClient, "book");
    long expected = 1;
    assertEquals(expected, actual);
  }

  @Test
  public void getBookCountQuery_noBookExists() throws Exception {
    insertPerson();
    long actual = StorageHandlerHelper.getBookCountQuery(dbClient, "bookThatDoesNotExist");
    long expected = 0;
    assertEquals(expected, actual);
  }
}
