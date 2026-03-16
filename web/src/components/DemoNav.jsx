export default function DemoNav({ activeView, setActiveView, authState, isAdmin, onSignOut }) {
  return (
    <nav className="navbar navbar-expand-lg demo-navbar sticky-top">
      <div className="container py-2">
        <button className="navbar-brand brand-button" type="button" onClick={() => setActiveView('home')}>
          Mortgage Loan Demo
        </button>
        <div className="navbar-nav ms-auto align-items-lg-center gap-2">
          <button className={`nav-link btn btn-link ${activeView === 'home' ? 'active' : ''}`} type="button" onClick={() => setActiveView('home')}>
            Home
          </button>
          <button className={`nav-link btn btn-link ${activeView === 'tools' ? 'active' : ''}`} type="button" onClick={() => setActiveView('tools')}>
            Calculators
          </button>
          <button className={`nav-link btn btn-link ${activeView === 'auth' ? 'active' : ''}`} type="button" onClick={() => setActiveView('auth')}>
            {authState ? 'My Account' : 'Sign In'}
          </button>
          {isAdmin ? (
            <a className="btn btn-outline-primary btn-sm rounded-pill px-3" href="https://localhost:8443/admin/" target="_blank" rel="noreferrer">
              Admin Console
            </a>
          ) : null}
          {authState ? (
            <div className="session-pill">
              <span>{authState.email}</span>
              <button type="button" className="btn btn-sm btn-link text-decoration-none" onClick={onSignOut}>
                Sign Out
              </button>
            </div>
          ) : null}
        </div>
      </div>
    </nav>
  )
}
