export interface BackendServiceInterface {
  loadProfile(id: string): Promise<string>;
  updateProfile(personJson: string): Promise<Boolean>;
  deleteProfile(id: string): Promise<Boolean>;
}
