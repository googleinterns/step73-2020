import { BackendYourClubsServiceInterface } from "../backend_service_interface/backend_service_interface";
import { ClubProps, MockYourClubsBackendService } from "../mock_backend/mock_your_clubs_backend";

/** Error that occurs when Id does not exist. */
export class ErrorLoadingClubs extends Error {
  constructor(readonly numClubs: number) {
    super(`Error occurred while loading ${numClubs} clubs.`);
  }
}

/** Error that occurs when an attempt to create a club fails. */
export class ErrorCreatingClub extends Error {
  constructor(readonly club: ClubProps) {
    super(`Error occurred while creating club '${club.name}'.`);
  }
}

/**
 * Handling service that obtains the club that a user is in and
 * loads them to the user's Your Clubs page.
 */
export class YourClubsHandlerService {
  /** Backend is responsible for holding all YourClubs information. */
  constructor(private readonly backend: BackendYourClubsServiceInterface) {};

  /* TODO: In another PR, create interfaces for handler services, and have
   *       have JSON functionality solely in backend for stronger typing.
   */
  async createClub(club: ClubProps) {
    try {
      const clubJson = JSON.stringify(club);
      const success = await this.backend.createClub(clubJson);
      return success;
    } catch(err) {
      throw new ErrorCreatingClub(club);
    }
  }

  async listClubs(numClubs: number) {
    try {
      const clubsJson = await this.backend.listClubs(numClubs);
      const clubs: ClubProps[] = JSON.parse(clubsJson);
      return clubs;
    } catch(err) {
      throw new ErrorLoadingClubs(numClubs);
    }
  }

  async leaveClub(clubId: string) {
    const success = await this.backend.leaveClub(clubId);
    return success;
  }
}
