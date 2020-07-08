import { PersonProps, ProfileBackendService } from "./mock_profile_backend";

/** Error that occurs when Id does not exist */
export class NonExistentProfileError extends Error {
  constructor(readonly id: string) {
    super(`Profile ${id} does not exist`);
  }
}

/** Error that occurs if Id was unable to be added */
export class FailureToUpdateProfile extends Error {
  constructor(readonly id: string) {
    super(`Unable to update Profile ${id}`);
  }
}

/**
 * Profile Handling service that manages list of user profiles using a mocked
 * backend database.
 */
export class ProfileHandlerService {
  private ProfilesListInternal: PersonProps[] = [];

  constructor(private readonly backend: ProfileBackendService) {};

  async getProfile(id: string) {
    for (let i = 0; i < this.ProfilesListInternal.length; i++) {
      if (id === this.ProfilesListInternal[i].userId) {
        return this.ProfilesListInternal[i];
      }
    }
  }

  getProfiles() {
    return [...this.ProfilesListInternal];
  }

  async loadProfile(id: string) {
    try {
      const person = await this.backend.loadProfile(id);
      return person;
    } catch(err) {
      throw new NonExistentProfileError(id);
    }
  }

  async updateProfile(person: PersonProps) {
    const success = await this.backend.updateProfile(person);
    return success;
  }

  async deleteProfile(id: string) {
    const success = await this.backend.deleteProfile(id);
    if (!success) {
      throw new NonExistentProfileError(id);
    } else {
      return success;
    }
  }
}