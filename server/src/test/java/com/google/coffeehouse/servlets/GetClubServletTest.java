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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.coffeehouse.common.Book;
import com.google.coffeehouse.common.Club;
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import com.google.coffeehouse.storagehandler.StorageHandler;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
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
 * Unit tests for {@link GetClubServlet}.
 */
public class GetClubServletTest {
  private static final String NAME = "Club Name";
  private static final String DESCRIPTION = "Club Description";
  private static final String CLUB_ID = "predetermined-identification-string";
  private static final String OWNER_ID = "predetermined-owner-identification-string";
  private static final String BOOK_TITLE = "Book Name";
  private static final String BOOK_ID = "predetermined-book-identification-string";
  private List<String> testContentWarnings = new ArrayList<>(Arrays.asList("1", "2"));
  private Book testBook = Book.newBuilder()
                              .setTitle(BOOK_TITLE)
                              .setBookId(BOOK_ID)
                              .build();
  private Club testClub = Club.newBuilder()
                              .setName(NAME)
                              .setCurrentBook(testBook)
                              .setOwnerId(OWNER_ID)
                              .setClubId(CLUB_ID)
                              .setDescription(DESCRIPTION)
                              .setContentWarnings(testContentWarnings)
                              .build();
  private static final String VALID_JSON = String.join("\n",
      "{",
      "  \"" + Club.CLUB_ID_FIELD_NAME + "\" : \"" + CLUB_ID + "\"",
      "}");
  private static final String NO_CLUB_ID_JSON = "{}";
  private static final String CLUB_NOT_FOUND_JSON = String.join("\n",
      "{",
      "  \"" + Club.CLUB_ID_FIELD_NAME + "\" : \"\"",
      "}");
  private static final String SYNTACTICALLY_INCORRECT_JSON =
      "{\"" + Club.CLUB_ID_FIELD_NAME + "\"";

  private GetClubServlet getClubServlet;
  private GetClubServlet failingGetClubServlet;
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();
  private StringWriter stringWriter = new StringWriter();

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private StorageHandlerApi successfulHandler;
  @Mock private StorageHandlerApi failingHandler;
  
  @Before
  public void setUp() throws IOException {
    helper.setUp();

    successfulHandler = mock(StorageHandlerApi.class);
    when(successfulHandler.fetchClubFromId(anyString())).thenReturn(testClub);
    getClubServlet = new GetClubServlet(successfulHandler);

    failingHandler = mock(StorageHandlerApi.class);
    when(failingHandler.fetchClubFromId(anyString()))
                       .thenThrow(new IllegalArgumentException(
                                      StorageHandler.CLUB_DOES_NOT_EXIST));
    failingGetClubServlet = new GetClubServlet(failingHandler);

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doGet_validInput() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(VALID_JSON)));

    getClubServlet.doGet(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Club c = gson.fromJson(result, Club.class);

    assertEquals(NAME, c.getName());
    assertEquals(OWNER_ID, c.getOwnerId());
    assertEquals(CLUB_ID, c.getClubId());
    assertEquals(DESCRIPTION, c.getDescription());
    assertEquals(testContentWarnings, c.getContentWarnings());
    assertEquals(BOOK_TITLE, c.getCurrentBook().getTitle());
    assertEquals(BOOK_ID, c.getCurrentBook().getBookId());
  }

  @Test
  public void doGet_noUserId() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_CLUB_ID_JSON)));
    getClubServlet.doGet(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST,
        String.format(GetClubServlet.LOG_INPUT_ERROR_MESSAGE, Club.CLUB_ID_FIELD_NAME));
  }

  @Test
  public void doGet_noClubFound() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(CLUB_NOT_FOUND_JSON)));
    failingGetClubServlet.doGet(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_NOT_FOUND,
        StorageHandler.CLUB_DOES_NOT_EXIST);
  }

  @Test
  public void doGet_syntacticallyIncorrectInput() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(SYNTACTICALLY_INCORRECT_JSON)));
    getClubServlet.doGet(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
        GetClubServlet.BODY_ERROR);
  }
}
