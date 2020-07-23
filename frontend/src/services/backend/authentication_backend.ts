/**
 * Communicates with the backend to exchange an auth code and a redirect 
 * URI for an ID token.
 */
export class AuthenticationBackendService {
  /** Returns the ID token given an auth code and redirect URI. */
  async retrieveToken(code: string, redirectUri: string): Promise<string> {
    let bodyContents = {
      code: code,
      redirectUri: redirectUri,
    }

    console.log(bodyContents);
    let response = await fetch("/api/retrieve-token", { 
      method: "POST",
      body: JSON.stringify(bodyContents),
    });

    return response.text();
  }
}
