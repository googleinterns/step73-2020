import { ClubInterface, MembershipType } from "../backend_service_interface/backend_service_interface"

/**
 * Directly connects with the servlet backend to perform club operations.
 */
export class ClubBackendService {
  /** Creates a club on the backend given the club object. */
  async createClub(club: ClubInterface): Promise<ClubInterface> {
    const response = await fetch("/api/create-club", {
      method: "POST",
      body: JSON.stringify(club),
    });
    return response.json();
  }

  /** Retrieves a list of clubs with some relation to a user. */
  async listClubs(membership: MembershipType,
                  token: string): Promise<ClubInterface[]> {
    const listClubsUrl = "/api/list-clubs?" + new URLSearchParams({
      idToken: token,
      membershipStatus: membership,
    }).toString();
    const response = await fetch(listClubsUrl, {
      method: "GET",
    });
    return response.json();
  }

  /**
   * Removes a user from a club.
   * @param clubId the ID of the club the user is to be removed from
   * @param token the ID token of the user leaving the club
   * @return the status code of the response
   */
  async leaveClub(clubId: string, token: string): Promise<number> {
    const bodyContents = {
      clubId,
      idToken: token,
    }
    const response = await fetch("/api/leave-club", {
      method: "POST",
      body: JSON.stringify(bodyContents),
    });
    return response.status;
  }

  /**
   * Joins a user to a club.
   * @param clubId the ID of the club the user is to be joined to
   * @param token the ID token of the user join the club
   * @return the status code of the response
   */
  async joinClub(clubId: string, token: string): Promise<number> {
    const bodyContents = {
      clubId,
      idToken: token,
    }
    const response = await fetch("/api/join-club", {
      method: "POST",
      body: JSON.stringify(bodyContents),
    });
    return response.status;
  }

  /** Retrieves a club given its ID. */
  async getClub(clubId: string): Promise<ClubInterface> {
    const getClubUrl = "/api/get-club?" + new URLSearchParams({
      clubId,
    }).toString();
    const response = await fetch(getClubUrl, {
      method: "GET",
    });
    return response.json();
  }
}
