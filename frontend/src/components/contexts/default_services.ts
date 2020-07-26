import { AuthenticationHandlerService } from "../../services/authentication_handler_service/authentication_handler_service";
import { AuthenticationBackendService } from "../../services/backend/authentication_backend";
import { LoginStatusHandlerService } from "../../services/login_status_handler_service/login_status_handler_service";
import { MockYourClubsBackendService } from "../../services/mock_backend/mock_your_clubs_backend";
import { NUM_PROFILES } from "../../utils/temporary_testing_consts";
import { ProfileBackendService } from "../../services/backend/profile_backend";
import { ProfileHandlerService } from "../../services/profile_handler_service/profile_handler_service";
import { YourClubsHandlerService } from "../../services/your_clubs_handler_service/your_clubs_handler_service";

/** TODO: Change to real backend service implementation. */
export const profileBackendService = new ProfileBackendService();
export const mockYourClubsBackendService = new MockYourClubsBackendService();
export const authenticationBackendService = new AuthenticationBackendService();

export const defaultServices = {
  profileHandlerService: new ProfileHandlerService(profileBackendService),
  yourClubsHandlerService: new YourClubsHandlerService(mockYourClubsBackendService),
  authenticationHandlerService: new AuthenticationHandlerService(authenticationBackendService),
  loginStatusHandlerService: new LoginStatusHandlerService(),
}
