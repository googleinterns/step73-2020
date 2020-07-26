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
import com.google.coffeehouse.common.Book;
import com.google.coffeehouse.common.Club;
import com.google.coffeehouse.common.Person;
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import com.google.coffeehouse.util.AuthenticationHelper;
import com.google.coffeehouse.util.TokenVerifier;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * Servlet to update a {@link Club} from Http POST Request Body (in JSON format),
 * save it in database, and return it in JSON format.
 */
@WebServlet("/api/update-club")
public class UpdateClubServlet extends HttpServlet {
  /** The fields of the Club object that can be updated. */
  public static final List<String> updateableClubFields = Arrays.asList(
      Club.DESCRIPTION_FIELD_NAME,
      Club.CONTENT_WARNINGS_FIELD_NAME);
  /** The fields of the Book object that can be updated. */
  public static final List<String> updateableBookFields = Arrays.asList(
      Book.AUTHOR_FIELD_NAME,
      Book.ISBN_FIELD_NAME,
      Book.TITLE_FIELD_NAME);
  /** 
   * The error string sent by the response object in doPost when the body of the 
   * POST request cannot be parsed for any reason.
   */
  public static final String BODY_ERROR = "- unable to parse body.";
  /** Message to be logged when the body of the POST request cannot be parsed. */
  public static final String LOG_BODY_ERROR_MESSAGE =
      "Body unable to be parsed in UpdateClubServlet: ";
  /** Message to be logged when the body of the POST request does not have required fields. */
  public static final String LOG_INPUT_ERROR_MESSAGE =
      "Error with JSON input in UpdateClubServlet: ";
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
  public static final String NO_FIELD_ERROR = "No \"%s\" found in JSON.";
  /** 
   * The error string sent by the response object in doPost when the user attempting
   * to update the club does not have permissions to do so.
   */
  public static final String LACK_OF_PRIVILEGE_ERROR = "Person does not have required privileges.";
  /** Name of the key in the input JSON that corresponds to the Club object. */
  public static final String CLUB_FIELD_NAME = "club";
  /** Name of the key in the input JSON that corresponds to the update mask. */
  public static final String UPDATE_MASK_FIELD_NAME = "updateMask";
  /** Name of the key in the input JSON that corresponds to the ID token. */
  public static final String ID_TOKEN_FIELD_NAME = "idToken";

  private static final Gson gson = new Gson();
  private static final HttpTransport transport = new NetHttpTransport();
  private static final GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
  private final TokenVerifier verifier;
  private final StorageHandlerApi storageHandler;

  /** 
   * Overloaded constructor for dependency injection.
   * @param verifier the class that verifies the validity of the ID token
   * @param storageHandler the {@link StorageHandlerApi} that is used when fetching the Club/Book
   */
  public UpdateClubServlet(TokenVerifier verifier, StorageHandlerApi storageHandler) {
    super();
    this.storageHandler = storageHandler;
    this.verifier = verifier;
  }

  /** 
   * Explicit default constructor used for instantiating the servlet when not testing.
   */
  public UpdateClubServlet() {
    super();
    this.storageHandler = new StorageHandlerApi();
    this.verifier = new TokenVerifier();
  }

  /** 
   * Updates a Club object in the database and returns that object in JSON format.
   * @param request the POST request that must have a valid JSON representation of the Club to be
   *     updated, the {@code "idToken"} of the user who wants to update the Club, as well as an
   *     optional mask of fields that will be updated as its body. The Club to be updated must be
   *     represented by a {@code "club"} key, whose value is an object that represents the updated
   *     Club. Inside of this object, there will be a {@code "currentBook"} key that is associated
   *     with an object that represents the Book the Club is reading. The update mask must be
   *     associated with the key {@code "updateMask"} and have the value of a comma separated list
   *     of the fields to be updated from the "club" key. To specify an update inside of the book
   *     object inside of the Club, prepend the field name in the update mask with "currentBook.".
   *     If no update mask exists, all fields from the "club" key will be used to update the Club
   *     (and nested Book). If the request does not have the required keys, or is syntactically
   *     incorrect, the response object will send a "400 Bad Request error". If the user attempting
   *     to update the Club is not the Club owner or has an invalid ID token, the response object
   *     will send a "403 Forbidden error"
   * @param response the response from this method, will contain the updated Club in JSON format.
   *     If the request object does not have a valid JSON body (as described in the request
   *     parameter) this object will send a "400 Bad Request error". If the user attempting
   *     to update the Club is not the Club owner or has an invalid ID token, the response object
   *     will send a "403 Forbidden error"
   * @throws IOException if an input or output error is detected when the servlet handles the request
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Club club;
    try {
      JsonObject requestJson = gson.fromJson(request.getReader(), JsonObject.class);
      JsonObject updatedClubJson = requestJson.getAsJsonObject(CLUB_FIELD_NAME);
      JsonObject updatedBookJson = updatedClubJson.getAsJsonObject(Club.CURRENT_BOOK_FIELD_NAME);
      JsonElement updateMask = requestJson.get(UPDATE_MASK_FIELD_NAME);
      
      // Get attempted updates for the Club and the Book inside the Club.
      List<String> bookMask = new ArrayList<>();
      List<String> clubMask = new ArrayList<>();
      if (updateMask == null) {
        bookMask = updateableBookFields;
        clubMask = updateableClubFields;
      } else {
        // Cannot use stream here because Java will complain about local variables not being final.
        String[] elements = updateMask.getAsString().split(",");
        for (String element : elements) {
          boolean isBookUpdate = element.startsWith(Club.CURRENT_BOOK_FIELD_NAME + ".");
          if (isBookUpdate) {
            bookMask.add(element.split("\\.")[1]);
          } else {
            clubMask.add(element);
          }
        }
      }

      JsonElement clubIdElement = updatedClubJson.get(Club.CLUB_ID_FIELD_NAME);
      if (clubIdElement == null) {
        throw new IllegalArgumentException(String.format(NO_FIELD_ERROR, Club.CLUB_ID_FIELD_NAME));
      }
      club = storageHandler.fetchClubFromId(clubIdElement.getAsString());

      // Determine if the user has the permissions to actually make changes on the Club.
      JsonElement idToken = requestJson.get(ID_TOKEN_FIELD_NAME);
      if (idToken == null) {
        throw new GeneralSecurityException(AuthenticationHelper.INVALID_ID_TOKEN_ERROR);
      }
      
      String userId = AuthenticationHelper.getUserIdFromIdToken(idToken.getAsString(), verifier);
      if (!club.getOwnerId().equals(userId)) {
        throw new GeneralSecurityException(LACK_OF_PRIVILEGE_ERROR);
      }
      JsonObject clubJson = gson.toJsonTree(club).getAsJsonObject();
      JsonObject bookJson = clubJson.get(Club.CURRENT_BOOK_FIELD_NAME).getAsJsonObject();

      // Make the changes to the Book and the Club.
      bookMask.stream()
          .filter(updateableBookFields::contains)
          .forEach(elem -> bookJson.addProperty(elem, updatedBookJson.get(elem).getAsString()));
      clubMask.stream()
          .filter(updateableClubFields::contains)
          .forEach(elem -> {
            if (elem.equals(Club.CONTENT_WARNINGS_FIELD_NAME)) {
              clubJson.add(elem, updatedClubJson.get(elem).getAsJsonArray());
            } else {
              clubJson.addProperty(elem, updatedClubJson.get(elem).getAsString());
            }
          });
      clubJson.add(Club.CURRENT_BOOK_FIELD_NAME, bookJson);
      
      club = gson.fromJson(clubJson, Club.class);
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

    club.setStorageHandler(storageHandler);
    club.getCurrentBook().setStorageHandler(storageHandler);
    club.save();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(club));
  }
}
