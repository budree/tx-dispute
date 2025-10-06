import * as React from 'react';
import { Button, useCreate, useNotify, useRecordContext, useRedirect } from 'react-admin';
import type { Transaction, Dispute } from '../types';
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, CircularProgress
} from '@mui/material';

export default function CreateDisputeButton({ onCreated }: { onCreated?: (d: Dispute) => void }) {
  const record = useRecordContext<Transaction>();
  const [open, setOpen] = React.useState(false);
  const [reason, setReason] = React.useState('');
  const [busy, setBusy] = React.useState(false);

  const [create] = useCreate();
  const notify = useNotify();
  const redirect = useRedirect();

  if (!record) return null;

  const handleOpen = () => {
    setReason(prev => prev || `Created from transaction ${record.reference}`);
    setOpen(true);
  };
  const handleClose = () => { if (!busy) setOpen(false); };

  const handleSubmit: React.FormEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();
    if (!record.reference) {
      notify('Transaction reference is missing', { type: 'warning' });
      return;
    }
    if (!reason.trim()) {
      notify('Please enter a reason', { type: 'warning' });
      return;
    }
    setBusy(true);
    try {
      const { data } = await create(
        'disputes',
        { data: { transactionRef: record.reference, reason } },
        { returnPromise: true }
      );
      const newId = (data as Dispute)?.id;
      notify(newId ? `Dispute #${newId} created` : 'Dispute created', { type: 'info' });
      setOpen(false);
      if (onCreated) onCreated(data as Dispute);
      else if (typeof newId !== 'undefined') redirect(`/disputes/${newId}/show`);
    } catch (err: any) {
      notify(err?.message || 'Failed to create dispute', { type: 'warning' });
    } finally {
      setBusy(false);
    }
  };

  return (
    <>
      <Button label="Create Dispute" onClick={handleOpen} disabled={!record.reference} />
      <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
        <form onSubmit={handleSubmit}>
          <DialogTitle>Create Dispute</DialogTitle>
          <DialogContent>
            <TextField
              autoFocus
              margin="dense"
              label="Reason"
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              fullWidth
              multiline
              minRows={2}
              required
            />
          </DialogContent>
          <DialogActions>
            <Button label="Cancel" onClick={handleClose} />
            <button
              type="submit"
              disabled={busy}
              style={{ padding: '6px 16px', display: 'inline-flex', alignItems: 'center', gap: 8 }}
            >
              {busy && <CircularProgress size={16} />}
              Create
            </button>
          </DialogActions>
        </form>
      </Dialog>
    </>
  );
}
