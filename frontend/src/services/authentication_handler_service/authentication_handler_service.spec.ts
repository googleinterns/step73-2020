import { AuthenticationHandlerService } from "./authentication_handler_service";

// load these objects to overwrite gapi calls.
let authInstance = {};
window.gapi = {
  load: jest.fn(),
  auth2: {
    getAuthInstance: jest.fn().mockReturnValue(authInstance),
  };
};

const EXPECTED_SCOPES = "profile email openid";
const TOKEN = "Test token";
const authService = new AuthenticationHandlerService({
  retrieveToken: jest.fn().mockReturnValue(TOKEN),
});
const failingAuthService = new AuthenticationHandlerService({
  retrieveToken: jest.fn().mockImplementation(() => throw new Error()),
});
const successfulGrantOffileAccess = jest.fn().mockReturnValue({code: "123"});

it("calls the token consumer function if successful", async () => {
  authInstance.grantOfflineAccess = successfulGrantOffileAccess;

  let expectedConsumer = jest.fn();
  await authService.signIn(EXPECTED_SCOPES, expectedConsumer, jest.fn());

  expect(expectedConsumer).toHaveBeenCalledWith(TOKEN);
});

it("calls failure if grantOffileAccess returns undefined", async () => {
  authInstance.grantOfflineAccess = jest.fn().mockReturnValue(undefined);

  const expectedFailure = jest.fn();
  await authService.signIn(EXPECTED_SCOPES, jest.fn(), expectedFailure);

  expect(expectedFailure).toHaveBeenCalled();
});

it("calls failure if grantOffileAccess throws error", async () => {
  authInstance.grantOfflineAccess = jest.fn().mockImplementation(() => {
    throw new Error();
  });

  const expectedFailure = jest.fn();
  await authService.signIn(EXPECTED_SCOPES, jest.fn(), expectedFailure);

  expect(expectedFailure).toHaveBeenCalled();
});

it("calls failure if backend API throws error", async () => {
  authInstance.grantOfflineAccess = successfulGrantOffileAccess;

  const expectedFailure = jest.fn();
  await failingAuthService.signIn(EXPECTED_SCOPES, jest.fn(), expectedFailure);

  expect(expectedFailure).toHaveBeenCalled();
});

it("signs out the user", async () => {
  authInstance.grantOfflineAccess = successfulGrantOffileAccess;
  await authService.signIn(EXPECTED_SCOPES, jest.fn(), jest.fn());

  authService.signOut().then(success => {
    expect(success).toBe(true);
  });
});
