/* ─────────────────────────────────────────────
   SMARTPARK MAIN JS - CLEAN PROFESSIONAL VERSION
────────────────────────────────────────────── */

/* ─── helpers ─────────────────────────────── */
function msg(id, text, isErr) {
  const el = document.getElementById(id);
  if (!el) return;
  el.className = 'message ' + (isErr ? 'error' : 'success');
  el.innerHTML = (isErr ? '&#9888; ' : '&#10003; ') + text;
}

async function parseRes(res) {
  const ct = res.headers.get('content-type') || '';
  return ct.includes('application/json') ? res.json() : res.text();
}

async function api(url, method, body) {
  const opts = { method, headers: {} };
  if (body !== undefined) {
    opts.headers['Content-Type'] = 'application/json';
    opts.body = JSON.stringify(body);
  }
  const res = await fetch(url, opts);
  const data = await parseRes(res);

  if (!res.ok) {
    const e = data?.error || data?.message ||
      (typeof data === 'string' ? data : JSON.stringify(data));
    throw new Error(e);
  }
  return data;
}

function getUser() {
  try { return JSON.parse(localStorage.getItem('sp_user')); }
  catch(e) { return null; }
}

function setUser(u) {
  localStorage.setItem('sp_user', JSON.stringify(u));
}

function logout() {
  localStorage.removeItem('sp_user');
  location.href = '/login';
}

/* ─── badges ─────────────────────────────── */
function badge(status) {
  const m = {
    PENDING:'badge-pending',
    APPROVED:'badge-approved',
    REJECTED:'badge-rejected',
    CANCELLED:'badge-cancelled',
    COMPLETED:'badge-completed'
  };
  return `<span class="badge ${m[status]||''}">${status}</span>`;
}

function fmtDt(dt) {
  if (!dt) return '—';
  return new Date(dt).toLocaleString('en-IN',{
    dateStyle:'medium',
    timeStyle:'short'
  });
}

function emptyRow(cols, txt) {
  return `<tr>
    <td colspan="${cols}" style="text-align:center;color:var(--muted);padding:24px">
      ${txt}
    </td>
  </tr>`;
}

/* ─── render current user ─────────────────────────────── */

/* parking list helpers */
async function loadAvailableSlots() {
  const grid = document.getElementById('slotsGrid');
  if (!grid) return;
  try {
    const slots = await api('/api/user/available-slots','GET');
    if (!slots.length) {
      grid.innerHTML = '<div class="empty-state">No slots available</div>';
      return;
    }
    grid.innerHTML = slots.map(s=>`
      <div class="slot-card">
        <div><strong>#${s.id}</strong> ${s.location}</div>
        <div>₹${s.price}/hr</div>
        <button class="btn btn-primary btn-sm" onclick="location.href='/booking?slotId=${s.id}'">
          Book Now
        </button>
      </div>`).join('');
  } catch(err) {
    grid.innerHTML = '<div class="empty-state error">' + err.message + '</div>';
  }
}
function renderUser() {
  const u = getUser();
  const el = document.getElementById('currentUser');
  if (!el) return;

  if (u) {
    const roleBadge = u.role === 'OWNER'
      ? '<span class="badge badge-approved" style="margin-left:6px">OWNER</span>'
      : '<span class="badge badge-completed" style="margin-left:6px">USER</span>';

    el.innerHTML = `<strong>${u.name}</strong>${roleBadge}`;
  } else {
    el.textContent = '';
  }
}

/* ───────────────── AUTH ───────────────── */

/* send-otp form on OTP page */
async function handleSendOtp(e) {
  e.preventDefault();
  const btn = e.target.querySelector('button[type=submit]');
  btn.disabled = true;
  btn.textContent = 'Sending...';

  try {
    const email = document.getElementById('sendEmail').value;
    await api('/api/auth/send-otp?email=' + encodeURIComponent(email), 'POST');
    msg('sendMsg', 'OTP sent to ' + email, false);
    // copy to verify field
    const ve = document.getElementById('verifyEmail');
    if (ve) ve.value = email;
  } catch (err) {
    msg('sendMsg', err.message, true);
  } finally {
    btn.disabled = false;
    btn.textContent = 'Send OTP';
  }
}


/* Register → Send OTP first */
// legacy register functionality (no longer used by merged page)
// kept for backward compatibility but registration now happens inline in
// register.html (see register script). If a future page uses this helper it
// will still send an OTP and redirect to /otp-verify.
async function handleRegister(e) {
  e.preventDefault();
  const btn = e.target.querySelector('button[type=submit]');
  btn.disabled = true;
  btn.textContent = 'Sending OTP...';

  try {
    const name = document.getElementById('name')?.value;
    const email = document.getElementById('email')?.value;
    const password = document.getElementById('password')?.value;
    const role = document.getElementById('role')?.value;

    await api('/api/auth/send-otp','POST',{ name, email, password, role });

    msg('msg','OTP sent to your email. Redirecting…',false);
    setTimeout(() => {
      location.href='/otp-verify?email=' + encodeURIComponent(email);
    },1500);

  } catch(err) {
    msg('msg',err.message,true);
  } finally {
    btn.disabled=false;
    btn.textContent='Create Account →';
  }
}

async function handleLogin(e) {
  e.preventDefault();
  const btn = e.target.querySelector('button[type=submit]');
  btn.disabled=true;
  btn.textContent='Logging in...';

  try {
    const user = await api('/api/auth/login','POST',{
      email: document.getElementById('email').value,
      password: document.getElementById('password').value
    });

    setUser(user);
    msg('msg','Login successful!',false);

    setTimeout(() => {
      location.href = user.role==='OWNER'
        ? '/owner-dashboard'
        : '/user-dashboard';
    },700);

  } catch(err) {
    msg('msg',err.message,true);
  } finally {
    btn.disabled=false;
    btn.textContent='Login';
  }
}

async function handleVerifyOtp(e) {
  e.preventDefault();
  const btn = e.target.querySelector('button[type=submit]');
  btn.disabled=true;
  btn.textContent='Verifying...';

  try {
    const email = document.getElementById('verifyEmail').value;
    const code = document.getElementById('code').value;
    await api('/api/auth/verify-otp','POST',{ email, code });

    msg('verifyMsg','OTP verified! Redirecting to login…',false);
    setTimeout(() => location.href='/login',1500);
  } catch(err) {
    msg('verifyMsg',err.message,true);
  } finally {
    btn.disabled=false;
    btn.textContent='Verify OTP';
  }
}

/* ───────────────── USER BOOKINGS ───────────────── */

/* owner dashboard helpers */
async function loadOwnerStats() {
  const ownerId = getUser()?.id;
  if (!ownerId) return;
  try {
    const data = await api('/api/owner/analytics?ownerId='+ownerId,'GET');
    const stats = data.stats || {};
    document.getElementById('statTotal').textContent = stats.totalBookings || 0;
    document.getElementById('statRevenue').textContent = '₹' + (data.revenue || 0);
  } catch(err) {
    msg('msg', err.message, true);
  }
}

async function loadOwnerSlots() {
  const tbody = document.getElementById('ownerSlotsBody');
  if (!tbody) return;
  const ownerId = getUser()?.id;
  if (!ownerId) return;
  try {
    const slots = await api('/api/owner/my-slots?ownerId='+ownerId,'GET');
    console.debug('owner slots fetched', slots);
    if (!slots || !slots.length) {
      tbody.innerHTML = '<tr><td colspan="5" class="empty-row">No slots added yet</td></tr>';
      return;
    }
    tbody.innerHTML = slots.map(s => `
      <tr>
        <td>#${s.id}</td>
        <td>${s.location}</td>
        <td>₹${s.price}/hr</td>
        <td>${s.available ? 'Yes' : 'No'}</td>
        <td>
          <button class="btn btn-secondary btn-sm" onclick="editSlot(${s.id},'${encodeURIComponent(s.location)}',${s.price},${s.available})">✎</button>
        </td>
        <td>
          <button class="btn btn-danger btn-sm" onclick="deleteSlot(${s.id})">Delete</button>
        </td>
      </tr>`).join('');
  } catch(err) {
    console.error('loadOwnerSlots error', err);
    msg('msg', err.message, true);
  }
}

async function loadOwnerBookings() {
  const tbody = document.getElementById('ownerBookingsBody');
  if (!tbody) return;
  const ownerId = getUser()?.id;
  if (!ownerId) return;
  try {
    const bookings = await api('/api/owner/bookings?ownerId='+ownerId,'GET');
    if (!bookings.length) {
      tbody.innerHTML = '<tr><td colspan="6" class="empty-row">No bookings yet</td></tr>';
      return;
    }
    tbody.innerHTML = bookings.map(b=>`
      <tr>
        <td>#${b.id}</td>
        <td>${b.user?.name||'–'}</td>
        <td>${b.slot?.location||'–'}</td>
        <td>${fmtDt(b.startTime)}</td>
        <td>${badge(b.status)}</td>
        <td>
          ${(b.status==='PENDING')
            ? `<button class="btn btn-primary btn-sm" onclick="updateBookingStatus(${b.id},'APPROVED')">✔</button>
               <button class="btn btn-warning btn-sm" onclick="updateBookingStatus(${b.id},'REJECTED')">✖</button>`
            : ''}
        </td>
      </tr>`).join('');
  } catch(err) {
    msg('msg', err.message, true);
  }
}

async function editSlot(slotId, locEncoded, price, available) {
  const newLoc = decodeURIComponent(locEncoded);
  const newPrice = prompt('Enter new price', price);
  const newAvail = confirm('Make slot available?');
  if (newPrice === null) return; // cancelled
  try {
    const ownerId = getUser()?.id;
    await api(`/api/owner/slot/${slotId}?ownerId=${ownerId}&location=` +
              encodeURIComponent(newLoc) +
              `&price=${parseFloat(newPrice)}&available=${newAvail}`,'PUT');
    msg('msg','Slot updated',false);
    loadOwnerSlots();
    if (document.getElementById('slotsGrid')) loadAvailableSlots();
  } catch(err) { msg('msg', err.message, true); }
}

async function deleteSlot(slotId) {
  if (!confirm('Delete slot #' + slotId + '?')) return;
  try {
    const ownerId = getUser()?.id;
    await api(`/api/owner/slot/${slotId}?ownerId=${ownerId}`,'DELETE');
    msg('msg','Slot deleted',false);
    loadOwnerSlots();
  } catch(err) { msg('msg', err.message, true); }
}

async function updateBookingStatus(bookingId,status) {
  try {
    const ownerId = getUser()?.id;
    await api(`/api/owner/bookings/${bookingId}/status?ownerId=${ownerId}&status=${status}`,'PUT');
    msg('msg','Booking '+status.toLowerCase(),false);
    loadOwnerBookings();
    loadOwnerStats();
  } catch(err) { msg('msg', err.message, true); }
}

async function handleAddSlot(e) {
  e.preventDefault();
  const btn = e.target.querySelector('button[type=submit]');
  btn.disabled=true;
  btn.textContent='Adding...';
  try {
    const ownerId = getUser()?.id;
    if (!ownerId) throw new Error('Not logged in');
    const loc = document.getElementById('location').value.trim();
    const price = parseFloat(document.getElementById('price').value);
    if (!loc) throw new Error('Location required');
    if (!price || price <= 0) throw new Error('Valid price required');

    const slot = { location: loc, price, owner: { id: ownerId } };
    const created = await api('/api/owner/add-slot','POST', slot);

    console.info('Slot created response', created);
    msg('msg','Slot added: #' + created.id, false);

    // append row right away (in case loadOwnerSlots fails)
    const tbody = document.getElementById('ownerSlotsBody');
    if (tbody) {
      const row = document.createElement('tr');
      row.innerHTML = `
        <td>#${created.id}</td>
        <td>${created.location}</td>
        <td>₹${created.price}/hr</td>
        <td>${created.available ? 'Yes' : 'No'}</td>
        <td><button class="btn btn-danger btn-sm" onclick="deleteSlot(${created.id})">Delete</button></td>
      `;
      tbody.appendChild(row);
    }

    // refresh stats/list
    loadOwnerSlots();
    loadOwnerStats();
    if (document.getElementById('slotsGrid')) loadAvailableSlots();
  } catch(err) {
    console.error('add slot error', err);
    msg('msg', err.message || 'Failed to add slot', true);
  } finally {
    btn.disabled=false;
    btn.textContent='Add Slot';
  }
}


async function loadUserBookings() {
  const tbody = document.getElementById('bookingsBody');
  if (!tbody) return;

  const user = getUser();
  if (!user) return;

  try {
    const bookings = await api('/api/user/bookings?userId='+user.id,'GET');

    if (!bookings.length) {
      tbody.innerHTML = emptyRow(7,'No bookings yet.');
      return;
    }

    tbody.innerHTML = bookings.map(b=>`
      <tr>
        <td><strong>#${b.id}</strong></td>
        <td>${b.slot ? '📍 '+b.slot.location : '—'}</td>
        <td>${b.slot ? '₹'+b.slot.price+'/hr' : '—'}</td>
        <td>${fmtDt(b.startTime)}</td>
        <td>${fmtDt(b.endTime)}</td>
        <td>${badge(b.status)}</td>
        <td>
          ${b.status==='APPROVED'
            ? `<a href="/payment?bookingId=${b.id}&amount=${b.slot?.price||''}" 
                 class="btn btn-primary btn-sm">💳 Pay</a>`:''}
          ${b.status==='COMPLETED'
            ? `<a href="/rating?bookingId=${b.id}" 
                 class="btn btn-secondary btn-sm">⭐ Rate</a>`:''}
          ${(b.status==='PENDING'||b.status==='APPROVED')
            ? `<button class="btn btn-danger btn-sm"
                 onclick="cancelBooking(${b.id})">Cancel</button>`:''}
        </td>
      </tr>
    `).join('');

  } catch(err) {
    msg('msg',err.message,true);
  }
}

async function cancelBooking(id) {
  if (!confirm('Cancel booking #'+id+'?')) return;

  try {
    await api('/api/bookings/'+id+'/cancel','PUT');
    msg('msg','Booking cancelled.',false);
    loadUserBookings();
  } catch(err) {
    msg('msg',err.message,true);
  }
}

/* ───────────────── PROFILE ───────────────── */

/* handle parking slot booking page */
async function handleBooking(e) {
  e.preventDefault();
  const btn = e.target.querySelector('button[type=submit]');
  btn.disabled = true;
  btn.textContent = 'Booking...';

  try {
    const user = getUser();
    if (!user) throw new Error('You must be logged in to book a slot');

    const slotId = Number(document.getElementById('slotId').value);
    const startTime = document.getElementById('startTime').value;
    const endTime   = document.getElementById('endTime').value;

    if (!slotId || slotId <=0) throw new Error('Invalid slot ID');
    if (!startTime || !endTime) throw new Error('Start and end times required');

    const booking = await api('/api/user/book-slot','POST',{
      userId: user.id,
      slotId, startTime, endTime
    });

    // store for future autofill
    localStorage.setItem('sp_last_booking', booking.id);

    msg('msg','Booking created (ID '+booking.id+'). Redirecting...',false);
    setTimeout(()=>{ location.href='/user-dashboard'; },1500);
  } catch(err) {
    msg('msg', err.message, true);
  } finally {
    btn.disabled=false;
    btn.textContent='Confirm Booking →';
  }
}


async function handleProfileUpdate(e) {
  e.preventDefault();

  const user = getUser();
  if (!user) return;

  const btn = e.target.querySelector('button[type=submit]');
  btn.disabled=true;
  btn.textContent='Saving...';

  try {
    const updated = await api('/api/user/profile','PUT',{
      userId:user.id,
      name:document.getElementById('profileName').value,
      contactNumber:document.getElementById('profileContact').value,
      password:document.getElementById('profilePassword').value
    });

    updated.role = user.role;
    setUser(updated);
    renderUser();

    msg('msg','Profile updated successfully!',false);

  } catch(err) {
    msg('msg',err.message,true);
  } finally {
    btn.disabled=false;
    btn.textContent='Save Changes';
  }
}

async function handleRating(e) {
  e.preventDefault();
  const btn = e.target.querySelector('button[type=submit]');
  btn.disabled = true;
  btn.textContent = 'Submitting...';

  try {
    const bookingId = Number(document.getElementById('bookingId').value);
    const score = Number(document.getElementById('score').value) || 0;
    const comment = document.getElementById('comment').value.trim();

    if (!bookingId || bookingId <= 0) throw new Error('Invalid booking ID');
    if (!score || score < 1 || score > 5) throw new Error('Please select a rating');

    const user = getUser();
    if (!user) throw new Error('Not logged in');

    await api('/api/user/rating', 'POST', {
      bookingId,
      score,
      comment,
      userId: user.id
    });

    msg('msg', 'Rating submitted! Redirecting...', false);
    setTimeout(() => { location.href = '/user-dashboard'; }, 1500);
  } catch(err) {
    msg('msg', err.message, true);
  } finally {
    btn.disabled = false;
    btn.textContent = 'Submit Rating →';
  }
}

/* ───────────────── INIT ───────────────── */

document.addEventListener('DOMContentLoaded',()=>{

  renderUser();

  const bind = (id,fn)=>{
    const el=document.getElementById(id);
    if(el) el.addEventListener('submit',fn);
  };

  bind('registerForm',handleRegister);
  bind('loginForm',handleLogin);
  bind('sendOtpForm',handleSendOtp);
  bind('verifyOtpForm',handleVerifyOtp);
  bind('bookingForm',handleBooking);
  bind('addSlotForm',handleAddSlot);
  bind('profileForm',handleProfileUpdate);
  bind('ratingForm',handleRating);

  // auto-populate hidden userId on booking page
  const u = getUser();
  if (u) {
    if (document.getElementById('userId')) document.getElementById('userId').value = u.id;
    if (document.getElementById('ownerId')) document.getElementById('ownerId').value = u.id;
  }

  // If OTP page is visited with ?email=..., prefills both fields
  const params = new URLSearchParams(location.search);
  const preEmail = params.get('email');
  if (preEmail) {
    const sendEl = document.getElementById('sendEmail');
    const verifyEl = document.getElementById('verifyEmail');
    if (sendEl) sendEl.value = preEmail;
    if (verifyEl) verifyEl.value = preEmail;
  }

  if(document.getElementById('bookingsBody'))
    loadUserBookings();

  // parking list page
  if(document.getElementById('slotsGrid')) {
    loadAvailableSlots();
  }

  // booking page: prefill slot id from query parameter
  const params2 = new URLSearchParams(location.search);
  const suppliedSlot = params2.get('slotId');
  if (suppliedSlot && document.getElementById('slotId')) {
    document.getElementById('slotId').value = suppliedSlot;
  }

  // owner dashboard initial calls
  if(document.getElementById('ownerSlotsBody')) {
    loadOwnerSlots();
    loadOwnerBookings();
    loadOwnerStats();
  }

  const logoutBtn=document.getElementById('logoutBtn');
  if(logoutBtn) logoutBtn.addEventListener('click',logout);

});