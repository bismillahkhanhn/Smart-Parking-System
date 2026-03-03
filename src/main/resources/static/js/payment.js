async function parseRes(res) {
  const ct = res.headers.get('content-type') || '';
  return ct.includes('application/json') ? res.json() : res.text();
}

function msg(id, text, isErr = false) {
  const el = document.getElementById(id);
  if (!el) return;

  el.className = 'message ' + (isErr ? 'error' : 'success');
  el.innerHTML = (isErr ? '&#9888; ' : '&#10003; ') + text;

  el.style.display = 'block';
}

document.addEventListener('DOMContentLoaded', () => {

  const params = new URLSearchParams(location.search);
  const bidEl  = document.getElementById('bookingId');
  const amtEl  = document.getElementById('amount');
  const form   = document.getElementById('paymentForm');

  // Auto-fill bookingId from URL or localStorage
  if (params.get('bookingId') && bidEl) {
    bidEl.value = params.get('bookingId');
  } else if (localStorage.getItem('sp_last_booking') && bidEl) {
    bidEl.value = localStorage.getItem('sp_last_booking');
  }

  // Auto-fill amount from URL
  if (params.get('amount') && amtEl) {
    amtEl.value = params.get('amount');
  }

  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const btn = form.querySelector('button[type=submit]');
    btn.disabled = true;
    btn.textContent = 'Processing...';

    try {
      const bookingId = Number(bidEl.value);
      const amount    = Number(amtEl.value);

      // Validation
      if (!bookingId || bookingId <= 0) {
        throw new Error("Invalid Booking ID");
      }

      if (!amount || amount <= 0) {
        throw new Error("Invalid payment amount");
      }

      const res = await fetch('/api/payments', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ bookingId, amount })
      });

      const data = await parseRes(res);

      if (!res.ok) {
        throw new Error(data?.error || data?.message || "Payment failed");
      }

      // Remove stored booking
      localStorage.removeItem('sp_last_booking');

      msg('msg', `Payment Successful! Payment ID: ${data.id} | Amount Paid: ₹${data.amount}`, false);

      // Redirect after 2 seconds
      setTimeout(() => {
        location.href = '/rating?bookingId=' + bookingId;
      }, 2000);

    } catch (err) {
      msg('msg', err.message || "Something went wrong", true);
    } finally {
      btn.disabled = false;
      btn.textContent = '💳 Pay Now';
    }
  });
});