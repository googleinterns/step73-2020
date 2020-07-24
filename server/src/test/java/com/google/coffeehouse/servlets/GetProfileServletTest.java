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

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.coffeehouse.common.Person;
import com.google.coffeehouse.servlets.GetProfileServlet;
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import com.google.coffeehouse.storagehandler.StorageHandler;
import com.google.coffeehouse.util.AuthenticationHelper;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
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
  private static final String ID_TOKEN = "Identification Token";
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
  @Mock private GoogleIdTokenVerifier correctVerifier;
  @Mock private GoogleIdTokenVerifier nullVerifier;
  @Mock private GoogleIdToken correctIdToken;
  @Mock private Payload correctPayload;
  
  @Before
  public void setUp() throws IOException, GeneralSecurityException {
    helper.setUp();

    successfulHandler = mock(StorageHandlerApi.class);
    when(successfulHandler.fetchPersonFromId(anyString())).thenReturn(testPerson);

    failingHandler = mock(StorageHandlerApi.class);
    when(failingHandler.fetchPersonFromId(anyString()))
                       .thenThrow(new IllegalArgumentException(
                                      StorageHandler.PERSON_DOES_NOT_EXIST));

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

    // Verification setup that successfully verifies and gives correct userId.
    correctPayload = mock(Payload.class);
    when(correctPayload.getSubject()).thenReturn(USER_ID);
    correctIdToken = mock(GoogleIdToken.class);
    when(correctIdToken.getPayload()).thenReturn(correctPayload);
    correctVerifier = mock(GoogleIdTokenVerifier.class);
    when(correctVerifier.verify(anyString())).thenReturn(correctIdToken);

    // Verification setup that does not successfully verify.
    nullVerifier = mock(GoogleIdTokenVerifier.class);
    when(nullVerifier.verify(anyString())).thenReturn(null);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doGet_validInput() throws IOException {
    when(request.getParameter(eq(GetProfileServlet.ID_TOKEN_PARAMETER)))
        .thenReturn(ID_TOKEN);
    getProfileServlet = new GetProfileServlet(correctVerifier, successfulHandler);

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
  public void doGet_noIdToken() throws IOException {
    when(request.getParameter(eq(GetProfileServlet.ID_TOKEN_PARAMETER)))
        .thenReturn(null);
    getProfileServlet = new GetProfileServlet(correctVerifier, successfulHandler);
    getProfileServlet.doGet(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST,
        String.format(getProfileServlet.NO_FIELD_ERROR, getProfileServlet.ID_TOKEN_PARAMETER));
  }

  @Test
  public void doGet_noProfileFound() throws IOException {
    when(request.getParameter(eq(GetProfileServlet.ID_TOKEN_PARAMETER)))
        .thenReturn(ID_TOKEN);
    failingGetProfileServlet = new GetProfileServlet(correctVerifier, failingHandler);
    failingGetProfileServlet.doGet(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_NOT_FOUND,
        StorageHandler.PERSON_DOES_NOT_EXIST);
  }

  @Test
  public void doGet_failVerification() throws IOException {
    when(request.getParameter(eq(GetProfileServlet.ID_TOKEN_PARAMETER)))
        .thenReturn(ID_TOKEN);
    getProfileServlet = new GetProfileServlet(nullVerifier, successfulHandler);
    getProfileServlet.doGet(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_FORBIDDEN, AuthenticationHelper.INVALID_ID_TOKEN_ERROR);
  }
}
