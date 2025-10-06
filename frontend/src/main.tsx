import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode><App /></React.StrictMode>
);

const _fetch = window.fetch;
window.fetch = async (input, init) => {
  console.log('[fetch]', input, init);
  const res = await _fetch(input as RequestInfo, init);
  console.log('[fetch:res]', res.status, res.url);
  return res;
};
