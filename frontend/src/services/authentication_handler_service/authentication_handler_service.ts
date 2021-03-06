import { BackendAuthenticationInterface } from "../backend_service_interface/backend_service_interface";

/** Error that occurs if sign in fails. */
export class FailureToSignInError extends Error {
  constructor() {
    super("Sign in has failed");

    // Explicitly set prototype to allow expect().toThrow() testing.
    Object.setPrototypeOf(this, FailureToSignInError.prototype);
  }
}

/** Fields available on a parsed ID token. */
export interface TokenInterface {
  at_hash: string,
  aud: string,
  azp: string,
  email: string,
  email_verified: boolean,
  exp: number,
  family_name: string,
  given_name: string,
  hd: string,
  iat: number,
  iss: string,
  locale: string,
  name: string,
  picture: string,
  sub: string,
}

/**
 * Communicates with gapi to manage a user's login status
 * and retrieve auth codes / tokens.
 */
export class AuthenticationHandlerService {
  token: string | undefined = undefined;
  authInstance: gapi.auth2.GoogleAuth | undefined = undefined;

  /** Backend is responsible for retrieving the ID token. */
  constructor(private readonly backend: BackendAuthenticationInterface) {}

  /**
   * Determines if a user is logged in.
   * @return true if user is logged in, false otherwise
   */
  getUserLoginStatus(): boolean {
    const auth = this.getAuthInstance();
    return auth.isSignedIn.get() && this.getToken() !== undefined;
  }

  /**
   * Gets the parsed ID token of the user.
   * @return the parsed token if it exists, otherwise undefined
   */
  getParsedToken(): TokenInterface | undefined {
    const token = this.getToken();
    return token
        ? JSON.parse(atob(token.split(".")[1]))
        : undefined;
  }

  /**
   * Gets the ID token of the user.
   * @return the token if it exists, otherwise undefined
   */
  getToken(): string | undefined {
    const auth = this.getAuthInstance();
    const response = auth.currentUser.get().getAuthResponse();
    if (!response.id_token) {
      return;
    }
    return response.id_token;
  }

  /**
   * Signs the user in, returns the user's ID token.
   * @param scopes the scopes our app requests for oauth
   * @return the retrieved ID token
   * @throws FailureToSignInError if sign in fails (Promise reject)
   */
  async signIn(scopes: string): Promise<string> {
    const code: string | undefined = await this.getAuthCode({scope: scopes});
    if (code === undefined) {
      throw new FailureToSignInError();
    }
    const redirectUri = window.location.origin;
    return new Promise(async (resolve, reject) => {
      try {
        const token = await this.backend.retrieveToken(code, redirectUri);
        const signedInToken = this.getToken();
        // If already logged in, listener won't run so we resolve here.
        if (signedInToken) {
          resolve(signedInToken);
          return;
        }
        this.oneTimeBindToSignIn(token, resolve);
      } catch (err) {
        reject(new FailureToSignInError());
      }
    })
  }

  /**
   * Signs the user out.
   * @return false if the user is already signed out, true if successful
   */
  async signOut(): Promise<boolean> {
    const auth = this.getAuthInstance();
    if (!auth.isSignedIn.get()) {
      return false;
    }
    await auth.signOut();
    return true;
  }

  private oneTimeBindToSignIn<T>(arg: T, func: (a: T) => void) {
    // Infinite listeners are allowed, this makes sure each listener runs once.
    let called = false;
    this.getAuthInstance().isSignedIn.listen((signIn: boolean) => {
      if (signIn && !called) {
        called = true;
        func(arg);
      }
    });
  }

  // Lazy initialization of the Auth2 instance singleton.
  private getAuthInstance() {
    if (!this.authInstance) {
      this.authInstance = window.gapi.auth2.getAuthInstance();
    }
    return this.authInstance;
  }

  private async getAuthCode(
      opt?: gapi.auth2.OfflineAccessOptions): Promise<string | undefined> {
    const auth = this.getAuthInstance();
    try {
      const { code } = await auth.grantOfflineAccess(opt);
      return code;
    } catch (err) {
      return;
    }
  }
}
