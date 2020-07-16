import * as React from "react";
import { BookProps } from "../../../services/mock_backend/mock_your_clubs_backend";
import Box from "@material-ui/core/Box";
import Button from "@material-ui/core/Button";
import { ClubProps } from "../../../services/mock_backend/mock_your_clubs_backend";
import { createStyles } from "@material-ui/core/styles";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import LibraryBooksRoundedIcon from "@material-ui/icons/LibraryBooksRounded";
import { makeStyles } from "@material-ui/core/styles";
import PageviewIcon from "@material-ui/icons/Pageview";
import { Theme } from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    break : {
      flexBasis: '100%',
      height: 0,
    },
    button : {
      margin: theme.spacing(1),
      marginLeft: theme.spacing(0),
      marginTop: theme.spacing(1),
      maxHeight: '50px',
    },
    club : {
      marginTop: theme.spacing(3),
      maxWidth: '900px',
    },
    clubContent: {
      display: 'flex',
      flexWrap: 'wrap',
      marginBottom: theme.spacing(1),
      marginLeft: theme.spacing(4),
      marginRight: theme.spacing(4),
      marginTop: theme.spacing(1),
    },
    clubPhoto: {
      marginTop: theme.spacing(2),
    },
    clubTitle: {
      marginBottom: theme.spacing(0),
      marginRight: theme.spacing(6), 
    },
    listedClubsContainer: {
      alignItems: 'center',
      display: 'flex',
      flexDirection: 'column',
      marginBottom: theme.spacing(3),
    },
    textElement: {
      marginBottom: theme.spacing(0),
    },
  }),
);

interface ClubListProps {
  clubsToDisplay: ClubProps[],
}

/**
 * Displays up to the number of clubs that the user has requested to
 * the page.
 */
export function ClubList(props: ClubListProps) {
  const classes = useStyles();
  const clubsToDisplay = props.clubsToDisplay;

  /** If clubs to display is not yet defined, or 'None' are chosen to display. */
  if (clubsToDisplay !== undefined)  {
    return (
      <div className={classes.listedClubsContainer}>
        {clubsToDisplay.map((item, index) => (
          <div className={classes.club} key={item.name}>
            <Box border={1} borderColor="text.primary" borderRadius={16}>
              <div className={classes.clubContent}>
                <h2 className={classes.clubTitle}>{item.name}</h2>
                <LibraryBooksRoundedIcon className={classes.clubPhoto} />
                <ClubDescription description={item.description} />
                <BookInfo book={item.currentBook} />
                <ContentWarnings contentWarnings={item.contentWarnings} />
                <div className={classes.break}></div>
                <div>
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
  description: string;
};

/** Displays the club's description. */
function ClubDescription(props: ClubDescriptionProps) {
  const classes = useStyles();

  return (
    <>
      <div className={classes.break}></div>
      <p className={classes.textElement}>
        <b>Description: </b><br/>
        {props.description}
      </p>
    </>
  );
}

interface BookInfoProps {
  book: BookProps;
};

/** Displays the title and author of the club's current book. */
function BookInfo(props: BookInfoProps) {
  const classes = useStyles();

  return (
    <>
      <p className={classes.textElement} style={{marginRight: 20}}>
        <b>Current Book: </b><br/>
        {props.book.title}<br/>
        by {props.book.author}
      </p>
    </>
  );
}

/** Displays content warnings associated with the club's current book. */
function ContentWarnings(props) {
  const classes = useStyles();
  const contentWarnings = props.contentWarnings;

  return (
    <>
      <p className={classes.textElement}>
        <b>Content Warnings:</b>
        {contentWarnings.map((item, index) => (
          <div>
            - <b>{item}<br/></b>
          </div>
        ))}
      </p>
    </>
  );
}
