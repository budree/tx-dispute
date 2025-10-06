import { Admin, Resource } from 'react-admin';
import { authProvider } from './authProvider';
import { dataProvider } from './dataProvider';
import { TransactionList } from './transactions';
import { DisputeList, DisputeShow } from './disputes';
import { UserList, UserCreate } from './users';

export default function App() {
  return (
    <Admin authProvider={authProvider} dataProvider={dataProvider}>
      {(permissions: string) => (
        <>
          {permissions === 'CLIENT' && (
            <Resource name="transactions" list={TransactionList} />
          )}
          {permissions === 'ADMIN' && (
            <>
              <Resource name="disputes" list={DisputeList} show={DisputeShow} />
              <Resource name="users" list={UserList} create={UserCreate} />
            </>
          )}
        </>
      )}
    </Admin>
  );
}
