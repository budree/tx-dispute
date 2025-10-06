// src/transactions.tsx
import * as React from 'react';
import {
  List, Datagrid, TextField, NumberField, DateField, FunctionField,
  useGetList, useRefresh
} from 'react-admin';
import type { Transaction, Dispute } from './types';
import CreateDisputeButton from './transactions/CreateDisputeButton';
import ViewDisputeButton from './transactions/ViewDisputeButton';
import { DATE_LOCALE, dateTimeOptions } from './dateLocale';

export const TransactionList = () => {
  const { data: disputes = [], isLoading } = useGetList<Dispute>('disputes', {
    pagination: { page: 1, perPage: 1000 },
    sort: { field: 'id', order: 'DESC' },
    filter: {},
  });
  const refresh = useRefresh();

  const disputeMap = React.useMemo(() => {
    const map = new Map<string, Dispute>();
    disputes.forEach(d => {
      const curr = map.get(d.transactionRef);
      if (!curr || new Date(d.createdAt) > new Date(curr.createdAt)) map.set(d.transactionRef, d);
    });
    return map;
  }, [disputes]);

  return (
    <List perPage={50}>
      <Datagrid rowClick="show" bulkActionButtons={false}>
        <TextField source="id" />
        <TextField source="reference" />
        <NumberField source="amount" />
        <TextField source="currency" />
		<TextField source="description" />
        <DateField source="createdAt" locales={DATE_LOCALE} options={dateTimeOptions} showTime />
        <FunctionField<Transaction>
          label="Actions"
          render={(record) => {
            if (isLoading) return <span>…</span>;
            const linked = record.reference ? disputeMap.get(record.reference) : undefined;
            return linked
              ? <ViewDisputeButton dispute={linked} />
              : <CreateDisputeButton onCreated={() => refresh()} />;
          }}
        />
      </Datagrid>
    </List>
  );
};
