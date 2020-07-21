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
import com.google.coffeehouse.common.Person;
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit tests for {@link GetProfileServlet}.
 */
public class GetProfileServletTest {
  // TODO: Change field name to be appropriate
  private static final String USER_ID = "predetermined-identification-string";
  private static final String EMAIL = "email@test.com";
  private static final String NICKNAME = "test";
  private static final String JSON = String.join("\n",
      "{",
      "  \"" + Person.USER_ID_FIELD_NAME + "\" : \"" + USER_ID + "\"",
      "}");
  private static final String SYNTACTICALLY_INCORRECT_JSON = 
      "{\"" + Person.USER_ID_FIELD_NAME + "\"";

  private GetProfileServlet GetProfileServlet;
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();
  private StringWriter stringWriter = new StringWriter();

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  
  @Before
  public void setUp() throws IOException {
    helper.setUp();
    GetProfileServlet = new GetProfileServlet();

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
          new BufferedReader(new StringReader(JSON)));

    GetProfileServlet.doGet(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Person.Builder personBuilder = Person.newBuilder()
                                         .setEmail(EMAIL)
                                         .setNickname(NICKNAME)
                                         .setUserId(USER_ID);
    Person p = personBuilder.build();

    assertEquals(USER_ID, p.getUserId());
  }

  @Test
  public void doGet_syntacticallyIncorrectInput() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(SYNTACTICALLY_INCORRECT_JSON)));
    GetProfileServlet.doGet(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, GetProfileServlet.BODY_ERROR);
  }
}
