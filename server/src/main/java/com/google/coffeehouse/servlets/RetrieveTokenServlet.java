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

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.coffeehouse.util.AuthenticationHelper;
import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Map;
import java.security.GeneralSecurityException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * Servlet to retrieve an ID/auth/refresh token from an auth code and redirect URI input.
 */
@WebServlet("/api/retrieve-token")
public class RetrieveTokenServlet extends HttpServlet {
  /** The name of the key to be associated with the auth code in the JSON. */
  public static final String CODE_KEY_NAME = "code";
  /** The name of the key to be associated with the redirect URI in the JSON. */
  public static final String REDIRECT_URI_KEY_NAME = "redirectUri";
  /** 
   * The error string sent by the response object in doPost when the body of the 
   * POST request cannot be parsed into JSON and a code/redirect URI retrieved.
   */
  public static final String BODY_ERROR = "- unable to parse body.";
  /** The logged error string when an error parsing the body of the POST request is encountered. */
  public static final String LOG_BODY_ERROR_MESSAGE = 
      "Body unable to be parsed in RetrieveTokenServlet: ";
  /** Message of the exception thrown when unable to find a valid code/redirect URI in body. */
  public static final String NO_CODE_REDIRECTURI = 
      "Unable to find 'code' or 'redirectUri'";
  /** 
   * The error string sent by the response object in doPost when the ID token
   * fails the verification step.
   */
  public static final String INVALID_ID_TOKEN = "- invalid ID token.";
  /** The logged error string when an an ID token fails verification. */
  public static final String LOG_INVALID_ID_TOKEN_MESSAGE = 
      "ID token failed verification: ";

  private static final Gson gson = new Gson();
  private static final HttpTransport transport = new NetHttpTransport();
  private static final GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
  private GoogleIdTokenVerifier verifier =
      new GoogleIdTokenVerifier.Builder(transport, jsonFactory).build();
  
  // Empty code and redirectUri will be overwritten before executing request.
  private GoogleAuthorizationCodeTokenRequest tokenRequest =
      new GoogleAuthorizationCodeTokenRequest(
          transport,
          jsonFactory,
          AuthenticationHelper.CLIENT_ID,
          AuthenticationHelper.CLIENT_SECRET,
          /* code= */ "",
          /* redirectUri= */ "");

  /** 
   * Overloaded constructor for dependency injection.
   * @param verifier the class that verifies the validity of the ID token
   * @param tokenRequest the class that makes the request to exchange auth code for token
   */
  public RetrieveTokenServlet(
      GoogleIdTokenVerifier verifier, GoogleAuthorizationCodeTokenRequest tokenRequest) {
    super();
    this.verifier = verifier;
    this.tokenRequest = tokenRequest;
  }

  public RetrieveTokenServlet() {
    super();
  }

  /** 
   * Exchanges an auth code for an ID/auth/refresh token. Returns the ID token in JSON format.
   * @param request the POST request that must have a JSON body with a valid auth code and 
   *     redirect URI (associated with the keys defined by the CODE_KEY_NAME and 
   *     REDIRECT_URI_KEY_NAME constants).
   * @param response the response from this method, will contain the retrieved ID token as JSON.
   *     If the request object does not have the required JSON, this object will send a 
   *     "400 Bad Request error". If the ID token retrieved by this servlet cannot be verified,
   *     this object will send a "403 Forbidden error"
   * @throws IOException if an input or output error is detected when the servlet handles the request
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String code;
    String redirectUri;
    try {
      Map body = gson.fromJson(request.getReader(), Map.class);
      code = (String) body.get(CODE_KEY_NAME);
      redirectUri = (String) body.get(REDIRECT_URI_KEY_NAME);
      if (code == null || redirectUri == null) {
        throw new IllegalArgumentException(NO_CODE_REDIRECTURI);
      }
    } catch (Exception e) {
      System.out.println(LOG_BODY_ERROR_MESSAGE + e.getMessage());
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, BODY_ERROR);
      return;
    }

    // Exchange auth code for token.
    GoogleTokenResponse res = tokenRequest.setCode(code).setRedirectUri(redirectUri).execute();
    // TODO: getRefreshToken() and getAccessToken(), save it in our database next to the ID token
    String idToken = res.getIdToken();

    try {
      // Perform basic security to make sure the ID token is valid.
      GoogleIdToken partialIdToken = verifier.verify(idToken);
      if (partialIdToken == null) {
        throw new GeneralSecurityException(idToken);
      }
    } catch (Exception e) {
      System.out.println(LOG_INVALID_ID_TOKEN_MESSAGE + e.getMessage());
      response.sendError(HttpServletResponse.SC_FORBIDDEN, INVALID_ID_TOKEN);
      return;
    }
    
    response.setContentType("text/plain;");
    response.getWriter().println(idToken);
  }
}
