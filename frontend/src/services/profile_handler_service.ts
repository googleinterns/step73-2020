import { PersonProps, MockProfileBackendService } from "./mock_profile_backend";

/** Error that occurs when Id does not exist */
export class NonExistentProfileError extends Error {
  constructor(readonly id: string) {
    super(`Profile ${id} does not exist`);
  }
}

/** Error that occurs if Id was unable to be updated */
export class FailureToUpdateProfile extends Error {
  constructor(readonly id: string) {
    super(`Unable to update Profile ${id}`);
  }
}

interface BackendServiceInterface {
  loadProfile(id: string): Promise<string>;
  updateProfile(personJson: string): Promise<Boolean>;
  deleteProfile(id: string): Promise<Boolean>;
}

/**
 * Profile Handling service that manages list of user profiles using a mocked
 * backend database.
 */
export class ProfileHandlerService {
  /** Backend is responsible for holding all profile information */
  constructor(private readonly backend: BackendServiceInterface) {}; 

  async getPerson(id: string) {
    try {
      const personJson = await this.backend.loadProfile(id);
      const person = JSON.parse(personJson);
      return person;
    /** 
     * TODO: Add different types of errors based off of what failure occurs. 
     * eg. NOT_FOUND
     */
    } catch(err) {
      throw new NonExistentProfileError(id);
    }
  }

  async updatePerson(person: PersonProps) {
    const personJson = JSON.stringify(person);
    const success = await this.backend.updateProfile(personJson);
    return success;
  }

  async deletePerson(id: string) {
    const success = await this.backend.deleteProfile(id);
    if (!success) {
      throw new NonExistentProfileError(id);
    } else {
      return success;
    }
  }
}
