import { AuthenticationHandlerService, FailureToSignInError } from "./authentication_handler_service";

// load these objects to overwrite gapi calls.
let authInstance = {};
window.gapi = {
  load: jest.fn(),
  auth2: {
    getAuthInstance: jest.fn().mockReturnValue(authInstance),
  },
};

const EXPECTED_SCOPES = "profile email openid";
const TOKEN = "Test token";
const authService = new AuthenticationHandlerService({
  retrieveToken: jest.fn().mockReturnValue(TOKEN),
});
const failingAuthService = new AuthenticationHandlerService({
  retrieveToken: jest.fn().mockImplementation(() => {throw new Error()}),
});
const successfulGrantOffileAccess = jest.fn().mockReturnValue({code: "123"});

it("returns the correct string if successful", async () => {
  authInstance.grantOfflineAccess = successfulGrantOffileAccess;

  let token = await authService.signIn(EXPECTED_SCOPES);

  expect(token).toBe(TOKEN);
});

it("throws error if grantOffileAccess returns undefined", async () => {
  authInstance.grantOfflineAccess = jest.fn().mockReturnValue(undefined);

  await expect(authService.signIn(EXPECTED_SCOPES))
  .rejects
  .toThrow(FailureToSignInError);
});

it("throws error if grantOffileAccess throws error", async () => {
  authInstance.grantOfflineAccess = jest.fn().mockImplementation(() => {
    throw new Error();
  });

  await expect(authService.signIn(EXPECTED_SCOPES))
  .rejects
  .toThrow(FailureToSignInError);
});

it("throws error if backend API throws error", async () => {
  authInstance.grantOfflineAccess = successfulGrantOffileAccess;

  await expect(failingAuthService.signIn(EXPECTED_SCOPES))
  .rejects
  .toThrow(FailureToSignInError);
});

it("signs out the user", async () => {
  authInstance.grantOfflineAccess = successfulGrantOffileAccess;
  await authService.signIn(EXPECTED_SCOPES);

  authService.signOut().then(success => {
    expect(success).toBe(true);
  });
});
