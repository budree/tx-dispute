// Use 'en-GB' for DD/MM/YYYY; or switch to 'en-ZA' if you prefer SA locale
export const DATE_LOCALE = 'en-GB';

export const dateOptions: Intl.DateTimeFormatOptions = { dateStyle: 'medium' };
export const dateTimeOptions: Intl.DateTimeFormatOptions = { dateStyle: 'medium', timeStyle: 'short' };

export const fmtDate = (value: string | number | Date) =>
  new Intl.DateTimeFormat(DATE_LOCALE, dateOptions).format(new Date(value));

export const fmtDateTime = (value: string | number | Date) =>
  new Intl.DateTimeFormat(DATE_LOCALE, dateTimeOptions).format(new Date(value));
