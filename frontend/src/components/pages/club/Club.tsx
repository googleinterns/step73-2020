import * as React from "react";
import { ClubInterface } from "../../../services/backend_service_interface/backend_service_interface";

export const Club = (props) => {
  const [handle, setHandle] = React.useState<string>(props.match.params.handle);
  const [club, setClub] = React.useState<ClubInterface>(props.location.state.club);

  return (
    <div>
      <p style={{marginTop: "100px"}}>{handle}</p>
    </div>
  );
}
