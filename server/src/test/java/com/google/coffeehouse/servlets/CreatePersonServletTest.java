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
import com.google.coffeehouse.common.Person;
import com.google.coffeehouse.util.IdentifierGenerator;
import com.google.gson.Gson;
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

/**
 * Unit tests for {@link CreatePersonServlet}.
 */
public class CreatePersonServletTest {
  private static final String NICKNAME = "Tim";
  private static final String EMAIL = "test@fake.fake";
  private static final String PRONOUNS = "he/him";
  private static final String IDENTIFICATION_STRING = "predetermined-identification-string";
  private static final String MINIMUM_JSON = String.join("\n", 
      "{",
      "  \"" + Person.NICKNAME_FIELD_NAME + "\" : \"" + NICKNAME + "\",",
      "  \"" + Person.EMAIL_FIELD_NAME + "\" : \"" + EMAIL + "\"",
      "}");
  private static final String MAXIMUM_JSON = String.join("\n", 
      "{",
      "  \"" + Person.NICKNAME_FIELD_NAME + "\" : \"" + NICKNAME + "\",",
      "  \"" + Person.PRONOUNS_FIELD_NAME + "\" : \"" + PRONOUNS + "\",",
      "  \"" + Person.EMAIL_FIELD_NAME + "\" : \"" + EMAIL + "\"",
      "}");
  private static final String NO_NICKNAME_JSON = String.join("\n", 
      "{",
      "  \"" + Person.PRONOUNS_FIELD_NAME + "\" : \"" + PRONOUNS + "\",",
      "  \"" + Person.EMAIL_FIELD_NAME + "\" : \"" + EMAIL + "\"",
      "}");
  private static final String NO_EMAIL_JSON = String.join("\n", 
      "{",
      "  \"" + Person.NICKNAME_FIELD_NAME + "\" : \"" + NICKNAME + "\",",
      "  \"" + Person.PRONOUNS_FIELD_NAME + "\" : \"" + PRONOUNS + "\"",
      "}");
  private static final String SYNTACTICALLY_INCORRECT_JSON = 
      "{\"" + Person.NICKNAME_FIELD_NAME + "\"";

  private CreatePersonServlet CreatePersonServlet;
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();
  private StringWriter stringWriter = new StringWriter();

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  
  @Before
  public void setUp() throws IOException {
    helper.setUp();

    IdentifierGenerator idGen = mock(IdentifierGenerator.class);
    when(idGen.generateId()).thenReturn(IDENTIFICATION_STRING);
    CreatePersonServlet = new CreatePersonServlet(idGen);

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

    CreatePersonServlet.doPost(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Person p = gson.fromJson(result, Person.class);

    Assert.assertEquals(NICKNAME, p.getNickname());
    Assert.assertEquals(EMAIL, p.getEmail());
    Assert.assertFalse(p.getPronouns().isPresent());
  }

  @Test
  public void doPost_maximumValidInput() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(MAXIMUM_JSON)));

    CreatePersonServlet.doPost(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Person p = gson.fromJson(result, Person.class);

    Assert.assertEquals(NICKNAME, p.getNickname());
    Assert.assertEquals(EMAIL, p.getEmail());
    Assert.assertTrue(p.getPronouns().isPresent());
    Assert.assertEquals(PRONOUNS, p.getPronouns().get());
  }

  @Test
  public void doPost_noEmailSpecified() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_EMAIL_JSON)));
    CreatePersonServlet.doPost(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, CreatePersonServlet.BODY_ERROR);
  }

  @Test
  public void doPost_noNicknameSpecified() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_NICKNAME_JSON)));
    CreatePersonServlet.doPost(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, CreatePersonServlet.BODY_ERROR);
  }

  @Test
  public void doPost_syntacticallyIncorrectInput() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(SYNTACTICALLY_INCORRECT_JSON)));
    CreatePersonServlet.doPost(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, CreatePersonServlet.BODY_ERROR);
  }
}
