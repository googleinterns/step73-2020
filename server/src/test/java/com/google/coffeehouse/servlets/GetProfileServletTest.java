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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.coffeehouse.common.Person;
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import com.google.coffeehouse.storagehandler.StorageHandler;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.Mock;
import org.mockito.Spy;

/**
 * Unit tests for {@link GetProfileServlet}.
 */
public class GetProfileServletTest {
  private static final String USER_ID = "predetermined-identification-string";
  private static final String EMAIL = "email@test.com";
  private static final String NICKNAME = "test";
  private static final String PRONOUNS = "they";
  private static final String JSON = String.join("\n",
      "{",
      "  \"" + Person.USER_ID_FIELD_NAME + "\" : \"" + USER_ID + "\"",
      "}");
  private static final String NO_USER_ID_JSON = "{}";
  private static final String PROFILE_NOT_FOUND_JSON = String.join("\n",
      "{",
      "  \"" + Person.USER_ID_FIELD_NAME + "\" : \"\"",
      "}");
  private static final String SYNTACTICALLY_INCORRECT_JSON = "{\"" + Person.USER_ID_FIELD_NAME + "\"";

  private Person testPerson = Person.newBuilder()
                                    .setEmail(EMAIL)
                                    .setNickname(NICKNAME)
                                    .setUserId(USER_ID)
                                    .setPronouns(PRONOUNS)
                                    .build();
  private GetProfileServlet getProfileServlet;
  private GetProfileServlet failingGetProfileServlet;
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
    when(successfulHandler.fetchPersonFromId(anyString())).thenReturn(testPerson);
    getProfileServlet = new GetProfileServlet(successfulHandler);

    failingHandler = mock(StorageHandlerApi.class);
    when(failingHandler.fetchPersonFromId(anyString()))
                       .thenThrow(new IllegalArgumentException(
                                      StorageHandler.PERSON_DOES_NOT_EXIST));
    failingGetProfileServlet = new GetProfileServlet(failingHandler);

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

    getProfileServlet.doGet(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Person p = gson.fromJson(result, Person.class);

    assertEquals(USER_ID, p.getUserId());
    assertEquals(EMAIL, p.getEmail());
    assertEquals(NICKNAME, p.getNickname());
    assertEquals(PRONOUNS, p.getPronouns().get());
  }

  @Test
  public void doGet_noUserId() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_USER_ID_JSON)));
    getProfileServlet.doGet(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST,
        GetProfileServlet.LOG_INPUT_ERROR_MESSAGE);
  }

  @Test
  public void doGet_noProfileFound() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(PROFILE_NOT_FOUND_JSON)));
    failingGetProfileServlet.doGet(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_NOT_FOUND,
        StorageHandler.PERSON_DOES_NOT_EXIST);
  }

  @Test
  public void doGet_syntacticallyIncorrectInput() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(SYNTACTICALLY_INCORRECT_JSON)));
    getProfileServlet.doGet(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
        GetProfileServlet.BODY_ERROR);
  }
}
