import * as React from "react";
import { BookInfo } from "../club_display/BookInfo";
import { ClubDescription } from "../club_display/ClubDescription";
import { ClubInterface } from "../../../services/backend_service_interface/backend_service_interface";
import { ContentWarnings } from "../club_display/ContentWarnings";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";
import { DEFAULT_NUM_DISPLAYED } from "../club_display/club_display_consts";
import { ExploreClubList} from "./ExploreClubList";
import { NumClubsToDisplay } from "../club_display/NumClubsToDisplay";
import { ServiceContext } from "../../contexts/contexts";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: 'flex',
      flexDirection: 'column',
      flexWrap: 'wrap',
      marginTop: theme.spacing(12),
      marginLeft: theme.spacing(4),
    },
    button : {
      margin: theme.spacing(1),
      marginLeft: theme.spacing(0),
      marginTop: theme.spacing(1),
      maxHeight: '50px',
    },
    topUtilitiesContainer: {
      display: 'flex',
      justifyContent: 'center',
    },
  }),
);

export const Explore = () => {
  const classes = useStyles();

  const contextServices = React.useContext(ServiceContext);
  const yourClubsHandlerService = contextServices.yourClubsHandlerService;
  const loginStatusHandlerService = contextServices.loginStatusHandlerService;

  const [listedClubs, setListedClubs] = React.useState<ClubInterface[]|undefined>(undefined);
  const [numClubsDisplayed, setNumClubsDisplayed] = React.useState<number|undefined>(DEFAULT_NUM_DISPLAYED);

  /* Re-renders Explore page only when number of displayed clubs changes. */
  React.useEffect(() => {
    (async() => {
      const numClubsToDisplay = numClubsDisplayed
        ? setNumClubsDisplayed(numClubsDisplayed)
        : setNumClubsDisplayed(DEFAULT_NUM_DISPLAYED)
      updateClubList();
    })();
  }, [numClubsDisplayed]);

  const updateClubList = async () => {
    const listedClubsPromise =
      await yourClubsHandlerService.listClubs(
          loginStatusHandlerService.getUserToken(), "not member");
    setListedClubs(listedClubsPromise);
  }

  const handleNumClubsChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setNumClubsDisplayed(Number(event.target.value));
  }

  const updateClubListAfterJoining = async (clubId: string) => {
    const success:boolean = await yourClubsHandlerService.joinClub(
        clubId, loginStatusHandlerService.getUserToken());
    if (success) {
      updateClubList();
    }
  }

  return (
    <div className={classes.root}>
      <div className={classes.topUtilitiesContainer}>
        <NumClubsToDisplay
          handleNumClubsChange={handleNumClubsChange}
          numClubsDisplayed={numClubsDisplayed}
          showClubsMemberOf={false}
        />
      </div>
      <ExploreClubList
        clubsToDisplay={listedClubs}
        handleJoinClub={updateClubListAfterJoining}
      />
    </div>
  );
}
