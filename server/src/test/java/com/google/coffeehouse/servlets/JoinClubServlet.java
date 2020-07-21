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

import com.google.coffeehouse.common.Person;
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * Servlet to get a {@link Person} from Http GET Request Body (in JSON format)
 * that exists in the database, and return it in JSON format.
 */
@WebServlet("/api/join-club")
public class JoinClubServlet extends HttpServlet {

  /** 
   * The error string sent by the response object in doPost when the body of the 
   * POST request cannot be used to fetch a {@link Person} for any reason.
   */
  public static final String BODY_ERROR = "- unable to parse body.";

  /** The logged error string when an error parsing the body of the POST request is encountered */
  public static final String LOG_BODY_ERROR_MESSAGE = 
      "Body unable to be parsed in JoinClubServlet: ";
  private static final Gson gson = new Gson();
  private StorageHandlerApi storageHandler = new StorageHandlerApi();

  /**
   * Overloaded constructor for dependency injection.
   * @param storageHandler the {@link StorageHandlerApi} that is used when fetching the Person
   */
  public JoinClubServlet(StorageHandlerApi storageHandler) {
    super();
    this.storageHandler = storageHandler;
  }

  /**
   * Explicity default constructor used for instantiating the servlet when not testing.
   */
  public JoinClubServlet() {
    super();
  }

  /** 
   * Adds a membership to the database using user and club ID.
   * @param request the POST request that must have a valid JSON representation of the userId and clubID
   *     to be passed in order to add a membership to the Mmeberships table in the database. If this is
   *     not the case the response will send a "400 Bad Request error"
   * @param response the response from this method, will contain the object in JSON format.
   *     If the request object does not have a valid JSON body that describes the Person, this object
   *     will send a "400 Bad Request error"
   * @throws IOException if an input or output error is detected when the servlet handles the request
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      Map clubAndUserInfo = gson.fromJson(request.getReader(), Map.class);
      String clubId = clubAndUserInfo.get("clubId").toString();
      String userId = clubAndUserInfo.get("userId").toString();
      StorageHandlerApi.addMembership(userId, clubId);
      response.setStatus(200);
    } catch (Exception e) {
      System.out.println(LOG_BODY_ERROR_MESSAGE + e.getMessage());
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, BODY_ERROR);
    }
  }
}
