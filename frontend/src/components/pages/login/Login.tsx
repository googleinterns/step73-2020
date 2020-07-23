import * as React from "react";
import GoogleSignInButton from "../../sign_in/GoogleSignInButton";
import { UserLoginStatusContext } from "../../contexts/contexts";

export const Login = () => {
  const contextServices = React.useContext(UserLoginStatusContext);
  const loginStatusHandlerService = contextServices.loginStatusHandlerService;

  // Fires upon login success
  const tokenConsumer = (token: string) => {
    // Cache token in localStorage to keep user signed in after refresh.
    localStorage.setItem('token', token);
    loginStatusHandlerService.setUserToken(token);
    loginStatusHandlerService.setUserLoginStatus(/*Successfully logged in*/ true);

    // Temporary: console.logs the parsed token
    console.log(JSON.parse(atob(token.split('.')[1])));
  }

  const failureCallback = () => {
    // Temporary: console.logs when we fail sign in
    console.log("Failure!");
  }

  return (
    <>
      <h2>Login</h2>
      <p>This is the Login page.</p>
      <GoogleSignInButton
        onFailure={failureCallback}
        scope="profile email openid"
        text="Sign in with Google"
        tokenConsumer={tokenConsumer}
      />
    </>
  );
}
