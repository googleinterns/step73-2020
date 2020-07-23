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

import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import com.google.coffeehouse.common.Club;
import com.google.coffeehouse.common.MembershipConstants;
import com.google.coffeehouse.common.Person;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * Servlet to leave a club from Http POST Request Body (in JSON format) containing a club ID
 * and user ID that exists in the database, and return a success status 200 (OK).
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
   * The error string sent by the response object in doPost when the body of the 
   * POST request does not have a required field.
   */
  public static final String NO_FIELD_ERROR = "Missing \"%s\" field in JSON";
  /** The message to be logged when the body of the GET request does not have required fields. */
  public static final String LOG_INPUT_ERROR_MESSAGE =
      "Error with JSON input in GetProfileServlet: ";

  private static final Gson gson = new Gson();
  private final StorageHandlerApi storageHandler;

  /**
   * Overloaded constructor for dependency injection.
   * @param storageHandler the {@link StorageHandlerApi} that is used when deleting the membership
   */
  public LeaveClubServlet(StorageHandlerApi storageHandler) {
    super();
    this.storageHandler = storageHandler;
  }

  /**
   * Explicit default constructor used for instantiating the servlet when not testing.
   */
  public LeaveClubServlet() {
    super();
    this.storageHandler = new StorageHandlerApi();
  }

  /** 
   * Deletes a membership from the database using user ID and club ID.
   * @param request the POST request that must have a valid JSON representation of the userId and
   *     clubId to be passed in order to delete a membership from the Memberships table in the
   *     database. If the required fields don't exist, the response object will send a
   *     "400 Bad Request error". If the JSON body is not valid, and unable to be parsed, the
   *     response object will send a "500 Internal Server error". If the membership already does
   *     not exist in the database, the response object will send a "409 Conflict" status
   * @param response the response from this method, will set the status to 200 (OK).
   *     If the request object has a valid JSON body without the required fields "userId" and
   *     "clubId", the response will send a "400 Bad Request error". If the request object is
   *     attempting to delete a membership which does not exist in the database, the response will
   *     send a "409 Conflict" status. If the request object is unable to be parsed, the response
   *     will send a "500 Internal Server error"
   * @throws IOException if an input or output error is detected when the servlet handles the request
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      Map clubAndUserInfo = gson.fromJson(request.getReader(), Map.class);
      String userId = (String) clubAndUserInfo.get(Person.USER_ID_FIELD_NAME);
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
    } catch (Exception e) {
      System.out.println(LOG_BODY_ERROR_MESSAGE + e.getMessage());
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, BODY_ERROR);
      return;
    }
    response.setStatus(200);
  }
}
