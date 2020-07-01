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
import java.io.IOException;
import java.io.PrintWriter;
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
 * Unit tests for {@link CreatePersonServlet}.
 */
public class CreatePersonServletTest {
  private static final String NICKNAME = "Tim";
  private static final String EMAIL = "test@fake.fake";
  private static final String PRONOUNS = "he/him";
  private static final String IDENTIFICATION_STRING = "predetermined-identification-string";

  private CreatePersonServlet CreatePersonServlet;
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();
  private StringWriter stringWriter = new StringWriter();

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  
  @Before
  public void beforeTest() throws IOException {
    helper.setUp();

    IdentifierGenerator idGen = Mockito.mock(IdentifierGenerator.class);
    Mockito.when(idGen.generateId()).thenReturn(IDENTIFICATION_STRING);
    CreatePersonServlet = new CreatePersonServlet(idGen);

    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
  }

  @After
  public void afterTest() {
    helper.tearDown();
  }

  @Test
  public void allSpecified() throws IOException {
    Mockito.when(request.getParameter("nickname")).thenReturn(NICKNAME);
    Mockito.when(request.getParameter("email")).thenReturn(EMAIL);
    Mockito.when(request.getParameter("pronouns")).thenReturn(PRONOUNS);

    CreatePersonServlet.doPost(request, response);
    String result = stringWriter.toString();

    // Use multiple contains because order of JSON should not be relied upon per GSON documentation
    Assert.assertTrue(
        result.contains("\"email\":\"" + EMAIL + "\"")
        && result.contains("\"nickname\":\"" + NICKNAME + "\"")
        && result.contains("\"pronouns\":\"" + PRONOUNS + "\"")
        && result.contains("\"userId\":\"" + IDENTIFICATION_STRING + "\"")
    );
  }

  @Test
  public void noPronounsSpecified() throws IOException {
    Mockito.when(request.getParameter("nickname")).thenReturn(NICKNAME);
    Mockito.when(request.getParameter("email")).thenReturn(EMAIL);

    CreatePersonServlet.doPost(request, response);
    String result = stringWriter.toString();

    // Use multiple contains because order of JSON should not be relied upon per GSON documentation
    Assert.assertTrue(
        result.contains("\"email\":\"" + EMAIL + "\"")
        && result.contains("\"nickname\":\"" + NICKNAME + "\"")
        && result.contains("\"userId\":\"" + IDENTIFICATION_STRING + "\"")
    );

    // Pronouns were not specified, so they should not be in JSON representation
    Assert.assertFalse(result.contains("pronouns"));
  }

  @Test
  public void noEmailSpecified() throws IOException {
    Mockito.when(request.getParameter("nickname")).thenReturn(NICKNAME);
    CreatePersonServlet.doPost(request, response);
    
    Mockito.verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, CreatePersonServlet.EMAIL_OR_NICKNAME_ERROR);
  }

  @Test
  public void noNicknameSpecified() throws IOException {
    Mockito.when(request.getParameter("email")).thenReturn(EMAIL);
    CreatePersonServlet.doPost(request, response);
    
    Mockito.verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, CreatePersonServlet.EMAIL_OR_NICKNAME_ERROR);
  }
}
