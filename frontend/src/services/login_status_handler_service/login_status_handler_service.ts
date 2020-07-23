import * as React from "react";

export interface LoginStatusHandlerInterface {
  token: string,
  userLoggedIn: boolean,
}

/**
 * Determines status of user login.
 * Updates status of user login, along with storing and retrieving user token.
 */
export class LoginStatusHandlerService implements LoginStatusHandlerInterface {
  token: string|undefined = undefined;
  userLoggedIn: boolean|undefined = undefined;

  /**
   * User token is cached in localStorage, which can be used to reliably
   * determine their login status independent of a page refresh.
   */
  constructor() {
    this.token = localStorage.getItem("token");
    this.userLoggedIn = (localStorage.getItem("token") == null ? false : true);
  }

  /**
   * Gets status of user login.
   * @return true if a user token is cached in local storage,
   *         false if user token does not exist.
   */
  public getUserLoginStatus(): boolean {
    return (this.userLoggedIn ? true : false);
  }

  public getUserToken(): string {
    return this.token;
  }

  setUserLoginStatus(loginStatus: boolean): void {
    this.userLoggedIn = loginStatus;
    if (!loginStatus) {
      this.setUserToken(undefined);
    }
  }

  setUserToken(userToken: string|undefined): void {
    this.token = userToken;
  }
}
