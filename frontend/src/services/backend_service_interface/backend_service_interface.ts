export interface BackendProfileServiceInterface {
  loadProfile(id: string): Promise<string>,
  updateProfile(personJson: string): Promise<boolean>,
  deleteProfile(id: string): Promise<boolean>,
}

export interface BackendYourClubsServiceInterface {
  listClubs(numClubs: number): Promise<string>,
  leaveClub(id: string): Promise<boolean>,
}

export interface BackendAuthenticationInterface {
  retrieveToken(code: string, redirectUri: string): Promise<string>,
}
