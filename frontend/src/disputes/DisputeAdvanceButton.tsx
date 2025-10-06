// src/disputes/DisputeAdvanceButton.tsx
import * as React from 'react';
import { Button, useNotify, useRecordContext, useRefresh } from 'react-admin';
import type { Dispute, DisputeStatus } from '../types';

const API = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';

function nextOptions(status: DisputeStatus): DisputeStatus[] {
  if (status === 'OPEN') return ['UNDER_REVIEW'];
  if (status === 'UNDER_REVIEW') return ['RESOLVED', 'REJECTED'];
  return [];
}
const label = (s: DisputeStatus) =>
  s === 'UNDER_REVIEW' ? 'Mark Under Review' : s === 'RESOLVED' ? 'Resolve' : s === 'REJECTED' ? 'Reject' : s;

export default function DisputeAdvanceButton() {
  const record = useRecordContext<Dispute>();
  const notify = useNotify();
  const refresh = useRefresh();
  const [busy, setBusy] = React.useState(false);

  if (!record) return null;
  // Safety: only show to admins
  if (localStorage.getItem('role') !== 'ADMIN') return null;

  const options = nextOptions(record.status);
  if (!options.length) return null;

  const advance = async (nextStatus: DisputeStatus) => {
    setBusy(true);
    try {
      const token = localStorage.getItem('token') || '';
      const res = await fetch(`${API}/disputes/${record.id}:advance`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ nextStatus }),
      });

      if (!res.ok) {
        const msg = await res.text();
        if (res.status === 401) throw new Error('Not authenticated. Please sign in again.');
        if (res.status === 403) throw new Error('You are not allowed to advance disputes.');
        throw new Error(msg || `Advance failed (${res.status})`);
      }

      notify(`Advanced to ${nextStatus.replace('_', ' ')}`, { type: 'info' });
      refresh();
    } catch (e: any) {
      notify(e?.message || 'Advance failed', { type: 'warning' });
    } finally {
      setBusy(false);
    }
  };

  return (
    <>
      {options.map(s => (
        <Button key={s} label={label(s)} disabled={busy} onClick={() => advance(s)} />
      ))}
    </>
  );
}
