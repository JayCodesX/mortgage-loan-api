import { useEffect, useMemo, useState } from 'react'
import 'bootstrap/dist/css/bootstrap.min.css'
import './App.css'
import DemoNav from './components/DemoNav'
import HomeView from './components/HomeView'
import CalculatorsView from './components/CalculatorsView'
import AuthView from './components/AuthView'
import {
  API_BASE_URL,
  defaultAmortizationForm,
  defaultAuthForm,
  defaultMortgageForm,
  defaultQuoteForm,
  defaultRefineForm,
  getOrCreateSessionId,
  getQuoteStatusMeta,
  getStoredAuth,
  isQuoteInFlight,
} from './lib/demoApp'

function App() {
  const [activeView, setActiveView] = useState('home')
  const [activeCalculator, setActiveCalculator] = useState('mortgage')
  const [publicQuoteForm, setPublicQuoteForm] = useState(defaultQuoteForm)
  const [refineForm, setRefineForm] = useState(defaultRefineForm)
  const [mortgageForm, setMortgageForm] = useState(defaultMortgageForm)
  const [amortizationForm, setAmortizationForm] = useState(defaultAmortizationForm)
  const [loginForm, setLoginForm] = useState(defaultAuthForm)
  const [registerForm, setRegisterForm] = useState(defaultAuthForm)
  const [quoteResult, setQuoteResult] = useState(null)
  const [mortgageResult, setMortgageResult] = useState(null)
  const [amortizationResult, setAmortizationResult] = useState(null)
  const [errorMessage, setErrorMessage] = useState('')
  const [loadingTarget, setLoadingTarget] = useState('')
  const [authState, setAuthState] = useState(() => getStoredAuth())
  const [sessionId] = useState(() => getOrCreateSessionId())
  const isAdmin = authState?.role === 'ADMIN'

  const endpoints = useMemo(() => ({
    register: `${API_BASE_URL}/auth/register`,
    login: `${API_BASE_URL}/auth/login`,
    publicQuote: `${API_BASE_URL}/loan-quotes/public`,
    refineQuoteBase: `${API_BASE_URL}/loan-quotes`,
    quoteBase: `${API_BASE_URL}/loan-quotes`,
    quoteEventsBase: `${API_BASE_URL}/notifications/quotes`,
    authSessionMetric: `${API_BASE_URL}/metrics/quotes/sessions/authenticated`,
    mortgage: `${API_BASE_URL}/loans/mortgage-payment/calculate`,
    amortization: `${API_BASE_URL}/loans/amortization/calculate`,
  }), [])

  const quoteStatus = useMemo(() => getQuoteStatusMeta(quoteResult), [quoteResult])

  const saveAuthState = (nextAuthState) => {
    setAuthState(nextAuthState)

    if (typeof window === 'undefined') {
      return
    }

    if (nextAuthState) {
      window.localStorage.setItem('mortgage-loan-api-auth', JSON.stringify(nextAuthState))
    } else {
      window.localStorage.removeItem('mortgage-loan-api-auth')
    }
  }

  const handleInput = (setter) => (event) => {
    const { name, value } = event.target
    setter((current) => ({ ...current, [name]: value }))
  }

  const callApi = async (target, endpoint, options = {}) => {
    setErrorMessage('')
    setLoadingTarget(target)

    try {
      const response = await fetch(endpoint, {
        headers: {
          ...(options.headers || {}),
        },
        ...options,
      })

      if (response.status === 401) {
        saveAuthState(null)
        setActiveView('auth')
      }

      if (!response.ok) {
        const errorBody = await response.text()
        throw new Error(errorBody || 'Unable to complete this request.')
      }

      return await response.json()
    } catch (error) {
      setErrorMessage(error.message || 'Something went wrong while calling the API.')
      return null
    } finally {
      setLoadingTarget('')
    }
  }

  const callCalculator = async (target, formValues, endpoint) => {
    const params = new URLSearchParams(formValues)
    return callApi(target, `${endpoint}?${params.toString()}`, { method: 'GET', headers: {} })
  }

  const postAuthSessionMetric = async (authEventType) => {
    try {
      await fetch(`${endpoints.authSessionMetric}?authEventType=${encodeURIComponent(authEventType)}`, {
        method: 'POST',
        headers: { 'X-Session-Id': sessionId },
      })
    } catch {
    }
  }

  const fetchQuoteStatus = async (quoteId) => {
    const result = await callApi('quote-status', `${endpoints.quoteBase}/${quoteId}`, {
      method: 'GET',
      headers: { 'X-Session-Id': sessionId },
    })

    if (result) {
      setQuoteResult(result)
      setRefineForm((current) => ({ ...current, quoteId: String(result.id) }))
    }
  }

  const handlePublicQuoteSubmit = async (event) => {
    if (event) {
      event.preventDefault()
    }

    const result = await callApi('public-quote', endpoints.publicQuote, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Session-Id': sessionId,
      },
      body: JSON.stringify({
        ...publicQuoteForm,
        homePrice: Number(publicQuoteForm.homePrice),
        downPayment: Number(publicQuoteForm.downPayment),
        termYears: Number(publicQuoteForm.termYears),
      }),
    })

    if (result) {
      setQuoteResult(result)
      setRefineForm((current) => ({ ...current, quoteId: String(result.id) }))
    }
  }

  const handleRefineQuoteSubmit = async (event) => {
    event.preventDefault()

    if (!authState?.accessToken) {
      setErrorMessage('Sign in before personalizing the quote.')
      setActiveView('auth')
      return
    }

    if (!refineForm.quoteId.trim()) {
      setErrorMessage('Create a quote first so we have a scenario to personalize.')
      return
    }

    const result = await callApi('refine-quote', `${endpoints.refineQuoteBase}/${refineForm.quoteId.trim()}/refine`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${authState.accessToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        ...refineForm,
        annualIncome: Number(refineForm.annualIncome),
        monthlyDebts: Number(refineForm.monthlyDebts),
        creditScore: Number(refineForm.creditScore),
        cashReserves: Number(refineForm.cashReserves),
        firstTimeBuyer: refineForm.firstTimeBuyer === 'true',
        vaEligible: refineForm.vaEligible === 'true',
      }),
    })

    if (result) {
      setQuoteResult(result)
    }
  }

  const handleLoginSubmit = async (event) => {
    event.preventDefault()
    const result = await callApi('login', endpoints.login, {
      method: 'POST',
      body: JSON.stringify(loginForm),
      headers: { 'Content-Type': 'application/json' },
    })

    if (result) {
      saveAuthState(result)
      setRegisterForm((current) => ({ ...current, email: result.email }))
      await postAuthSessionMetric('LOGIN')
      setActiveView('home')
    }
  }

  const handleRegisterSubmit = async (event) => {
    event.preventDefault()
    const result = await callApi('register', endpoints.register, {
      method: 'POST',
      body: JSON.stringify(registerForm),
      headers: { 'Content-Type': 'application/json' },
    })

    if (result) {
      saveAuthState(result)
      setLoginForm((current) => ({ ...current, email: result.email }))
      await postAuthSessionMetric('REGISTER')
      setActiveView('home')
    }
  }

  const handleMortgageSubmit = async (event) => {
    event.preventDefault()
    const result = await callCalculator('mortgage', mortgageForm, endpoints.mortgage)
    if (result) {
      setMortgageResult(result)
    }
  }

  const handleAmortizationSubmit = async (event) => {
    event.preventDefault()
    const result = await callCalculator('amortization', amortizationForm, endpoints.amortization)
    if (result) {
      setAmortizationResult(result)
    }
  }

  const handleSignOut = () => {
    saveAuthState(null)
    setErrorMessage('')
  }

  useEffect(() => {
    if (!isQuoteInFlight(quoteResult)) {
      return undefined
    }

    const timer = window.setInterval(() => {
      fetchQuoteStatus(quoteResult.id)
    }, 4000)

    return () => window.clearInterval(timer)
  }, [quoteResult?.id, quoteResult?.processingStatus])

  useEffect(() => {
    if (typeof window === 'undefined' || !quoteResult?.id || !isQuoteInFlight(quoteResult)) {
      return undefined
    }

    const stream = new EventSource(`${endpoints.quoteEventsBase}/${quoteResult.id}/events`)
    stream.onmessage = (event) => {
      try {
        const payload = JSON.parse(event.data)
        setQuoteResult(payload)
        setRefineForm((current) => ({ ...current, quoteId: String(payload.id) }))
        if (!isQuoteInFlight(payload)) {
          stream.close()
        }
      } catch {
      }
    }
    stream.onerror = () => stream.close()

    return () => stream.close()
  }, [endpoints.quoteEventsBase, quoteResult?.id, quoteResult?.processingStatus])

  return (
    <div className="demo-app min-vh-100">
      <DemoNav
        activeView={activeView}
        setActiveView={setActiveView}
        authState={authState}
        isAdmin={isAdmin}
        onSignOut={handleSignOut}
      />

      <main className="container py-4 py-lg-5">
        {errorMessage ? <div className="alert alert-danger border-0 demo-alert">{errorMessage}</div> : null}

        {activeView === 'home' ? (
          <HomeView
            activeView={activeView}
            setActiveView={setActiveView}
            publicQuoteForm={publicQuoteForm}
            setPublicQuoteForm={setPublicQuoteForm}
            quoteStatus={quoteStatus}
            handlePublicQuoteSubmit={handlePublicQuoteSubmit}
            loadingTarget={loadingTarget}
            quoteResult={quoteResult}
            fetchQuoteStatus={fetchQuoteStatus}
            sessionId={sessionId}
            refineForm={refineForm}
            setRefineForm={setRefineForm}
            handleRefineQuoteSubmit={handleRefineQuoteSubmit}
            authState={authState}
            handleInput={handleInput}
          />
        ) : null}

        {activeView === 'tools' ? (
          <CalculatorsView
            activeCalculator={activeCalculator}
            setActiveCalculator={setActiveCalculator}
            mortgageForm={mortgageForm}
            setMortgageForm={setMortgageForm}
            mortgageResult={mortgageResult}
            amortizationForm={amortizationForm}
            setAmortizationForm={setAmortizationForm}
            amortizationResult={amortizationResult}
            loadingTarget={loadingTarget}
            handleMortgageSubmit={handleMortgageSubmit}
            handleAmortizationSubmit={handleAmortizationSubmit}
            handleInput={handleInput}
          />
        ) : null}

        {activeView === 'auth' ? (
          <AuthView
            authState={authState}
            loginForm={loginForm}
            setLoginForm={setLoginForm}
            registerForm={registerForm}
            setRegisterForm={setRegisterForm}
            loadingTarget={loadingTarget}
            handleLoginSubmit={handleLoginSubmit}
            handleRegisterSubmit={handleRegisterSubmit}
            handleInput={handleInput}
          />
        ) : null}
      </main>
    </div>
  )
}

export default App
