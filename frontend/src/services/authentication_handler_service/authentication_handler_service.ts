import { BackendAuthenticationInterface } from "../backend_service_interface/backend_service_interface";
import { CLIENT_ID } from "./authentication_constants";

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

  /**
   * Signs the user in, retrieving a token from the backend.
   * @param scopes the scopes our app requests for oauth
   * @param tokenConsumer a function that is called once our token is generated
   * @param onFailure a function that is called if we fail to get a token/code
   */
  async signIn(
      scopes: string,
      tokenConsumer: (token: string) => void,
      onFailure: () => void): Promise<void> {
    let code: string | undefined = await this.getAuthCode({scope: scopes});
    if (code === undefined) {
      return onFailure();
    }
    try {
      let idToken: string = 
        await this.backend.retrieveToken(code, window.location.origin);
      tokenConsumer(idToken);
    } catch (err) {
      onFailure();
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
    return true;
  }
}
