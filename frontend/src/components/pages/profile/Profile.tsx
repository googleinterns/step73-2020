import * as React from "react";
import { createStyles } from "@material-ui/core/styles";
import FilledInput from "@material-ui/core/FilledInput";
import FormControl from "@material-ui/core/FormControl";
import FormHelperText from "@material-ui/core/FormHelperText";
import Input from "@material-ui/core/Input";
import InputLabel from "@material-ui/core/InputLabel";
import { makeStyles } from "@material-ui/core/styles";
import OutlinedInput from "@material-ui/core/OutlinedInput";
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
  const [email, setEmail] = React.useState('Email');
  const [name, setName] = React.useState('Name');
  const [pronouns, setPronouns] = React.useState('Pronouns');

  const classes = useStyles();

  const handleEmailChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setEmail(event.target.value);
  };

  const handleNicknameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setName(event.target.value);
  };

  const handlePronounsChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setPronouns(event.target.value);
  };

  return (
    <div className={classes.root}>
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
          onChange={handleNicknameChange}
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
    </div>
  );
}
