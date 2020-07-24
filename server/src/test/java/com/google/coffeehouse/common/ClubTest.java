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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.Mutation;
import com.google.coffeehouse.storagehandler.StorageHandler;
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import com.google.coffeehouse.storagehandler.StorageHandlerTestHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit tests for {@link Club}.
 */
@RunWith(JUnit4.class)
public final class ClubTest {
  private static final String NAME = "Club Name";
  private static final String DESCRIPTION = "Club Description";
  private static final String ALT_DESCRIPTION = "New Club Description";
  private static final String CLUB_ID = "predetermined-identification-string";
  private static final String OWNER_ID = "predetermined-owner-identification-string";
  private static final String BOOK_TITLE = "Book Name";
  private static final String BOOK_ID = "predetermined-book-identification-string";
  private static final String ALT_BOOK_TITLE = "New Book Name";
  private static DatabaseClient dbClient;
  private List<String> testContentWarnings;
  private Club.Builder clubBuilder;
  private Book testBook;
  private Book altTestBook;
  private Map clubInfo;
  private Map bookInfo;

  @Mock private StorageHandlerApi handler;

  @Before
  public void setUp() {
    dbClient = StorageHandlerTestHelper.setUpHelper();
    StorageHandlerTestHelper.setUpClearDb();

    handler = spy(StorageHandlerApi.class);
    doAnswer(i -> {
      List<Mutation> called = (List<Mutation>) i.getArguments()[0];
      dbClient.write(called);
      return null;
    }).when(handler).writeMutations(anyListOf(Mutation.class));

    testContentWarnings = new ArrayList<>(Arrays.asList("1", "2"));

    clubInfo = new HashMap();
    bookInfo = new HashMap<String, String>();
    bookInfo.put(Book.TITLE_FIELD_NAME, BOOK_TITLE);
    bookInfo.put(Book.BOOK_ID_FIELD_NAME, BOOK_ID);

    testBook = Book.newBuilder()
                   .setTitle(BOOK_TITLE)
                   .setBookId(BOOK_ID)
                   .setStorageHandler(handler)
                   .build();
    altTestBook = Book.newBuilder()
                      .setTitle(ALT_BOOK_TITLE)
                      .setBookId(BOOK_ID)
                      .setStorageHandler(handler)
                      .build();
    clubBuilder = Club.newBuilder()
                      .setName(NAME)
                      .setCurrentBook(testBook)
                      .setOwnerId(OWNER_ID)
                      .setClubId(CLUB_ID)
                      .setStorageHandler(handler);
  }

  @After
  public void tearDown() {
    StorageHandlerTestHelper.setUpClearDb();
  }

  @Test
  public void save_insert() {
    Club c = clubBuilder.build();
    c.save();
    Club retrieved = StorageHandler.getClub(dbClient, CLUB_ID);
    assertEquals(c.getName(), retrieved.getName());
    assertEquals(c.getClubId(), retrieved.getClubId());
    assertEquals(c.getOwnerId(), retrieved.getOwnerId());
    assertEquals(c.getDescription(), retrieved.getDescription());
    assertEquals(c.getCurrentBook().getTitle(), retrieved.getCurrentBook().getTitle());
  }

  @Test
  public void save_update() {
    Club c = clubBuilder.setContentWarnings(testContentWarnings).build();
    c.save();
    c.setDescription(ALT_DESCRIPTION);
    c.save();

    Club retrieved = StorageHandler.getClub(dbClient, CLUB_ID);
    assertEquals(c.getName(), retrieved.getName());
    assertEquals(c.getClubId(), retrieved.getClubId());
    assertEquals(c.getOwnerId(), retrieved.getOwnerId());
    assertArrayEquals(c.getContentWarnings().toArray(new String[0]), 
                      retrieved.getContentWarnings().toArray(new String[0]));
    assertEquals(c.getDescription(), retrieved.getDescription());
    assertEquals(c.getCurrentBook().getTitle(), retrieved.getCurrentBook().getTitle());
  }

  @Test
  public void getName_exists() {
    Club c = clubBuilder.build();
    assertEquals(NAME, c.getName());
  }

  @Test
  public void getDescription_exists() {
    Club c = clubBuilder.setDescription(DESCRIPTION).build();
    assertEquals(DESCRIPTION, c.getDescription());
  }

  @Test
  public void getOwnerId_exists() {
    Club c = clubBuilder.build();
    assertEquals(OWNER_ID, c.getOwnerId());
  }

  @Test
  public void getCurrentBook_exists() {
    Club c = clubBuilder.build();
    assertEquals(testBook, c.getCurrentBook());
  }

  @Test
  public void getClubId_exists() {
    Club c = clubBuilder.build();
    assertEquals(CLUB_ID, c.getClubId());
  }

  @Test
  public void getContentWarnings_exists() {
    Club c = clubBuilder.setContentWarnings(testContentWarnings).build();
    assertEquals(testContentWarnings, c.getContentWarnings());
  }
  
  @Test
  public void setContentWarnings() {
    Club c = clubBuilder.setContentWarnings(testContentWarnings).build();
    c.setContentWarnings(new ArrayList<>());
    assertEquals(new ArrayList<>(), c.getContentWarnings());
  }

  @Test
  public void setDescription() {
    Club c = clubBuilder.build();
    c.setDescription(ALT_DESCRIPTION);
    assertEquals(ALT_DESCRIPTION, c.getDescription());
  }

  @Test
  public void setBook() {
    Club c = clubBuilder.build();
    c.setBook(altTestBook);
    assertEquals(altTestBook, c.getCurrentBook());
  }
  
  @Test 
  public void fromMap_invalidInput() {
    clubInfo.put(Club.CONTENT_WARNINGS_FIELD_NAME, testContentWarnings);
    assertThrows(IllegalStateException.class, () -> {
        Club.fromMap(clubInfo);
    });
  }

  @Test 
  public void fromMap_minimumValidInput() {
    clubInfo.put(Club.NAME_FIELD_NAME, NAME);
    clubInfo.put(Club.CURRENT_BOOK_FIELD_NAME, bookInfo);
    clubInfo.put(Club.CLUB_ID_FIELD_NAME, CLUB_ID);
    clubInfo.put(Club.OWNER_ID_FIELD_NAME, OWNER_ID);
    Club c = Club.fromMap(clubInfo);
    assertEquals(NAME, c.getName());
    assertEquals(CLUB_ID, c.getClubId());
    assertEquals(OWNER_ID, c.getOwnerId());
    assertEquals(new ArrayList<>(), c.getContentWarnings());
    assertEquals(BOOK_TITLE, c.getCurrentBook().getTitle());
  }

  @Test 
  public void fromMap_maximumValidInput() {
    clubInfo.put(Club.NAME_FIELD_NAME, NAME);
    clubInfo.put(Club.CONTENT_WARNINGS_FIELD_NAME, testContentWarnings);
    clubInfo.put(Club.CURRENT_BOOK_FIELD_NAME, bookInfo);
    clubInfo.put(Club.DESCRIPTION_FIELD_NAME, DESCRIPTION);
    clubInfo.put(Club.CLUB_ID_FIELD_NAME, CLUB_ID);
    clubInfo.put(Club.OWNER_ID_FIELD_NAME, OWNER_ID);

    Club c = Club.fromMap(clubInfo);
    assertEquals(NAME, c.getName());
    assertEquals(testContentWarnings, c.getContentWarnings());
    assertEquals(CLUB_ID, c.getClubId());
    assertEquals(OWNER_ID, c.getOwnerId());
    assertEquals(BOOK_TITLE, c.getCurrentBook().getTitle());
    assertEquals(DESCRIPTION, c.getDescription());
  }

  // TODO: test saving @linamontes10
}
