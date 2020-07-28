import {
  FailureToGetClubError,
  FailureToGetClubsError,
  FailureToCreateClubError,
  YourClubsHandlerService,
} from "./your_clubs_handler_service";
import { MembershipType } from "../backend_service_interface/backend_service_interface";

const testBook = {
  bookId: "BOOK_ID",
  title: "TITLE",
  author: "AUTHOR",
  isbn: "ISBN",
}
const CLUB_ID = "CLUB_ID";
const testClub = {
  name: "NAME",
  clubId: CLUB_ID,
  ownerId: "OWNER_ID",
  contentWarnings: ["1", "2"],
  description: "DESCRIPTION",
  currentBook: testBook,
}
const TOKEN = "Token";
const MEMBERSHIP = MembershipType.Member;

const createClubSuccessful = jest.fn().mockReturnValue(
  new Promise((resolve, reject) => resolve(testClub));
);
const listClubsSuccessful = jest.fn().mockReturnValue(
  new Promise((resolve, reject) => resolve([testClub]));
);
const leaveClubSuccessful = jest.fn().mockReturnValue(
  new Promise((resolve, reject) => resolve(200));
);
const joinClubSuccessful = jest.fn().mockReturnValue(
  new Promise((resolve, reject) => resolve(200));
);
const getClubSuccessful = jest.fn().mockReturnValue(
  new Promise((resolve, reject) => resolve(testClub));
);

const clubService = new YourClubsHandlerService({
  createClub: createClubSuccessful,
  listClubs: listClubsSuccessful,
  leaveClub: leaveClubSuccessful,
  joinClub: joinClubSuccessful,
  getClub: getClubSuccessful,
});

const createClubFailure = jest.fn().mockImplementation(() => {
  throw new Error();
});
const listClubFailure = jest.fn().mockImplementation(() => {
  throw new Error();
});
const getClubFailure = jest.fn().mockImplementation(() => {
  throw new Error();
});
const leaveClubFailure = jest.fn().mockReturnValue(
  new Promise((resolve, reject) => resolve(500));
);
const joinClubFailure = jest.fn().mockReturnValue(
  new Promise((resolve, reject) => resolve(500));
);

const failingClubService = new YourClubsHandlerService({
  createClub: createClubFailure,
  listClubs: listClubFailure,
  leaveClub: leaveClubFailure,
  joinClub: joinClubFailure,
  getClub: getClubFailure,
});

it("calls the createClub function correctly", async () => {
  const response = await clubService.createClub(testClub);
  expect(createClubSuccessful).toHaveBeenCalledWith(testClub);
  expect(response).toBe(testClub);
});

it("calls the listClubs function correctly", async () => {
  const response = await clubService.listClubs(MEMBERSHIP, TOKEN);
  expect(listClubsSuccessful).toHaveBeenCalledWith(MEMBERSHIP, TOKEN);
  expect(response).toStrictEqual([testClub]);
});

it("calls the leaveClub function correctly", async () => {
  const response = await clubService.leaveClub(CLUB_ID, TOKEN);
  expect(leaveClubSuccessful).toHaveBeenCalledWith(CLUB_ID, TOKEN);
  expect(response).toBe(true);
});

it("calls the joinClub function correctly", async () => {
  const response = await clubService.joinClub(CLUB_ID, TOKEN);
  expect(joinClubSuccessful).toHaveBeenCalledWith(CLUB_ID, TOKEN);
  expect(response).toBe(true);
});

it("calls the getClub function correctly", async () => {
  const response = await clubService.getClub(CLUB_ID);
  expect(getClubSuccessful).toHaveBeenCalledWith(CLUB_ID);
  expect(response).toBe(testClub);
});

it("throws FailureToGetClubsError when getClub fails", async () => {
  await expect(failingClubService.getClub(CLUB_ID))
  .rejects
  .toThrow(FailureToGetClubsError);
});

it("throws FailureToGetClubsError when listClubs fails", async () => {
  await expect(failingClubService.listClubs(MEMBERSHIP, TOKEN))
  .rejects
  .toThrow(FailureToGetClubsError);
});

it("throws FailureToCreateClubError when createClub fails", async () => {
  await expect(failingClubService.createClub(testClub))
  .rejects
  .toThrow(FailureToCreateClubError);
});

it("returns false if joinClub does not respond with 200", async () => {
  const response = await failingClubService.joinClub(CLUB_ID, TOKEN);
  expect(joinClubFailure).toHaveBeenCalledWith(CLUB_ID, TOKEN);
  expect(response).toBe(false);
});

it("returns false if leaveClub does not respond with 200", async () => {
  const response = await failingClubService.leaveClub(CLUB_ID, TOKEN);
  expect(leaveClubFailure).toHaveBeenCalledWith(CLUB_ID, TOKEN);
  expect(response).toBe(false);
});
