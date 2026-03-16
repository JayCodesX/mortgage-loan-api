export default function AuthView({
  authState,
  loginForm,
  setLoginForm,
  registerForm,
  setRegisterForm,
  loadingTarget,
  handleLoginSubmit,
  handleRegisterSubmit,
  handleInput,
}) {
  return (
    <section className="auth-shell row g-4">
      <div className="col-lg-5">
        <div className="auth-copy-panel h-100">
          <span className="eyebrow-chip">Sign in</span>
          <h2 className="display-6 fw-bold mt-3">Continue your quote when you are ready.</h2>
          <p className="text-secondary mt-3 mb-4">
            Sign in to personalize the borrower scenario, save the JWT-backed session, and unlock the admin console when you have an admin role.
          </p>
          <div className="auth-hint-box">
            <strong>Seeded admin</strong>
            <span>admin@jaycodesx.dev / StrongPass123!</span>
          </div>
          {authState ? (
            <div className="signed-in-box mt-3">
              <strong>{authState.email}</strong>
              <span>{authState.role} · {authState.tokenType}</span>
            </div>
          ) : null}
        </div>
      </div>
      <div className="col-lg-7">
        <div className="row g-4 h-100">
          <div className="col-md-6">
            <div className="auth-form-panel h-100">
              <h3 className="h4">Sign in</h3>
              <p className="text-secondary">Use auth-service to continue a borrower or admin session.</p>
              <form className="row g-3" onSubmit={handleLoginSubmit}>
                <div className="col-12">
                  <label className="form-label">Email</label>
                  <input className="form-control" type="email" name="email" value={loginForm.email} onChange={handleInput(setLoginForm)} required />
                </div>
                <div className="col-12">
                  <label className="form-label">Password</label>
                  <input className="form-control" type="password" name="password" value={loginForm.password} onChange={handleInput(setLoginForm)} required />
                </div>
                <div className="col-12 d-grid">
                  <button className="btn btn-primary btn-lg" type="submit" disabled={loadingTarget === 'login'}>
                    {loadingTarget === 'login' ? 'Signing in...' : 'Sign in'}
                  </button>
                </div>
              </form>
            </div>
          </div>
          <div className="col-md-6">
            <div className="auth-form-panel h-100 alt">
              <h3 className="h4">Create account</h3>
              <p className="text-secondary">Register a new user and immediately reuse the JWT in this demo.</p>
              <form className="row g-3" onSubmit={handleRegisterSubmit}>
                <div className="col-12">
                  <label className="form-label">Email</label>
                  <input className="form-control" type="email" name="email" value={registerForm.email} onChange={handleInput(setRegisterForm)} required />
                </div>
                <div className="col-12">
                  <label className="form-label">Password</label>
                  <input className="form-control" type="password" name="password" value={registerForm.password} onChange={handleInput(setRegisterForm)} required />
                </div>
                <div className="col-12 d-grid">
                  <button className="btn btn-outline-dark btn-lg" type="submit" disabled={loadingTarget === 'register'}>
                    {loadingTarget === 'register' ? 'Creating account...' : 'Register'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
