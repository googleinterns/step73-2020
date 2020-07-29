import * as React from "react";
import Button from "@material-ui/core/Button";
import { ServiceContext } from "../contexts/contexts";

interface GoogleSignInButtonProps {
  onFailure: () => void,
  scope: string,
  text: string,
  tokenConsumer: (token: string) => void,
}

const GoogleSignInButton = (props: GoogleSignInButtonProps) => {
  const contextServices = React.useContext(ServiceContext);
  const authenticationHandlerService =
      contextServices.authenticationHandlerService;

  const wrappedSignIn = async () => {
    try {
      const token = await authenticationHandlerService.signIn(props.scope);
      props.tokenConsumer(token);
    } catch (err) {
      props.onFailure();
    }
  }

  return (
    <Button
      color="primary"
      variant="contained"
      onClick={wrappedSignIn}
    >
      {props.text}
    </Button>
  );
}

export default GoogleSignInButton;
