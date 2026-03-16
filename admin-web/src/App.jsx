import { useEffect, useMemo, useState } from 'react'
import 'bootstrap/dist/css/bootstrap.min.css'
import './App.css'
import AdminLoginView from './components/AdminLoginView'
import AdminNav from './components/AdminNav'
import DashboardView from './components/DashboardView'
import QuoteLabView from './components/QuoteLabView'
import WorkspaceView from './components/WorkspaceView'
import PricingView from './components/PricingView'
import ReportsView from './components/ReportsView'
import DocsView from './components/DocsView'
import {
  API_BASE_URL,
  defaultAdminLogin,
  defaultBorrowerForm,
  defaultLoanForm,
  defaultProductForm,
  defaultQuoteForm,
  defaultReportForm,
  formatCurrency,
  getOrCreateSessionId,
  getStoredAuth,
} from './lib/adminApp'

function App() {
  const [activeTab, setActiveTab] = useState('dashboard')
  const [authState, setAuthState] = useState(() => getStoredAuth())
  const [loginForm, setLoginForm] = useState(defaultAdminLogin)
  const [summary, setSummary] = useState(null)
  const [quoteForm, setQuoteForm] = useState(defaultQuoteForm)
  const [quoteMonitorId, setQuoteMonitorId] = useState('')
  const [quoteResult, setQuoteResult] = useState(null)
  const [borrowerForm, setBorrowerForm] = useState(defaultBorrowerForm)
  const [borrowers, setBorrowers] = useState([])
  const [createdBorrower, setCreatedBorrower] = useState(null)
  const [loanForm, setLoanForm] = useState(defaultLoanForm)
  const [loanLookupId, setLoanLookupId] = useState('')
  const [loanResult, setLoanResult] = useState(null)
  const [productForm, setProductForm] = useState(defaultProductForm)
  const [products, setProducts] = useState([])
  const [editingProductId, setEditingProductId] = useState(null)
  const [reportForm, setReportForm] = useState(defaultReportForm)
  const [reportResult, setReportResult] = useState(null)
  const [errorMessage, setErrorMessage] = useState('')
  const [loadingTarget, setLoadingTarget] = useState('')
  const [sessionId] = useState(() => getOrCreateSessionId())

  const isAdmin = authState?.role === 'ADMIN'

  const endpointPreview = useMemo(() => ({
    login: `${API_BASE_URL}/auth/login`,
    adminSummary: `${API_BASE_URL}/metrics/admin/summary`,
    publicQuote: `${API_BASE_URL}/loan-quotes/public`,
    quoteById: `${API_BASE_URL}/loan-quotes`,
    borrowers: `${API_BASE_URL}/borrowers`,
    loans: `${API_BASE_URL}/loans`,
    products: `${API_BASE_URL}/admin/products`,
    reports: `${API_BASE_URL}/admin/reports/query`,
    mortgage: `${API_BASE_URL}/loans/mortgage-payment/calculate`,
    amortization: `${API_BASE_URL}/loans/amortization/calculate`,
  }), [])

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
      }

      if (!response.ok) {
        const errorBody = await response.text()
        throw new Error(errorBody || 'Unable to complete this admin request.')
      }

      if (response.status === 204) {
        return {}
      }

      return await response.json()
    } catch (error) {
      setErrorMessage(error.message || 'Unable to complete this admin request.')
      return null
    } finally {
      setLoadingTarget('')
    }
  }

  const fetchSummary = async () => {
    if (!authState?.accessToken) {
      return
    }

    const result = await callApi('summary', endpointPreview.adminSummary, {
      method: 'GET',
      headers: { Authorization: `Bearer ${authState.accessToken}` },
    })

    if (result) {
      setSummary(result)
    }
  }

  const handleLoginSubmit = async (event) => {
    event.preventDefault()
    const result = await callApi('login', endpointPreview.login, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(loginForm),
    })

    if (result) {
      saveAuthState(result)
    }
  }

  const handleAdminQuoteSubmit = async (event) => {
    event.preventDefault()
    const result = await callApi('admin-public-quote', endpointPreview.publicQuote, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Session-Id': sessionId,
      },
      body: JSON.stringify({
        ...quoteForm,
        homePrice: Number(quoteForm.homePrice),
        downPayment: Number(quoteForm.downPayment),
        termYears: Number(quoteForm.termYears),
      }),
    })

    if (result) {
      setQuoteResult(result)
      setQuoteMonitorId(String(result.id))
    }
  }

  const fetchQuote = async () => {
    if (!quoteMonitorId.trim()) {
      setErrorMessage('Enter a quote ID to load the quote snapshot.')
      return
    }

    const result = await callApi('quote-monitor', `${endpointPreview.quoteById}/${quoteMonitorId.trim()}`, {
      method: 'GET',
      headers: { 'X-Session-Id': sessionId },
    })

    if (result) {
      setQuoteResult(result)
    }
  }

  const fetchBorrowers = async () => {
    if (!authState?.accessToken) {
      setErrorMessage('Sign in as an admin before loading borrowers.')
      return
    }

    const result = await callApi('borrowers', endpointPreview.borrowers, {
      method: 'GET',
      headers: { Authorization: `Bearer ${authState.accessToken}` },
    })

    if (result) {
      setBorrowers(result)
    }
  }

  const createBorrower = async (event) => {
    event.preventDefault()
    if (!authState?.accessToken) {
      setErrorMessage('Sign in as an admin before creating a borrower.')
      return
    }

    const result = await callApi('create-borrower', endpointPreview.borrowers, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${authState.accessToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        ...borrowerForm,
        creditScore: Number(borrowerForm.creditScore),
      }),
    })

    if (result) {
      setCreatedBorrower(result)
      setLoanForm((current) => ({ ...current, borrowerId: String(result.id) }))
      fetchBorrowers()
    }
  }

  const createLoan = async (event) => {
    event.preventDefault()
    if (!authState?.accessToken) {
      setErrorMessage('Sign in as an admin before creating a loan.')
      return
    }

    const result = await callApi('create-loan', endpointPreview.loans, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${authState.accessToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        ...loanForm,
        borrowerId: Number(loanForm.borrowerId),
        loanAmount: Number(loanForm.loanAmount),
        interestRate: Number(loanForm.interestRate),
        termYears: Number(loanForm.termYears),
      }),
    })

    if (result) {
      setLoanResult(result)
      setLoanLookupId(String(result.id))
    }
  }

  const fetchLoan = async () => {
    if (!loanLookupId.trim()) {
      setErrorMessage('Enter a loan ID to load the loan snapshot.')
      return
    }

    const result = await callApi('loan-monitor', `${endpointPreview.loans}/${loanLookupId.trim()}`, {
      method: 'GET',
      headers: {},
    })

    if (result) {
      setLoanResult(result)
    }
  }

  const loadProducts = async () => {
    const result = await callApi('products', endpointPreview.products, {
      method: 'GET',
      headers: { Authorization: `Bearer ${authState.accessToken}` },
    })

    if (result) {
      setProducts(result)
    }
  }

  const startEditProduct = (product) => {
    setEditingProductId(product.id)
    setProductForm({
      programCode: product.programCode,
      productName: product.productName,
      baseRate: String(product.baseRate),
      active: String(product.active),
    })
  }

  const resetProductForm = () => {
    setEditingProductId(null)
    setProductForm(defaultProductForm)
  }

  const saveProduct = async (event) => {
    event.preventDefault()
    const endpoint = editingProductId ? `${endpointPreview.products}/${editingProductId}` : endpointPreview.products
    const method = editingProductId ? 'PUT' : 'POST'
    const result = await callApi('save-product', endpoint, {
      method,
      headers: {
        Authorization: `Bearer ${authState.accessToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        ...productForm,
        baseRate: Number(productForm.baseRate),
        active: productForm.active === 'true',
      }),
    })

    if (result) {
      resetProductForm()
      loadProducts()
      fetchSummary()
    }
  }

  const deleteProduct = async (id) => {
    const result = await callApi(`delete-product-${id}`, `${endpointPreview.products}/${id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${authState.accessToken}` },
    })

    if (result !== null) {
      setProducts((current) => current.filter((product) => product.id !== id))
      fetchSummary()
    }
  }

  const runReport = async (event) => {
    event.preventDefault()
    const result = await callApi('run-report', endpointPreview.reports, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${authState.accessToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        ...reportForm,
        activeOnly: reportForm.activeOnly === 'true',
        minCreditScore: reportForm.minCreditScore ? Number(reportForm.minCreditScore) : null,
        maxCreditScore: reportForm.maxCreditScore ? Number(reportForm.maxCreditScore) : null,
      }),
    })

    if (result) {
      setReportResult(result)
    }
  }

  const handleSignOut = () => {
    saveAuthState(null)
    setSummary(null)
    setQuoteResult(null)
    setBorrowers([])
    setCreatedBorrower(null)
    setLoanResult(null)
    setProducts([])
    setReportResult(null)
    setErrorMessage('')
  }

  useEffect(() => {
    if (isAdmin) {
      fetchSummary()
    }
  }, [isAdmin])

  useEffect(() => {
    if (isAdmin && activeTab === 'pricing') {
      loadProducts()
    }
  }, [activeTab, isAdmin])

  const quoteOutcomeData = summary ? [
    { label: 'Completed', value: summary.quotes.quotesCompleted },
    { label: 'Failed', value: summary.quotes.quotesFailed },
    { label: 'Deduped', value: summary.quotes.quotesDeduped },
  ] : []

  const funnelData = summary ? [
    { label: 'Quotes', value: summary.quotes.sessionsWithQuotes },
    { label: 'Authenticated', value: summary.quotes.authenticatedSessions },
    { label: 'Refined', value: summary.quotes.sessionsWithRefinements },
    { label: 'Lead Converted', value: summary.quotes.leadConvertedSessions },
  ] : []

  if (!authState?.accessToken || !isAdmin) {
    return (
      <AdminLoginView
        authState={authState}
        errorMessage={errorMessage}
        loginForm={loginForm}
        setLoginForm={setLoginForm}
        loadingTarget={loadingTarget}
        handleLoginSubmit={handleLoginSubmit}
        handleInput={handleInput}
      />
    )
  }

  return (
    <div className="admin-app min-vh-100">
      <AdminNav activeTab={activeTab} setActiveTab={setActiveTab} authState={authState} onSignOut={handleSignOut} />

      <main className="container py-4 py-lg-5">
        {errorMessage ? <div className="alert alert-danger border-0 admin-alert">{errorMessage}</div> : null}

        {activeTab === 'dashboard' ? (
          <DashboardView
            summary={summary}
            funnelData={funnelData}
            quoteOutcomeData={quoteOutcomeData}
            fetchSummary={fetchSummary}
            loadingTarget={loadingTarget}
            setActiveTab={setActiveTab}
          />
        ) : null}

        {activeTab === 'quote-lab' ? (
          <QuoteLabView
            quoteForm={quoteForm}
            setQuoteForm={setQuoteForm}
            quoteMonitorId={quoteMonitorId}
            setQuoteMonitorId={setQuoteMonitorId}
            quoteResult={quoteResult}
            loadingTarget={loadingTarget}
            handleAdminQuoteSubmit={handleAdminQuoteSubmit}
            fetchQuote={fetchQuote}
            handleInput={handleInput}
          />
        ) : null}

        {activeTab === 'workspace' ? (
          <WorkspaceView
            borrowerForm={borrowerForm}
            setBorrowerForm={setBorrowerForm}
            borrowers={borrowers}
            createdBorrower={createdBorrower}
            loanForm={loanForm}
            setLoanForm={setLoanForm}
            loanLookupId={loanLookupId}
            setLoanLookupId={setLoanLookupId}
            loanResult={loanResult}
            loadingTarget={loadingTarget}
            fetchBorrowers={fetchBorrowers}
            createBorrower={createBorrower}
            createLoan={createLoan}
            fetchLoan={fetchLoan}
            handleInput={handleInput}
            formatCurrency={formatCurrency}
          />
        ) : null}

        {activeTab === 'pricing' ? (
          <PricingView
            productForm={productForm}
            setProductForm={setProductForm}
            products={products}
            editingProductId={editingProductId}
            loadingTarget={loadingTarget}
            loadProducts={loadProducts}
            saveProduct={saveProduct}
            startEditProduct={startEditProduct}
            resetProductForm={resetProductForm}
            deleteProduct={deleteProduct}
            handleInput={handleInput}
          />
        ) : null}

        {activeTab === 'reports' ? (
          <ReportsView
            reportForm={reportForm}
            setReportForm={setReportForm}
            reportResult={reportResult}
            loadingTarget={loadingTarget}
            runReport={runReport}
            handleInput={handleInput}
          />
        ) : null}

        {activeTab === 'docs' ? <DocsView endpointPreview={endpointPreview} /> : null}
      </main>
    </div>
  )
}

export default App
