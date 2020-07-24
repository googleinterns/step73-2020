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
import com.google.cloud.spanner.ReadContext;
import com.google.coffeehouse.common.Book;
import com.google.coffeehouse.common.Club;
import com.google.coffeehouse.common.MembershipConstants;
import com.google.coffeehouse.common.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
* The StorageHandlerTest class runs tests to verify the results of transactions.
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
  public void getPerson_doesNotExistInDb() throws Exception {
    assertThrows(RuntimeException.class, () -> {
      StorageHandler.getPerson(dbClient, "personNotInDb");
    });
  }

  @Test
  public void getPerson_existsWithPronouns() throws Exception {
    StorageHandlerTestHelper.insertPersonWithPronouns("person");
    Person actual = StorageHandler.getPerson(dbClient, "person");
    Person expected = StorageHandlerTestHelper.createTestPersonObject(
      "person", /* pronouns= */true);
    assertEquals(actual.getNickname(), expected.getNickname());
    assertEquals(actual.getEmail(), expected.getEmail());
    assertEquals(actual.getUserId(), expected.getUserId());
    assertEquals(actual.getPronouns().get(), expected.getPronouns().get());
  }

  @Test
  public void getPerson_existsWithNullPronouns() throws Exception {
    StorageHandlerTestHelper.insertPersonWithNullPronouns("personWithNullPronouns");
    Person actual = StorageHandler.getPerson(dbClient, "personWithNullPronouns");
    Person expected = StorageHandlerTestHelper.createTestPersonObject(
      "personWithNullPronouns", /* pronouns= */false);
    assertEquals(actual.getNickname(), expected.getNickname());
    assertEquals(actual.getEmail(), expected.getEmail());
    assertEquals(actual.getUserId(), expected.getUserId());
    assertFalse(actual.getPronouns().isPresent());
  }

  @Test
  public void getBook_doesNotExistInDb() throws Exception {
    assertThrows(RuntimeException.class, () -> {
      StorageHandler.getBook(dbClient, "bookNotInDb");
    });
  }

  @Test
  public void getBook_existsWithAuthorAndIsbn() throws Exception {
    StorageHandlerTestHelper.insertBookWithAuthorAndIsbn("book");
    Book actual = StorageHandler.getBook(dbClient, "book");
    Book expected = StorageHandlerTestHelper.createTestBookObject(
                                                "book",
                                                /* isbnExists= */true,
                                                 /* authorExists= */true);
    assertEquals(actual.getTitle(), expected.getTitle());
    assertEquals(actual.getBookId(), expected.getBookId());
    assertEquals(actual.getAuthor(), expected.getAuthor());
    assertEquals(actual.getIsbn(), expected.getIsbn());
  }

  @Test
  public void getBook_existsWithAuthorAndNoIsbn() throws Exception {
    StorageHandlerTestHelper.insertBookWithNullIsbn("bookNullIsbn");
    Book actual = StorageHandler.getBook(dbClient, "bookNullIsbn");
    Book expected = StorageHandlerTestHelper.createTestBookObject(
                                                "bookNullIsbn",
                                                /* isbnExists= */false,
                                                 /* authorExists= */true);
    assertEquals(actual.getTitle(), expected.getTitle());
    assertEquals(actual.getBookId(), expected.getBookId());
    assertEquals(actual.getAuthor(), expected.getAuthor());
    assertFalse(actual.getIsbn().isPresent());
  }

  @Test
  public void getBook_existsWithIsbnAndNoAuthor() throws Exception {
    StorageHandlerTestHelper.insertBookWithNullAuthor("bookNullAuthor");
    Book actual = StorageHandler.getBook(dbClient, "bookNullAuthor");
    Book expected = StorageHandlerTestHelper.createTestBookObject(
                                                "bookNullAuthor",
                                                /* isbnExists= */true,
                                                 /* authorExists= */false);
    assertEquals(actual.getTitle(), expected.getTitle());
    assertEquals(actual.getBookId(), expected.getBookId());
    assertEquals(actual.getIsbn(), expected.getIsbn());
    assertFalse(actual.getAuthor().isPresent());
  }

  @Test
  public void getBook_existsWithNoIsbnAndNoAuthor() throws Exception {
    StorageHandlerTestHelper.insertBookWithNullAuthorAndNullIsbn("bookNullAuthorNullIsbn");
    Book actual = StorageHandler.getBook(dbClient, "bookNullAuthorNullIsbn");
    Book expected = StorageHandlerTestHelper.createTestBookObject(
                                                "bookNullAuthorNullIsbn",
                                                /* isbnExists= */false,
                                                 /* authorExists= */false);
    assertEquals(actual.getTitle(), expected.getTitle());
    assertEquals(actual.getBookId(), expected.getBookId());
    assertFalse(actual.getAuthor().isPresent());
    assertFalse(actual.getIsbn().isPresent());
  }

  @Test
  public void getClub_doesNotExistInDb() throws Exception {
    assertThrows(RuntimeException.class, () -> {
      StorageHandler.getClub(dbClient, "clubNotInDb");
    });
  }

  @Test
  public void getClub_existsWithContentWarnings() throws Exception {
    StorageHandlerTestHelper.insertBook("book");
    StorageHandlerTestHelper.insertClubWithContentWarnings("clubWithContentWarnings");
    Club actual = StorageHandler.getClub(dbClient, "clubWithContentWarnings");
    Club expected = StorageHandlerTestHelper.createTestClubObject(
      "clubWithContentWarnings", /* contentWarnings= */true);
    assertEquals(actual.getName(), expected.getName());
    assertEquals(actual.getOwnerId(), expected.getOwnerId());
    assertEquals(actual.getClubId(), expected.getClubId());
    assertEquals(actual.getDescription(), expected.getDescription());
    assertEquals(actual.getCurrentBook().getTitle(), expected.getCurrentBook().getTitle());
    assertArrayEquals(actual.getContentWarnings().toArray(new String[0]), 
                      expected.getContentWarnings().toArray(new String[0]));
  }

  @Test
  public void getClub_existsWithNoContentWarnings() throws Exception {
    StorageHandlerTestHelper.insertBook("book");
    StorageHandlerTestHelper.insertClubWithNoContentWarnings("clubWithNoContentWarnings");
    Club actual = StorageHandler.getClub(dbClient, "clubWithNoContentWarnings");
    Club expected = StorageHandlerTestHelper.createTestClubObject(
      "clubWithNoContentWarnings", /* contentWarnings= */false);
    assertEquals(actual.getName(), expected.getName());
    assertEquals(actual.getOwnerId(), expected.getOwnerId());
    assertEquals(actual.getClubId(), expected.getClubId());
    assertEquals(actual.getDescription(), expected.getDescription());
    assertEquals(actual.getCurrentBook().getTitle(), expected.getCurrentBook().getTitle());
    assertArrayEquals(actual.getContentWarnings().toArray(new String[0]), 
                      expected.getContentWarnings().toArray(new String[0]));
  }

  @Test
  public void runAddAnyMembershipTypeTransaction_member() throws Exception {
    StorageHandlerTestHelper.insertPerson("person");
    StorageHandler.runAddAnyMembershipTypeTransaction(
      dbClient, "person", "club", MembershipConstants.MEMBER);
    ReadContext readContext = dbClient.singleUse();
    Boolean actual = StorageHandlerHelper.checkAnyMembership(readContext, "person", "club");
    assertTrue(actual);
  }

  @Test
  public void runAddAnyMembershipTypeTransaction_owner() throws Exception {
    StorageHandlerTestHelper.insertPerson("person");
    StorageHandler.runAddAnyMembershipTypeTransaction(
      dbClient, "person", "club", MembershipConstants.OWNER);
    ReadContext readContext = dbClient.singleUse();
    Boolean actual = StorageHandlerHelper.checkOwnership(readContext, "person", "club");
    assertTrue(actual);
  }

  @Test
  public void getDeleteMembershipTransaction() throws Exception {
    StorageHandlerTestHelper.insertPerson("person");
    StorageHandlerTestHelper.insertMembership("person", "club", MembershipConstants.MEMBER);
    StorageHandler.runDeleteMembershipTransaction(dbClient, "person", "club");
    ReadContext readContext = dbClient.singleUse();
    Boolean actual = StorageHandlerHelper.checkAnyMembership(readContext, "person", "club");
    assertFalse(actual);
  }
}
