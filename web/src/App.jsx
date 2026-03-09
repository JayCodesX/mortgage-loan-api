import { useMemo, useState } from 'react'
import './App.css'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const defaultMortgageForm = {
  loanAmount: '450000',
  downPayment: '90000',
  annualInterestRate: '6.5',
  termYears: '30',
}

const defaultAmortizationForm = {
  principal: '350000',
  annualInterestRate: '6.5',
  termYears: '30',
}

const views = ['home', 'calculators', 'docs', 'login']

function formatCurrency(value) {
  if (value === undefined || value === null || Number.isNaN(Number(value))) {
    return '--'
  }

  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 2,
  }).format(Number(value))
}

function App() {
  const [activeView, setActiveView] = useState('home')
  const [activeCalculator, setActiveCalculator] = useState('mortgage')
  const [mortgageForm, setMortgageForm] = useState(defaultMortgageForm)
  const [amortizationForm, setAmortizationForm] = useState(defaultAmortizationForm)
  const [mortgageResult, setMortgageResult] = useState(null)
  const [amortizationResult, setAmortizationResult] = useState(null)
  const [errorMessage, setErrorMessage] = useState('')
  const [loadingTarget, setLoadingTarget] = useState('')
  const [authUser, setAuthUser] = useState(null)
  const [loginForm, setLoginForm] = useState({ email: '', password: '' })

  const endpointPreview = useMemo(
    () => ({
      mortgage: `${API_BASE_URL}/loans/mortgage-payment/calculate`,
      amortization: `${API_BASE_URL}/loans/amortization/calculate`,
    }),
    [],
  )

  const docsEndpointPreview = useMemo(() => {
    const secureOrigin = (() => {
      if (typeof window === 'undefined') {
        return 'https://localhost:8443'
      }
      if (window.location.protocol === 'https:') {
        return window.location.origin
      }
      if (window.location.hostname === 'localhost') {
        return 'https://localhost:8443'
      }
      return `https://${window.location.host}`
    })()

    const base = `${secureOrigin}/api`
    return {
      borrowerPost: `${base}/borrowers`,
      loanPost: `${base}/loans`,
      loanGetExample: `${base}/loans/1`,
      loanAmortizationExample: `${base}/loans/1/amortization`,
      mortgageExample: `${base}/loans/mortgage-payment/calculate?loanAmount=450000&downPayment=90000&annualInterestRate=6.5&termYears=30`,
      amortizationExample: `${base}/loans/amortization/calculate?principal=360000&annualInterestRate=6.5&termYears=30`,
    }
  }, [])

  const handleInput = (setter) => (event) => {
    const { name, value } = event.target
    setter((current) => ({ ...current, [name]: value }))
  }

  const callCalculator = async (target, formValues, endpoint) => {
    setErrorMessage('')
    setLoadingTarget(target)

    const params = new URLSearchParams(formValues)

    try {
      const response = await fetch(`${endpoint}?${params.toString()}`)
      if (!response.ok) {
        const errorBody = await response.text()
        throw new Error(errorBody || 'Unable to calculate at this time.')
      }

      return await response.json()
    } catch (error) {
      setErrorMessage(error.message || 'Something went wrong while calling the API.')
      return null
    } finally {
      setLoadingTarget('')
    }
  }

  const handleMortgageSubmit = async (event) => {
    event.preventDefault()
    const result = await callCalculator('mortgage', mortgageForm, endpointPreview.mortgage)
    if (result) {
      setMortgageResult(result)
    }
  }

  const handleAmortizationSubmit = async (event) => {
    event.preventDefault()
    const result = await callCalculator('amortization', amortizationForm, endpointPreview.amortization)
    if (result) {
      setAmortizationResult(result)
    }
  }

  const handleLoginSubmit = (event) => {
    event.preventDefault()
    if (!loginForm.email || !loginForm.password) {
      setErrorMessage('Email and password are required.')
      return
    }

    setErrorMessage('')
    setAuthUser({ firstName: 'Jay', email: loginForm.email })
    setLoginForm({ email: '', password: '' })
    setActiveView('calculators')
  }

  const showView = (view) => {
    if (views.includes(view)) {
      setActiveView(view)
    }
  }

  return (
    <div className="app-shell">
      <header className="top-nav-wrap">
        <div className="top-nav">
          <button className="brand" type="button" onClick={() => showView('home')}>
            Mortgage Loan API
          </button>
          <nav className="menu">
            <button type="button" onClick={() => showView('home')} className={activeView === 'home' ? 'active' : ''}>
              Home
            </button>
            <button
              type="button"
              onClick={() => showView('calculators')}
              className={activeView === 'calculators' ? 'active' : ''}
            >
              Calculators
            </button>
            <button type="button" onClick={() => showView('docs')} className={activeView === 'docs' ? 'active' : ''}>
              API Docs
            </button>
            <button
              type="button"
              onClick={() => showView('login')}
              className={activeView === 'login' ? 'active' : ''}
            >
              {authUser ? 'Profile' : 'Login'}
            </button>
          </nav>
          {authUser ? (
            <button className="ghost-btn" type="button" onClick={() => setAuthUser(null)}>
              Logout
            </button>
          ) : (
            <button className="ghost-btn" type="button" onClick={() => showView('login')}>
              Sign In
            </button>
          )}
        </div>
      </header>

      <main className="content-wrap">
        {errorMessage && <p className="error-banner">{errorMessage}</p>}

        {activeView === 'home' && (
          <section className="hero">
            <p className="eyebrow">Mortgage Loan API</p>
            <h1>Professional Mortgage Tools, Fast and Free.</h1>
            <p className="hero-copy">
              A polished API gateway project that offers free calculators for monthly mortgage estimates and
              amortization previews, powered by Spring Boot and a React frontend.
            </p>
            <div className="hero-actions">
              <button type="button" onClick={() => showView('calculators')}>
                Open Calculators
              </button>
              <button className="alt-btn" type="button" onClick={() => showView('docs')}>
                View API Endpoints
              </button>
            </div>
          </section>
        )}

        {activeView === 'calculators' && (
          <section className="panel">
            <div className="panel-head">
              <h2>Calculator Center</h2>
              <p>Choose a tool. Mortgage uses home price + down payment, amortization uses principal only.</p>
            </div>

            <div className="tab-row">
              <button
                type="button"
                className={activeCalculator === 'mortgage' ? 'active' : ''}
                onClick={() => setActiveCalculator('mortgage')}
              >
                Monthly Mortgage Payment
              </button>
              <button
                type="button"
                className={activeCalculator === 'amortization' ? 'active' : ''}
                onClick={() => setActiveCalculator('amortization')}
              >
                Amortization Preview
              </button>
            </div>

            {activeCalculator === 'mortgage' && (
              <article className="calc-card">
                <h3>Free Monthly Mortgage Payment Calculator</h3>
                <p>Estimate your payment from home price, down payment, annual interest, and term.</p>
                <form className="calc-form" onSubmit={handleMortgageSubmit}>
                  <label>
                    Home Price (Loan Amount)
                    <input
                      name="loanAmount"
                      inputMode="decimal"
                      value={mortgageForm.loanAmount}
                      onChange={handleInput(setMortgageForm)}
                      required
                    />
                  </label>
                  <label>
                    Down Payment
                    <input
                      name="downPayment"
                      inputMode="decimal"
                      value={mortgageForm.downPayment}
                      onChange={handleInput(setMortgageForm)}
                      required
                    />
                  </label>
                  <label>
                    Annual Interest Rate (%)
                    <input
                      name="annualInterestRate"
                      inputMode="decimal"
                      value={mortgageForm.annualInterestRate}
                      onChange={handleInput(setMortgageForm)}
                      required
                    />
                  </label>
                  <label>
                    Loan Term (Years)
                    <input
                      name="termYears"
                      inputMode="numeric"
                      value={mortgageForm.termYears}
                      onChange={handleInput(setMortgageForm)}
                      required
                    />
                  </label>
                  <button type="submit" disabled={loadingTarget === 'mortgage'}>
                    {loadingTarget === 'mortgage' ? 'Calculating...' : 'Calculate Payment'}
                  </button>
                </form>

                <div className="result-block">
                  <span>Estimated Monthly Payment</span>
                  <strong>{formatCurrency(mortgageResult?.monthlyPayment)}</strong>
                  <small>
                    Financed Principal: {formatCurrency(mortgageResult?.financedPrincipal)} | Payments:{' '}
                    {mortgageResult?.numberOfPayments ?? '--'}
                  </small>
                </div>
              </article>
            )}

            {activeCalculator === 'amortization' && (
              <article className="calc-card alt">
                <h3>Amortization Preview (Principal Only)</h3>
                <p>Preview payment amount and payment count using financed principal only.</p>
                <form className="calc-form" onSubmit={handleAmortizationSubmit}>
                  <label>
                    Principal
                    <input
                      name="principal"
                      inputMode="decimal"
                      value={amortizationForm.principal}
                      onChange={handleInput(setAmortizationForm)}
                      required
                    />
                  </label>
                  <label>
                    Annual Interest Rate (%)
                    <input
                      name="annualInterestRate"
                      inputMode="decimal"
                      value={amortizationForm.annualInterestRate}
                      onChange={handleInput(setAmortizationForm)}
                      required
                    />
                  </label>
                  <label>
                    Loan Term (Years)
                    <input
                      name="termYears"
                      inputMode="numeric"
                      value={amortizationForm.termYears}
                      onChange={handleInput(setAmortizationForm)}
                      required
                    />
                  </label>
                  <button type="submit" disabled={loadingTarget === 'amortization'}>
                    {loadingTarget === 'amortization' ? 'Calculating...' : 'Calculate Amortization'}
                  </button>
                </form>

                <div className="result-block">
                  <span>Monthly Payment From Principal</span>
                  <strong>{formatCurrency(amortizationResult?.monthlyPayment)}</strong>
                  <small>
                    Principal: {formatCurrency(amortizationResult?.principal)} | Payments:{' '}
                    {amortizationResult?.numberOfPayments ?? '--'}
                  </small>
                </div>
              </article>
            )}
          </section>
        )}

        {activeView === 'docs' && (
          <section className="panel docs">
            <div className="panel-head">
              <h2>API Endpoints</h2>
              <p>Secure edge-routed endpoints through Nginx. Click any link to open directly.</p>
            </div>
            <div className="api-grid">
              <a href={docsEndpointPreview.borrowerPost} target="_blank" rel="noreferrer">
                POST {docsEndpointPreview.borrowerPost}
              </a>
              <a href={docsEndpointPreview.loanPost} target="_blank" rel="noreferrer">
                POST {docsEndpointPreview.loanPost}
              </a>
              <a href={docsEndpointPreview.loanGetExample} target="_blank" rel="noreferrer">
                GET {docsEndpointPreview.loanGetExample}
              </a>
              <a href={docsEndpointPreview.loanAmortizationExample} target="_blank" rel="noreferrer">
                GET {docsEndpointPreview.loanAmortizationExample}
              </a>
              <a href={docsEndpointPreview.mortgageExample} target="_blank" rel="noreferrer">
                GET {docsEndpointPreview.mortgageExample}
              </a>
              <a href={docsEndpointPreview.amortizationExample} target="_blank" rel="noreferrer">
                GET {docsEndpointPreview.amortizationExample}
              </a>
            </div>
          </section>
        )}

        {activeView === 'login' && (
          <section className="panel login">
            <div className="panel-head">
              <h2>{authUser ? 'Profile' : 'Login'}</h2>
              <p>{authUser ? 'Signed in for calculator personalization.' : 'Sign in to save preferences later.'}</p>
            </div>

            {authUser ? (
              <div className="profile-card">
                <strong>Welcome, Jay</strong>
                <p>{authUser.email}</p>
                <button type="button" onClick={() => showView('calculators')}>
                  Go to Calculators
                </button>
              </div>
            ) : (
              <form className="calc-form login-form" onSubmit={handleLoginSubmit}>
                <label>
                  Email
                  <input
                    type="email"
                    name="email"
                    value={loginForm.email}
                    onChange={handleInput(setLoginForm)}
                    required
                  />
                </label>
                <label>
                  Password
                  <input
                    type="password"
                    name="password"
                    value={loginForm.password}
                    onChange={handleInput(setLoginForm)}
                    required
                  />
                </label>
                <button type="submit">Login</button>
              </form>
            )}
          </section>
        )}
      </main>

      <footer className="footer">
        <p>Mortgage Loan API by Jay</p>
      </footer>
    </div>
  )
}

export default App
