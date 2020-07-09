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
* The StorageHandlerTest class encompasses functions to set up the spanner service
* and database client, insert data for each specific test, and then deletes the data in tearDown().
*/
@RunWith(JUnit4.class)
public class StorageHandlerTest {

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

  public void insertClub() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Clubs")
        .set("clubId")
        .to("bellhooksbooks")
        .set("bookId")
        .to("bellhooksallaboutlove")
        .set("description")
        .to("All of bell hooks' books about revolutionary Black feminism.")
        .set("name")
        .to("bell hooks lovers")
        .set("ownerId")
        .to("bellhookstopfan")
        .set("timestamp")
        .to(Value.COMMIT_TIMESTAMP)
        .build());
    dbClient.write(mutations);
  }

  public void insertBookWithAuthorAndISBN() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("bookWithAuthorAndISBN")
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

  public void insertBookWithNullISBN() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("bookWithNullISBN")
        .set("author")
        .to("bell hooks")
        .set("title")
        .to("all about love: new visions")
        .build());
    dbClient.write(mutations);
  }

  public void insertBookWithNullAuthorAndNullISBN() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("bookWithNullAuthorAndNullISBN")
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

  public void insertBookWithEmptyISBN() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("bookWithEmptyISBN")
        .set("author")
        .to("bell hooks")
        .set("isbn")
        .to("")
        .set("title")
        .to("all about love: new visions")
        .build());
    dbClient.write(mutations);
  }

  public void insertBookWithEmptyAuthorAndEmptyISBN() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("bookWithEmptyAuthorAndEmptyISBN")
        .set("author")
        .to("")
        .set("isbn")
        .to("")
        .set("title")
        .to("anonymous book about love")
        .build());
    dbClient.write(mutations);
  }

  public void insertBookWithEmptyAuthorAndNullISBN() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("bookWithEmptyAuthorAndNullISBN")
        .set("author")
        .to("")
        .set("title")
        .to("anonymous book about love")
        .build());
    dbClient.write(mutations);
  }

  public void insertBookWithNullAuthorAndEmptyISBN() {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to("bookWithNullAuthorAndEmptyISBN")
        .set("isbn")
        .to("")
        .set("title")
        .to("anonymous book about love")
        .build());
    dbClient.write(mutations);
  }

  @Test
  public void getPersonQuery_doesNotExistInDb() throws Exception {
    String actual = StorageHandler.getPersonQuery(dbClient, "personThatDoesNotExist");
    String expected = StorageHandler.PERSON_DOES_NOT_EXIST;
    assertEquals(expected, actual);
  }

  @Test
  public void getPersonQuery_existsWithNoPronouns() throws Exception {
    insertPersonWithPronouns();
    String actual = StorageHandler.getPersonQuery(dbClient, "hasPronouns");
    String expected = "User ID: hasPronouns || Email: pronouns@test.com || Nickname: Pro-Test || Pronouns: she/he/they\n";
    assertEquals(expected, actual);
  }

  @Test
  public void getPersonQuery_existsWithNullPronouns() throws Exception {
    insertPersonWithNullPronouns();
    String actual = StorageHandler.getPersonQuery(dbClient, "nullPronouns");
    String expected = String.format("User ID: nullPronouns || Email: null@test.com || "
                                      + "Nickname: Null-Test || %s\n", StorageHandler.NO_PRONOUNS);
    assertEquals(expected, actual);
  }

  @Test
  public void getPersonQuery_existsWithEmptyPronouns() throws Exception {
    insertPersonWithEmptyPronouns();
    String actual = StorageHandler.getPersonQuery(dbClient, "emptyPronouns");
    String expected = String.format("User ID: emptyPronouns || Email: empty@test.com || "
                                      + "Nickname: Empty-Test || No pronouns\n",
                                      StorageHandler.NO_PRONOUNS);
    assertEquals(expected, actual);
  }

  @Test
  public void getClubQuery_doesNotExistInDb() throws Exception {
    String actual = StorageHandler.getClubQuery(dbClient, "clubThatDoesNotExist");
    String expected = StorageHandler.CLUB_DOES_NOT_EXIST;
    assertEquals(expected, actual);
  }

  @Test
  public void getClubQuery_existsInDb() throws Exception {
    insertClub();
    String actual = StorageHandler.getClubQuery(dbClient, "bellhooksbooks");
    String expected = "Club ID: bellhooksbooks || Book ID: bellhooksallaboutlove || "
                        + "Description: All of bell hooks' books about revolutionary Black feminism. || "
                        + "Name: bell hooks lovers || Owner ID: bellhookstopfan\n";
    assertEquals(expected, actual);
  }

  @Test
  public void getBookQuery_doesNotExistInDb() throws Exception {
    String actual = StorageHandler.getBookQuery(dbClient, "bookThatDoesNotExist");
    String expected = StorageHandler.BOOK_DOES_NOT_EXIST;
    assertEquals(expected, actual);
  }

  @Test
  public void getBookQuery_existsInDb() throws Exception {
    insertBookWithAuthorAndISBN();
    String actual = StorageHandler.getBookQuery(dbClient, "bookWithAuthorAndISBN");
    String expected = "Book ID: bookWithAuthorAndISBN || Author: bell hooks || "
                        + "ISBN: 9780060959470 || Title: all about love: new visions\n";
    assertEquals(expected, actual);
  }

  @Test
  public void getBookQuery_existsWithNullAuthor() throws Exception {
    insertBookWithNullAuthor();
    String actual = StorageHandler.getBookQuery(dbClient, "bookWithNullAuthor");
    String expected = "Book ID: bookWithNullAuthor || No author || "
                        + "ISBN: 9780060959470 || Title: all about love: new visions\n";
    assertEquals(expected, actual);
  }

  @Test
  public void getBookQuery_existsWithNullISBN() throws Exception {
    insertBookWithNullISBN();
    String actual = StorageHandler.getBookQuery(dbClient, "bookWithNullISBN");
    String expected = "Book ID: bookWithNullISBN || Author: bell hooks || "
                        + "No ISBN || Title: all about love: new visions\n";
    assertEquals(expected, actual);
  }

  @Test
  public void getBookQuery_existsWithNullAuthorAndNullISBN() throws Exception {
    insertBookWithNullAuthorAndNullISBN();
    String actual = StorageHandler.getBookQuery(dbClient, "bookWithNullAuthorAndNullISBN");
    String expected = "Book ID: bookWithNullAuthorAndNullISBN || No author || "
                        + "No ISBN || Title: anonymous book about love\n";
    assertEquals(expected, actual);
  }

  @Test
  public void getBookQuery_existsWithEmptyAuthor() throws Exception {
    insertBookWithEmptyAuthor();
    String actual = StorageHandler.getBookQuery(dbClient, "bookWithEmptyAuthor");
    String expected = "Book ID: bookWithEmptyAuthor || No author || "
                        + "ISBN: 9780060959470 || Title: all about love: new visions\n";
    assertEquals(expected, actual);
  }

  @Test
  public void getBookQuery_existsWithEmptyISBN() throws Exception {
    insertBookWithEmptyISBN();
    String actual =StorageHandler.getBookQuery(dbClient, "bookWithEmptyISBN");
    String expected = "Book ID: bookWithEmptyISBN || Author: bell hooks || "
                        + "No ISBN || Title: all about love: new visions\n";
    assertEquals(expected, actual);
  }

  @Test
  public void getBookQuery_existsWithEmptyAuthorAndEmptyISBN() throws Exception {
    insertBookWithEmptyAuthorAndEmptyISBN();
    String actual = StorageHandler.getBookQuery(dbClient, "bookWithEmptyAuthorAndEmptyISBN");
    String expected = "Book ID: bookWithEmptyAuthorAndEmptyISBN || No author || "
                        + "No ISBN || Title: anonymous book about love\n";
    assertEquals(expected, actual);
  }

  @Test
  public void getBookQuery_existsWithEmptyAuthorAndNullISBN() throws Exception {
    insertBookWithEmptyAuthorAndNullISBN();
    String actual = StorageHandler.getBookQuery(dbClient, "bookWithEmptyAuthorAndNullISBN");
    String expected = "Book ID: bookWithEmptyAuthorAndNullISBN || No author || "
                        + "No ISBN || Title: anonymous book about love\n";
    assertEquals(expected, actual);
  }

  @Test
  public void getBookQuery_existsWithNullAuthorAndEmptyISBN() throws Exception {
    insertBookWithNullAuthorAndEmptyISBN();
    String actual = StorageHandler.getBookQuery(dbClient, "bookWithNullAuthorAndEmptyISBN");
    String expected = "Book ID: bookWithNullAuthorAndEmptyISBN || No author || "
                        + "No ISBN || Title: anonymous book about love\n";
    assertEquals(expected, actual);
  }
}