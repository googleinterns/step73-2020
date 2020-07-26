import { PersonInterface } from "../backend_service_interface/backend_service_interface"

/**
 * Directly connects with the servlet backend to perform profile operations.
 */
export class ProfileBackendService {
  /** Returns a profile given their ID token. */
  async loadProfile(token: string): Promise<PersonInterface> {
    const getProfileURL = "/api/get-profile?" + new URLSearchParams({
      idToken: token,
    }).toString();
    const response = await fetch(getProfileURL, {
      method: "GET",
    });
    return response.json();
  }

  /** Updates a profile given the updated person and their ID token. */
  async updateProfile(
      person: PersonInterface, token: string): Promise<PersonInterface> {
    const bodyContents = {
      idToken: token,
      person,
    }
    const response = await fetch("/api/update-person", {
      method: "POST",
      body: JSON.stringify(bodyContents),
    });
    return response.json();
  }

  /** Creates a new person object in the backend. */
  async createPerson(person: PersonInterface): Promise<PersonInterface> {
    const response = await fetch("/api/create-person", {
      method: "POST",
      body: JSON.stringify(person),
    });
    return response.json();
  }
}
