const tabLabels = {
  dashboard: 'Dashboard',
  'quote-lab': 'Quote Lab',
  workspace: 'Workspace',
  docs: 'Docs',
}

export default function AdminNav({ activeTab, setActiveTab, authState, onSignOut }) {
  return (
    <nav className="navbar navbar-expand-lg admin-navbar sticky-top">
      <div className="container py-2">
        <span className="navbar-brand admin-brand">Mortgage Loan Admin</span>
        <div className="navbar-nav ms-auto align-items-lg-center gap-2">
          {Object.entries(tabLabels).map(([tab, label]) => (
            <button
              key={tab}
              className={`nav-link btn btn-link ${activeTab === tab ? 'active' : ''}`}
              type="button"
              onClick={() => setActiveTab(tab)}
            >
              {label}
            </button>
          ))}
          <a className="btn btn-outline-primary btn-sm rounded-pill px-3" href="https://localhost:8443" target="_blank" rel="noreferrer">
            Public Demo
          </a>
          <div className="session-pill">
            <span>{authState.email}</span>
            <button type="button" className="btn btn-sm btn-link text-decoration-none" onClick={onSignOut}>
              Sign Out
            </button>
          </div>
        </div>
      </div>
    </nav>
  )
}
