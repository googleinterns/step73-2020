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

import com.google.coffeehouse.common.MembershipConstants;
import com.google.cloud.spanner.Database;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Value;
import java.util.ArrayList;
import java.util.List;

/**
* The StorageHandlerTestHelper class holds functions that are used in multiple database test files.
*/
public class StorageHandlerTestHelper {
  static DatabaseClient dbClient;
  private static final String INSTANCE_ID = "coffeehouse-instance-test";
  private static final String DATABASE_ID = "coffeehouse-db-test";

  public static DatabaseClient setUpHelper() {
    // Create spanner service and database client
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), INSTANCE_ID, DATABASE_ID);
    dbClient = spanner.getDatabaseClient(db);
    return dbClient;
  }

  public static void setUpClearDb() {
    // Delete all the data while keeping the existing database
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(Mutation.delete("Persons", KeySet.all()));
    mutations.add(Mutation.delete("Books", KeySet.all()));
    mutations.add(Mutation.delete("Clubs", KeySet.all()));
    mutations.add(Mutation.delete("Memberships", KeySet.all()));
    dbClient.write(mutations);
  }

  public static void insertPerson(String person_id) {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertBuilder("Persons")
        .set("userId")
        .to(person_id)
        .set("email")
        .to("person@test.com")
        .set("nickname")
        .to("person")
        .set("pronouns")
        .to("she/he/they")
        .build());
    dbClient.write(mutations);
  }

  public static void insertClub(String club_id) {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Clubs")
        .set("clubId")
        .to(club_id)
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

  public static void insertMembership(String person_id, String club_id) {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertBuilder("Memberships")
        .set("userId")
        .to(person_id)
        .set("clubId")
        .to(club_id)
        .set("membershipType")
        .to(MembershipConstants.MEMBER)
        .set("timestamp")
        .to(Value.COMMIT_TIMESTAMP)
        .build());
    dbClient.write(mutations);
  }

  public static void insertBook(String book_id) {
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

  public void insertPersonWithPronouns(String person_id) {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Persons")
        .set("userId")
        .to(person_id)
        .set("email")
        .to("pronouns@test.com")
        .set("nickname")
        .to("Pro-Test")
        .set("pronouns")
        .to("she/he/they")
        .build());
    dbClient.write(mutations);
  }

  public void insertPersonWithNullPronouns(String person_id) {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Persons")
        .set("userId")
        .to(person_id)
        .set("email")
        .to("null@test.com")
        .set("nickname")
        .to("Null-Test")
        .build());
    dbClient.write(mutations);
  }

  public void insertPersonWithEmptyPronouns(String person_id) {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Persons")
        .set("userId")
        .to(person_id)
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
}
