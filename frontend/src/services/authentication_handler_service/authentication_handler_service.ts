import { BackendAuthenticationInterface } from "../backend_service_interface/backend_service_interface";
import { CLIENT_ID } from "./authentication_constants";

/** Error that occurs if sign in fails. */
export class FailureToSignInError extends Error {
  constructor() {
    super("Sign in has failed");

    // Explicitly set prototype to allow expect().toThrow() testing.
    Object.setPrototypeOf(this, FailureToSignInError.prototype);
  }
}

/**
 * Communicates with gapi to retrieve auth code from user.
 * Exchanges auth code and redirect URI for ID token from backend.
 */
export class AuthenticationHandlerService {
  /** Backend is responsible for retrieving the ID token. */
  constructor(private readonly backend: BackendAuthenticationInterface) {
    // Safe because the gapi script is loaded in the head with no async/defer.
    // TODO: Make gapi script async and call this when it has loaded
    window.gapi.load('auth2', () => {
      window.gapi.auth2.init({
        client_id: CLIENT_ID,
      });
    });
  }

  /**
   * Signs the user in, retrieving a token from the backend.
   * @param scopes the scopes our app requests for oauth
   * @return the string of the JWT token retrieved by the backend
   * @throws FailureToSignInError if sign in fails
   */
  async signIn(scopes: string): Promise<string> {
    const code: string | undefined = await this.getAuthCode({scope: scopes});
    if (code === undefined) {
      throw new FailureToSignInError();
    }
    try {
      return await this.backend.retrieveToken(code, window.location.origin);
    } catch (err) {
      throw new FailureToSignInError();
    }
  }

  /**
   * Signs the user out.
   * @return false if the user is already signed out, true if successful
   */
  async signOut(): Promise<boolean> {
    const authInstance = window.gapi.auth2.getAuthInstance();
    if (!authInstance) {
      return false;
    }
    await authInstance.signOut();
    localStorage.removeItem('token');
    return true;
  }

  private async getAuthCode(
      opt?: gapi.auth2.OfflineAccessOptions): Promise<string | undefined> {
    const authInstance = window.gapi.auth2.getAuthInstance();
    try {
      const { code } = await authInstance.grantOfflineAccess(opt);
      return code;
    } catch (err) {
      return;
    }
  }
}
