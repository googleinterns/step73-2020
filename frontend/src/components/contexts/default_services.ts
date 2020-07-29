import { AuthenticationHandlerService } from "../../services/authentication_handler_service/authentication_handler_service";
import { AuthenticationBackendService } from "../../services/backend/authentication_backend";
import { ClubBackendService } from "../../services/backend/club_backend";
import { ProfileBackendService } from "../../services/backend/profile_backend";
import { ProfileHandlerService } from "../../services/profile_handler_service/profile_handler_service";
import { YourClubsHandlerService } from "../../services/your_clubs_handler_service/your_clubs_handler_service";

/** TODO: Change to real backend service implementation. */
export const profileBackendService = new ProfileBackendService();
export const clubBackendService = new ClubBackendService();
export const authenticationBackendService = new AuthenticationBackendService();

export const defaultServices = {
  profileHandlerService: new ProfileHandlerService(profileBackendService),
  yourClubsHandlerService: new YourClubsHandlerService(clubBackendService),
  authenticationHandlerService:
      new AuthenticationHandlerService(authenticationBackendService),
}
