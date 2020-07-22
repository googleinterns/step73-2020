export interface BackendProfileServiceInterface {
  loadProfile(id: string): Promise<string>,
  updateProfile(personJson: string): Promise<boolean>,
  deleteProfile(id: string): Promise<boolean>,
}

export interface BackendYourClubsServiceInterface {
  createClub(clubJson: string): Promise<boolean>,
  listClubs(numClubs: number): Promise<string>,
  leaveClub(clubId: string): Promise<boolean>,
}

export interface BackendAuthenticationInterface {
  retrieveToken(code: string, redirectUri: string): Promise<string>,
}
