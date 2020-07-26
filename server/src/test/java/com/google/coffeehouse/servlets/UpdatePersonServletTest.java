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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.coffeehouse.common.Person;
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import com.google.coffeehouse.util.AuthenticationHelper;
import com.google.coffeehouse.util.TokenVerifier;
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
 * Unit tests for {@link UpdatePersonServlet}.
 */
public class UpdatePersonServletTest {
  private static final String USER_ID = "Old User Id";
  private static final String ALT_USER_ID = "New User Id";
  private static final String NICKNAME = "Old Name";
  private static final String ALT_NICKNAME = "New Name";
  private static final String EMAIL = "Old Email";
  private static final String ALT_EMAIL = "New Email";
  private static final String PRONOUNS = "Old Pronouns";
  private static final String ALT_PRONOUNS = "New Pronouns";
  private static final String ID_TOKEN = "Identification Token";
  private static final Person testPerson = Person.newBuilder()
                                                 .setNickname(NICKNAME)
                                                 .setEmail(EMAIL)
                                                 .setUserId(USER_ID)
                                                 .setPronouns(PRONOUNS)
                                                 .build();
  private static final String MASK_PARTIAL_UPDATE = String.join("\n",
      "{",
      "  \"" + UpdatePersonServlet.ID_TOKEN_FIELD_NAME + "\" : \"" + ID_TOKEN + "\",",
      "  \"" + UpdatePersonServlet.UPDATE_MASK_FIELD_NAME + "\" : \"" +
      Person.NICKNAME_FIELD_NAME + "," + Person.PRONOUNS_FIELD_NAME + "\",",
      "  \"" + UpdatePersonServlet.PERSON_FIELD_NAME + "\" : {",
      "    \"" + Person.NICKNAME_FIELD_NAME + "\" : \"" + ALT_NICKNAME + "\",",
      "    \"" + Person.EMAIL_FIELD_NAME + "\" : \"" + ALT_EMAIL + "\",",
      "    \"" + Person.PRONOUNS_FIELD_NAME + "\" : \"" + ALT_PRONOUNS + "\",",
      "    \"" + Person.USER_ID_FIELD_NAME + "\" : \"" + USER_ID + "\"",
      "  }",
      "}");
  private static final String NO_MASK_UPDATE = String.join("\n",
      "{",
      "  \"" + UpdatePersonServlet.ID_TOKEN_FIELD_NAME + "\" : \"" + ID_TOKEN + "\",",
      "  \"" + UpdatePersonServlet.PERSON_FIELD_NAME + "\" : {",
      "    \"" + Person.NICKNAME_FIELD_NAME + "\" : \"" + ALT_NICKNAME + "\",",
      "    \"" + Person.EMAIL_FIELD_NAME + "\" : \"" + ALT_EMAIL + "\",",
      "    \"" + Person.PRONOUNS_FIELD_NAME + "\" : \"" + ALT_PRONOUNS + "\",",
      "    \"" + Person.USER_ID_FIELD_NAME + "\" : \"" + USER_ID + "\"",
      "  }",
      "}");
  private static final String MASK_ALL_UPDATE = String.join("\n",
      "{",
      "  \"" + UpdatePersonServlet.ID_TOKEN_FIELD_NAME + "\" : \"" + ID_TOKEN + "\",",
      "  \"" + UpdatePersonServlet.UPDATE_MASK_FIELD_NAME + "\" : \"" +
      Person.NICKNAME_FIELD_NAME + "," + Person.PRONOUNS_FIELD_NAME + "," +
      Person.EMAIL_FIELD_NAME + "," + Person.USER_ID_FIELD_NAME + "\",",
      "  \"" + UpdatePersonServlet.PERSON_FIELD_NAME + "\" : {",
      "    \"" + Person.NICKNAME_FIELD_NAME + "\" : \"" + ALT_NICKNAME + "\",",
      "    \"" + Person.EMAIL_FIELD_NAME + "\" : \"" + ALT_EMAIL + "\",",
      "    \"" + Person.PRONOUNS_FIELD_NAME + "\" : \"" + ALT_PRONOUNS + "\",",
      "    \"" + Person.USER_ID_FIELD_NAME + "\" : \"" + USER_ID + "\"",
      "  }",
      "}");
  private static final String NO_ID_TOKEN = String.join("\n",
      "{",
      "  \"" + UpdatePersonServlet.UPDATE_MASK_FIELD_NAME + "\" : \"" +
      Person.NICKNAME_FIELD_NAME + "," + Person.PRONOUNS_FIELD_NAME + "," +
      Person.EMAIL_FIELD_NAME + "," + Person.USER_ID_FIELD_NAME + "\",",
      "  \"" + UpdatePersonServlet.PERSON_FIELD_NAME + "\" : {",
      "    \"" + Person.NICKNAME_FIELD_NAME + "\" : \"" + ALT_NICKNAME + "\",",
      "    \"" + Person.EMAIL_FIELD_NAME + "\" : \"" + ALT_EMAIL + "\",",
      "    \"" + Person.PRONOUNS_FIELD_NAME + "\" : \"" + ALT_PRONOUNS + "\"",
      "  }",
      "}");
  private static final String NO_PERSON = String.join("\n",
      "{",
      "  \"" + UpdatePersonServlet.ID_TOKEN_FIELD_NAME + "\" : \"" + ID_TOKEN + "\",",
      "  \"" + UpdatePersonServlet.UPDATE_MASK_FIELD_NAME + "\" : \"" +
      Person.NICKNAME_FIELD_NAME + "," + Person.PRONOUNS_FIELD_NAME + "," +
      Person.EMAIL_FIELD_NAME + "," + Person.USER_ID_FIELD_NAME + "\"",
      "}");
  private static final String SYNTACTICALLY_INCORRECT_JSON = "{\"{";
  private static final String ID_MISMATCH_JSON = String.join("\n",
      "{",
      "  \"" + UpdatePersonServlet.ID_TOKEN_FIELD_NAME + "\" : \"" + ID_TOKEN + "\",",
      "  \"" + UpdatePersonServlet.UPDATE_MASK_FIELD_NAME + "\" : \"" +
      Person.NICKNAME_FIELD_NAME + "," + Person.PRONOUNS_FIELD_NAME + "\",",
      "  \"" + UpdatePersonServlet.PERSON_FIELD_NAME + "\" : {",
      "    \"" + Person.NICKNAME_FIELD_NAME + "\" : \"" + ALT_NICKNAME + "\",",
      "    \"" + Person.EMAIL_FIELD_NAME + "\" : \"" + ALT_EMAIL + "\",",
      "    \"" + Person.PRONOUNS_FIELD_NAME + "\" : \"" + ALT_PRONOUNS + "\",",
      "    \"" + Person.USER_ID_FIELD_NAME + "\" : \"" + ALT_USER_ID + "\"",
      "  }",
      "}");

  private UpdatePersonServlet updatePersonServlet;
  private UpdatePersonServlet failingUpdatePersonServlet;
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();
  private StringWriter stringWriter = new StringWriter();

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private StorageHandlerApi handler;
  @Mock private TokenVerifier verifier;
  @Mock private TokenVerifier nullVerifier;

  @Before
  public void setUp() throws IOException, GeneralSecurityException {
    helper.setUp();

    handler = spy(StorageHandlerApi.class);
    doNothing().when(handler).writeMutations(anyList());
    doReturn(testPerson).when(handler).fetchPersonFromId(anyString());

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

    // Verification setup that successfully verifies and gives correct userId.
    verifier = mock(TokenVerifier.class);
    when(verifier.getSubject(anyString())).thenReturn(USER_ID);

    // Verification setup that does not successfully verify.
    nullVerifier = mock(TokenVerifier.class);
    when(nullVerifier.getSubject(anyString())).thenReturn(null);

    updatePersonServlet = new UpdatePersonServlet(verifier, handler);
    failingUpdatePersonServlet = new UpdatePersonServlet(nullVerifier, handler);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doPost_validInputWithPartialMask() throws IOException {
    when(request.getReader()).thenReturn(
        new BufferedReader(new StringReader(MASK_PARTIAL_UPDATE)));
    updatePersonServlet.doPost(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Person p = gson.fromJson(result, Person.class);

    assertEquals(ALT_NICKNAME, p.getNickname());
    assertEquals(ALT_PRONOUNS, p.getPronouns().get());
    assertEquals(EMAIL, p.getEmail());
    assertEquals(USER_ID, p.getUserId());
  }

  @Test
  public void doPost_validInputWithoutMask() throws IOException {
    when(request.getReader()).thenReturn(
        new BufferedReader(new StringReader(NO_MASK_UPDATE)));
    updatePersonServlet.doPost(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Person p = gson.fromJson(result, Person.class);

    assertEquals(ALT_NICKNAME, p.getNickname());
    assertEquals(ALT_PRONOUNS, p.getPronouns().get());
    assertEquals(ALT_EMAIL, p.getEmail());
    assertEquals(USER_ID, p.getUserId());
  }

  @Test
  public void doPost_validInputWithFullMask() throws IOException {
    when(request.getReader()).thenReturn(
        new BufferedReader(new StringReader(MASK_ALL_UPDATE)));
    updatePersonServlet.doPost(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Person p = gson.fromJson(result, Person.class);

    assertEquals(ALT_NICKNAME, p.getNickname());
    assertEquals(ALT_PRONOUNS, p.getPronouns().get());
    assertEquals(ALT_EMAIL, p.getEmail());
    assertEquals(USER_ID, p.getUserId());
  }

  @Test
  public void doPost_noUserId() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_ID_TOKEN)));
    updatePersonServlet.doPost(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_FORBIDDEN, AuthenticationHelper.INVALID_ID_TOKEN_ERROR);
  }

  @Test
  public void doPost_noPerson() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_PERSON)));
    updatePersonServlet.doPost(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST,
        String.format(updatePersonServlet.NO_FIELD_ERROR, updatePersonServlet.PERSON_FIELD_NAME));
  }
  
  @Test
  public void doPost_syntacticallyIncorrectInput() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(SYNTACTICALLY_INCORRECT_JSON)));
    updatePersonServlet.doPost(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, updatePersonServlet.BODY_ERROR);
  }

  @Test
  public void doPost_failedVerification() throws IOException {
    when(request.getReader()).thenReturn(
        new BufferedReader(new StringReader(MASK_ALL_UPDATE)));
    failingUpdatePersonServlet.doPost(request, response);
    verify(response).sendError(
        HttpServletResponse.SC_FORBIDDEN, AuthenticationHelper.INVALID_ID_TOKEN_ERROR);
  }

  @Test
  public void doPost_userIdMismatch() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(ID_MISMATCH_JSON)));
    updatePersonServlet.doPost(request, response);
    
    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST, updatePersonServlet.LOG_USER_ID_MISMATCH);
  }
}
