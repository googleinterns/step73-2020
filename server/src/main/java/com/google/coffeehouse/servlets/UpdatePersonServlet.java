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
import com.google.coffeehouse.common.Person;
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import com.google.coffeehouse.util.AuthenticationHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * Servlet to update a {@link Person} from Http POST Request Body (in JSON format),
 * save it in database, and return it in JSON format.
 */
@WebServlet("/api/update-person")
public class UpdatePersonServlet extends HttpServlet {
  /** The fields of the Person object that can be updated. */
  public static final List<String> updateableFields = Arrays.asList(
      Person.NICKNAME_FIELD_NAME, Person.EMAIL_FIELD_NAME, Person.PRONOUNS_FIELD_NAME);
  /** 
   * The error string sent by the response object in doPost when the body of the 
   * POST request cannot be parsed for any reason.
   */
  public static final String BODY_ERROR = "- unable to parse body.";
  /** Message to be logged when the body of the POST request cannot be parsed. */
  public static final String LOG_BODY_ERROR_MESSAGE =
      "Body unable to be parsed in UpdatePersonServlet: ";
  /** Message to be logged when an invalid ID token is passed in. */
  public static final String LOG_SECURITY_MESSAGE = "Forbidden action attempted: ";
  /** 
   * The error string sent by the response object in doPost when the body of the 
   * POST request does not have a required field.
   */
  public static final String NO_FIELD_ERROR = "No \"%s\" found in JSON.";
  /** Message to be logged when the body of the POST request does not have required fields. */
  public static final String LOG_INPUT_ERROR_MESSAGE =
      "Error with JSON input in UpdatePersonServlet: ";
  /** Name of the key in the input JSON that corresponds to the person object. */
  public static final String PERSON_FIELD_NAME = "person";
  /** Name of the key in the input JSON that corresponds to the update mask. */
  public static final String UPDATE_MASK_FIELD_NAME = "updateMask";
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
   * @param storageHandler the {@link StorageHandlerApi} that is used when fetching the Person
   */
  public UpdatePersonServlet(GoogleIdTokenVerifier verifier, StorageHandlerApi storageHandler) {
    super();
    this.storageHandler = storageHandler;
    this.verifier = verifier;
  }

  /** 
   * Explicit default constructor used for instantiating the servlet when not testing.
   */
  public UpdatePersonServlet() {
    super();
    this.storageHandler = new StorageHandlerApi();
    this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory).build();
  }

  /** 
   * Updates a Person object in the database and returns that object in JSON format.
   * @param request the POST request that must have a valid JSON representation of the Person to be
   *     updated, a {@code "idToken"} key that corresponds to the OpenID ID token of the user
   *     who wants to update their Person, as well as an optional mask of fields that will be
   *     updated as its body. The Person to be updated must be represnted by a {@code "person"}
   *     key, who's value is an object that represents the updated Person. The update mask must
   *     have the key {@code "updateMask"} and the value of a comma separated list of the fields to
   *     be updated from the "person" key. If no update mask exists, all fields from the "person"
   *     key will be used to update the Person. If the request does not have a "person" key,
   *     or is syntactically incorrect, the response object will send a "400 Bad Request error". If
   *     the request does not have an "idToken" key or has an invalid ID token associated with that
   *     key, the response object will send a "403 Forbidden error"
   * @param response the response from this method, will contain the updated Person in JSON format.
   *     If the request object does not have a valid "person" key (or is syntactically incorrect)
   *     this object will send a "400 Bad Request error". If the request does not have an "idToken"
   *     key or has an invalid ID token associated with that key, the response object will send a
   *     "403 Forbidden error"
   * @throws IOException if an input or output error is detected when the servlet handles the request
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Person personToUpdate;
    try {
      // Get the update mask / updated Person from the post request.
      Map mappedRequest = gson.fromJson(request.getReader(), Map.class);
      Map personInfo = (Map) mappedRequest.getOrDefault(PERSON_FIELD_NAME, null);
      if (personInfo == null) {
        throw new IllegalArgumentException(String.format(NO_FIELD_ERROR, PERSON_FIELD_NAME));
      }
      String rawUpdateMask = (String) mappedRequest.getOrDefault(UPDATE_MASK_FIELD_NAME, null);

      // Get the Person from the database and convert it to JSON for easy manipulation.
      String idToken = (String) mappedRequest.get(ID_TOKEN_FIELD_NAME);
      String userId = AuthenticationHelper.getUserIdFromIdToken(idToken, verifier);

      personToUpdate = storageHandler.fetchPersonFromId(userId);
      JsonObject personToUpdateJson = gson.toJsonTree(personToUpdate).getAsJsonObject();

      // Merge the updates from the request with the Person from the database.
      List<String> mask = rawUpdateMask == null 
          ? updateableFields
          : Arrays.asList(rawUpdateMask.split(","));

      mask.stream()
          .filter(updateableFields::contains)
          .forEach(elem -> personToUpdateJson.addProperty(elem, (String) personInfo.get(elem)));

      // Convert the Person from the database back to a Person object.
      personToUpdate = gson.fromJson(personToUpdateJson, Person.class);
    } catch (IllegalArgumentException e) {
      System.out.println(LOG_INPUT_ERROR_MESSAGE + e.getMessage());
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
      return;
    } catch (GeneralSecurityException e) {
      System.out.println(LOG_SECURITY_MESSAGE + e.getMessage());
      response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
      return;
    } catch (Exception e) {
      System.out.println(LOG_BODY_ERROR_MESSAGE + e.getMessage());
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, BODY_ERROR);
      return;
    }

    personToUpdate.save();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(personToUpdate));
  }
}
