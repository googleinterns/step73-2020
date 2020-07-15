import { CLUBS } from "../../utils/mock_club_data";
import { MAX_NUM_CLUBS } from "../../utils/temporary_testing_consts";

export interface BookProps {
  title: string, 
  author: string, 
  isbn: string, 
}

export interface ClubProps {
  name: string,
  description: string,
  contentWarnings: Array<string>, 
  currentBook: BookProps,
}

/**
 * Mimics the functionality of a Java Servlet that fetches list of clubs
 * that the user is in.
 */
export class MockYourClubsBackendService {
  private mockClubs: ClubProps[] = [];

  constructor() {
    for (let i = 0; i < MAX_NUM_CLUBS; i++) {
      this.mockClubs.push({
        name: CLUBS[i].name,
        description: CLUBS[i].description,
        contentWarnings: CLUBS[i].contentWarnings,
        currentBook: CLUBS[i].currentBook,
      })
    }
  }

  /** TODO: Update parameters to include Membership status, user Id, page. */
  listClubs(numClubs: number): Promise<string> {
    let listedClubs: ClubProps[] = [];
    for (let i = 0; i < Math.min(numClubs, this.mockClubs.length); i++) {
      listedClubs.push(this.mockClubs[i]);
    }
    const listedClubsJson = JSON.stringify(listedClubs);
    return Promise.resolve(listedClubsJson);
  }

  leaveClub(id: string): Promise<boolean> {
    /** TODO: Add functionality for user to leave club. */
    return Promise.resolve(true);
  }
}
