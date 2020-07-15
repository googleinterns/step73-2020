import * as React from "react";
import AddIcon from "@material-ui/icons/Add";
import { BookProps } from
  "../../../services/mock_backend/mock_your_clubs_backend";
import { borders } from "@material-ui/system";
import Box from "@material-ui/core/Box";
import Button from "@material-ui/core/Button";
import { ClubProps } from
  "../../../services/mock_backend/mock_your_clubs_backend";
import { createStyles } from "@material-ui/core/styles";
import { defaultServices } from "../../contexts/contexts";
import FormControl from "@material-ui/core/FormControl";
import FormHelperText from "@material-ui/core/FormHelperText";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import InputLabel from "@material-ui/core/InputLabel";
import LibraryBooksRoundedIcon from "@material-ui/icons/LibraryBooksRounded";
import { makeStyles } from "@material-ui/core/styles";
import MenuItem from "@material-ui/core/MenuItem";
import PageviewIcon from "@material-ui/icons/Pageview";
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
    break : {
      flexBasis: '100%',
      height: 0,
    },
    button : {
      margin: theme.spacing(1),
      marginLeft: theme.spacing(0),
      marginTop: '20px',
      maxHeight: '50px',
    },
    buttonsContainer : {
      alignItems: 'right',
      display: 'flex',
    },
    club : {
      marginTop: '20px',
      maxWidth: '900px',
    },
    clubContent: {
      display: 'flex',
      flexWrap: 'wrap',
      margin: theme.spacing(1),
    },
    clubPhoto: {
      marginTop: '15px',
    },
    clubTitle: {
      marginBottom: 0,
      marginRight: 50, 
    },
    formControl: {
      margin: theme.spacing(1),
      marginBottom: '5px',
      marginRight: theme.spacing(8),
      minWidth: 120,
    },
    listedClubsContainer: {
      alignItems: 'center',
      display: 'flex',
      flexDirection: 'column',
      marginBottom: '20px',
    },
    textElement: {
      marginBottom: '0px',
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
      <div className={classes.listedClubsContainer}>
        <DisplayListedClubs clubsToDisplay={listedClubs} />
      </div>
    </div>
  );
}

interface DisplayListedClubsProps {
  clubsToDisplay: ClubProps[],
}

/**
 * Displays up to the number of clubs that the user has requested to to
 * the page.
 */
function DisplayListedClubs(props: DisplayListedClubsProps) {
  const classes = useStyles();
  const clubsToDisplay = props.clubsToDisplay;

  /** If clubs to display is not yet defined, or 'None' are chosen to display. */
  if (clubsToDisplay ? /** defined */ true : /** undefined */ false)  {
    return (
      <div className={classes.listedClubsContainer}>
        {clubsToDisplay.map((item, index) => (
          <div className={classes.club} key={item.name}>
            <Box border={1} borderColor="text.primary" borderRadius={16}>
              <div className={classes.clubContent}>
                <h2 className={classes.clubTitle}>{item.name}</h2>
                <LibraryBooksRoundedIcon className={classes.clubPhoto} />
                <LoadClubDescription description={item.description} />
                <LoadBookInfo book={item.currentBook} />
                <LoadContentWarnings contentWarnings={item.contentWarnings} />
                <div className={classes.break}></div>
                <LoadButtons />
              </div>
            </Box>
          </div>
        ))}
      </div>
    );
  } else {
    return (<div></div>);
  }
}

interface LoadClubDescriptionProps {
  description: string;
};

/** Displays the club's description. */
function LoadClubDescription(props: LoadClubDescriptionProps) {
  const classes = useStyles();

  return (
    <div>
      <div className={classes.break}></div>
      <p className={classes.textElement}>
        <b>Description: </b><br/>
        {props.description}
      </p>
    </div>
  );
}

interface LoadBookInfoProps {
  book: BookProps;
};

/** Displays the title and author of the club's current book. */
function LoadBookInfo(props: LoadBookInfoProps) {
  const classes = useStyles();

  return (
    <div>
      <p className={classes.textElement} style={{marginRight: 20}}>
        <b>Current Book: </b><br/>
        {' ' + props.book.title}<br/>
        by {props.book.author}
      </p>
    </div>
  );
}

/** Displays content warnings associated with the club's current book. */
function LoadContentWarnings(props) {
  const classes = useStyles();
  const contentWarnings = props.contentWarnings;

  return (
    <div>
      <p  className={classes.textElement}><b>Content Warnings</b></p>
      {contentWarnings.map((item, index) => (
        <div>
          - <b>{item}<br/></b>
        </div>
      ))}
    </div>
  );
}

/**
 * Displays two buttons, one for viewing the club and another for leaving
 * the club.
 */
function LoadButtons () {
  const classes = useStyles();

  return (
    <div className={classes.buttonsContainer}>
      <Button
        className={classes.button}
        color="primary"
        endIcon={<PageviewIcon />}
        variant="contained"
      >
        View Club
      </Button>
      <Button
        className={classes.button}
        color="secondary"
        endIcon={<HighlightOffIcon />}
        variant="contained"
      >
        Leave Club
      </Button>
    </div>
  );
}
