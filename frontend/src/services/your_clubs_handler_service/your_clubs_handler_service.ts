import { BackendYourClubsServiceInterface } from
  "../backend_service_interface/backend_service_interface";
import { MockYourClubsBackendService } from 
  "../mock_backend/mock_your_clubs_backend";
import { ClubProps } from 
  "../mock_backend/mock_your_clubs_backend";

/** Error that occurs when Id does not exist */
export class ErrorLoadingClubs extends Error {
  constructor(readonly numClubs: number) {
    super(`Error occurred while loading ${numClubs} clubs.`);
  }
}

/**
 * Handling service that obtains the club that a user is in and 
 * loads them to the user's Your Clubs page.
 */
export class YourClubsHandlerService {
  /** Backend is responsible for holding all YourClubs information */
  constructor(private readonly backend: BackendYourClubsServiceInterface) {}; 

  async listClubs(numClubs: number) {
    try {
      const clubsJson = await this.backend.listClubs(numClubs);
      const clubs: ClubProps[] = JSON.parse(clubsJson);
      return clubs;
    } catch(err) {
      throw new ErrorLoadingClubs(numClubs);
    }
  }

  async leaveClub(id: string) {
    const success = await this.backend.leaveClub(id);
    return success;
  }
}
