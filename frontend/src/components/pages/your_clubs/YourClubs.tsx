import * as React from "react";
import AddIcon from "@material-ui/icons/Add";
import Button from "@material-ui/core/Button";
import { ClubList } from "./ClubList";
import { ClubProps } from
  "../../../services/mock_backend/mock_your_clubs_backend";
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
      marginTop: '100px',
      marginLeft: theme.spacing(4),
    },
    button : {
      margin: theme.spacing(1),
      marginLeft: theme.spacing(0),
      marginTop: '20px',
      maxHeight: '50px',
    },
    formControl: {
      margin: theme.spacing(1),
      marginBottom: '5px',
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

  const [listedClubs, setListedClubs] =
    React.useState<ClubProps[]|undefined>(undefined);
  const [numClubsDisplayed, setNumClubsDisplayed] =
    React.useState<number|undefined>(undefined);

  /** Re-renders Profile only when number of displayed clubs changes. */
  React.useEffect(() => {
    (async() => {
      let numClubsToDisplay = numClubsDisplayed ? numClubsDisplayed : 0;
      const listedClubsPromise =
        await yourClubsHandlerService.listClubs(numClubsToDisplay);
      setListedClubs(listedClubsPromise);
    })();
  }, [numClubsDisplayed]);

  /** Associates each text value with its corresponding number. */
  const textNumberRepresentation = [
    {text: "None", val: 0},
    {text: "One", val: 1},
    {text: "Two", val: 2},
    {text: "Three", val: 3},
    {text: "Four", val: 4},
    {text: "Five", val: 5},
    {text: "Six", val: 6},
    {text: "Seven", val: 7},
    {text: "Eight", val: 8},
    {text: "Nine", val: 9},
    {text: "Ten", val: 10},
  ];

  const handleNumClubsChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setNumClubsDisplayed(Number(event.target.value));
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
            value={numClubsDisplayed ? numClubsDisplayed : 0}
            onChange={handleNumClubsChange}
            label="Age"
          >
            {textNumberRepresentation.map((item, index) => (
              <MenuItem value={item.val}>{item.text}</MenuItem>
            ))}
          </Select>
          <FormHelperText>
            The number of clubs of which you are a member to be displayed.
          </FormHelperText>
        </FormControl>
        <Button
          className={classes.button}
          color="primary"
          endIcon={<AddIcon />}
          variant="contained"
        >
          Create Club
        </Button>
      </div>
      <ClubList clubsToDisplay={listedClubs} />
    </div>
  );
}
