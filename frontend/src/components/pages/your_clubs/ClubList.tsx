import * as React from "react";
import { BookInfo } from "../club_display/BookInfo";
import { BookInterface } from "../../../services/backend_service_interface/backend_service_interface";
import Box from "@material-ui/core/Box";
import Button from "@material-ui/core/Button";
import { ClubDescription } from "../club_display/ClubDescription";
import { ClubInterface } from "../../../services/backend_service_interface/backend_service_interface";
import { ContentWarnings } from "../club_display/ContentWarnings";
import { createStyles } from "@material-ui/core/styles";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import { Link } from "react-router-dom";
import { makeStyles } from "@material-ui/core/styles";
import PageviewIcon from "@material-ui/icons/Pageview";
import { Theme } from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
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
    link: {
      color: theme.palette.text.primary,
      textDecoration: 'none',
    },
    listedClubsContainer: {
      alignItems: 'center',
      display: 'flex',
      flexDirection: 'column',
      marginBottom: theme.spacing(3),
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
  const [idOfClubAlert, setIdOfClubAlert] =
    React.useState<string|undefined>(undefined);

  const openAlertWindow = (clubId: string) => {
    setIdOfClubAlert(clubId);
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
                    <Link to={{
                      pathname:'/Club/' + item.name,
                      state: {club: item}
                      }}
                      className={classes.link}
                      key={item.name}
                    >
                      <Button
                        className={classes.button}
                        color="primary"
                        endIcon={<PageviewIcon />}
                        variant="contained"
                      >
                        View Club
                      </Button>
                    </Link>
                    {item.ownerId !== props.userId ? (
                      <Button
                        className={classes.button}
                        color="secondary"
                        endIcon={<HighlightOffIcon />}
                        onClick={() => (openAlertWindow(item.clubId))}
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
                  idOfClubLeaving={idOfClubAlert}
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

interface LeaveClubAlertWindowProps {
  clubId: string,
  clubName: string,
  idOfClubLeaving: string,
  alertOpen: boolean,
  handleAlertWindowClose(): void,
  handleLeaveClub(clubId: string): void,
}

function LeaveClubAlertWindow(props: LeaveClubAlertWindowProps) {
  return (
    <Dialog open={props.alertOpen && (props.clubId === props.idOfClubLeaving)}>
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
