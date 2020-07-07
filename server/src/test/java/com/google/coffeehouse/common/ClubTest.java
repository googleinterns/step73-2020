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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.function.ThrowingRunnable;
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
  public void getExistingName() {
    Club c = clubBuilder.build();
    Assert.assertEquals(NAME, c.getName());
  }

  @Test
  public void getExistingDescription() {
    Club c = clubBuilder.setDescription(DESCRIPTION).build();
    Assert.assertEquals(DESCRIPTION, c.getDescription());
  }

  @Test
  public void getExistingCurrentBook() {
    Club c = clubBuilder.build();
    Assert.assertEquals(testBook, c.getCurrentBook());
  }

  @Test
  public void getExistingClubId() {
    Club c = clubBuilder.setIdGenerator(idGen).build();
    Assert.assertEquals(IDENTIFICATION_STRING, c.getClubId());
  }

  @Test
  public void getExistingContentWarnings() {
    Club c = clubBuilder.setContentWarnings(testContentWarnings).build();
    Assert.assertEquals(testContentWarnings, c.getContentWarnings());
  }
  
  @Test
  public void setNewContentWarnings() {
    Club c = clubBuilder.setContentWarnings(testContentWarnings).build();
    c.setContentWarnings(new ArrayList<>());
    Assert.assertEquals(new ArrayList<>(), c.getContentWarnings());
  }

  @Test
  public void setNewDescription() {
    Club c = clubBuilder.build();
    c.setDescription(ALT_DESCRIPTION);
    Assert.assertEquals(ALT_DESCRIPTION, c.getDescription());
  }

  @Test
  public void setNewBook() {
    Club c = clubBuilder.build();
    c.setBook(altTestBook);
    Assert.assertEquals(altTestBook, c.getCurrentBook());
  }
  
  @Test 
  public void fromInvalidMap() {
    clubInfo.put(Club.CONTENTWARNINGS_FIELD_NAME, testContentWarnings);
    Assert.assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
        @Override
        public void run() throws Throwable {
          Club.fromMap(clubInfo);
        }
    });
  }

  @Test 
  public void fromMinimumValidMap() {
    clubInfo.put(Club.NAME_FIELD_NAME, NAME);
    clubInfo.put(Club.CURRENTBOOK_FIELD_NAME, bookInfo);
    Club c = Club.fromMap(clubInfo);
    Assert.assertEquals(NAME, c.getName());
    Assert.assertEquals(new ArrayList<>(), c.getContentWarnings());
    Assert.assertEquals(BOOK_TITLE, c.getCurrentBook().getTitle());
  }

  @Test 
  public void fromMaximumValidMap() {
    clubInfo.put(Club.NAME_FIELD_NAME, NAME);
    clubInfo.put(Club.CONTENTWARNINGS_FIELD_NAME, testContentWarnings);
    clubInfo.put(Club.CURRENTBOOK_FIELD_NAME, bookInfo);
    clubInfo.put(Club.DESCRIPTION_FIELD_NAME, DESCRIPTION);

    Club c = Club.fromMap(clubInfo, idGen);
    Assert.assertEquals(NAME, c.getName());
    Assert.assertEquals(testContentWarnings, c.getContentWarnings());
    Assert.assertEquals(BOOK_TITLE, c.getCurrentBook().getTitle());
    Assert.assertEquals(DESCRIPTION, c.getDescription());
    Assert.assertEquals(IDENTIFICATION_STRING, c.getClubId());
  }

  // TODO: test saving @linamontes10
}
