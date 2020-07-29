import { AuthenticationHandlerService, FailureToSignInError } from "./authentication_handler_service";

// load these objects to overwrite gapi calls.
let authInstance = {
  isSignedIn: {
    listen: jest.fn().mockImplementation(func => func(true)),
  },
  signOut: jest.fn(),
};
window.gapi = {
  load: jest.fn(),
  auth2: {
    getAuthInstance: jest.fn().mockReturnValue(authInstance),
  },
};

const EXPECTED_SCOPES = "profile email openid";
const TOKEN = "Test token";
const ENCODED_TOKEN = "1.IlRlc3QgdG9rZW4i";
const tokenConsumer = jest.fn();
const authService = new AuthenticationHandlerService({
  retrieveToken: jest.fn().mockReturnValue(TOKEN),
});
const failingAuthService = new AuthenticationHandlerService({
  retrieveToken: jest.fn().mockImplementation(() => {throw new Error()}),
});
const successfulGrantOffileAccess = jest.fn().mockReturnValue({code: "123"});

it("returns the correct string if sign in successful", async () => {
  authInstance.grantOfflineAccess = successfulGrantOffileAccess;

  await authService.signIn(EXPECTED_SCOPES);

  expect(authInstance.isSignedIn.listen).toHaveBeenCalled();
});

it("throws error if grantOffileAccess returns undefined in sign in", async () => {
  authInstance.grantOfflineAccess = jest.fn().mockReturnValue(undefined);

  await expect(authService.signIn(EXPECTED_SCOPES))
  .rejects
  .toThrow(FailureToSignInError);
});

it("throws error if grantOffileAccess throws error in sign in", async () => {
  authInstance.grantOfflineAccess = jest.fn().mockImplementation(() => {
    throw new Error();
  });

  await expect(authService.signIn(EXPECTED_SCOPES))
  .rejects
  .toThrow(FailureToSignInError);
});

it("throws error if backend API throws error in sign in", async () => {
  authInstance.grantOfflineAccess = successfulGrantOffileAccess;

  await expect(failingAuthService.signIn(EXPECTED_SCOPES))
  .rejects
  .toThrow(FailureToSignInError);
});

it("signs out the user if they're signed in", async () => {
  await authService.signIn(EXPECTED_SCOPES);
  authInstance.isSignedIn.get = jest.fn().mockReturnValue(true);

  const success = await authService.signOut();
  expect(success).toBe(true);
});

it("returns false when signing out a signed out user", async () => {
  await authService.signIn(EXPECTED_SCOPES);
  authInstance.isSignedIn.get = jest.fn().mockReturnValue(false);

  const success = await authService.signOut();
  expect(success).toBe(false);
});

it("gets a user's login status if they're logged in", async () => {
  authInstance.isSignedIn.get = jest.fn().mockReturnValue(true);
  authService.getToken = jest.fn().mockReturnValue(TOKEN);

  const success = authService.getUserLoginStatus();
  expect(success).toBe(true);
});

it("gets a user's login status if they're logged out", async () => {
  authInstance.isSignedIn.get = jest.fn().mockReturnValue(false);
  authService.getToken = jest.fn().mockReturnValue(TOKEN);
  
  const success = authService.getUserLoginStatus();
  expect(success).toBe(false);
});

it("parses a token if one exists", async () => {
  authService.getToken = jest.fn().mockReturnValue(ENCODED_TOKEN);
  const result = authService.getParsedToken();
  expect(result).toBe(TOKEN);
});

it("returns an empty object if no parsed token exists", async () => {
  authService.getToken = jest.fn().mockReturnValue(undefined);
  const result = authService.getParsedToken();
  expect(result).toStrictEqual({});
});
