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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.coffeehouse.servlets.RetrieveTokenServlet;
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
import org.mockito.Mock;

/**
 * Unit tests for {@link RetrieveTokenServlet}.
 */
public class RetrieveTokenServletTest {
  private static final String ID_TOKEN = "ID Token";
  private static final String VALID_JSON = String.join("\n",
      "{",
      "  \"" + RetrieveTokenServlet.CODE_KEY_NAME + "\":\"123\",",
      "  \"" + RetrieveTokenServlet.REDIRECTURI_KEY_NAME + "\":\"abc\"",
      "}");
  private static final String NO_CODE_JSON = String.join("\n",
      "{",
      "  \"" + RetrieveTokenServlet.REDIRECTURI_KEY_NAME + "\":\"abc\"",
      "}");
  private static final String NO_REDIRECTURI_JSON = String.join("\n",
      "{",
      "  \"" + RetrieveTokenServlet.CODE_KEY_NAME + "\":\"123\"",
      "}");
  private static final String SYNTACTICALLY_INCORRECT_JSON = "{\"}";

  private RetrieveTokenServlet retrieveTokenServlet;
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();
  private StringWriter stringWriter = new StringWriter();
  private static final Gson gson = new Gson();

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private GoogleIdTokenVerifier verifier;
  @Mock private GoogleAuthorizationCodeTokenRequest tokenRequest;
  @Mock private GoogleTokenResponse tokenResponse;
  @Mock private GoogleIdToken idToken;

  @Before
  public void setUp() throws IOException {
    helper.setUp();

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

    tokenRequest = mock(GoogleAuthorizationCodeTokenRequest.class);
    tokenResponse = mock(GoogleTokenResponse.class);
    when(tokenRequest.setCode(any(String.class))).thenReturn(tokenRequest);
    when(tokenRequest.setRedirectUri(any(String.class))).thenReturn(tokenRequest);
    when(tokenRequest.execute()).thenReturn(tokenResponse);
    when(tokenResponse.getIdToken()).thenReturn(ID_TOKEN);

    verifier = mock(GoogleIdTokenVerifier.class);
    idToken = mock(GoogleIdToken.class);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doPost_validInputVerified() throws IOException, GeneralSecurityException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(VALID_JSON)));
    when(verifier.verify(any(String.class))).thenReturn(idToken);

    retrieveTokenServlet = new RetrieveTokenServlet(verifier, tokenRequest);
    retrieveTokenServlet.doPost(request, response);
    String result = stringWriter.toString();
    String actual = gson.fromJson(result, String.class);
    assertEquals(ID_TOKEN, actual);
  }

  @Test
  public void doPost_validInputNotVerified() throws IOException, GeneralSecurityException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(VALID_JSON)));
    when(verifier.verify(any(String.class))).thenReturn(null);

    retrieveTokenServlet = new RetrieveTokenServlet(verifier, tokenRequest);
    retrieveTokenServlet.doPost(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_FORBIDDEN, RetrieveTokenServlet.INVALID_ID_TOKEN);
  }

  @Test
  public void doPost_missingCode() throws IOException, GeneralSecurityException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_CODE_JSON)));
    when(verifier.verify(any(String.class))).thenReturn(idToken);

    retrieveTokenServlet = new RetrieveTokenServlet(verifier, tokenRequest);
    retrieveTokenServlet.doPost(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, RetrieveTokenServlet.BODY_ERROR);
  }

  @Test
  public void doPost_missingRedirectUri() throws IOException, GeneralSecurityException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_REDIRECTURI_JSON)));
    when(verifier.verify(any(String.class))).thenReturn(idToken);

    retrieveTokenServlet = new RetrieveTokenServlet(verifier, tokenRequest);
    retrieveTokenServlet.doPost(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, RetrieveTokenServlet.BODY_ERROR);
  }

  @Test
  public void doPost_syntacticallyIncorrectInput() throws IOException, GeneralSecurityException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(SYNTACTICALLY_INCORRECT_JSON)));
    when(verifier.verify(any(String.class))).thenReturn(idToken);

    retrieveTokenServlet = new RetrieveTokenServlet(verifier, tokenRequest);
    retrieveTokenServlet.doPost(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, RetrieveTokenServlet.BODY_ERROR);
  }
}
