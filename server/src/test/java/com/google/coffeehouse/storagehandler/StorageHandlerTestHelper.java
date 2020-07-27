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
import com.google.coffeehouse.common.Book;
import com.google.coffeehouse.common.Club;
import com.google.coffeehouse.common.Person;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Value;
import java.util.Arrays;
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

  public static void insertClub(String club_id, String owner_id) {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Clubs")
        .set("clubId")
        .to(club_id)
        .set("bookId")
        .to("book")
        .set("description")
        .to("description")
        .set("name")
        .to("club")
        .set("ownerId")
        .to(owner_id)
        .set("timestamp")
        .to(Value.COMMIT_TIMESTAMP)
        .build());
    dbClient.write(mutations);
  }

  public static void insertClubWithNoContentWarnings(String club_id) {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Clubs")
        .set("clubId")
        .to(club_id)
        .set("bookId")
        .to("book")
        .set("description")
        .to("description")
        .set("name")
        .to("club")
        .set("ownerId")
        .to("owner")
        .set("timestamp")
        .to(Value.COMMIT_TIMESTAMP)
        .build());
    dbClient.write(mutations);
  }

  public static void insertClubWithContentWarnings(String club_id) {
    List<String> testContentWarnings = new ArrayList<>(Arrays.asList("test"));
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Clubs")
        .set("clubId")
        .to(club_id)
        .set("bookId")
        .to("book")
        .set("description")
        .to("description")
        .set("name")
        .to("club")
        .set("ownerId")
        .to("owner")
        .set("contentWarning")
        .to(String.join("\n", testContentWarnings))
        .set("timestamp")
        .to(Value.COMMIT_TIMESTAMP)
        .build());
    dbClient.write(mutations);
  }

  public static void insertMembership(String person_id, String club_id, int membershipLevel) {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertBuilder("Memberships")
        .set("userId")
        .to(person_id)
        .set("clubId")
        .to(club_id)
        .set("membershipType")
        .to(membershipLevel)
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
        .to("author")
        .set("isbn")
        .to("isbn")
        .set("title")
        .to("title")
        .build());
    dbClient.write(mutations);
  }

  public static void insertPersonWithPronouns(String person_id) {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Persons")
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

  public static void insertPersonWithNullPronouns(String person_id) {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Persons")
        .set("userId")
        .to(person_id)
        .set("email")
        .to("person@test.com")
        .set("nickname")
        .to("person")
        .build());
    dbClient.write(mutations);
  }

  public static void insertBookWithAuthorAndIsbn(String book_id) {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to(book_id)
        .set("author")
        .to("author")
        .set("isbn")
        .to("isbn")
        .set("title")
        .to("title")
        .build());
    dbClient.write(mutations);
  }

  public static void insertBookWithNullAuthor(String book_id) {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to(book_id)
        .set("isbn")
        .to("isbn")
        .set("title")
        .to("title")
        .build());
    dbClient.write(mutations);
  }

  public static void insertBookWithNullIsbn(String book_id) {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to(book_id)
        .set("author")
        .to("author")
        .set("title")
        .to("title")
        .build());
    dbClient.write(mutations);
  }

  public static void insertBookWithNullAuthorAndNullIsbn(String book_id) {
    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
      Mutation.newInsertOrUpdateBuilder("Books")
        .set("bookId")
        .to(book_id)
        .set("title")
        .to("title")
        .build());
    dbClient.write(mutations);
  }

  public static Person createTestPersonObject(String person_id, Boolean pronouns_exist) {
    Person.Builder personBuilder = Person.newBuilder()
                                         .setEmail("person@test.com")
                                         .setNickname("person")
                                         .setUserId(person_id);
    if (pronouns_exist) {
      personBuilder.setPronouns("she/he/they");
    }
    return personBuilder.build();
  }

  public static Book createTestBookObject(String book_id, Boolean isbn_exists,
                                            Boolean author_exists) {
    Book.Builder bookBuilder = Book.newBuilder()
                                   .setTitle("title")
                                   .setBookId(book_id);
    if (isbn_exists) {
      bookBuilder.setIsbn("isbn");
    }
    if (author_exists) {
      bookBuilder.setAuthor("author");
    }
    return bookBuilder.build();
  }

  public static Club createTestClubObject(String club_id, Boolean content_warnings_exist) {
    List<String> testContentWarnings = new ArrayList<>(Arrays.asList("test"));
    Book testBook = createTestBookObject("book", /* isbnExists= */true,
                                                 /* authorExists= */true);
    Club.Builder clubBuilder = Club.newBuilder()
                                   .setName("club")
                                   .setCurrentBook(testBook)
                                   .setOwnerId("owner")
                                   .setClubId(club_id)
                                   .setDescription("description");
    if (content_warnings_exist) {
      clubBuilder.setContentWarnings(testContentWarnings);
    }
    return clubBuilder.build();
  }
}
