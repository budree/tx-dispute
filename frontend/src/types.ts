export type Transaction = {
  id: number;
  reference: string;
  amount: number;
  currency: string;    // "ZAR" by default
  createdAt: string;
};

export type DisputeStatus = 'OPEN' | 'UNDER_REVIEW' | 'RESOLVED' | 'REJECTED';

export type Dispute = {
  id: number;
  transactionRef: string;
  reason: string;
  status: DisputeStatus;
  createdAt: string;
  updatedAt?: string | null;

  // NEW timestamp fields (ISO strings from backend)
  openedAt: string;
  underReviewAt?: string | null;
  resolvedAt?: string | null;
  rejectedAt?: string | null;
};
