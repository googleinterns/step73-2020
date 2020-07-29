import * as React from "react";
import App from "./components/App";
import { CLIENT_ID } from "./services/authentication_handler_service/authentication_constants";
import { render } from "react-dom";
import { Unloaded } from "./components/pages/unloaded/Unloaded";

const rootEl = document.getElementById("root");

// Perform initialization steps with gapi.
window.gapi.load('auth2', () => {
  window.gapi.auth2.init({
    client_id: CLIENT_ID,
  }).then(() => {
    // Upon the initialization of Auth2, render the page.
    render(<App/>, rootEl);
  });
});

// Render a spinner until we have access to Auth2.
render(<Unloaded />, rootEl);
