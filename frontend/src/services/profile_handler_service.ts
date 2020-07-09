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

interface ProfileHandlerServiceAPI {
  getPerson(id: string): Promise<PersonProps>;
  updatePerson(PersonProps): Promise<boolean>;
  deletePerson(id: string): Promise<boolean>;
}

/**
 * Profile Handling service that manages list of user profiles using a mocked
 * backend database.
 */
export class ProfileHandlerService implements ProfileHandlerServiceAPI {

  /** Backend is responsible for holding all profile information */
  constructor(private readonly backend: MockProfileBackendService) {}; 

  async getPerson(id: string) {
    try {
      const person = await this.backend.loadPerson(id);
      return person;
    } catch(err) {
      throw new NonExistentProfileError(id);
    }
  }

  async updatePerson(person: PersonProps) {
    const success = await this.backend.updatePerson(person);
    return success;
  }

  async deletePerson(id: string) {
    const success = await this.backend.deletePerson(id);
    if (!success) {
      throw new NonExistentProfileError(id);
    } else {
      return success;
    }
  }
}