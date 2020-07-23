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

import com.google.coffeehouse.common.Club;
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import com.google.coffeehouse.storagehandler.StorageHandler;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * Servlet to get a {@link Club} from Http GET Request Body (in JSON format)
 * that exists in the database through a call to the Storage Handler API,
 * and return it in JSON format.
 */
@WebServlet("/api/get-club")
public class GetClubServlet extends HttpServlet {
  /** 
   * The error string sent by the response object in doGet when the body of the
   * GET request cannot be used to fetch a {@link Club} for any reason.
   */
  public static final String BODY_ERROR = "- unable to parse body.";
  /** The logged error string when an error parsing the body of the GET request is encountered. */
  public static final String LOG_BODY_ERROR_MESSAGE =
      "Body unable to be parsed in GetClubServlet: ";
  /** Message to be logged when the body of the GET request does not have required fields. */
  public static final String LOG_INPUT_ERROR_MESSAGE =
      "Error with JSON input in GetClubServlet: No \"%s\" found in JSON.";

  private static final Gson gson = new Gson();
  private final StorageHandlerApi storageHandler;

  /**
   * Overloaded constructor for dependency injection.
   * @param storageHandler the {@link StorageHandlerApi} that is used when fetching the Club
   */
  public GetClubServlet(StorageHandlerApi storageHandler) {
    super();
    this.storageHandler = storageHandler;
  }

  /**
   * Explicit default constructor used for instantiating the servlet when not testing.
   */
  public GetClubServlet() {
    super();
    this.storageHandler = new StorageHandlerApi();
  }
  
  /** 
   * Returns a {@link Club} object in JSON format from information in the database.
   * @param request the GET request that must have a valid JSON representation of the "clubId" to
   *     be passed in order to fetch a Club from ID in the database. If this is not the case the
   *     response will send a "400 Bad Request error"
   * @param response the response from this method, will contain the object in JSON format.
   *     If the request object does not have a valid JSON body that describes the Club, this object
   *     will send a "400 Bad Request error"
   * @throws IOException if an input or output error is detected when the servlet handles the request
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Club club;
    try {
      Map requestInfo = gson.fromJson(request.getReader(), Map.class);
      String clubId = (String) requestInfo.get(Club.CLUB_ID_FIELD_NAME);
      if (clubId == null) {
        throw new IllegalArgumentException(
            String.format(LOG_INPUT_ERROR_MESSAGE, Club.CLUB_ID_FIELD_NAME));
      }
      club = storageHandler.fetchClubFromId(clubId);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      if (e.getMessage().equals(StorageHandler.CLUB_DOES_NOT_EXIST)) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND,
                           StorageHandler.CLUB_DOES_NOT_EXIST);
      } else {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
      }
      return;
    } catch (Exception e) {
      System.out.println(LOG_BODY_ERROR_MESSAGE + e.getMessage());
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, BODY_ERROR);
      return;
    }
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(club));
  }
}
