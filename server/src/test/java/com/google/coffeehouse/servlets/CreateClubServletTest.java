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

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.coffeehouse.util.IdentifierGenerator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

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

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;

  @Before
  public void beforeTest() throws IOException {
    helper.setUp();

    IdentifierGenerator idGen = Mockito.mock(IdentifierGenerator.class);
    Mockito.when(idGen.generateId()).thenReturn(IDENTIFICATION_STRING);
    CreateClubServlet = new CreateClubServlet(idGen);

    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
  }

  @After
  public void afterTest() {
    helper.tearDown();
  }

  @Test
  public void minimumValidInput() throws IOException {
    Mockito.when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(MINIMUM_JSON)));

    CreateClubServlet.doPost(request, response);
    String result = stringWriter.toString();

    // Use multiple contains because order of JSON should not be relied upon per GSON documentation
    Assert.assertTrue(
        result.contains("\"name\":\"" + NAME + "\"")
        && result.contains("\"clubId\":\"" + IDENTIFICATION_STRING + "\"")
        && result.contains("\"currentBook\":{")
        && result.contains("\"title\":\"" + BOOK_TITLE + "\"")
        && result.contains("\"bookId\":\"" + IDENTIFICATION_STRING + "\"")
        && result.contains("\"contentWarnings\":[]")
    );

    // No book author or ISBN was specified, so they should not be in the output
    Assert.assertFalse(
        result.contains("\"author\":\"" + BOOK_AUTHOR + "\"")
        || result.contains("\"isbn\":\"" + BOOK_ISBN + "\"")
    );
  }

  @Test
  public void maximumValidInput() throws IOException {
    Mockito.when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(MAXIMUM_JSON)));

    CreateClubServlet.doPost(request, response);
    String result = stringWriter.toString();

    // Use multiple contains because order of JSON should not be relied upon per GSON documentation
    Assert.assertTrue(
        result.contains("\"name\":\"" + NAME + "\"")
        && result.contains("\"description\":\"" + DESCRIPTION + "\"")
        && result.contains("\"clubId\":\"" + IDENTIFICATION_STRING + "\"")
        && result.contains("\"currentBook\":{")
        && result.contains("\"title\":\"" + BOOK_TITLE + "\"")
        && result.contains("\"author\":\"" + BOOK_AUTHOR + "\"")
        && result.contains("\"isbn\":\"" + BOOK_ISBN + "\"")
        && result.contains("\"bookId\":\"" + IDENTIFICATION_STRING + "\"")
        && result.contains("\"contentWarnings\":[\"1\",\"2\"]")
    );
  }

  @Test
  public void noNameSpecified() throws IOException {
    Mockito.when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_NAME_JSON)));
    CreateClubServlet.doPost(request, response);

    Mockito.verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, CreateClubServlet.BODY_ERROR);
  }

  @Test
  public void noBookSpecified() throws IOException {
    Mockito.when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_BOOK_JSON)));
    CreateClubServlet.doPost(request, response);

    Mockito.verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, CreateClubServlet.BODY_ERROR);
  }

  @Test
  public void badInput() throws IOException {
    Mockito.when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(SYNTACTICALLY_INCORRECT_JSON)));
    CreateClubServlet.doPost(request, response);

    Mockito.verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, CreateClubServlet.BODY_ERROR);
  }
}
