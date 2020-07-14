export interface BackendProfileServiceInterface {
  loadProfile(id: string): Promise<string>;
  updateProfile(personJson: string): Promise<Boolean>;
  deleteProfile(id: string): Promise<Boolean>;
}

export interface BackendYourClubsServiceInterface {
  listClubs(numClubs: number): Promise<string>;
  leaveClub(id: string): Promise<Boolean>;
}
