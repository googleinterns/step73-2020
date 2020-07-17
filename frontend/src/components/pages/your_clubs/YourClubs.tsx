import * as React from "react";
import AddIcon from "@material-ui/icons/Add";
import Button from "@material-ui/core/Button";
import { ClubList } from "./ClubList";
import { ClubProps } from "../../../services/mock_backend/mock_your_clubs_backend";
import { CreateNewClubWindow } from "./CreateNewClub";
import { createStyles } from "@material-ui/core/styles";
import { defaultServices } from "../../contexts/contexts";
import FormControl from "@material-ui/core/FormControl";
import FormHelperText from "@material-ui/core/FormHelperText";
import InputLabel from "@material-ui/core/InputLabel";
import { makeStyles } from "@material-ui/core/styles";
import MenuItem from "@material-ui/core/MenuItem";
import Select from "@material-ui/core/Select";
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
  const DEFAULT_NUM_DISPLAYED = 10;

  /**
   * ServiceHandlers is an object containing various TS Handlers and provides
   * functionality to communicate data from the frontend to the backend.
   */
  const contextServices = React.useContext(ServiceContext);
  const yourClubsHandlerService = contextServices.yourClubsHandlerService;

  const [listedClubs, setListedClubs] =
    React.useState<ClubProps[]|undefined>(undefined);
  const [numClubsDisplayed, setNumClubsDisplayed] =
    React.useState<number|undefined>(DEFAULT_NUM_DISPLAYED);
  const [createNewClub, setCreateNewClub] = React.useState<boolean>(false);

  /* Re-renders Profile only when number of displayed clubs changes. */
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
      await yourClubsHandlerService.listClubs(numClubsDisplayed);
    setListedClubs(listedClubsPromise);
  }

  /**
   * TODO: Currently using club name as ID; replace with actual ID upon
   *       backend implementation.
   */
  const updateClubListAfterLeaving = async (clubId: string) => {
    const success = await yourClubsHandlerService.leaveClub(clubId);
    if (success) {
      updateClubList();
    }
  }

  const openCreateClubWindow = () => {
    setCreateNewClub(true);
  }

  const closeCreateClubWindow = () => {
    setCreateNewClub(false);
  }

  return (
    <div className={classes.root}>
      <div className={classes.topUtilitiesContainer}>
        <FormControl className={classes.formControl}>
          <InputLabel id="number-of-displayed-clubs-label">
            Number of Displayed Clubs
          </InputLabel>
          <Select
            labelId="number-of-displayed-clubs-label"
            id="number-of-displayed-clubs"
            value={numClubsDisplayed ? numClubsDisplayed : DEFAULT_NUM_DISPLAYED}
            onChange={handleNumClubsChange}
            label="Age"
          >
            <MenuItem value={10}>10</MenuItem>
            <MenuItem value={25}>25</MenuItem>
            <MenuItem value={50}>50</MenuItem>
            <MenuItem value={100}>100</MenuItem>
          </Select>
          <FormHelperText>
            The number of clubs of which you are a member to be displayed.
          </FormHelperText>
        </FormControl>
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
      />
      <CreateNewClubWindow isCreatingNewClub={createNewClub} closeWindow={closeCreateClubWindow} />
    </div>
  );
}
