import * as React from "react";
import * as ReactDOM from "react-dom";
import * as TestUtils from "react-dom/test-utils";
import GoogleSignInButton from "./GoogleSignInButton";
import { ServiceContext } from "../contexts/contexts";

const expectedFailure = jest.fn();
const expectedConsumer = jest.fn();
const EXPECTED_SCOPES = "profile email openid";
const EXPECTED_TEXT = "Sign in with Google";
const TOKEN = "Test token";

const signInMockSuccessful= jest.fn().mockReturnValue(
  new Promise((resolve, reject) => resolve(TOKEN));
);
const signInMockFailure= jest.fn().mockReturnValue(
  new Promise((resolve, reject) => {throw new Error()});
);

it("calls the correct function if successful", async () => {
  const mockedAuthHandlerService = {
    signIn: signInMockSuccessful,
  };
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
  await expect(signInMockSuccessful).toHaveBeenCalledWith(EXPECTED_SCOPES);
  expect(expectedConsumer).toHaveBeenCalledWith(TOKEN);
});

it("calls the correct function if failed", async () => {
  const mockedAuthHandlerService = {
    signIn: signInMockFailure,
  };
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
  await expect(signInMockSuccessful).toHaveBeenCalledWith(EXPECTED_SCOPES);
  expect(expectedFailure).toHaveBeenCalled();
});

it("displays the right text", () => {
  const mockedAuthHandlerService = {};
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
