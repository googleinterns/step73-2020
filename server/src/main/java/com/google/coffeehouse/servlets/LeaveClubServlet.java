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

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.coffeehouse.common.Club;
import com.google.coffeehouse.common.MembershipConstants;
import com.google.coffeehouse.common.Person;
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import com.google.coffeehouse.util.AuthenticationHelper;
import com.google.gson.Gson;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to leave a club from Http POST Request Body (in JSON format) containing a club ID
 * that exists in the database and a valid user ID token, and return a success status 200 (OK).
 */
@WebServlet("/api/leave-club")
public class LeaveClubServlet extends HttpServlet {

  /**
   * The error string sent by the response object in doPost when the body of the 
   * POST request cannot be used to leave a club for any reason.
   */
  public static final String BODY_ERROR = "- unable to parse body.";
  /** The message to be logged when the body of the POST request cannot be parsed. */
  public static final String LOG_BODY_ERROR_MESSAGE =
      "Body unable to be parsed in LeaveClubServlet: ";

  /**
   * Message to be logged when an invalid ID token is passed in or a valid ID token that is
   * associated with a user who does not have the permissions to update the Club is passed in.
   */
  public static final String LOG_SECURITY_MESSAGE =
      "Forbidden action attempted: ";

  /**
   * The error string sent by the response object in doPost when the body of the 
   * POST request does not have a required field.
   */
  public static final String NO_FIELD_ERROR = "Missing \"%s\" field in JSON";
  /** The message to be logged when the body of the GET request does not have required fields. */
  public static final String LOG_INPUT_ERROR_MESSAGE =
      "Error with JSON input in GetProfileServlet: ";
  /** Name of the key in the input JSON that corresponds to the ID token. */
  public static final String ID_TOKEN_FIELD_NAME = "idToken";

  private static final Gson gson = new Gson();
  private static final HttpTransport transport = new NetHttpTransport();
  private static final GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
  private final GoogleIdTokenVerifier verifier;
  private final StorageHandlerApi storageHandler;

  /**
   * Overloaded constructor for dependency injection.
   * @param verifier the class that verifies the validity of the ID token
   * @param storageHandler the {@link StorageHandlerApi} that is used when deleting the membership
   */
  public LeaveClubServlet(GoogleIdTokenVerifier verifier, StorageHandlerApi storageHandler) {
    super();
    this.storageHandler = storageHandler;
    this.verifier = verifier;
  }

  /**
   * Explicit default constructor used for instantiating the servlet when not testing.
   */
  public LeaveClubServlet() {
    super();
    this.storageHandler = new StorageHandlerApi();
    this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory).build();
  }

  /**
   * Deletes a membership from the database using user ID and club ID.
   * @param request the POST request that must have a valid JSON representation of the userId and
   *     the {@code "idToken"} of the user to be passed in order to delete a membership from the
   *     Memberships table in the database. If the required fields don't exist, the response
   *     object will send a "400 Bad Request error". If the membership already does not exist in
   *     the database, the response object will send a "409 Conflict" status. If the JSON body is
   *     not valid, and unable to be parsed, the response object will send a
   *     "500 Internal Server error". If the user attempting to leave the Club does not have a
   *     valid ID token, the response object will send a "403 Forbidden error"
   * @param response the response from this method, will set the status to 200 (OK).
   *     If the request object has a valid JSON body without the required fields "idToken" and
   *     "clubId", the response will send a "400 Bad Request error". If the request object is
   *     attempting to delete a membership which does not exist in the database, the response will
   *     send a "409 Conflict" status. If the request object is unable to be parsed, the response
   *     will send a "500 Internal Server error". If the request object does not have a valid ID
   *     token, the response object will send a "403 Forbidden error"
   * @throws IOException if an input or output error is detected when the servlet handles the request
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      Map clubAndUserInfo = gson.fromJson(request.getReader(), Map.class);
      String idToken = (String) clubAndUserInfo.get(ID_TOKEN_FIELD_NAME);
      String userId = AuthenticationHelper.getUserIdFromIdToken(idToken, verifier);
      if (userId == null) {
        throw new IllegalArgumentException(String.format(NO_FIELD_ERROR, Person.USER_ID_FIELD_NAME));
      }
      String clubId = (String) clubAndUserInfo.get(Club.CLUB_ID_FIELD_NAME);
      if (clubId == null) {
        throw new IllegalArgumentException(String.format(NO_FIELD_ERROR, Club.CLUB_ID_FIELD_NAME));
      }
      storageHandler.deleteMembership(userId, clubId);
    } catch (IllegalArgumentException e) {
      System.out.println(LOG_INPUT_ERROR_MESSAGE + e.getMessage());
      if (e.getMessage() == MembershipConstants.PERSON_NOT_IN_CLUB) {
        response.sendError(HttpServletResponse.SC_CONFLICT,
                           MembershipConstants.PERSON_NOT_IN_CLUB);
      } else {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
      }
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
    response.setStatus(200);
  }
}
