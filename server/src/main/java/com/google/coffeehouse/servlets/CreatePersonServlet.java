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
import com.google.coffeehouse.util.IdentifierGenerator;
import com.google.coffeehouse.util.UuidWrapper;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * Servlet to create a {@link Person} from URL parameter input, 
 * save it in database, and return it in JSON format.
 */
@WebServlet("/create-person")
public class CreatePersonServlet extends HttpServlet {

  /** 
   * The error string sent by the response object in doPost when no email or nickname parameter 
   * exists.
   */
  public static final String EMAIL_OR_NICKNAME_ERROR = "- email or nickname not specified.";
  private IdentifierGenerator idGen = null;
  
  /** 
   * Overloaded constructor for dependency injection.
   * @param idGen the {@link IdentifierGenerator} that is used when constructing the Person
   */
  public CreatePersonServlet(IdentifierGenerator idGen) {
    super();
    this.idGen = idGen;
  }

  /** 
   * Explicit default constructor used for instantiating the servlet when not testing.
   */
  public CreatePersonServlet() {
    super();
  }

  /** 
   * Creates a {@link Person} object, saves it in the database and returns it in JSON format.
   * @param request the post request that must have {@code email} and {@code nickname} defined
   *     as parameters or the respone will send a "bad request error"
   * @param reponse the response from this method, will contain the created object in JSON format
   * @throws IOException if an input or output error is detected when the servlet handles the request
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String email = request.getParameter("email");
    String nickname = request.getParameter("nickname");
    String pronouns = request.getParameter("pronouns");

    if (email == null || nickname == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, EMAIL_OR_NICKNAME_ERROR);
      return;
    }

    Person.Builder personInProgress = Person.newBuilder(email, nickname);
    if (pronouns != null) {
      personInProgress.setPronouns(pronouns);
    }
    // check for dependency injection on ID generator
    if (idGen != null) {
      personInProgress.setIdGenerator(idGen);
    }
    Person newPerson = personInProgress.build();
    newPerson.save();

    Gson gson = new Gson();
    String json = gson.toJson(newPerson);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}
