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

package com.google.coffeehouse.servlets;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.coffeehouse.common.Club;
import com.google.coffeehouse.common.Book;
import com.google.coffeehouse.common.Person;
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import com.google.coffeehouse.util.AuthenticationHelper;
import com.google.coffeehouse.util.TokenVerifier;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit tests for {@link UpdateClubServlet}.
 */
public class UpdateClubServletTest {
  private static final String NAME = "Club Name";
  private static final String ALT_NAME = "New Club Name";
  private static final String DESCRIPTION = "Club Description";
  private static final String ALT_DESCRIPTION = "New Club Description";
  private static final String CLUB_ID = "predetermined-identification-string";
  private static final String OWNER_ID = "predetermined-owner-identification-string";
  private static final String ALT_OWNER_ID = "New predetermined-owner-identification-string";
  private static final String TITLE = "Book Title";
  private static final String ALT_TITLE = "Alternate Book Title";
  private static final String AUTHOR = "Book Author";
  private static final String ALT_AUTHOR = "Alternate Book Author";
  private static final String ISBN = "978-3-16-148410-0";
  private static final String ALT_ISBN = "111-1-11-111111-1";
  private static final String BOOK_ID = "predetermined-identification-string";
  private static final String ID_TOKEN = "Identification Token";
  private static final List<String> testContentWarnings = new ArrayList<>(Arrays.asList("1", "2"));
  private static final Book testBook = Book.newBuilder()
                                           .setTitle(TITLE)
                                           .setAuthor(AUTHOR)
                                           .setIsbn(ISBN)
                                           .setBookId(BOOK_ID)
                                           .build();
  private static final Club testClub = Club.newBuilder()
                                           .setName(NAME)
                                           .setCurrentBook(testBook)
                                           .setOwnerId(OWNER_ID)
                                           .setClubId(CLUB_ID)
                                           .setContentWarnings(testContentWarnings)
                                           .setDescription(DESCRIPTION)
                                           .build();
  private static final String MASK_PARTIAL_UPDATE = String.join("\n",
      "{",
      "  \"" + UpdateClubServlet.UPDATE_MASK_FIELD_NAME + "\" : \"" +
      Club.DESCRIPTION_FIELD_NAME + "\",",
      "  \"" + UpdateClubServlet.ID_TOKEN_FIELD_NAME + "\" : \"" + ID_TOKEN + "\",",
      "  \"" + UpdateClubServlet.CLUB_FIELD_NAME + "\" : {",
      "    \"" + Club.NAME_FIELD_NAME + "\" : \"" + ALT_NAME + "\",",
      "    \"" + Club.OWNER_ID_FIELD_NAME + "\" : \"" + OWNER_ID + "\",",
      "    \"" + Club.CLUB_ID_FIELD_NAME + "\" : \"" + CLUB_ID + "\",",
      "    \"" + Club.DESCRIPTION_FIELD_NAME + "\" : \"" + ALT_DESCRIPTION + "\",",
      "    \"" + Club.CONTENT_WARNINGS_FIELD_NAME + "\" : [\"3\",\"4\"],",
      "    \"" + Club.CURRENT_BOOK_FIELD_NAME + "\" : {",
      "      \"" + Book.BOOK_ID_FIELD_NAME + "\" : \"" + BOOK_ID + "\",",
      "      \"" + Book.TITLE_FIELD_NAME + "\" : \"" + ALT_TITLE + "\",",
      "      \"" + Book.AUTHOR_FIELD_NAME + "\" : \"" + ALT_AUTHOR + "\",",
      "      \"" + Book.ISBN_FIELD_NAME + "\" : \"" + ALT_ISBN + "\"",
      "    }",
      "  }",
      "}");
  private static final String MASK_BOOK_UPDATE = String.join("\n",
      "{",
      "  \"" + UpdateClubServlet.UPDATE_MASK_FIELD_NAME + "\" : \"" +
      Club.CURRENT_BOOK_FIELD_NAME + "." + Book.TITLE_FIELD_NAME + "\",",
      "  \"" + UpdateClubServlet.ID_TOKEN_FIELD_NAME + "\" : \"" + ID_TOKEN + "\",",
      "  \"" + UpdateClubServlet.CLUB_FIELD_NAME + "\" : {",
      "    \"" + Club.NAME_FIELD_NAME + "\" : \"" + ALT_NAME + "\",",
      "    \"" + Club.OWNER_ID_FIELD_NAME + "\" : \"" + OWNER_ID + "\",",
      "    \"" + Club.CLUB_ID_FIELD_NAME + "\" : \"" + CLUB_ID + "\",",
      "    \"" + Club.DESCRIPTION_FIELD_NAME + "\" : \"" + ALT_DESCRIPTION + "\",",
      "    \"" + Club.CONTENT_WARNINGS_FIELD_NAME + "\" : [\"3\",\"4\"],",
      "    \"" + Club.CURRENT_BOOK_FIELD_NAME + "\" : {",
      "      \"" + Book.BOOK_ID_FIELD_NAME + "\" : \"" + BOOK_ID + "\",",
      "      \"" + Book.TITLE_FIELD_NAME + "\" : \"" + ALT_TITLE + "\",",
      "      \"" + Book.AUTHOR_FIELD_NAME + "\" : \"" + ALT_AUTHOR + "\",",
      "      \"" + Book.ISBN_FIELD_NAME + "\" : \"" + ALT_ISBN + "\"",
      "    }",
      "  }",
      "}");
  private static final String NO_MASK = String.join("\n",
      "{",
      "  \"" + UpdateClubServlet.ID_TOKEN_FIELD_NAME + "\" : \"" + ID_TOKEN + "\",",
      "  \"" + UpdateClubServlet.CLUB_FIELD_NAME + "\" : {",
      "    \"" + Club.NAME_FIELD_NAME + "\" : \"" + ALT_NAME + "\",",
      "    \"" + Club.OWNER_ID_FIELD_NAME + "\" : \"" + OWNER_ID + "\",",
      "    \"" + Club.CLUB_ID_FIELD_NAME + "\" : \"" + CLUB_ID + "\",",
      "    \"" + Club.DESCRIPTION_FIELD_NAME + "\" : \"" + ALT_DESCRIPTION + "\",",
      "    \"" + Club.CONTENT_WARNINGS_FIELD_NAME + "\" : [\"3\",\"4\"],",
      "    \"" + Club.CURRENT_BOOK_FIELD_NAME + "\" : {",
      "      \"" + Book.BOOK_ID_FIELD_NAME + "\" : \"" + BOOK_ID + "\",",
      "      \"" + Book.TITLE_FIELD_NAME + "\" : \"" + ALT_TITLE + "\",",
      "      \"" + Book.AUTHOR_FIELD_NAME + "\" : \"" + ALT_AUTHOR + "\",",
      "      \"" + Book.ISBN_FIELD_NAME + "\" : \"" + ALT_ISBN + "\"",
      "    }",
      "  }",
      "}");
  private static final String NO_ID_TOKEN = String.join("\n",
      "{",
      "  \"" + UpdateClubServlet.UPDATE_MASK_FIELD_NAME + "\" : \"" +
      Club.DESCRIPTION_FIELD_NAME + "\",",
      "  \"" + UpdateClubServlet.CLUB_FIELD_NAME + "\" : {",
      "    \"" + Club.NAME_FIELD_NAME + "\" : \"" + ALT_NAME + "\",",
      "    \"" + Club.OWNER_ID_FIELD_NAME + "\" : \"" + OWNER_ID + "\",",
      "    \"" + Club.CLUB_ID_FIELD_NAME + "\" : \"" + CLUB_ID + "\",",
      "    \"" + Club.DESCRIPTION_FIELD_NAME + "\" : \"" + ALT_DESCRIPTION + "\",",
      "    \"" + Club.CONTENT_WARNINGS_FIELD_NAME + "\" : [\"3\",\"4\"],",
      "    \"" + Club.CURRENT_BOOK_FIELD_NAME + "\" : {",
      "      \"" + Book.BOOK_ID_FIELD_NAME + "\" : \"" + BOOK_ID + "\",",
      "      \"" + Book.TITLE_FIELD_NAME + "\" : \"" + ALT_TITLE + "\",",
      "      \"" + Book.AUTHOR_FIELD_NAME + "\" : \"" + ALT_AUTHOR + "\",",
      "      \"" + Book.ISBN_FIELD_NAME + "\" : \"" + ALT_ISBN + "\"",
      "    }",
      "  }",
      "}");
  private static final String NO_CLUB_ID = String.join("\n",
      "{",
      "  \"" + UpdateClubServlet.UPDATE_MASK_FIELD_NAME + "\" : \"" +
      Club.DESCRIPTION_FIELD_NAME + "\",",
      "  \"" + UpdateClubServlet.ID_TOKEN_FIELD_NAME + "\" : \"" + OWNER_ID + "\",",
      "  \"" + UpdateClubServlet.CLUB_FIELD_NAME + "\" : {",
      "    \"" + Club.NAME_FIELD_NAME + "\" : \"" + ALT_NAME + "\",",
      "    \"" + Club.OWNER_ID_FIELD_NAME + "\" : \"" + OWNER_ID + "\",",
      "    \"" + Club.DESCRIPTION_FIELD_NAME + "\" : \"" + ALT_DESCRIPTION + "\",",
      "    \"" + Club.CONTENT_WARNINGS_FIELD_NAME + "\" : [\"3\",\"4\"],",
      "    \"" + Club.CURRENT_BOOK_FIELD_NAME + "\" : {",
      "      \"" + Book.BOOK_ID_FIELD_NAME + "\" : \"" + BOOK_ID + "\",",
      "      \"" + Book.TITLE_FIELD_NAME + "\" : \"" + ALT_TITLE + "\",",
      "      \"" + Book.AUTHOR_FIELD_NAME + "\" : \"" + ALT_AUTHOR + "\",",
      "      \"" + Book.ISBN_FIELD_NAME + "\" : \"" + ALT_ISBN + "\"",
      "    }",
      "  }",
      "}");
  private static final String SYNTACTICALLY_INCORRECT_JSON = "{\"{";

  private UpdateClubServlet updateClubServlet;
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();
  private StringWriter stringWriter = new StringWriter();

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private StorageHandlerApi handler;
  @Mock private TokenVerifier correctVerifier;
  @Mock private TokenVerifier incorrectVerifier;
  @Mock private TokenVerifier nullVerifier;

  @Before
  public void setUp() throws IOException, GeneralSecurityException {
    helper.setUp();

    handler = spy(StorageHandlerApi.class);
    doNothing().when(handler).writeMutations(anyList());
    doReturn(testClub).when(handler).fetchClubFromId(anyString());

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

    // Verification setup that successfully verifies and gives correct userId.
    correctVerifier = mock(TokenVerifier.class);
    when(correctVerifier.getSubject(anyString())).thenReturn(OWNER_ID);

    // Verification setup that successfully verifies and gives incorrect userId.
    incorrectVerifier = mock(TokenVerifier.class);
    when(incorrectVerifier.getSubject(anyString())).thenReturn(ALT_OWNER_ID);

    // Verification setup that does not successfully verify.
    nullVerifier = mock(TokenVerifier.class);
    when(nullVerifier.getSubject(anyString())).thenReturn(null);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doPost_validInputWithMask() throws IOException {
    when(request.getReader()).thenReturn(
        new BufferedReader(new StringReader(MASK_PARTIAL_UPDATE)));
    updateClubServlet = new UpdateClubServlet(correctVerifier, handler);
    updateClubServlet.doPost(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Club c = gson.fromJson(result, Club.class);    

    assertEquals(NAME, c.getName());
    assertEquals(ALT_DESCRIPTION, c.getDescription());
    assertEquals(testContentWarnings, c.getContentWarnings());
    assertEquals(CLUB_ID, c.getClubId());
    assertEquals(OWNER_ID, c.getOwnerId());
    assertEquals(TITLE, c.getCurrentBook().getTitle());
    assertEquals(AUTHOR, c.getCurrentBook().getAuthor().get());
    assertEquals(ISBN, c.getCurrentBook().getIsbn().get());
    assertEquals(BOOK_ID, c.getCurrentBook().getBookId());
  }

  @Test
  public void doPost_validInputWithNoMask() throws IOException {
    when(request.getReader()).thenReturn(
        new BufferedReader(new StringReader(NO_MASK)));
    updateClubServlet = new UpdateClubServlet(correctVerifier, handler);
    updateClubServlet.doPost(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Club c = gson.fromJson(result, Club.class);    

    assertEquals(NAME, c.getName());
    assertEquals(ALT_DESCRIPTION, c.getDescription());
    assertEquals(Arrays.asList("3", "4"), c.getContentWarnings());
    assertEquals(CLUB_ID, c.getClubId());
    assertEquals(OWNER_ID, c.getOwnerId());
    assertEquals(ALT_TITLE, c.getCurrentBook().getTitle());
    assertEquals(ALT_AUTHOR, c.getCurrentBook().getAuthor().get());
    assertEquals(ALT_ISBN, c.getCurrentBook().getIsbn().get());
    assertEquals(BOOK_ID, c.getCurrentBook().getBookId());
  }
  
  @Test
  public void doPost_validInputWithBookMask() throws IOException {
    when(request.getReader()).thenReturn(
        new BufferedReader(new StringReader(MASK_BOOK_UPDATE)));
    updateClubServlet = new UpdateClubServlet(correctVerifier, handler);
    updateClubServlet.doPost(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Club c = gson.fromJson(result, Club.class);    

    assertEquals(NAME, c.getName());
    assertEquals(DESCRIPTION, c.getDescription());
    assertEquals(testContentWarnings, c.getContentWarnings());
    assertEquals(CLUB_ID, c.getClubId());
    assertEquals(OWNER_ID, c.getOwnerId());
    assertEquals(ALT_TITLE, c.getCurrentBook().getTitle());
    assertEquals(AUTHOR, c.getCurrentBook().getAuthor().get());
    assertEquals(ISBN, c.getCurrentBook().getIsbn().get());
    assertEquals(BOOK_ID, c.getCurrentBook().getBookId());
  }

  @Test
  public void doPost_noIdToken() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_ID_TOKEN)));
    updateClubServlet = new UpdateClubServlet(correctVerifier, handler);
    updateClubServlet.doPost(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_FORBIDDEN, AuthenticationHelper.INVALID_ID_TOKEN_ERROR);
  }

  @Test
  public void doPost_wrongUserId() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_MASK)));
    updateClubServlet = new UpdateClubServlet(incorrectVerifier, handler);
    updateClubServlet.doPost(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_FORBIDDEN, UpdateClubServlet.LACK_OF_PRIVILEGE_ERROR);
  }

  @Test
  public void doPost_noClubId() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_CLUB_ID)));
    updateClubServlet = new UpdateClubServlet(correctVerifier, handler);
    updateClubServlet.doPost(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST,
        String.format(UpdateClubServlet.NO_FIELD_ERROR, Club.CLUB_ID_FIELD_NAME));
  }
  
  @Test
  public void doPost_syntacticallyIncorrectInput() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(SYNTACTICALLY_INCORRECT_JSON)));
    updateClubServlet = new UpdateClubServlet(correctVerifier, handler);
    updateClubServlet.doPost(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, UpdateClubServlet.BODY_ERROR);
  }

  @Test
  public void doPost_failVerification() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_MASK)));
    updateClubServlet = new UpdateClubServlet(nullVerifier, handler);
    updateClubServlet.doPost(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_FORBIDDEN, AuthenticationHelper.INVALID_ID_TOKEN_ERROR);
  }
}
