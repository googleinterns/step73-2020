import * as React from "react";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    boldTextElement: {
      fontWeight: 'bold',
      marginBottom: theme.spacing(0),
    },
    textElement: {
      marginBottom: theme.spacing(0),
      marginTop: theme.spacing(0),
    },
  })
);

interface ClubDescriptionProps {
  description: string,
};

/** Displays the club's description. */
export const ClubDescription = (props: ClubDescriptionProps) => {
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
