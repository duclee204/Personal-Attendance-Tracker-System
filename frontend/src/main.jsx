import React, { useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import { BarChart3, CalendarDays, Clock3, Home, LogOut, Settings, Shield, UserRound, UsersRound } from 'lucide-react';
import { api, clearSession, setSession } from './api';
import './styles.css';

const tabs = [
  { id: 'home', label: 'Home', icon: Home },
  { id: 'history', label: 'History', icon: Clock3 },
  { id: 'schedule', label: 'Schedule', icon: CalendarDays },
  { id: 'profile', label: 'Profile', icon: UserRound },
  { id: 'admin', label: 'Admin', icon: Shield }
];

function fmtMinutes(minutes = 0) {
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return `${h}h ${String(m).padStart(2, '0')}m`;
}

function App() {
  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem('pats_user') || 'null'));
  const [tab, setTab] = useState('home');
  const [toast, setToast] = useState('');

  useEffect(() => {
    if ('serviceWorker' in navigator) navigator.serviceWorker.register('/sw.js');
  }, []);

  if (!user) return <AuthScreen onAuth={(auth) => { setSession(auth); setUser(auth.user); }} />;

  const visibleTabs = tabs.filter((item) => item.id !== 'admin' || user.role === 'ADMIN');
  return (
    <div className="app-shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">PATS</p>
          <h1>{tab === 'home' ? 'Dashboard' : visibleTabs.find((x) => x.id === tab)?.label}</h1>
        </div>
        <button className="icon-btn" title="Đăng xuất" onClick={() => { clearSession(); setUser(null); }}>
          <LogOut size={20} />
        </button>
      </header>

      <main className="content">
        {tab === 'home' && <Dashboard toast={setToast} />}
        {tab === 'history' && <History />}
        {tab === 'schedule' && <Schedule toast={setToast} />}
        {tab === 'profile' && <Profile user={user} setUser={setUser} toast={setToast} />}
        {tab === 'admin' && user.role === 'ADMIN' && <Admin />}
      </main>

      <nav className="bottom-nav">
        {visibleTabs.map(({ id, label, icon: Icon }) => (
          <button key={id} className={tab === id ? 'active' : ''} onClick={() => setTab(id)} title={label}>
            <Icon size={20} />
            <span>{label}</span>
          </button>
        ))}
      </nav>
      {toast && <div className="toast" onAnimationEnd={() => setToast('')}>{toast}</div>}
    </div>
  );
}

function AuthScreen({ onAuth }) {
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({ fullName: '', email: '', password: '' });
  const [error, setError] = useState('');

  async function submit(e) {
    e.preventDefault();
    setError('');
    try {
      const auth = await api(`/api/auth/${mode}`, { method: 'POST', body: JSON.stringify(form) });
      onAuth(auth);
    } catch (err) {
      setError('Không đăng nhập được. Kiểm tra email hoặc mật khẩu.');
    }
  }

  return (
    <div className="auth">
      <section className="auth-panel">
        <p className="eyebrow">Personal Attendance Tracker</p>
        <h1>Chấm công cá nhân</h1>
        <div className="segmented">
          <button className={mode === 'login' ? 'active' : ''} onClick={() => setMode('login')}>Đăng nhập</button>
          <button className={mode === 'register' ? 'active' : ''} onClick={() => setMode('register')}>Đăng ký</button>
        </div>
        <form onSubmit={submit}>
          {mode === 'register' && <input placeholder="Họ tên" value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} required />}
          <input placeholder="Email" type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
          <input placeholder="Mật khẩu" type="password" minLength="6" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required />
          {error && <p className="error">{error}</p>}
          <button className="primary" type="submit">{mode === 'login' ? 'Vào ứng dụng' : 'Tạo tài khoản'}</button>
        </form>
      </section>
    </div>
  );
}

function Dashboard({ toast }) {
  const [data, setData] = useState(null);
  const [note, setNote] = useState('');
  const load = () => api('/api/attendance/dashboard').then(setData);
  useEffect(() => {
    load();
  }, []);

  async function action(path) {
    await api(path, { method: 'POST', body: JSON.stringify({ note }) });
    setNote('');
    toast('Đã cập nhật chấm công');
    load();
  }

  return (
    <>
      <section className="status-band">
        <p>{new Date().toLocaleDateString('vi-VN', { weekday: 'long', day: '2-digit', month: '2-digit' })}</p>
        <h2>{data?.today ? (data.today.status === 'WORKING' ? 'Đang làm việc' : 'Đã hoàn tất') : 'Chưa check-in'}</h2>
        <textarea placeholder="Ghi chú chấm công" value={note} onChange={(e) => setNote(e.target.value)} />
        <div className="actions">
          <button className="primary" onClick={() => action('/api/attendance/check-in')} disabled={!!data?.today}>Check-in</button>
          <button className="secondary" onClick={() => action('/api/attendance/check-out')} disabled={!data?.today || data.today.checkOutAt}>Check-out</button>
        </div>
      </section>
      <section className="metric-grid">
        <Metric label="Tuần này" value={fmtMinutes(data?.workedMinutesThisWeek)} />
        <Metric label="Tháng này" value={fmtMinutes(data?.workedMinutesThisMonth)} />
        <Metric label="Ngày công" value={data?.attendanceDaysThisMonth || 0} />
        <Metric label="Đi muộn" value={`${Math.round(data?.lateRateThisMonth || 0)}%`} />
      </section>
      <RecordList rows={data?.recent || []} />
    </>
  );
}

function Metric({ label, value }) {
  return <article className="metric"><span>{label}</span><strong>{value}</strong></article>;
}

function History() {
  const [rows, setRows] = useState([]);
  useEffect(() => { api('/api/attendance').then(setRows); }, []);
  return <RecordList rows={rows} />;
}

function RecordList({ rows }) {
  return (
    <section className="list">
      {rows.map((row) => (
        <article className="row" key={row.id}>
          <div>
            <strong>{new Date(row.workDate).toLocaleDateString('vi-VN')}</strong>
            <span>{row.note || 'Không có ghi chú'}</span>
          </div>
          <div className="right">
            <b>{fmtMinutes(row.workedMinutes)}</b>
            <span>{row.late ? 'Muộn' : row.status === 'WORKING' ? 'Đang làm' : 'Đúng giờ'}</span>
          </div>
        </article>
      ))}
      {!rows.length && <p className="empty">Chưa có dữ liệu.</p>}
    </section>
  );
}

function Schedule({ toast }) {
  const [form, setForm] = useState(null);
  const days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
  useEffect(() => { api('/api/schedule').then(setForm); }, []);
  if (!form) return null;

  async function save() {
    const saved = await api('/api/schedule', { method: 'PUT', body: JSON.stringify(form) });
    setForm(saved);
    toast('Đã lưu lịch làm việc');
  }

  return (
    <section className="form-section">
      <div className="day-grid">
        {days.map((day) => (
          <button key={day} className={form.workDays.includes(day) ? 'active' : ''} onClick={() => {
            const has = form.workDays.includes(day);
            setForm({ ...form, workDays: has ? form.workDays.filter((x) => x !== day) : [...form.workDays, day] });
          }}>{day.slice(0, 3)}</button>
        ))}
      </div>
      <label>Giờ vào<input type="time" value={form.startTime} onChange={(e) => setForm({ ...form, startTime: e.target.value })} /></label>
      <label>Giờ ra<input type="time" value={form.endTime} onChange={(e) => setForm({ ...form, endTime: e.target.value })} /></label>
      <label className="toggle"><input type="checkbox" checked={form.reminderEnabled} onChange={(e) => setForm({ ...form, reminderEnabled: e.target.checked })} /> Nhắc chấm công</label>
      <label>Nhắc trước phút<input type="number" min="0" value={form.reminderMinutesBefore} onChange={(e) => setForm({ ...form, reminderMinutesBefore: Number(e.target.value) })} /></label>
      <button className="primary" onClick={save}>Lưu lịch</button>
    </section>
  );
}

function Profile({ user, setUser, toast }) {
  const [form, setForm] = useState(user);
  async function save() {
    const saved = await api('/api/me', { method: 'PUT', body: JSON.stringify(form) });
    localStorage.setItem('pats_user', JSON.stringify(saved));
    setUser(saved);
    toast('Đã cập nhật hồ sơ');
  }
  return (
    <section className="form-section">
      <label>Họ tên<input value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} /></label>
      <label>Email<input value={form.email} disabled /></label>
      <label>Số điện thoại<input value={form.phone || ''} onChange={(e) => setForm({ ...form, phone: e.target.value })} /></label>
      <button className="primary" onClick={save}>Lưu hồ sơ</button>
    </section>
  );
}

function Admin() {
  const [summary, setSummary] = useState(null);
  const [users, setUsers] = useState([]);
  const load = () => Promise.all([api('/api/admin/dashboard'), api('/api/admin/users')]).then(([a, b]) => { setSummary(a); setUsers(b); });
  useEffect(() => {
    load();
  }, []);

  return (
    <>
      <section className="metric-grid">
        <Metric label="Users" value={summary?.totalUsers || 0} />
        <Metric label="Active" value={summary?.activeUsers || 0} />
        <Metric label="Hôm nay" value={summary?.checkedInToday || 0} />
        <Metric label="Giờ tháng" value={fmtMinutes(summary?.workedMinutesThisMonth)} />
      </section>
      <section className="list">
        {users.map((u) => (
          <article className="row" key={u.id}>
            <div><strong>{u.fullName}</strong><span>{u.email}</span></div>
            <div className="right"><UsersRound size={18} /><span>{u.role}</span></div>
          </article>
        ))}
      </section>
    </>
  );
}

createRoot(document.getElementById('root')).render(<App />);
