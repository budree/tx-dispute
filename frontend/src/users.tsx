import { List, Datagrid, TextField, Create, SimpleForm, TextInput, SelectInput } from 'react-admin';

export const UserList = () => (
  <List perPage={50}>
    <Datagrid>
      <TextField source="id" />
      <TextField source="username" />
      <TextField source="role" />
    </Datagrid>
  </List>
);

export const UserCreate = () => (
  <Create>
    <SimpleForm>
      <TextInput source="username" required />
      <TextInput source="password" type="password" required />
      <SelectInput source="role" choices={[
        { id: 'CLIENT', name: 'Client' },
        { id: 'ADMIN', name: 'Administrator' },
      ]} defaultValue="CLIENT" />
    </SimpleForm>
  </Create>
);
