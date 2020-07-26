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

import static com.google.coffeehouse.common.MembershipConstants.MembershipStatus;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.coffeehouse.common.Book;
import com.google.coffeehouse.common.Club;
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import com.google.coffeehouse.storagehandler.StorageHandler;
import com.google.coffeehouse.util.AuthenticationHelper;
import com.google.coffeehouse.util.TokenVerifier;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit tests for {@link ListClubsServlet}.
 */
public class ListClubsServletTest {
  private static final String NAME = "Club Name";
  private static final String DESCRIPTION = "Club Description";
  private static final String CLUB_ID = "predetermined-identification-string";
  private static final String OWNER_ID = "predetermined-owner-identification-string";
  private static final String BOOK_TITLE = "Book Name";
  private static final String BOOK_ID = "predetermined-book-identification-string";
  private static final String ID_TOKEN = "Identification Token";
  private List<String> testContentWarnings = new ArrayList<>(Arrays.asList("1", "2"));
  private Book testBook = Book.newBuilder()
                              .setTitle(BOOK_TITLE)
                              .setBookId(BOOK_ID)
                              .build();
  private Club testClub = Club.newBuilder()
                              .setName(NAME)
                              .setCurrentBook(testBook)
                              .setOwnerId(OWNER_ID)
                              .setClubId(CLUB_ID)
                              .setDescription(DESCRIPTION)
                              .setContentWarnings(testContentWarnings)
                              .build();
  private ListClubsServlet listClubsServlet;
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();
  private StringWriter stringWriter = new StringWriter();

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private StorageHandlerApi memberHandler;
  @Mock private StorageHandlerApi notMemberHandler;
  @Mock private TokenVerifier verifier;
  @Mock private TokenVerifier nullVerifier;

  @Before
  public void setUp() throws IOException, GeneralSecurityException {
    helper.setUp();

    memberHandler = mock(StorageHandlerApi.class);
    when(memberHandler.listClubsFromUserId(
        anyString(), eq(MembershipStatus.MEMBER))).thenReturn(Arrays.asList(testClub));

    notMemberHandler = mock(StorageHandlerApi.class);
    when(notMemberHandler.listClubsFromUserId(
        anyString(), eq(MembershipStatus.NOT_MEMBER))).thenReturn(Arrays.asList(testClub));

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
    
    // Verification setup that successfully verifies and gives userId.
    verifier = mock(TokenVerifier.class);
    when(verifier.getSubject(anyString())).thenReturn(OWNER_ID);

    // Verification setup that does not successfully verify.
    nullVerifier = mock(TokenVerifier.class);
    when(nullVerifier.getSubject(anyString())).thenReturn(null);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doGet_validInputMember() throws IOException {
    listClubsServlet = new ListClubsServlet(verifier, memberHandler);
    when(request.getParameter(eq(ListClubsServlet.ID_TOKEN_PARAMETER))).thenReturn(ID_TOKEN);
    when(request.getParameter(eq(ListClubsServlet.MEMBERSHIP_STATUS_PARAMETER)))
        .thenReturn(ListClubsServlet.MEMBER);

    listClubsServlet.doGet(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Club[] clubs = gson.fromJson(result, Club[].class);

    assertEquals(1, clubs.length);
    Club c = clubs[0];
    assertEquals(NAME, c.getName());
    assertEquals(OWNER_ID, c.getOwnerId());
    assertEquals(CLUB_ID, c.getClubId());
    assertEquals(DESCRIPTION, c.getDescription());
    assertEquals(testContentWarnings, c.getContentWarnings());
    assertEquals(BOOK_TITLE, c.getCurrentBook().getTitle());
    assertEquals(BOOK_ID, c.getCurrentBook().getBookId());
  }

  @Test
  public void doGet_validInputNotMember() throws IOException {
    listClubsServlet = new ListClubsServlet(verifier, notMemberHandler);
    when(request.getParameter(eq(ListClubsServlet.ID_TOKEN_PARAMETER))).thenReturn(ID_TOKEN);
    when(request.getParameter(eq(ListClubsServlet.MEMBERSHIP_STATUS_PARAMETER)))
        .thenReturn(ListClubsServlet.NOT_MEMBER);

    listClubsServlet.doGet(request, response);
    String result = stringWriter.toString();

    Gson gson = new Gson();
    Club[] clubs = gson.fromJson(result, Club[].class);

    assertEquals(1, clubs.length);
    Club c = clubs[0];
    assertEquals(NAME, c.getName());
    assertEquals(OWNER_ID, c.getOwnerId());
    assertEquals(CLUB_ID, c.getClubId());
    assertEquals(DESCRIPTION, c.getDescription());
    assertEquals(testContentWarnings, c.getContentWarnings());
    assertEquals(BOOK_TITLE, c.getCurrentBook().getTitle());
    assertEquals(BOOK_ID, c.getCurrentBook().getBookId());
  }

  @Test
  public void doGet_noMembershipStatus() throws IOException {
    listClubsServlet = new ListClubsServlet(verifier, memberHandler);
    when(request.getParameter(eq(ListClubsServlet.ID_TOKEN_PARAMETER))).thenReturn(ID_TOKEN);
    
    listClubsServlet.doGet(request, response);

    verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST,
        String.format(ListClubsServlet.LOG_INPUT_ERROR_MESSAGE,
                      ListClubsServlet.MEMBERSHIP_STATUS_PARAMETER));
  }

  @Test
  public void doGet_noIdToken() throws IOException {
    listClubsServlet = new ListClubsServlet(verifier, memberHandler);
    when(request.getParameter(eq(ListClubsServlet.MEMBERSHIP_STATUS_PARAMETER)))
        .thenReturn(ListClubsServlet.NOT_MEMBER);
    
    listClubsServlet.doGet(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_FORBIDDEN,
        AuthenticationHelper.INVALID_ID_TOKEN_ERROR);
  }

  @Test
  public void doGet_failVerification() throws IOException {
    listClubsServlet = new ListClubsServlet(nullVerifier, memberHandler);
    when(request.getParameter(eq(ListClubsServlet.ID_TOKEN_PARAMETER))).thenReturn(ID_TOKEN);
    when(request.getParameter(eq(ListClubsServlet.MEMBERSHIP_STATUS_PARAMETER)))
        .thenReturn(ListClubsServlet.NOT_MEMBER);

    listClubsServlet.doGet(request, response);

    verify(response).sendError(
        HttpServletResponse.SC_FORBIDDEN,
        AuthenticationHelper.INVALID_ID_TOKEN_ERROR);
  }
}
