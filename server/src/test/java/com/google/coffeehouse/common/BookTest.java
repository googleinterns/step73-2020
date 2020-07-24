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
 * Unit tests for {@link Book}.
 */
@RunWith(JUnit4.class)
public final class BookTest {
  private static final String TITLE = "Book Title";
  private static final String ALT_TITLE = "Alternate Book Title";
  private static final String AUTHOR = "Book Author";
  private static final String ALT_AUTHOR = "Alternate Book Author";
  private static final String ISBN = "978-3-16-148410-0";
  private static final String ALT_ISBN = "111-1-11-111111-1";
  private static final String BOOK_ID = "predetermined-identification-string";
  private Book.Builder bookBuilder;
  private Map bookInfo;
  private static DatabaseClient dbClient;

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

    bookInfo = new HashMap<String, String>();
    bookBuilder = Book.newBuilder()
                      .setTitle(TITLE)
                      .setBookId(BOOK_ID)
                      .setStorageHandler(handler);
  }
  
  @After
  public void tearDown() {
    StorageHandlerTestHelper.setUpClearDb();
  }

  @Test
  public void save_insert() {
    Book b = bookBuilder.build();
    b.save();
    Book retrieved = StorageHandler.getBook(dbClient, BOOK_ID);
    assertEquals(b.getTitle(), retrieved.getTitle());
    assertEquals(b.getBookId(), retrieved.getBookId());
    assertEquals(b.getAuthor().isPresent(), retrieved.getAuthor().isPresent());
    assertEquals(b.getIsbn().isPresent(), retrieved.getIsbn().isPresent());
  }

  @Test
  public void save_update() {
    Book b = bookBuilder.build();
    b.save();
    b.setAuthor(ALT_AUTHOR);
    b.setIsbn(ALT_ISBN);
    b.save();
    Book retrieved = StorageHandler.getBook(dbClient, BOOK_ID);
    assertEquals(b.getTitle(), retrieved.getTitle());
    assertEquals(b.getBookId(), retrieved.getBookId());
    assertEquals(b.getAuthor().get(), retrieved.getAuthor().get());
    assertEquals(b.getIsbn().get(), retrieved.getIsbn().get());
  }

  @Test
  public void getTitle_exists() {
    Book b = bookBuilder.build();
    assertEquals(TITLE, b.getTitle());
  }

  @Test
  public void getBookId_exists() {
    Book b = bookBuilder.build();
    assertEquals(BOOK_ID, b.getBookId());
  }

  @Test
  public void getAuthor_exists() {
    Book b = bookBuilder.setAuthor(AUTHOR).build();
    assertTrue(b.getAuthor().isPresent());
    assertEquals(AUTHOR, b.getAuthor().get());
  }

  @Test
  public void getIsbn_exists() {
    Book b = bookBuilder.setIsbn(ISBN).build();
    assertTrue(b.getIsbn().isPresent());
    assertEquals(ISBN, b.getIsbn().get());
  }

  @Test
  public void getAuthor_notExist() {
    Book b = bookBuilder.build();
    assertFalse(b.getAuthor().isPresent());
  }

  @Test
  public void getIsbn_notExist() {
    Book b = bookBuilder.build();
    assertFalse(b.getIsbn().isPresent());
  }

  @Test
  public void setTitle() {
    Book b = bookBuilder.build();
    b.setTitle(ALT_TITLE);
    assertEquals(ALT_TITLE, b.getTitle());
  }

  @Test
  public void setAuthor() {
    Book b = bookBuilder.build();
    b.setAuthor(ALT_AUTHOR);
    assertTrue(b.getAuthor().isPresent());
    assertEquals(ALT_AUTHOR, b.getAuthor().get());
  }

  @Test
  public void setIsbn() {
    Book b = bookBuilder.build();
    b.setIsbn(ALT_ISBN);
    assertTrue(b.getIsbn().isPresent());
    assertEquals(ALT_ISBN, b.getIsbn().get());
  }

  @Test 
  public void fromMap_invalidInput() {
    bookInfo.put(Book.ISBN_FIELD_NAME, ISBN);
    assertThrows(IllegalStateException.class, () -> {
        Book.fromMap(bookInfo);
    });
  }

  @Test 
  public void fromMap_minimumValidMap() {
    bookInfo.put(Book.TITLE_FIELD_NAME, TITLE);
    bookInfo.put(Book.BOOK_ID_FIELD_NAME, BOOK_ID);
    Book b = Book.fromMap(bookInfo);
    assertEquals(TITLE, b.getTitle());
    assertEquals(BOOK_ID, b.getBookId());
    assertFalse(b.getIsbn().isPresent());
    assertFalse(b.getAuthor().isPresent());
  }

  @Test 
  public void fromMap_maximumValidInput() {
    bookInfo.put(Book.TITLE_FIELD_NAME, TITLE);
    bookInfo.put(Book.ISBN_FIELD_NAME, ISBN);
    bookInfo.put(Book.AUTHOR_FIELD_NAME, AUTHOR);
    bookInfo.put(Book.BOOK_ID_FIELD_NAME, BOOK_ID);
    Book b = Book.fromMap(bookInfo);
    assertEquals(TITLE, b.getTitle());
    assertTrue(b.getIsbn().isPresent());
    assertEquals(ISBN, b.getIsbn().get());
    assertTrue(b.getAuthor().isPresent());
    assertEquals(AUTHOR, b.getAuthor().get());
    assertEquals(BOOK_ID, b.getBookId());
  }

  // TODO: test saving @linamontes10
}
