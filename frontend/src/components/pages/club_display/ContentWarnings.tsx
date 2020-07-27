import * as React from "react";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
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

interface ContentWarningsProps {
  contentWarnings: string[],
}

/** Displays content warnings associated with the club's current book. */
export const ContentWarnings = (props: ContentWarningsProps) => {
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
