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
import { PersonProps } from "../../../services/mock_profile_backend";
import { ProfileHandlerService } from "../../../services/profile_handler_service";
import { ProfileBackendService } from "../../../services/mock_profile_backend";
import { Theme } from "@material-ui/core/styles";
import TextField from '@material-ui/core/TextField';

const NUM_PROFILES = 10;
const USER_ID = "user_0";

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

interface ProfileProps {
  profileHandlerService: ProfileHandlerService;
}

interface ProfileState {
  personProps: PersonProps;
}

export default function Profile(props: ProfileProps, state: ProfileState) {

  const profileHandlerService = new ProfileHandlerService(new ProfileBackendService(NUM_PROFILES));
  const classes = useStyles();

  const [person, setPerson] = React.useState<PersonProps|undefined>(undefined);

  const [userEmail, setUserEmail] = React.useState<string>(undefined);
  const [userName, setUserName] = React.useState<string>(undefined);
  const [userPronouns, setUserPronouns] = React.useState<string>(undefined);
  const [profileId, setProfileId] = React.useState<string>(undefined);

  /** @TODO: Load state in from backend */
  (async() => {
    const personPromise = await profileHandlerService.getProfile(USER_ID);
    setPerson(personPromise);
  })();
  
  /*
  try {
    setPerson(ProfileProps.ProfileHandlerService.getProfile(USER_ID));
  } catch(err) {
    ProfileHandlerService.NonExistentProfileError(USER_ID);
  }
  */

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

    const personObj = {
      email: userName, 
      nickname: userName, 
      pronouns: userPronouns, 
      userId: profileId,
    };
    const personJson = JSON.stringify(personObj);

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
