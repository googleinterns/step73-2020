import * as React from "react";
import AddIcon from "@material-ui/icons/Add";
import Button from "@material-ui/core/Button";
import { ClubInterface } from "../../../services/backend_service_interface/backend_service_interface";
import { ClubList } from "./ClubList";
import { CreateNewClubWindow } from "./CreateNewClub";
import { createStyles } from "@material-ui/core/styles";
import { DEFAULT_NUM_DISPLAYED } from "../club_display/club_display_consts";
import { makeStyles } from "@material-ui/core/styles";
import { NumClubsToDisplay } from "../club_display/NumClubsToDisplay";
import { ServiceContext } from "../../contexts/contexts";
import { Theme } from "@material-ui/core/styles";

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
    formControl: {
      margin: theme.spacing(1),
      marginBottom: theme.spacing(2),
      marginRight: theme.spacing(8),
      minWidth: 120,
    },
    topUtilitiesContainer: {
      display: 'flex',
      justifyContent: 'center',
    },
  }),
);

export const YourClubs = () => {
  const classes = useStyles();

  /**
   * ServiceHandlers is an object containing various TS Handlers and provides
   * functionality to communicate data from the frontend to the backend.
   */
  const contextServices = React.useContext(ServiceContext);
  const yourClubsHandlerService = contextServices.yourClubsHandlerService;
  const loginStatusHandlerService = contextServices.loginStatusHandlerService;

  const [listedClubs, setListedClubs] =
    React.useState<ClubInterface[]|undefined>(undefined);
  const [numClubsDisplayed, setNumClubsDisplayed] =
    React.useState<number>(DEFAULT_NUM_DISPLAYED);
  const [createNewClub, setCreateNewClub] = React.useState<boolean>(false);

  /* Re-renders YourClubs only when number of displayed clubs changes. */
  React.useEffect(() => {
    (async() => {
      const numClubsToDisplay = numClubsDisplayed
        ? setNumClubsDisplayed(numClubsDisplayed)
        : setNumClubsDisplayed(DEFAULT_NUM_DISPLAYED)
      updateClubList();
    })();
  }, [numClubsDisplayed]);

  const handleNumClubsChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setNumClubsDisplayed(Number(event.target.value));
  }

  const updateClubList = async () => {
    const listedClubsPromise =
      await yourClubsHandlerService.listClubs(
          loginStatusHandlerService.getUserToken(), "member");
    setListedClubs(listedClubsPromise);
  }

  /**
   * TODO: Currently using club name as ID; replace with actual ID upon
   *       backend implementation.
   */
  const updateClubListAfterLeaving = async (clubId: string) => {
    const success:boolean = await yourClubsHandlerService.leaveClub(
        clubId, loginStatusHandlerService.getUserToken());
    if (success) {
      updateClubList();
    }
  }

  const openCreateClubWindow = () => {
    setCreateNewClub(true);
  }

  const getUserId = () => {
    const parsedToken = JSON.parse(atob(
    loginStatusHandlerService.getUserToken().split(".")[1]));
    return parsedToken.sub;
  }

  const closeCreateClubWindow = (successfulCreation: boolean) => {
    setCreateNewClub(false);
    if (successfulCreation) {
      updateClubList();
    }
  }

  return (
    <div className={classes.root}>
      <div className={classes.topUtilitiesContainer}>
        <NumClubsToDisplay
          handleNumClubsChange={handleNumClubsChange}
          numClubsDisplayed={numClubsDisplayed}
          showClubsMemberOf={true}
        />
        <Button
          className={classes.button}
          color="primary"
          endIcon={<AddIcon />}
          onClick={openCreateClubWindow}
          variant="contained"
        >
          Create Club
        </Button>
      </div>
      <ClubList
        clubsToDisplay={listedClubs}
        handleLeaveClub={updateClubListAfterLeaving}
        userId={getUserId()}
      />
      <CreateNewClubWindow
        closeWindow={closeCreateClubWindow}
        isCreatingNewClub={createNewClub}
      />
    </div>
  );
}
