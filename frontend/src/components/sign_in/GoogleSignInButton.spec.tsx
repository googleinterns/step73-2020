import * as React from "react";
import * as ReactDOM from "react-dom";
import * as TestUtils from "react-dom/test-utils";
import GoogleSignInButton from "./GoogleSignInButton"
import { ServiceContext } from "../contexts/contexts";

const EXPECTED_SCOPES = "profile email openid";
const expectedFailure = jest.fn();
const expectedConsumer = jest.fn();
const EXPECTED_TEXT = "Sign in with Google";

const signInMock= jest.fn();
const mockedAuthHandlerService = {
  signIn: signInMock,
};

it("calls the correct function on click", () => {
  const div = TestUtils.renderIntoDocument(
    <div>
      <ServiceContext.Provider
        value={{authenticationHandlerService: mockedAuthHandlerService}}
      >
        <GoogleSignInButton
          onFailure={expectedFailure}
          scope={EXPECTED_SCOPES}
          text={EXPECTED_TEXT}
          tokenConsumer={expectedConsumer} 
        />
      </ServiceContext.Provider>
    </div>
  );
  TestUtils.Simulate.click(div.querySelector("button"));
  expect(signInMock).toHaveBeenCalledWith(
        EXPECTED_SCOPES, expectedConsumer, expectedFailure);
});

it("displays the right text", () => {
  const div = TestUtils.renderIntoDocument(
    <div>
      <ServiceContext.Provider 
        value={{authenticationHandlerService: mockedAuthHandlerService}}
      >
        <GoogleSignInButton
          onFailure={expectedFailure}
          scope={EXPECTED_SCOPES}
          text={EXPECTED_TEXT}
          tokenConsumer={expectedConsumer} 
        />
      </ServiceContext.Provider>
    </div>
  );
  expect(div.querySelector("button").textContent).toBe(EXPECTED_TEXT);
});
