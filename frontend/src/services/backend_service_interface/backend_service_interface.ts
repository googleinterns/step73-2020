export interface BackendProfileServiceInterface {
  loadProfile(id: string): Promise<string>;
  updateProfile(personJson: string): Promise<boolean>;
  deleteProfile(id: string): Promise<boolean>;
}

export interface BackendYourClubsServiceInterface {
  listClubs(numClubs: number): Promise<string>;
  leaveClub(clubId: string): Promise<boolean>;
}
