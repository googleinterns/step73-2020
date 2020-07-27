import * as React from "react";
import { BookInfo } from "../club_display/BookInfo";
import Box from "@material-ui/core/Box";
import { ClubDescription } from "../club_display/ClubDescription";
import { ContentWarnings } from "../club_display/ContentWarnings";
import { ClubInterface } from "../../../services/backend_service_interface/backend_service_interface";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    club: {
      width: "1500px",
    },
    clubContainer: {
      alignItems: 'center',
      display: 'flex',
      flexDirection: 'column',
      flexShrink: 3,
      marginTop: theme.spacing(12),
    },
    contentContainer: {
      display: 'flex',
      flexDirection: 'column',
      marginBottom: theme.spacing(2),
      marginLeft: theme.spacing(3),
      marginRight: theme.spacing(3),
      marginTop: theme.spacing(2),
    },
    infoAndWarnings: {
      display: 'flex',
      marginBottom: theme.spacing(2),
      marginTop: theme.spacing(1),
    },
  }),
);

/**
 * Displays information of a club that the user is a member of.
 */
export const Club = (props) => {
  const classes = useStyles();

  const [handle, setHandle] = React.useState<string>(props.match.params.handle);
  const [club, setClub] = React.useState<ClubInterface>(props.location.state.club);

  return (
    <div className={classes.clubContainer}>
      <div className={classes.club}>
        <Box border={1} borderColor="text.primary" borderRadius={16}>
          <div className={classes.contentContainer}>
            <h1>{handle}</h1>
            <ClubDescription description={club.description} />
            <div className={classes.infoAndWarnings}>
              <BookInfo book={club.currentBook} />
              <ContentWarnings contentWarnings={club.contentWarnings} />
            </div>
          </div>
        </Box>
      </div>
    </div>
  );
}
