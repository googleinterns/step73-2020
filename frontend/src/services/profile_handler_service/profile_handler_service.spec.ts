import {
  FailureToCreatePersonError,
  FailureToUpdateProfileError,
  NonExistentProfileError,
  ProfileHandlerService,
} from "./profile_handler_service";

const testPerson = {
  nickname: "NICKNAME",
  email: "EMAIL",
  userId: "USER_ID",
}
const TOKEN = "Token";

const createPersonSuccessful = jest.fn().mockReturnValue(
  new Promise((resolve, reject) => resolve(testPerson));
);
const loadProfileSuccessful = jest.fn().mockReturnValue(
  new Promise((resolve, reject) => resolve(testPerson));
);
const updateProfileSuccessful = jest.fn().mockReturnValue(
  new Promise((resolve, reject) => resolve(testPerson));
);

const profileService = new ProfileHandlerService({
  createPerson: createPersonSuccessful,
  loadProfile: loadProfileSuccessful,
  updateProfile: updateProfileSuccessful,
});

const createPersonFailure = jest.fn().mockImplementation(() => {
  throw new Error();
});
const loadProfileFailure = jest.fn().mockImplementation(() => {
  throw new Error();
});
const updateProfileFailure = jest.fn().mockImplementation(() => {
  throw new Error();
});

const failingProfileService = new ProfileHandlerService({
  createPerson: createPersonFailure,
  loadProfile: loadProfileFailure,
  updateProfile: updateProfileFailure,
});

it("calls the createPerson function correctly", async () => {
  const response = await profileService.createPerson(testPerson);
  expect(createPersonSuccessful).toHaveBeenCalledWith(testPerson);
  expect(response).toBe(testPerson);
});

it("calls the loadProfile function correctly", async () => {
  const response = await profileService.getPerson(TOKEN);
  expect(loadProfileSuccessful).toHaveBeenCalledWith(TOKEN);
  expect(response).toBe(testPerson);
});

it("calls the updateProfile function correctly", async () => {
  const response = await profileService.updatePerson(testPerson, TOKEN);
  expect(updateProfileSuccessful).toHaveBeenCalledWith(testPerson, TOKEN);
  expect(response).toBe(testPerson);
});

it("throws FailureToCreatePersonError when createPerson fails", async () => {
  await expect(failingProfileService.createPerson(testPerson))
  .rejects
  .toThrow(FailureToCreatePersonError);
});

it("throws FailureToUpdateProfileError when updateProfile fails", async () => {
  await expect(failingProfileService.updatePerson(testPerson))
  .rejects
  .toThrow(FailureToUpdateProfileError);
});

it("throws NonExistentProfileError when loadProfile fails", async () => {
  await expect(failingProfileService.getPerson(TOKEN))
  .rejects
  .toThrow(NonExistentProfileError);
});
