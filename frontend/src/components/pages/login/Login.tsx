import * as React from "react";
import GoogleSignInButton from "../../sign_in/GoogleSignInButton";
import { ServiceContext } from "../../contexts/contexts";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    header: {
      fontFamily: 'sans-serif',
      fontSize: '400%',
      marginBottom: theme.spacing(2),
      marginTop: theme.spacing(60),
    },
    loginContainer: {
      display: 'flex',
      justifyContent: 'center',
    },
    loginContent: {
      display: 'flex',
      flexDirection: 'column',
    },
  }),
);

interface LoginProps {
  handleUserLogin(): void,
}

export const Login = (props: LoginProps) => {
  const contextServices = React.useContext(ServiceContext)
  const loginStatusHandlerService = contextServices.loginStatusHandlerService;

  const classes = useStyles();

  // Fires upon login success.
  const tokenConsumer = async (token: string) => {
    // Cache token in localStorage to keep user signed in after refresh.
    localStorage.setItem('token', token);
    loginStatusHandlerService.setUserToken(token);
    loginStatusHandlerService.setUserLoginStatus(/*Successfully logged in*/ true);
    props.handleUserLogin();

    // Determine if user profile exists; if not, create it.
    const getProfileURL = "/api/get-profile?" + new URLSearchParams({idToken: token});
    const getProfileResponse = await fetch(getProfileURL, {
      method: "GET",
    });
    if (getProfileResponse.status != 200) {
      const parsedToken = JSON.parse(atob(token.split('.')[1]));
      const person = {
        nickname: parsedToken.name,
        email: parsedToken.email,
        userId: parsedToken.sub,
      }
      const createPersonResponse = await fetch("/api/create-person", {
        method: "POST",
        body: JSON.stringify(person),
      });
    }
  }

  const failureCallback = () => {
    // Temporary: console.logs when we fail sign in
    console.log("Failure!");
  }

  return (
    <>
    <div className={classes.loginContainer}>
      <div className={classes.loginContent}>
        <h2 className={classes.header}>CoffeeHouse</h2>
      </div>
    </div>
    <div className={classes.loginContainer}>
      <div className={classes.loginContent}>
          <GoogleSignInButton
            onFailure={failureCallback}
            scope="profile email openid"
            text="Sign in with Google"
            tokenConsumer={tokenConsumer}
          />
      </div>
    </div>
    </>
  );
}
