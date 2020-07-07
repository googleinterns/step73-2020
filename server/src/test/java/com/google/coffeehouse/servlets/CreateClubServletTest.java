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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit tests for {@link CreateClubServlet}.
 */
public class CreateClubServletTest {
  private static final String NAME = "Club Name";
  private static final String DESCRIPTION = "Club Description";
  private static final String IDENTIFICATION_STRING = "predetermined-identification-string";
  private static final String BOOK_TITLE = "Book Name";
  private static final String BOOK_AUTHOR = "Book Author";
  private static final String BOOK_ISBN = "978-3-16-148410-0";
  private static final String MINIMUM_JSON = String.join("\n", 
      "{",
      "  \"name\" : \"" + NAME + "\",",
      "  \"currentBook\" : {",
      "    \"title\" : \"" + BOOK_TITLE + "\"",
      "  }",
      "}");
  private static final String MAXIMUM_JSON = String.join("\n", 
      "{",
      "  \"name\" : \"" + NAME + "\",",
      "  \"description\" : \"" + DESCRIPTION + "\",",
      "  \"contentWarnings\" : [\"1\", \"2\"],",
      "  \"currentBook\" : {",
      "    \"title\" : \"" + BOOK_TITLE + "\",",
      "    \"author\" : \"" + BOOK_AUTHOR + "\",",
      "    \"isbn\" : \"" + BOOK_ISBN + "\"",
      "  }",
      "}");
  private static final String NO_NAME_JSON = String.join("\n", 
      "{",
      "  \"currentBook\" : {",
      "    \"title\" : \"" + BOOK_TITLE + "\"",
      "  }",
      "}");
  private static final String NO_BOOK_JSON = String.join("\n", 
      "{",
      "  \"name\" : \"" + NAME + "\"",
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
  public void minimumValidInput() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(MINIMUM_JSON)));

    CreateClubServlet.doPost(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Club c = gson.fromJson(result, Club.class);

    Assert.assertEquals(NAME, c.getName());
    Assert.assertEquals(IDENTIFICATION_STRING, c.getClubId());
    Assert.assertEquals(new ArrayList<String>(), c.getContentWarnings());
    Assert.assertEquals(BOOK_TITLE, c.getCurrentBook().getTitle());
    Assert.assertEquals(IDENTIFICATION_STRING, c.getCurrentBook().getBookId());
    Assert.assertFalse(c.getCurrentBook().getAuthor().isPresent());
    Assert.assertFalse(c.getCurrentBook().getIsbn().isPresent());
  }

  @Test
  public void maximumValidInput() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(MAXIMUM_JSON)));

    CreateClubServlet.doPost(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Club c = gson.fromJson(result, Club.class);

    Assert.assertEquals(NAME, c.getName());
    Assert.assertEquals(IDENTIFICATION_STRING, c.getClubId());
    Assert.assertEquals(testContentWarnings, c.getContentWarnings());
    Assert.assertEquals(DESCRIPTION, c.getDescription());
    Assert.assertEquals(BOOK_TITLE, c.getCurrentBook().getTitle());
    Assert.assertEquals(IDENTIFICATION_STRING, c.getCurrentBook().getBookId());
    Assert.assertTrue(c.getCurrentBook().getAuthor().isPresent());
    Assert.assertEquals(BOOK_AUTHOR, c.getCurrentBook().getAuthor().get());
    Assert.assertTrue(c.getCurrentBook().getIsbn().isPresent());
    Assert.assertEquals(BOOK_ISBN, c.getCurrentBook().getIsbn().get());
  }

  @Test
  public void noNameSpecified() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_NAME_JSON)));
    CreateClubServlet.doPost(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, CreateClubServlet.BODY_ERROR);
  }

  @Test
  public void noBookSpecified() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_BOOK_JSON)));
    CreateClubServlet.doPost(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, CreateClubServlet.BODY_ERROR);
  }

  @Test
  public void badInput() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(SYNTACTICALLY_INCORRECT_JSON)));
    CreateClubServlet.doPost(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, CreateClubServlet.BODY_ERROR);
  }
}
