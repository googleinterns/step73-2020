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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.coffeehouse.util.IdentifierGenerator;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.function.ThrowingRunnable;
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
  private static final String IDENTIFICATION_STRING = "predetermined-identification-string";
  private Book.Builder bookBuilder;
  private Map bookInfo;

  @Mock private IdentifierGenerator idGen;

  @Before
  public void setUp() {
    bookInfo = new HashMap<String, String>();

    idGen = mock(IdentifierGenerator.class);
    when(idGen.generateId()).thenReturn(IDENTIFICATION_STRING);

    bookBuilder = Book.newBuilder(TITLE);
  }

  @Test
  public void getTitle_exists() {
    Book b = bookBuilder.build();
    Assert.assertEquals(TITLE, b.getTitle());
  }

  @Test
  public void getBookId_exists() {
    Book b = bookBuilder.setIdGenerator(idGen).build();
    Assert.assertEquals(IDENTIFICATION_STRING, b.getBookId());
  }

  @Test
  public void getAuthor_exists() {
    Book b = bookBuilder.setAuthor(AUTHOR).build();
    Assert.assertTrue(b.getAuthor().isPresent());
    Assert.assertEquals(AUTHOR, b.getAuthor().get());
  }

  @Test
  public void getIsbn_exists() {
    Book b = bookBuilder.setIsbn(ISBN).build();
    Assert.assertTrue(b.getIsbn().isPresent());
    Assert.assertEquals(ISBN, b.getIsbn().get());
  }

  @Test
  public void getAuthor_notExist() {
    Book b = bookBuilder.build();
    Assert.assertFalse(b.getAuthor().isPresent());
  }

  @Test
  public void getIsbn_notExist() {
    Book b = bookBuilder.build();
    Assert.assertFalse(b.getIsbn().isPresent());
  }

  @Test
  public void setTitle() {
    Book b = bookBuilder.build();
    b.setTitle(ALT_TITLE);
    Assert.assertEquals(ALT_TITLE, b.getTitle());
  }

  @Test
  public void setAuthor() {
    Book b = bookBuilder.build();
    b.setAuthor(ALT_AUTHOR);
    Assert.assertTrue(b.getAuthor().isPresent());
    Assert.assertEquals(ALT_AUTHOR, b.getAuthor().get());
  }

  @Test
  public void setIsbn() {
    Book b = bookBuilder.build();
    b.setIsbn(ALT_ISBN);
    Assert.assertTrue(b.getIsbn().isPresent());
    Assert.assertEquals(ALT_ISBN, b.getIsbn().get());
  }

  @Test 
  public void fromMap_invalidInput() {
    bookInfo.put(Book.ISBN_FIELD_NAME, ISBN);
    Assert.assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
        @Override
        public void run() throws Throwable {
          Book.fromMap(bookInfo);
        }
    });
  }

  @Test 
  public void fromMap_minimumValidMap() {
    bookInfo.put(Book.TITLE_FIELD_NAME, TITLE);
    Book b = Book.fromMap(bookInfo);
    Assert.assertEquals(TITLE, b.getTitle());
    Assert.assertFalse(b.getIsbn().isPresent());
    Assert.assertFalse(b.getAuthor().isPresent());
  }

  @Test 
  public void fromMap_maximumValidInput() {
    bookInfo.put(Book.TITLE_FIELD_NAME, TITLE);
    bookInfo.put(Book.ISBN_FIELD_NAME, ISBN);
    bookInfo.put(Book.AUTHOR_FIELD_NAME, AUTHOR);
    Book b = Book.fromMap(bookInfo, idGen);
    Assert.assertEquals(TITLE, b.getTitle());
    Assert.assertTrue(b.getIsbn().isPresent());
    Assert.assertEquals(ISBN, b.getIsbn().get());
    Assert.assertTrue(b.getAuthor().isPresent());
    Assert.assertEquals(AUTHOR, b.getAuthor().get());
    Assert.assertEquals(IDENTIFICATION_STRING, b.getBookId());
  }

  // TODO: test saving @linamontes10
}
