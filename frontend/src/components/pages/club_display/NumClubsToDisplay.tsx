import * as React from "react";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";
import { DEFAULT_NUM_DISPLAYED } from "../club_display/club_display_consts";
import FormControl from "@material-ui/core/FormControl";
import FormHelperText from "@material-ui/core/FormHelperText";
import InputLabel from "@material-ui/core/InputLabel";
import MenuItem from "@material-ui/core/MenuItem";
import Select from "@material-ui/core/Select";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    formControl: {
      margin: theme.spacing(1),
      marginBottom: theme.spacing(2),
      marginRight: theme.spacing(8),
      minWidth: 120,
    },
  }),
);

interface NumClubsToDisplayProps {
  handleNumClubsChange(event: React.ChangeEvent<HTMLInputElement>): void;
  numClubsDisplayed: number;
  showClubsMemberOf: boolean;
};

/** Displays the title and author of the club's current book. */
export const NumClubsToDisplay = (props: NumClubsToDisplayProps) => {
  const classes = useStyles();

  return (
    <FormControl className={classes.formControl}>
      <InputLabel id="number-of-displayed-clubs-label">
        Number of Displayed Clubs
      </InputLabel>
      <Select
        labelId="number-of-displayed-clubs-label"
        id="number-of-displayed-clubs"
        value={props.numClubsDisplayed ? props.numClubsDisplayed : DEFAULT_NUM_DISPLAYED}
        onChange={props.handleNumClubsChange}
        label="Number of Clubs to Display"
      >
        <MenuItem value={10}>10</MenuItem>
        <MenuItem value={25}>25</MenuItem>
        <MenuItem value={50}>50</MenuItem>
        <MenuItem value={100}>100</MenuItem>
      </Select>
      <FormHelperText>
        {props.showClubsMemberOf
          ? "The number of clubs of which you are a member to be displayed."
          : "The number of public clubs you would like to be displayed."
        }
      </FormHelperText>
    </FormControl>
  );
}
