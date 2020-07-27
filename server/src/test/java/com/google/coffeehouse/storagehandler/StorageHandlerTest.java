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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
      "person", /* pronouns= */ true);
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
      "personWithNullPronouns", /* pronouns= */ false);
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
                                                /* isbnExists= */ true,
                                                 /* authorExists= */ true);
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
                                                /* isbnExists= */ false,
                                                 /* authorExists= */ true);
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
                                                /* isbnExists= */ true,
                                                 /* authorExists= */ false);
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
                                                /* isbnExists= */ false,
                                                 /* authorExists= */ false);
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
      "clubWithContentWarnings", /* contentWarnings= */ true);
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
      "clubWithNoContentWarnings", /* contentWarnings= */ false);
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
  public void runDeleteMembershipTransaction_member() throws Exception {
    StorageHandlerTestHelper.insertPerson("person");
    StorageHandlerTestHelper.insertMembership("person", "club", MembershipConstants.MEMBER);
    StorageHandler.runDeleteMembershipTransaction(dbClient, "person", "club");
    ReadContext readContext = dbClient.singleUse();
    Boolean actual = StorageHandlerHelper.checkAnyMembership(readContext, "person", "club");
    assertFalse(actual);
  }

  @Test
  public void runDeleteMembershipTransaction_ownerFailsToLeaveClub() throws Exception {
    StorageHandlerTestHelper.insertPerson("person");
    StorageHandlerTestHelper.insertMembership("person", "club", MembershipConstants.OWNER);
    ReadContext readContext = dbClient.singleUse();
    assertThrows(RuntimeException.class, () -> {
      StorageHandler.runDeleteMembershipTransaction(dbClient, "person", "club");
    });
  }

  @Test
  public void getListOfMembers_clubDoesNotExistInDb() throws Exception {
    assertThrows(RuntimeException.class, () -> {
      StorageHandler.getPerson(dbClient, "club");
    });
  }

  @Test
  public void getListOfMembers_none() throws Exception {
    StorageHandlerTestHelper.insertClub("club", /* owner_id= */ "owner");
    assertThrows(RuntimeException.class, () -> {
      StorageHandler.getListOfMembers(dbClient, "club");
    });
  }

  @Test
  public void getListOfMembers_oneOwner() throws Exception {
    List<Person> expected = new ArrayList<Person>();
    expected.add(StorageHandlerTestHelper.createTestPersonObject("owner", /* pronouns= */ true));

    StorageHandlerTestHelper.insertPerson("owner");
    StorageHandlerTestHelper.insertClub("club", /* owner_id= */ "owner");
    StorageHandlerTestHelper.insertMembership("owner", "club", MembershipConstants.OWNER);
    List<Person> actual =  new ArrayList<Person>(StorageHandler.getListOfMembers(dbClient, "club"));

    assertEquals(1, actual.size());
    assertEquals(expected.get(0).getNickname(), actual.get(0).getNickname());
    assertEquals(expected.get(0).getEmail(), actual.get(0).getEmail());
    assertEquals(expected.get(0).getUserId(), actual.get(0).getUserId());
    assertEquals(expected.get(0).getPronouns().get(), actual.get(0).getPronouns().get());
  }

  @Test
  public void getListOfMembers_ownerAndMembers() throws Exception {
    List<Person> expected = new ArrayList<Person>();
    expected.add(StorageHandlerTestHelper.createTestPersonObject("member", /* pronouns= */ true));
    expected.add(StorageHandlerTestHelper.createTestPersonObject("owner", /* pronouns= */ true));

    StorageHandlerTestHelper.insertPerson("owner");
    StorageHandlerTestHelper.insertClub("club", /* owner_id= */ "owner");
    StorageHandlerTestHelper.insertMembership("owner", "club", MembershipConstants.OWNER);

    StorageHandlerTestHelper.insertPerson("member");
    StorageHandlerTestHelper.insertMembership("member", "club", MembershipConstants.MEMBER);
    List<Person> actual =  new ArrayList<Person>(StorageHandler.getListOfMembers(dbClient, "club"));

    assertEquals(2, actual.size());
    assertEquals(expected.size(), actual.size());

    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i).getNickname(), actual.get(i).getNickname());
      assertEquals(expected.get(i).getEmail(), actual.get(i).getEmail());
      assertEquals(expected.get(i).getUserId(), actual.get(i).getUserId());
      assertEquals(expected.get(i).getPronouns().get(), actual.get(i).getPronouns().get());
    }
  }

  @Test
  public void getListOfClubs_memberOfAll() throws Exception {
    List<Club> expected = new ArrayList<Club>();
    expected.add(StorageHandlerTestHelper.createTestClubObject(
      "clubs1", /* contentWarning= */ true));
    expected.add(StorageHandlerTestHelper.createTestClubObject(
      "clubs2", /* contentWarning= */ true));
    expected.add(StorageHandlerTestHelper.createTestClubObject(
      "clubs3", /* contentWarning= */ true));

    StorageHandlerTestHelper.insertPerson("member");
    StorageHandlerTestHelper.insertClub("clubs1", /* owner_id= */ "owner");
    StorageHandlerTestHelper.insertClub("clubs2", /* owner_id= */ "owner");
    StorageHandlerTestHelper.insertClub("clubs3", /* owner_id= */ "owner");
    StorageHandlerTestHelper.insertMembership("member", "clubs1", MembershipConstants.MEMBER);
    StorageHandlerTestHelper.insertMembership("member", "clubs2", MembershipConstants.MEMBER);
    StorageHandlerTestHelper.insertMembership("member", "clubs3", MembershipConstants.MEMBER);
    StorageHandlerTestHelper.insertBook("book");
    
    List<Club> actual =  new ArrayList<Club>(StorageHandler.getListOfClubs(dbClient, "member",
                                             MembershipConstants.MembershipStatus.MEMBER));

    assertEquals(3, actual.size());
    assertEquals(expected.size(), actual.size());
    for (int i =0; i < actual.size(); i++) {
      assertEquals(actual.get(i).getName(), expected.get(i).getName());
      assertEquals(actual.get(i).getOwnerId(), expected.get(i).getOwnerId());
      assertEquals(actual.get(i).getClubId(), expected.get(i).getClubId());
      assertEquals(actual.get(i).getDescription(), expected.get(i).getDescription());
      assertEquals(actual.get(i).getCurrentBook().getTitle(), expected.get(i).getCurrentBook().getTitle());
    }
  }

  @Test
  public void getListOfClubs_memberOfOne() throws Exception {
    List<Club> expected = new ArrayList<Club>();
    expected.add(StorageHandlerTestHelper.createTestClubObject("club", /* contentWarning= */ true));
    StorageHandlerTestHelper.insertPerson("member");
    StorageHandlerTestHelper.insertPerson("owner");
    StorageHandlerTestHelper.insertClub("club", /* owner_id= */ "owner");
    StorageHandlerTestHelper.insertMembership("owner", "club", MembershipConstants.OWNER);
    StorageHandlerTestHelper.insertBook("book");
    StorageHandlerTestHelper.insertMembership("member", "club", MembershipConstants.MEMBER);
    List<Club> actual =  new ArrayList<Club>(StorageHandler.getListOfClubs(dbClient, "member",
                                             MembershipConstants.MembershipStatus.MEMBER));
    assertEquals(1, actual.size());
    assertEquals(actual.get(0).getName(), expected.get(0).getName());
    assertEquals(actual.get(0).getOwnerId(), expected.get(0).getOwnerId());
    assertEquals(actual.get(0).getClubId(), expected.get(0).getClubId());
    assertEquals(actual.get(0).getDescription(), expected.get(0).getDescription());
    assertEquals(actual.get(0).getCurrentBook().getTitle(), expected.get(0).getCurrentBook().getTitle());
  }

  @Test
  public void getListOfClubs_memberOfNone() throws Exception {
    List<Club> expected = new ArrayList<Club>();
    expected.add(StorageHandlerTestHelper.createTestClubObject(
      "clubs1", /* contentWarning= */ true));
    expected.add(StorageHandlerTestHelper.createTestClubObject(
      "clubs2", /* contentWarning= */ true));
    expected.add(StorageHandlerTestHelper.createTestClubObject(
      "clubs3", /* contentWarning= */ true));
    StorageHandlerTestHelper.insertPerson("non-member");
    StorageHandlerTestHelper.insertPerson("owner");    
    StorageHandlerTestHelper.insertClub("clubs1", /* owner_id= */ "owner");
    StorageHandlerTestHelper.insertClub("clubs2", /* owner_id= */ "owner");
    StorageHandlerTestHelper.insertClub("clubs3", /* owner_id= */ "owner");
    StorageHandlerTestHelper.insertBook("book");
    List<Club> actual =  new ArrayList<Club>(StorageHandler.getListOfClubs(dbClient, "non-member",
                                             MembershipConstants.MembershipStatus.NOT_MEMBER));
    assertEquals(3, actual.size());
    assertEquals(expected.size(), actual.size());
    for (int i =0; i < actual.size(); i++) {
      assertEquals(actual.get(i).getName(), expected.get(i).getName());
      assertEquals(actual.get(i).getOwnerId(), expected.get(i).getOwnerId());
      assertEquals(actual.get(i).getClubId(), expected.get(i).getClubId());
      assertEquals(actual.get(i).getDescription(), expected.get(i).getDescription());
      assertEquals(actual.get(i).getCurrentBook().getTitle(), expected.get(i).getCurrentBook().getTitle());
    }
  }

  @Test
  public void getListOfClubs_notMemberWhenMemberOfAllExistingClubs() throws Exception {
    StorageHandlerTestHelper.insertPerson("member");
    StorageHandlerTestHelper.insertPerson("owner");    
    StorageHandlerTestHelper.insertClub("clubs1", /* owner_id= */ "owner");
    StorageHandlerTestHelper.insertClub("clubs2", /* owner_id= */ "owner");
    StorageHandlerTestHelper.insertClub("clubs3", /* owner_id= */ "owner");
    StorageHandlerTestHelper.insertBook("book");
    StorageHandlerTestHelper.insertMembership("member", "clubs1", MembershipConstants.MEMBER);
    StorageHandlerTestHelper.insertMembership("member", "clubs2", MembershipConstants.MEMBER);
    StorageHandlerTestHelper.insertMembership("member", "clubs3", MembershipConstants.MEMBER);
    List<Club> actual =  new ArrayList<Club>(StorageHandler.getListOfClubs(dbClient, "member",
                                             MembershipConstants.MembershipStatus.NOT_MEMBER));
    assertEquals(0, actual.size());
  }

  @Test
  public void getListOfClubs_memberWhenNotMemberOfAllExistingClubs() throws Exception {
    StorageHandlerTestHelper.insertPerson("member");
    StorageHandlerTestHelper.insertPerson("owner");    
    StorageHandlerTestHelper.insertClub("clubs1", /* owner_id= */ "owner");
    StorageHandlerTestHelper.insertClub("clubs2", /* owner_id= */ "owner");
    StorageHandlerTestHelper.insertClub("clubs3", /* owner_id= */ "owner");
    StorageHandlerTestHelper.insertBook("book");
    List<Club> actual =  new ArrayList<Club>(StorageHandler.getListOfClubs(dbClient, "member",
                                             MembershipConstants.MembershipStatus.MEMBER));
    assertEquals(0, actual.size());
  }
}
