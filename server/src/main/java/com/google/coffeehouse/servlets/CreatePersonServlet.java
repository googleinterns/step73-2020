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
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * Servlet to create a {@link Person} from Http POST Request Body (in JSON format),
 * save it in database, and return it in JSON format.
 */
@WebServlet("/api/create-person")
public class CreatePersonServlet extends HttpServlet {
  /** 
   * The error string sent by the response object in doPost when the body of the 
   * POST request cannot be used to construct a {@link Person} for any reason.
   */
  public static final String BODY_ERROR = "- unable to parse body.";

  /** The logged error string when an error parsing the body of the POST request is encountered */
  public static final String LOG_BODY_ERROR_MESSAGE = 
      "Body unable to be parsed in CreatePersonServlet: ";
  private static final Gson gson = new Gson();
  private final StorageHandlerApi handler;

  /** 
   * Overloaded constructor for dependency injection.
   * @param handler the {@link StorageHandlerApi} that is used when saving the Person
   */
  public CreatePersonServlet(StorageHandlerApi handler) {
    super();
    this.handler = handler;
  }

  /** 
   * Explicit default constructor used for instantiating the servlet when not testing.
   */
  public CreatePersonServlet() {
    super();
    this.handler = new StorageHandlerApi();
  }

  /** 
   * Creates a {@link Person} object, saves it in the database and returns it in JSON format.
   * @param request the POST request that must have a valid JSON representation of the Person to be
   *     created as its body. If this is not the case the response will send a 
   *     "400 Bad Request error"
   * @param response the response from this method, will contain the created object in JSON format.
   *     If the request object does not have a valid JSON body that describes the Person to be 
   *     created, this object will send a "400 Bad Request error"
   * @throws IOException if an input or output error is detected when the servlet handles the request
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Person newPerson;
    try {
      Map personInfo = gson.fromJson(request.getReader(), Map.class);
      newPerson = Person.fromMap(personInfo);
    } catch (Exception e) {
      System.out.println(LOG_BODY_ERROR_MESSAGE + e.getMessage());
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, BODY_ERROR);
      return;
    }

    newPerson.setStorageHandler(handler);
    newPerson.save();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(newPerson));
  }
}
