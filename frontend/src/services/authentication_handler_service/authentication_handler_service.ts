import { BackendAuthenticationInterface } from "../backend_service_interface/backend_service_interface";

/** Error that occurs if sign in fails. */
export class FailureToSignInError extends Error {
  constructor() {
    super("Sign in has failed");

    // Explicitly set prototype to allow expect().toThrow() testing.
    Object.setPrototypeOf(this, FailureToSignInError.prototype);
  }
}

/**
 * Communicates with gapi to manage a user's login status
 * and retrieve auth codes / tokens
 */
export class AuthenticationHandlerService {
  token: string | undefined = undefined;
  authInstance = undefined;

  /** Backend is responsible for retrieving the ID token. */
  constructor(private readonly backend: BackendAuthenticationInterface) {}

  /**
   * Determines if a user is logged in.
   * @return true if user is logged in, false otherwise
   */
  getUserLoginStatus(): boolean {
    const auth = this.getAuthInstance()
    return auth.isSignedIn.get() && this.getToken() !== undefined;
  }

  /**
   * Gets the parsed ID token of the user.
   * @return the parsed token if it exists, otherwise the empty object
   */
  getParsedToken() {
    const token = this.getToken();
    return token
        ? JSON.parse(atob(token.split(".")[1]))
        : {};
  }

  /**
   * Gets the ID token of the user.
   * @return the token if it exists, otherwise undefined
   */
  getToken(): string | undefined {
    const auth = this.getAuthInstance()
    const response = auth.currentUser.get().getAuthResponse()
    if (!response.id_token) {
      return;
    }
    return response.id_token;
  }

  /**
   * Signs the user in, calls a function on the token retrieved from backend.
   * @param scopes the scopes our app requests for oauth
   * @param tokenConsumer a function that takes the token as input and is
   *     called when the token is received from the backend.
   * @throws FailureToSignInError if sign in fails
   */
  async signIn(scopes: string,
               tokenConsumer: (token: string) => void): Promise<void> {
    const code: string | undefined = await this.getAuthCode({scope: scopes});
    if (code === undefined) {
      throw new FailureToSignInError();
    }
    try {
      const redirectUri = window.location.origin;
      const token = await this.backend.retrieveToken(code, redirectUri);
      // Bind the token consumer to be called when Auth2 says user logged in.
      this.oneTimeBindToSignIn(token, tokenConsumer)
    } catch (err) {
      throw new FailureToSignInError();
    }
  }

  /**
   * Signs the user out.
   * @return false if the user is already signed out, true if successful
   */
  async signOut(): Promise<boolean> {
    const auth = this.getAuthInstance()
    if (!auth.isSignedIn.get()) {
      return false;
    }
    await auth.signOut();
    return true;
  }

  // TypeScript 4.0 will introduce variadic generics, until then this works.
  private oneTimeBindToSignIn<T>(arg: T, func: (a: T) => void) {
    // Infinite listeners are allowed, this make sure each listener runs once.
    let called = false;
    this.getAuthInstance().isSignedIn.listen((signIn: boolean) => {
      if (signIn && !called) {
        called = true;
        func(arg)
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
    const auth = this.getAuthInstance()
    try {
      const { code } = await auth.grantOfflineAccess(opt);
      return code;
    } catch (err) {
      return;
    }
  }
}
