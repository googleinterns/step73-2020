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

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.coffeehouse.common.Club;
import com.google.coffeehouse.common.Person;
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import com.google.coffeehouse.storagehandler.StorageHandler;
import com.google.coffeehouse.util.AuthenticationHelper;
import com.google.gson.Gson;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to get a list of Clubs that a user is in (or not in) and return it in JSON format.
 *
 * <p>TODO: There is currently no pagination implemented in this servlet. In the future,
 * pagination should be supported, as it will allow this code to be used even when the 
 * number of clubs in our database increases.
 */
@WebServlet("/api/list-clubs")
public class ListClubsServlet extends HttpServlet {
  /**
   * The error string sent by the response object in doGet when the body of the 
   * GET request cannot be be parsed.
   */
  public static final String BODY_ERROR = "- unable to parse body.";
  /** The logged error string when an error parsing the body of the GET request is encountered. */
  public static final String LOG_BODY_ERROR_MESSAGE =
      "Body unable to be parsed in ListClubsServlet: ";
  /** Message to be logged when the body of the GET request does not have required fields. */
  public static final String LOG_INPUT_ERROR_MESSAGE =
      "Error with JSON input in ListClubsServlet: No \"%s\" found in JSON.";
  /**
   * Message to be logged when an invalid ID token is passed in or a no ID token is passed in.
   */
  public static final String LOG_SECURITY_MESSAGE =
      "Forbidden action attempted: ";
  /** Name of the key in the input JSON that corresponds to the ID token. */
  public static final String ID_TOKEN_FIELD_NAME =
      "idToken";
  /**
   * Name of the key in the input JSON that corresponds to if we are searching for clubs the user
   * is a member of, or clubs that the user is not a member of.
   */
  public static final String MEMBERSHIP_STATUS_FIELD_NAME =
      "membershipStatus";
  /**
   * A possible value for the MEMBERSHIP_STATUS_FIELD_NAME key that means we are searching for
   * clubs that the user is a member of.
   */
  public static final String MEMBER =
      "member";
  /**
   * A possible value for the MEMBERSHIP_STATUS_FIELD_NAME key that means we are searching for
   * clubs that the user is not a member of.
   */
  public static final String NOT_MEMBER =
      "not member";

  private static final Gson gson = new Gson();
  private static final HttpTransport transport = new NetHttpTransport();
  private static final GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
  private final GoogleIdTokenVerifier verifier;
  private final StorageHandlerApi storageHandler;

  /** 
   * Overloaded constructor for dependency injection.
   * @param verifier the class that verifies the validity of the ID token
   * @param storageHandler the {@link StorageHandlerApi} that is used when fetching the Clubs
   */
  public ListClubsServlet(GoogleIdTokenVerifier verifier, StorageHandlerApi storageHandler) {
    super();
    this.storageHandler = storageHandler;
    this.verifier = verifier;
  }

  /** 
   * Explicit default constructor used for instantiating the servlet when not testing.
   */
  public ListClubsServlet() {
    super();
    this.storageHandler = new StorageHandlerApi();
    this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory).build();
  }

  /**
   * Responds with a JSON list of {@link Club} objects that a user is either a member of or not.
   * @param request the GET request that must have the {@code "idToken"} of the user who wants
   *     a list of clubs that they are in (or not in) back. It must also have a
   *     {@code "membershipStatus"} key which is mapped to either "member" or "not member". This
   *     key determines if we return a list of clubs where the user is a member, or not a member.
   *     If the required "membershipStatus" field does not exist, the response object
   *     will send a "400 Bad Request error". If the JSON body is not valid, and unable to be
   *     parsed, the response object will send a "500 Internal Server error". If the "idToken"
   *     field is missing or invalid, the response object will send a "403 Forbidden error"
   * @param response the response from this method, will contain the list of Clubs in JSON format.
   *     If the required "membershipStatus" field does not exist in the request
   *     object, this object will send a "400 Bad Request error". If the JSON body is not valid,
   *     and unable to be parsed, this object will send a "500 Internal Server error". If the
   *     "idToken" field is missing or invalid, the response object will send a
   *     "403 Forbidden error"
   * @throws IOException if an input or output error is detected when the servlet handles the request
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<Club> clubs;
    try {
      // Get the userId after validating the user's ID token.
      Map requestInfo = gson.fromJson(request.getReader(), Map.class);
      String idToken = (String) requestInfo.get(ID_TOKEN_FIELD_NAME);
      String userId = AuthenticationHelper.getUserIdFromIdToken(idToken, verifier);

      String status = (String) requestInfo.get(MEMBERSHIP_STATUS_FIELD_NAME);
      if (status == null || !(status.equals(MEMBER) || status.equals(NOT_MEMBER))) {
        throw new IllegalArgumentException(
            String.format(LOG_INPUT_ERROR_MESSAGE, MEMBERSHIP_STATUS_FIELD_NAME));
      }

      MembershipStatus membershipStatus = status.equals(MEMBER)
          ? MembershipStatus.MEMBER
          : MembershipStatus.NOT_MEMBER;
      
      clubs = storageHandler.listClubsFromUserId(userId, membershipStatus);
    } catch (IllegalArgumentException e) {
      System.out.println(LOG_BODY_ERROR_MESSAGE + e.getMessage());
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
      return;
    } catch (GeneralSecurityException e) {
      System.out.println(LOG_SECURITY_MESSAGE + e.getMessage());
      response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
      return;
    } catch (Exception e) {
      System.out.println(LOG_BODY_ERROR_MESSAGE + e.getMessage());
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, BODY_ERROR);
      return;
    }
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(clubs));
  }
}
