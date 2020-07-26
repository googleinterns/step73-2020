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
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.coffeehouse.common.Person;
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import com.google.coffeehouse.storagehandler.StorageHandler;
import com.google.coffeehouse.util.AuthenticationHelper;
import com.google.coffeehouse.util.TokenVerifier;
import com.google.gson.Gson;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to get a {@link Person} from Http GET Request URL parameters
 * that exists in the database through a call to the Storage Handler API,
 * and return it in JSON format.
 */
@WebServlet("/api/get-profile")
public class GetProfileServlet extends HttpServlet {
  /** Message to be logged when the GET request does not have a required URL parameter. */
  public static final String NO_FIELD_ERROR = "No \"%s\" parameter found.";
  /** Message to be logged when a non-security related exception is thrown in the servlet. */
  public static final String LOG_INPUT_ERROR_MESSAGE =
      "Exception encountered in GetProfileServlet: ";
  /**
   * Message to be logged when an invalid ID token is passed in or a no ID token is passed in.
   */
  public static final String LOG_SECURITY_MESSAGE = "Forbidden action attempted: ";
  /** Name of the key in the input JSON that corresponds to the ID token. */
  public static final String ID_TOKEN_PARAMETER = "idToken";

  private static final Gson gson = new Gson();
  private static final HttpTransport transport = new NetHttpTransport();
  private static final GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
  private final TokenVerifier verifier;
  private final StorageHandlerApi storageHandler;

  /**
   * Overloaded constructor for dependency injection.
   * @param verifier the class that verifies the validity of the ID token
   * @param storageHandler the {@link StorageHandlerApi} that is used when fetching the Person
   */
  public GetProfileServlet(TokenVerifier verifier, StorageHandlerApi storageHandler) {
    super();
    this.storageHandler = storageHandler;
    this.verifier = verifier;
  }

  /**
   * Explicity default constructor used for instantiating the servlet when not testing.
   */
  public GetProfileServlet() {
    super();
    this.storageHandler = new StorageHandlerApi();
    this.verifier = new TokenVerifier();
  }
  
  /** 
   * Returns a {@link Person} object in JSON format from information in the database.
   * @param request the GET request that must have a {@code "idToken"} URL parameter corresponding
   *     to the user's OpenID ID token. If the required "idToken" parameter doesn't exist, the
   *     response object will send a "400 Bad Request error". If the person's userdId (extracted
   *     from the ID token) does not exist in the database, the response object will send a
   *     "404 Not Found error". If the user does not have a valid ID token, the response object
   *     will send a "403 Forbidden error"
   * @param response the response from this method, will contain the object in JSON format.
   *     If the required "idToken" parameter from the request doesn't exist, this object will send
   *     a "400 Bad Request error". If the person's userdId (extracted from the ID token) does not
   *     exist in the database, this object will send a "404 Not Found error". If the user does not
   *     have a valid ID token, this object will send a "403 Forbidden error"
   * @throws IOException if an input or output error is detected when the servlet handles the request
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Person person;
    try {
      String idToken = request.getParameter(ID_TOKEN_PARAMETER);
      if (idToken == null) {
        throw new IllegalArgumentException(String.format(NO_FIELD_ERROR, ID_TOKEN_PARAMETER));
      }
      String userId = AuthenticationHelper.getUserIdFromIdToken(idToken, verifier);
      person = storageHandler.fetchPersonFromId(userId);
    } catch (GeneralSecurityException e) {
      System.out.println(LOG_SECURITY_MESSAGE + e.getMessage());
      response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
      return;
    } catch (Exception e) {
      System.out.println(LOG_INPUT_ERROR_MESSAGE + e.getMessage());
      if (e.getMessage() == StorageHandler.PERSON_DOES_NOT_EXIST) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND,
                           StorageHandler.PERSON_DOES_NOT_EXIST);
      } else {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
      }
      return;
    }
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(person));
  }
}
