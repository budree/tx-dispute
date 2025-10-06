// src/transactions/ViewDisputeButton.tsx
import * as React from 'react';
import { Button } from 'react-admin';
import type { Dispute, DisputeStatus } from '../types';
import { fmtDateTime } from '../dateLocale';
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Typography, Stack, Stepper, Step, StepLabel, Chip, Box
} from '@mui/material';
import type { StepIconProps } from '@mui/material/StepIcon';
import {
  HourglassEmpty,
  ManageSearch,
  CheckCircle,
  Cancel,
  HelpOutline
} from '@mui/icons-material';

function chipColor(status: DisputeStatus) {
  if (status === 'RESOLVED') return 'success';
  if (status === 'REJECTED') return 'error';
  return 'info';
}

function ts(value?: string | null) {
  return value ? fmtDateTime(value) : '—';
}

export default function ViewDisputeButton({ dispute }: { dispute: Dispute }) {
  const [open, setOpen] = React.useState(false);
  if (!dispute) return null;

  const status = dispute.status as DisputeStatus;
  const isFinal = status === 'RESOLVED' || status === 'REJECTED';

  // Steps & labels (dynamic last step)
  const steps = isFinal
    ? [
        { key: 'OPEN', label: 'OPEN', ts: dispute.openedAt },
        { key: 'UNDER_REVIEW', label: 'UNDER REVIEW', ts: dispute.underReviewAt },
        { key: status, label: status.replace('_', ' '), ts: status === 'RESOLVED' ? dispute.resolvedAt : dispute.rejectedAt }
      ]
    : [
        { key: 'OPEN', label: 'OPEN', ts: dispute.openedAt },
        { key: 'UNDER_REVIEW', label: 'UNDER REVIEW', ts: dispute.underReviewAt },
        { key: 'PENDING_FINAL', label: 'Resolved/Rejected', ts: null }
      ];

  const activeStep =
    status === 'OPEN' ? 0 :
    status === 'UNDER_REVIEW' ? 1 :
    2; // RESOLVED or REJECTED

  const StepIcon = React.useMemo(
    () =>
      function StepIconComp(props: StepIconProps) {
        const idx = Number(props.icon) - 1;
        let IconComp: typeof HourglassEmpty;

        if (idx === 0) IconComp = HourglassEmpty;
        else if (idx === 1) IconComp = ManageSearch;
        else IconComp = isFinal ? (status === 'RESOLVED' ? CheckCircle : Cancel) : HelpOutline;

        let color = 'text.disabled';
        const isCompleted = idx < activeStep;
        const isActive = idx === activeStep;

        if (isCompleted || isActive) color = 'primary.main';
        if (idx === 2 && isFinal) color = status === 'RESOLVED' ? 'success.main' : 'error.main';

        return <IconComp sx={{ color, fontSize: 24 }} />;
      },
    [activeStep, isFinal, status]
  );

  return (
    <>
      <Button label="View Dispute Status" onClick={() => setOpen(true)} />
      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>Dispute #{dispute.id}</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{ mt: 1 }}>
            {/* Status flow with icons + per-step timestamps */}
            <Box>
              <Stepper activeStep={activeStep} alternativeLabel>
                {steps.map((s) => (
                  <Step key={s.key}>
                    <StepLabel
                      StepIconComponent={StepIcon}
                      optional={
                        <Typography variant="caption" sx={{ mt: 0.5, display: 'block' }}>
                          {ts(s.ts)}
                        </Typography>
                      }
                    >
                      {s.label}
                    </StepLabel>
                  </Step>
                ))}
              </Stepper>

              <Box sx={{ mt: 1, display: 'flex', justifyContent: 'center' }}>
                <Chip
                  label={`Current: ${status.replace('_', ' ')}`}
                  color={chipColor(status) as any}
                  size="small"
                />
              </Box>
            </Box>

            {/* Details */}
            <Typography><b>Transaction Ref:</b> {dispute.transactionRef}</Typography>
            <Typography><b>Reason:</b> {dispute.reason}</Typography>
            <Typography><b>Created:</b> {fmtDateTime(dispute.createdAt)}</Typography>
            {dispute.updatedAt && (
              <Typography><b>Updated:</b> {fmtDateTime(dispute.updatedAt)}</Typography>
            )}
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button label="Close" onClick={() => setOpen(false)} />
        </DialogActions>
      </Dialog>
    </>
  );
}
