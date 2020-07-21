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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.coffeehouse.common.Book;
import com.google.coffeehouse.common.Club;
import com.google.coffeehouse.util.IdentifierGenerator;
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
 * Unit tests for {@link CreateClubServlet}.
 */
public class CreateClubServletTest {
  private static final String NAME = "Club Name";
  private static final String OWNER_ID = "predetermined-owner-identification-string";
  private static final String DESCRIPTION = "Club Description";
  private static final String IDENTIFICATION_STRING = "predetermined-identification-string";
  private static final String BOOK_TITLE = "Book Name";
  private static final String BOOK_AUTHOR = "Book Author";
  private static final String BOOK_ISBN = "978-3-16-148410-0";
  private static final String MINIMUM_JSON = String.join("\n", 
      "{",
      "  \"" + Club.NAME_FIELD_NAME + "\" : \"" + NAME + "\",",
      "  \"" + Club.OWNER_ID_FIELD_NAME + "\" : \"" + OWNER_ID + "\",",
      "  \"" + Club.CURRENT_BOOK_FIELD_NAME + "\" : {",
      "    \"" + Book.TITLE_FIELD_NAME + "\" : \"" + BOOK_TITLE + "\"",
      "  }",
      "}");
  private static final String MAXIMUM_JSON = String.join("\n", 
      "{",
      "  \"" + Club.NAME_FIELD_NAME + "\" : \"" + NAME + "\",",
      "  \"" + Club.OWNER_ID_FIELD_NAME + "\" : \"" + OWNER_ID + "\",",
      "  \"" + Club.DESCRIPTION_FIELD_NAME + "\" : \"" + DESCRIPTION + "\",",
      "  \"" + Club.CONTENT_WARNINGS_FIELD_NAME + "\" : [\"1\", \"2\"],",
      "  \"" + Club.CURRENT_BOOK_FIELD_NAME + "\" : {",
      "    \"" + Book.TITLE_FIELD_NAME + "\" : \"" + BOOK_TITLE + "\",",
      "    \"" + Book.AUTHOR_FIELD_NAME + "\" : \"" + BOOK_AUTHOR + "\",",
      "    \"" + Book.ISBN_FIELD_NAME + "\" : \"" + BOOK_ISBN + "\"",
      "  }",
      "}");
  private static final String NO_NAME_JSON = String.join("\n", 
      "{",
      "  \"" + Club.CURRENT_BOOK_FIELD_NAME + "\" : {",
      "  \"" + Club.OWNER_ID_FIELD_NAME + "\" : \"" + OWNER_ID + "\",",
      "    \"" + Book.TITLE_FIELD_NAME + "\" : \"" + BOOK_TITLE + "\"",
      "  }",
      "}");
  private static final String NO_BOOK_JSON = String.join("\n", 
      "{",
      "  \"" + Club.NAME_FIELD_NAME + "\" : \"" + NAME + "\"",
      "}");
  private static final String SYNTACTICALLY_INCORRECT_JSON = "{\"name\"";

  private CreateClubServlet CreateClubServlet;
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();
  private StringWriter stringWriter = new StringWriter();
  private List<String> testContentWarnings = Arrays.asList("1", "2");

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;

  @Before
  public void setUp() throws IOException {
    helper.setUp();

    IdentifierGenerator idGen = mock(IdentifierGenerator.class);
    when(idGen.generateId()).thenReturn(IDENTIFICATION_STRING);
    CreateClubServlet = new CreateClubServlet(idGen);

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doPost_minimumValidInput() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(MINIMUM_JSON)));

    CreateClubServlet.doPost(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Club c = gson.fromJson(result, Club.class);

    assertEquals(NAME, c.getName());
    assertEquals(IDENTIFICATION_STRING, c.getClubId());
    assertEquals(new ArrayList<String>(), c.getContentWarnings());
    assertEquals(BOOK_TITLE, c.getCurrentBook().getTitle());
    assertEquals(IDENTIFICATION_STRING, c.getCurrentBook().getBookId());
    assertFalse(c.getCurrentBook().getAuthor().isPresent());
    assertFalse(c.getCurrentBook().getIsbn().isPresent());
  }

  @Test
  public void doPost_maximumValidInput() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(MAXIMUM_JSON)));

    CreateClubServlet.doPost(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Club c = gson.fromJson(result, Club.class);

    assertEquals(NAME, c.getName());
    assertEquals(IDENTIFICATION_STRING, c.getClubId());
    assertEquals(testContentWarnings, c.getContentWarnings());
    assertEquals(DESCRIPTION, c.getDescription());
    assertEquals(BOOK_TITLE, c.getCurrentBook().getTitle());
    assertEquals(IDENTIFICATION_STRING, c.getCurrentBook().getBookId());
    assertTrue(c.getCurrentBook().getAuthor().isPresent());
    assertEquals(BOOK_AUTHOR, c.getCurrentBook().getAuthor().get());
    assertTrue(c.getCurrentBook().getIsbn().isPresent());
    assertEquals(BOOK_ISBN, c.getCurrentBook().getIsbn().get());
  }

  @Test
  public void doPost_noNameSpecified() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_NAME_JSON)));
    CreateClubServlet.doPost(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, CreateClubServlet.BODY_ERROR);
  }

  @Test
  public void doPost_noBookSpecified() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_BOOK_JSON)));
    CreateClubServlet.doPost(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, CreateClubServlet.BODY_ERROR);
  }

  @Test
  public void doPost_syntacticallyIncorrectInput() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(SYNTACTICALLY_INCORRECT_JSON)));
    CreateClubServlet.doPost(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, CreateClubServlet.BODY_ERROR);
  }
}
