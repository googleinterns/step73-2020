import * as React from "react";
import GoogleSignInButton from "../../sign_in/GoogleSignInButton";

export const Login = () => {
  // Fires upon login success
  const tokenConsumer = (token: string) => {
    // Temporary: console.logs the parsed token
    // TODO: set boolean logged in
    // TODO: set jwt
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
