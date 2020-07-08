import * as React from "react";
import Button from "@material-ui/core/Button";
import { createStyles } from "@material-ui/core/styles";
import FilledInput from "@material-ui/core/FilledInput";
import FormControl from "@material-ui/core/FormControl";
import FormHelperText from "@material-ui/core/FormHelperText";
import Input from "@material-ui/core/Input";
import InputLabel from "@material-ui/core/InputLabel";
import { makeStyles } from "@material-ui/core/styles";
import OutlinedInput from "@material-ui/core/OutlinedInput";
import { PersonProps } from "../../services/profile_handler_service";
import { Theme } from "@material-ui/core/styles";
import TextField from '@material-ui/core/TextField';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: 'flex',
      flexWrap: 'wrap',
      marginTop: '100px',
      marginLeft: theme.spacing(4),
    },
    '& input:invalid + fieldset': {
      borderColor: 'red',
      borderWidth: 2,
    },
    textField: {
      marginLeft: theme.spacing(1),
      marginRight: theme.spacing(1),
      width: '25ch',
    },
  }),
);

export default function Profile() {

  const classes = useStyles();

  const [person] = React.useState<PersonProps|undefined>(undefined);
  const [userEmail, setUserEmail] = React.useState<string>(undefined);
  const [userName, setUserName] = React.useState<string>(undefined);
  const [userPronouns, setUserPronouns] = React.useState<string>(undefined);

  /** @TODO: Load state in from backend */
  // fetch('/getPerson', {method: 'GET'})

  const handleEmailChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setUserEmail(event.target.value);
  }

  const handleNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setUserName(event.target.value);
  }

  const handlePronounsChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setUserPronouns(event.target.value);
  }

  const handleSubmit = (event) => {
    event.preventDefault();

    const personObj = person;
    const personJson = JSON.stringify(person);
    console.log(personJson);

    const requestParams = {
      method: 'POST', 
      headers: {'Content-Type': 'application/json'},
      body: personJson,
    }

  /** @TODO: fetch personJson to the backend */
  // fetch('/updatePerson', {method: 'POST'})
  console.log(personJson);
  };

  return (
    <div className={classes.root}>
      <form>
        <div>
          <TextField
            fullWidth
            helperText="to be displayed around CoffeeHouse"
            id="nickname"
            InputLabelProps={{
              shrink: true,
            }}
            label="Nickname"
            margin="normal"
            onChange={handleNameChange}
            placeholder="Nickname"
            style={{ margin: 8 }}
            variant="outlined"
          />
          <TextField
            fullWidth
            helperText="to be displayed around CoffeeHouse"
            id="pronouns"
            InputLabelProps={{
              shrink: true,
            }}
            label="Pronouns"
            margin="normal"
            onChange={handlePronounsChange}
            placeholder="Pronouns"
            style={{ margin: 8 }}
            variant="outlined"
          />
          <TextField
            fullWidth
            id="email"
            InputLabelProps={{
              shrink: true,
            }}
            label="Email"
            margin="normal"
            onChange={handleEmailChange}
            placeholder="Email"
            style={{ margin: 8 }}      
            variant="outlined"
          />
        </div>
        <div>
          <Button 
            color="primary" 
            onClick={handleSubmit}
            style={{ margin: 8 }} 
            variant="contained" 
          >
            Submit
          </Button>
        </div>
      </form>
    </div>
  );
}
