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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.coffeehouse.util.IdentifierGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  private static final String IDENTIFICATION_STRING = "predetermined-identification-string";
  private static final String BOOK_TITLE = "Book Name";
  private static final String ALT_BOOK_TITLE = "New Book Name";
  private List<String> testContentWarnings;
  private Club.Builder clubBuilder;
  private Book testBook;
  private Book altTestBook;
  private Map clubInfo;
  private Map bookInfo;

  @Mock private IdentifierGenerator idGen;

  @Before
  public void setUp() {
    testContentWarnings = new ArrayList<>(Arrays.asList("1", "2"));

    clubInfo = new HashMap();
    bookInfo = new HashMap<String, String>();
    bookInfo.put(Book.TITLE_FIELD_NAME, BOOK_TITLE);
    
    idGen = mock(IdentifierGenerator.class);
    when(idGen.generateId()).thenReturn(IDENTIFICATION_STRING);

    testBook = Book.newBuilder(BOOK_TITLE).build();
    altTestBook = Book.newBuilder(ALT_BOOK_TITLE).build();
    clubBuilder = Club.newBuilder(NAME, testBook);
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
  public void getCurrentBook_exists() {
    Club c = clubBuilder.build();
    assertEquals(testBook, c.getCurrentBook());
  }

  @Test
  public void getClubId_exists() {
    Club c = clubBuilder.setIdGenerator(idGen).build();
    assertEquals(IDENTIFICATION_STRING, c.getClubId());
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
    clubInfo.put(Club.CONTENTWARNINGS_FIELD_NAME, testContentWarnings);
    assertThrows(IllegalArgumentException.class, () -> {
        Club.fromMap(clubInfo);
    });
  }

  @Test 
  public void fromMap_minimumValidInput() {
    clubInfo.put(Club.NAME_FIELD_NAME, NAME);
    clubInfo.put(Club.CURRENTBOOK_FIELD_NAME, bookInfo);
    Club c = Club.fromMap(clubInfo);
    assertEquals(NAME, c.getName());
    assertEquals(new ArrayList<>(), c.getContentWarnings());
    assertEquals(BOOK_TITLE, c.getCurrentBook().getTitle());
  }

  @Test 
  public void fromMap_maximumValidInput() {
    clubInfo.put(Club.NAME_FIELD_NAME, NAME);
    clubInfo.put(Club.CONTENTWARNINGS_FIELD_NAME, testContentWarnings);
    clubInfo.put(Club.CURRENTBOOK_FIELD_NAME, bookInfo);
    clubInfo.put(Club.DESCRIPTION_FIELD_NAME, DESCRIPTION);

    Club c = Club.fromMap(clubInfo, idGen);
    assertEquals(NAME, c.getName());
    assertEquals(testContentWarnings, c.getContentWarnings());
    assertEquals(BOOK_TITLE, c.getCurrentBook().getTitle());
    assertEquals(DESCRIPTION, c.getDescription());
    assertEquals(IDENTIFICATION_STRING, c.getClubId());
  }

  // TODO: test saving @linamontes10
}
