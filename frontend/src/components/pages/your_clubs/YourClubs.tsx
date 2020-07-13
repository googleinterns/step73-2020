import * as React from "react";
import ChromeReaderModeIcon from "@material-ui/icons/ChromeReaderMode";
import { createStyles } from "@material-ui/core/styles";
import FormControl from "@material-ui/core/FormControl";
import FormHelperText from "@material-ui/core/FormHelperText";
import InputLabel from "@material-ui/core/InputLabel";
import { makeStyles } from "@material-ui/core/styles";
import MenuItem from "@material-ui/core/MenuItem";
import Select from "@material-ui/core/Select";
import { Theme } from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: 'flex',
      flexWrap: 'wrap',
      marginTop: '100px',
      marginLeft: theme.spacing(4),
    },
    club : {
      display: 'flex', 
      flexWrap: 'wrap',
      justifyContent: 'center',
    },
    formControl: {
      margin: theme.spacing(1),
      marginBottom: '5px',
      minWidth: 120,
    },
  }),
);

export const YourClubs = () => {
  const classes = useStyles();
  const [numClubsDisplayed, setNumClubsDisplayed] = React.useState<number|undefined>(undefined);
  
  // Associates each text value with its corresponding number.
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
      <FormControl className={classes.formControl}>
        <InputLabel id="number-of-displayed-clubs-label">Number of Displayed Clubs</InputLabel>
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
    </div>
  );
}
