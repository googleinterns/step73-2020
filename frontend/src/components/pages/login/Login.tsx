import * as React from "react";
import GoogleSignInButton from "../../sign_in/GoogleSignInButton";
import { PersonInterface } from "../../../services/backend_service_interface/backend_service_interface";
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
  const profileHandlerService = contextServices.profileHandlerService;

  const classes = useStyles();

  // Fires upon login success.
  const tokenConsumer = async (token: string) => {
    // Cache token in localStorage to keep user signed in after refresh.
    localStorage.setItem('token', token);
    loginStatusHandlerService.setUserToken(token);
    loginStatusHandlerService.setUserLoginStatus(/*Successfully logged in*/ true);
    props.handleUserLogin();
    try {
      await profileHandlerService.getPerson(loginStatusHandlerService.getUserToken());
    } catch (err) {
      const parsedToken = loginStatusHandlerService.getParsedToken();
      const person: PersonInterface = {
        nickname: parsedToken.name,
        email: parsedToken.email,
        userId: parsedToken.sub,
      }
      profileHandlerService.createPerson(person);
    }
  }

  const failureCallback = () => {
    // TODO: make this function handle login failure.
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
