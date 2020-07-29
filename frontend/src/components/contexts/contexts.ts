import * as React from "react";
import { AuthenticationHandlerService } from "../../services/authentication_handler_service/authentication_handler_service";
import { ProfileHandlerService } from "../../services/profile_handler_service/profile_handler_service";
import { YourClubsHandlerService } from "../../services/your_clubs_handler_service/your_clubs_handler_service";

interface ServicesInterfaces {
  profileHandlerService: ProfileHandlerService,
  yourClubsHandlerService: YourClubsHandlerService,
  authenticationHandlerService: AuthenticationHandlerService,
}

export const ServiceContext =
    React.createContext<undefined | ServicesInterfaces>(undefined);
