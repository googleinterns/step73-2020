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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.coffeehouse.common.MembershipConstants;
import com.google.coffeehouse.servlets.LeaveClubServlet;
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
import org.mockito.Spy;

/**
 * Unit tests for {@link LeaveClubServlet}.
 */
public class LeaveClubServletTest {
  private static final String USER_ID = "predetermined-user-identification-string";
  private static final String CLUB_ID = "predetermined-club-identification-string";
  private static final String JSON = String.join("\n",
      "{",
      "  \"" + LeaveClubServlet.USER_ID_FIELD_NAME + "\" : \"" + USER_ID + "\",",
      "  \"" + LeaveClubServlet.CLUB_ID_FIELD_NAME + "\" : \"" + CLUB_ID + "\"",
      "}");
  private static final String NO_USER_ID_JSON = String.join("\n",
      "{",
      "  \"" + LeaveClubServlet.CLUB_ID_FIELD_NAME + "\" : \"" + CLUB_ID + "\"",
      "}");
  private static final String NO_CLUB_ID_JSON = String.join("\n",
      "{",
      "  \"" + LeaveClubServlet.USER_ID_FIELD_NAME + "\" : \"" + USER_ID + "\"",
      "}");
  private static final String SYNTACTICALLY_INCORRECT_JSON =
      "{\"" + LeaveClubServlet.USER_ID_FIELD_NAME + "\"";

  private LeaveClubServlet leaveClubServlet;
  private LeaveClubServlet failingLeaveClubServlet;
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();
  private StringWriter stringWriter = new StringWriter();

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private StorageHandlerApi failingHandler;
  @Spy private StorageHandlerApi successfulHandlerSpy;

  @Before
  public void setUp() throws IOException {
    helper.setUp();

    // The deleteMembership method is a void function, and thus requires the use of a spy.
    successfulHandlerSpy = spy(StorageHandlerApi.class);
    doNothing().when(successfulHandlerSpy).deleteMembership(anyString(), anyString());
    leaveClubServlet = new LeaveClubServlet(successfulHandlerSpy);

    failingHandler = mock(StorageHandlerApi.class);
    doThrow(new IllegalArgumentException(MembershipConstants.PERSON_NOT_IN_CLUB))
                .when(failingHandler).deleteMembership(anyString(), anyString());
    failingLeaveClubServlet = new LeaveClubServlet(failingHandler);

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doPost_validInput() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(JSON)));

    leaveClubServlet.doPost(request, response);
    verify(response).setStatus(200);
  }

  @Test
  public void doPost_noUserId() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_USER_ID_JSON)));
    leaveClubServlet.doPost(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST,
        String.format(leaveClubServlet.NO_FIELD_ERROR,
                      leaveClubServlet.USER_ID_FIELD_NAME));
  }

  @Test
  public void doPost_noClubId() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(NO_CLUB_ID_JSON)));
    leaveClubServlet.doPost(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_BAD_REQUEST,
        String.format(leaveClubServlet.NO_FIELD_ERROR,
                      leaveClubServlet.CLUB_ID_FIELD_NAME));
  }

  @Test
  public void doPost_userNotInClub() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(JSON)));
    failingLeaveClubServlet.doPost(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_CONFLICT,
        MembershipConstants.PERSON_NOT_IN_CLUB);
  }

  @Test
  public void doPost_syntacticallyIncorrectInput() throws IOException {
    when(request.getReader()).thenReturn(
          new BufferedReader(new StringReader(SYNTACTICALLY_INCORRECT_JSON)));
    leaveClubServlet.doPost(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
        leaveClubServlet.BODY_ERROR);
  }
}