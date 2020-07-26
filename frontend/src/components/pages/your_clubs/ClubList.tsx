import * as React from "react";
import { BookInterface } from "../../../services/backend_service_interface/backend_service_interface";
import Box from "@material-ui/core/Box";
import Button from "@material-ui/core/Button";
import { ClubInterface } from "../../../services/backend_service_interface/backend_service_interface";
import { createStyles } from "@material-ui/core/styles";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import { makeStyles } from "@material-ui/core/styles";
import PageviewIcon from "@material-ui/icons/Pageview";
import { Theme } from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    boldTextElement: {
      fontWeight: 'bold',
      marginBottom: theme.spacing(0),
    },
    button : {
      marginLeft: theme.spacing(1),
      marginTop: theme.spacing(1),
      height: '35px',
    },
    buttonContainer: {
      marginTop: theme.spacing(1),
    },
    club : {
      marginTop: theme.spacing(3),
      width: '900px',
    },
    clubContent: {
      display: 'flex',
      flexBasis: '100%',
      flexWrap: 'wrap',
      marginBottom: theme.spacing(2),
      marginLeft: theme.spacing(2),
      marginRight: theme.spacing(2),
    },
    clubHeader: {
      display: 'flex',
      flexBasis: '100%',
      justifyContent: 'space-between',
      marginBottom: theme.spacing(2),
      marginLeft: theme.spacing(2),
      marginRight: theme.spacing(2),
      marginTop: theme.spacing(1),
    },
    clubTitle: {
      marginBottom: theme.spacing(0),
      maxWidth: '500px',
    },
    listedClubsContainer: {
      alignItems: 'center',
      display: 'flex',
      flexDirection: 'column',
      marginBottom: theme.spacing(3),
    },
    textElement: {
      marginBottom: theme.spacing(0),
      marginTop: theme.spacing(0),
    },
    warningsHeader: {
      fontWeight: 'bold',
      marginBottom: theme.spacing(0),
      marginLeft: '-17px',
    },
    warningsList: {
      fontWeight: 'bold',
      justifyContent: 'left',
      marginBottom: theme.spacing(0),
      marginTop: theme.spacing(0),
    },
  }),
);

interface ClubListProps {
  clubsToDisplay: ClubInterface[],
  handleLeaveClub(clubId: string): void,
  userId: string,
}

/**
 * Displays up to the number of clubs that the user has requested to
 * the page.
 */
export function ClubList(props: ClubListProps) {
  const classes = useStyles();
  const clubsToDisplay = props.clubsToDisplay;
  const [leaveClubAlertOpen, setLeaveAlertOpen] =
    React.useState<boolean>(/* closed */ false);
  const [nameOfClubAlert, setNameofClubAlert] =
    React.useState<string|undefined>(undefined);

  const openAlertWindow = (clubId: string) => {
    setNameofClubAlert(clubId);
    setLeaveAlertOpen(true);
  }

  const closeAlertWindow = () => {
    setLeaveAlertOpen(false);
  }

  const handleLeaveClub = (clubId: string) => {
    props.handleLeaveClub(clubId);
    setLeaveAlertOpen(false);
  }

  /* If clubs to display is not yet defined. */
  if (clubsToDisplay !== undefined)  {
    return (
      <div className={classes.listedClubsContainer}>
        {clubsToDisplay.map((item, index) => (
          <div className={classes.club} key={item.name}>
            <Box border={1} borderColor="text.primary" borderRadius={16}>
              <div className={classes.clubContent}>
                <div className={classes.clubHeader}>
                  <h2 className={classes.clubTitle}>{item.name}</h2>
                  <div className={classes.buttonContainer}>
                    <Button
                      className={classes.button}
                      color="primary"
                      endIcon={<PageviewIcon />}
                      variant="contained"
                    >
                      View Club
                    </Button>
                    {item.ownerId !== props.userId ? (
                      <Button
                        className={classes.button}
                        color="secondary"
                        endIcon={<HighlightOffIcon />}
                        onClick={() => (openAlertWindow(item.name))}
                        variant="contained"
                      >
                        Leave Club
                      </Button>
                    ) : (
                      <></>
                    )}
                  </div>
                </div>
                <div className={classes.clubContent} >
                  <ClubDescription description={item.description} />
                </div>
                <div className={classes.clubContent}>
                  <BookInfo book={item.currentBook} />
                  <ContentWarnings contentWarnings={item.contentWarnings} />
                </div>
                <LeaveClubAlertWindow
                  clubName={item.name}
                  clubId={item.clubId}
                  nameOfClubLeaving={nameOfClubAlert}
                  alertOpen={leaveClubAlertOpen}
                  handleAlertWindowClose={closeAlertWindow}
                  handleLeaveClub={handleLeaveClub}
                />
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

interface ClubDescriptionProps {
  description: string,
};

/** Displays the club's description. */
function ClubDescription(props: ClubDescriptionProps) {
  const classes = useStyles();

  return (
    <>
      <div className={classes.boldTextElement}>
        Description:
      </div>
      <p className={classes.textElement}>
        {props.description}
      </p>
    </>
  );
}

interface BookInfoProps {
  book: BookInterface;
};

/** Displays the title and author of the club's current book. */
function BookInfo(props: BookInfoProps) {
  const classes = useStyles();

  return (
    <div>
      <div className={classes.boldTextElement}>
        Current Book:
      </div>
      <p className={classes.textElement}>
        <div>{props.book.title}</div>
        <div>by {props.book.author}</div>
      </p>
    </div>
  );
}

/** Displays content warnings associated with the club's current book. */
function ContentWarnings(props) {
  const classes = useStyles();
  const contentWarnings = props.contentWarnings;

  if (contentWarnings) {
    return (
        <div>
          <ul className={classes.warningsList}>
            <div className={classes.warningsHeader}>Content Warnings:</div>
            {contentWarnings.map((item, index) => (
              <li>{item}</li>
            ))}
          </ul>
        </div>
    );
  } else {
    return (
      <p className={classes.warningsList}>
        No Content Warnings
      </p>
    );
  }
}

interface LeaveClubAlertWindowProps {
  clubId: string,
  clubName: string,
  nameOfClubLeaving: string,
  alertOpen: boolean,
  handleAlertWindowClose(): void,
  handleLeaveClub(clubId: string): void,
}

function LeaveClubAlertWindow(props: LeaveClubAlertWindowProps) {
  return (
    <Dialog open={props.alertOpen && (props.clubName === props.nameOfClubLeaving)}>
      <DialogTitle>Leave Club '{props.clubName}'?</DialogTitle>
      <DialogContent>
        <DialogContentText>
          By pressing 'Confirm', you will no longer be a member of Club
          '{props.clubName}'. This means that you will no longer have access
          to its content, including discussion forums and material.
        </DialogContentText>
        <DialogActions>
          <Button onClick={props.handleAlertWindowClose} color="primary">
            Cancel
          </Button>
          <Button
            onClick={() => (props.handleLeaveClub(props.clubId))} color="primary"
          >
            Confirm
          </Button>
        </DialogActions>
      </DialogContent>
    </Dialog>
  );
}
