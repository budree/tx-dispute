import {
  List, Datagrid, TextField, DateField,
  TextInput, SelectInput, Show, SimpleShowLayout,
  DateInput, SearchInput,
} from 'react-admin';
import { DATE_LOCALE, dateTimeOptions } from './dateLocale';
import DisputeAdvanceButton from './disputes/DisputeAdvanceButton';

// Reusable filter inputs shown in the List toolbar
const disputeFilters = [
  <SearchInput source="q" key="q" alwaysOn placeholder="Search all fields" />,
  <TextInput source="transactionRef" key="txref" label="Transaction Ref" />,
  <TextInput source="reason" key="reason" label="Reason" />,
  <SelectInput
    source="status" key="status" label="Status"
    choices={[
      { id: 'OPEN', name: 'OPEN' },
      { id: 'UNDER_REVIEW', name: 'UNDER REVIEW' },
      { id: 'RESOLVED', name: 'RESOLVED' },
      { id: 'REJECTED', name: 'REJECTED' },
    ]}
  />,
  <TextInput source="username" key="username" label="User (username)" />,
  <DateInput source="createdAt_gte" key="from" label="Created From" />,
  <DateInput source="createdAt_lte" key="to" label="Created To" />,
];

export const DisputeList = () => (
  <List perPage={50} filters={disputeFilters}>
    <Datagrid rowClick="show">
      <TextField source="id" />
      <TextField source="transactionRef" />
      <TextField source="reason" />
      <TextField source="status" />
      <TextField source="username" label="User" />
      <DateField source="createdAt" locales={DATE_LOCALE} options={dateTimeOptions} showTime />
      <DateField source="updatedAt" locales={DATE_LOCALE} options={dateTimeOptions} showTime />
    </Datagrid>
  </List>
);

export const DisputeShow = () => (
  <Show>
    <SimpleShowLayout>
      <TextField source="id" />
      <TextField source="transactionRef" />
      <TextField source="reason" />
      <TextField source="status" />
      <TextField source="username" label="User" />
      <DateField source="createdAt" locales={DATE_LOCALE} options={dateTimeOptions} showTime />
      <DateField source="updatedAt" locales={DATE_LOCALE} options={dateTimeOptions} showTime />
      {/* Only visible for admins internally (component already hides itself for non-admin) */}
      <DisputeAdvanceButton />
    </SimpleShowLayout>
  </Show>
);


