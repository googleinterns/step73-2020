import * as React from "react";
import { CircularProgress } from '@material-ui/core';
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    loadContainer: {
      display: "flex",
      justifyContent: "center",
    },
  }),
);

/** Returns a spinner that indicates loading. */
export const Unloaded = () => {
  const classes = useStyles();
  return (
    <div className={classes.loadContainer}>
      <CircularProgress />
    </div>
  );
}
