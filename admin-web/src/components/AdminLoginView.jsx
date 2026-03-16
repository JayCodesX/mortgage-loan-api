export default function AdminLoginView({ authState, errorMessage, loginForm, setLoginForm, loadingTarget, handleLoginSubmit, handleInput }) {
  const isNonAdminSession = authState?.accessToken && authState?.role !== 'ADMIN'

  return (
    <div className="admin-app min-vh-100 d-flex align-items-center justify-content-center py-5">
      <div className="container">
        <div className="row justify-content-center">
          <div className="col-xl-10">
            <div className="admin-login-shell row g-0 overflow-hidden">
              <div className="col-lg-5 admin-login-copy p-4 p-lg-5">
                <span className="eyebrow-chip">Admin console</span>
                <h1 className="display-6 fw-bold mt-3">Run the full mortgage loan demo from one place.</h1>
                <p className="text-secondary mt-3 mb-4">
                  Sign in directly here to inspect borrower metrics, products, quote results, workspace actions, and the API surfaces hidden from the public demo.
                </p>
                <div className="login-hint-box">
                  <strong>Seeded admin credentials</strong>
                  <span>admin@jaycodesx.dev</span>
                  <span>StrongPass123!</span>
                </div>
                {isNonAdminSession ? (
                  <div className="alert alert-warning mt-4 mb-0">This session is valid but does not have the `ADMIN` role.</div>
                ) : null}
              </div>
              <div className="col-lg-7 p-4 p-lg-5 bg-white">
                <h2 className="h3 mb-3">Sign in to the admin console</h2>
                <p className="text-secondary mb-4">This route no longer depends on a token from the public app. It can authenticate directly against `auth-service`.</p>
                {errorMessage ? <div className="alert alert-danger">{errorMessage}</div> : null}
                <form className="row g-3" onSubmit={handleLoginSubmit}>
                  <div className="col-12">
                    <label className="form-label">Email</label>
                    <input className="form-control" type="email" name="email" value={loginForm.email} onChange={handleInput(setLoginForm)} required />
                  </div>
                  <div className="col-12">
                    <label className="form-label">Password</label>
                    <input className="form-control" type="password" name="password" value={loginForm.password} onChange={handleInput(setLoginForm)} required />
                  </div>
                  <div className="col-12 d-grid d-sm-flex gap-2">
                    <button className="btn btn-primary btn-lg px-4" type="submit" disabled={loadingTarget === 'login'}>
                      {loadingTarget === 'login' ? 'Signing in...' : 'Sign in as admin'}
                    </button>
                    <a className="btn btn-outline-secondary btn-lg px-4" href="https://localhost:8443" target="_blank" rel="noreferrer">
                      Open public demo
                    </a>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
