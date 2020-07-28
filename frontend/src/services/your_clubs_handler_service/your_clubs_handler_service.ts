import { BackendYourClubsServiceInterface, ClubInterface, MembershipType } from "../backend_service_interface/backend_service_interface";


/** Error that occurs when loading clubs fails. */
export class FailureToLoadClubError extends Error {
  constructor() {
    super("Error occurred while loading clubs.");

    // Explicitly set prototype to allow expect().toThrow() testing.
    Object.setPrototypeOf(this, FailureToLoadClubError.prototype);
  }
}

/** Error that occurs when an attempt to create a club fails. */
export class FailureToCreateClubError extends Error {
  constructor(readonly club: ClubInterface) {
    super(`Error occurred while creating club '${club.name}'.`);

    // Explicitly set prototype to allow expect().toThrow() testing.
    Object.setPrototypeOf(this, FailureToCreateClubError.prototype);
  }
}

/** Error that occurs when getting a club fails. */
export class FailureToGetClubsError extends Error {
  constructor() {
    super("Error occurred while getting club.");

    // Explicitly set prototype to allow expect().toThrow() testing.
    Object.setPrototypeOf(this, FailureToGetClubsError.prototype);
  }
}

/**
 * Communicates with backend to create, join, leave, and retrieve clubs.
 */
export class YourClubsHandlerService {
  /** Backend is responsible for holding all YourClubs information. */
  constructor(private readonly backend: BackendYourClubsServiceInterface) {};

  /**
   * Creates a club on the backend given a club object.
   * @param club the club to be created
   * @return the club object created by the backend
   * @throws FailureToCreateClubError if the club was unable to be created
   */
  async createClub(club: ClubInterface): Promise<ClubInterface> {
    try {
      if (!club?.contentWarnings) {
        club.contentWarnings = [];
      }
      return await this.backend.createClub(club);
    } catch(err) {
      throw new FailureToCreateClubError(club);
    }
  }

  /**
   * Lists clubs that a user is in or not in.
   * @param token the ID token of the user
   * @param membership the relationship of the user to the clubs returned
   * @return the list of clubs that the user is in or not in
   * @throws FailureToGetClubsError if an error was encountered listing the clubs
   */
  async listClubs(token: string,
                  membership: MembershipType): Promise<ClubInterface[]> {
    try {
      return await this.backend.listClubs(token, membership);
    } catch (err) {
      throw new FailureToGetClubsError();
    }
  }

  /**
   * Removes a user from a club.
   * @param clubId the ID of the club to be left
   * @param token the ID token of the user
   * @return true if the user successfully left the club, false otherwise
   */
  async leaveClub(clubId: string, token: string): Promise<boolean> {
    try {
      const status = await this.backend.leaveClub(clubId, token);
      return status === 200;
    } catch (err) {
      return false;
    }
  }

  /**
   * Joins a user to a club.
   * @param clubId the ID of the club to be joined
   * @param token the ID token of the user
   * @return true if the user successfully joined the club, false otherwise
   */
  async joinClub(clubId: string, token: string): Promise<boolean> {
    try {
      const status = await this.backend.joinClub(clubId, token);
      return status === 200;
    } catch (err) {
      return false;
    }
  }

  /**
   * Retrieves a club given its ID.
   * @param clubId the ID of the club to retrieve
   * @return the retrieved club
   * @throws FailureToGetClubsError if an error was encountered getting the club
   */
  async getClub(clubId: string): Promise<ClubInterface> {
    try {
      return await this.backend.getClub(clubId);
    } catch(err) {
      throw new FailureToGetClubsError();
    }
  }

  // TODO: We have an update-club servlet that is not being touched by our
  //       frontend (this was cut in the rush for the MVP).
}
