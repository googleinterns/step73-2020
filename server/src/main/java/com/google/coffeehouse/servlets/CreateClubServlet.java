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
import com.google.coffeehouse.common.Book;
import com.google.coffeehouse.util.IdentifierGenerator;
import com.google.coffeehouse.util.UuidWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * Servlet to create a {@link Club} from Http POST Request Body (in JSON format),
 * save it in database, and return it in JSON format.
 */
@WebServlet("/api/create-club")
public class CreateClubServlet extends HttpServlet {
  /** 
   * The error string sent by the response object in doPost when the body of the 
   * POST request cannot be used to construct a {@link Club} for any reason.
   */
  public static final String BODY_ERROR = "- unable to parse body.";
  /** The logged error string when an error parsing the body of the POST request is encoutered. */
  public static final String LOG_BODY_ERROR_MESSAGE = 
      "LOGGING: Body unable to be parsed in CreateClubServlet: ";
  /** 
   * The error string sent by the response object in doPost when the body of the 
   * POST request does not have a required field.
   */
  public static final String NO_FIELD_ERROR = "No \"%s\" found in JSON.";
  /** Message to be logged when the body of the POST request does not have required fields. */
  public static final String LOG_INPUT_ERROR_MESSAGE =
      "Error with JSON input in CreateClubServlet: ";
  private static IdentifierGenerator idGen;
  private static final Gson gson = new Gson();

  /** 
   * Overloaded constructor for dependency injection.
   * @param idGen the {@link IdentifierGenerator} that is used when constructing the Club
   */
  public CreateClubServlet(IdentifierGenerator idGen) {
    super();
    this.idGen = idGen;
  }

  /** 
   * Explicit default constructor used for instantiating the servlet when not testing.
   */
  public CreateClubServlet() {
    super();
    this.idGen = new UuidWrapper();
  }

  /** 
   * Creates a {@link Club} object, saves it in the database and returns it in JSON format.
   * @param request the POST request that must have a valid JSON representation of the Club to be
   *     created as its body. If this is not the case the response will send a 
   *     "400 Bad Request error"
   * @param response the response from this method, will contain the created object in JSON format.
   *     If the request object does not have a valid JSON body that described the Club to be 
   *     created, this object will send a "400 Bad Request error"
   * @throws IOException if an input or output error is detected when the servlet handles the request
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Club newClub;
    try {
      Map clubInfo = gson.fromJson(request.getReader(), Map.class);
      Map bookInfo = (Map) clubInfo.getOrDefault(Club.CURRENT_BOOK_FIELD_NAME, null);
      if (bookInfo == null) {
        throw new IllegalArgumentException(
            String.format(NO_FIELD_ERROR, Club.CURRENT_BOOK_FIELD_NAME));
      }

      // Generate IDs for the club and the book.
      bookInfo.put(Book.BOOK_ID_FIELD_NAME, idGen.generateId());
      clubInfo.put(Club.CLUB_ID_FIELD_NAME, idGen.generateId());
      newClub = Club.fromMap(clubInfo);
    } catch (IllegalArgumentException e) {
      System.out.println(LOG_BODY_ERROR_MESSAGE + e.getMessage());
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
      return;
    } catch (Exception e) {
      System.out.println(LOG_BODY_ERROR_MESSAGE + e.getMessage());
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, BODY_ERROR);
      return;
    }

    newClub.save();

    // TODO: Once membership table is implemented, create a membership for the person who made Club

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(newClub));
  }
}
