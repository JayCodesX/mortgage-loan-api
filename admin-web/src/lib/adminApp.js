export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'
export const AUTH_STORAGE_KEY = 'mortgage-loan-api-auth'
export const SESSION_STORAGE_KEY = 'mortgage-loan-api-session-id'
export const palette = ['#0b5ed7', '#1d7a69', '#f59f28', '#cf4f4f', '#6c757d']

export const defaultAdminLogin = {
  email: 'admin@jaycodesx.dev',
  password: 'StrongPass123!',
}

export const defaultQuoteForm = {
  homePrice: '525000',
  downPayment: '105000',
  zipCode: '75201',
  loanProgram: 'CONVENTIONAL',
  propertyUse: 'PRIMARY_RESIDENCE',
  termYears: '30',
}

export const defaultBorrowerForm = {
  firstName: 'Jay',
  lastName: 'Harper',
  email: 'jay.harper@jaycodesx.dev',
  creditScore: '741',
}

export const defaultLoanForm = {
  borrowerId: '',
  loanAmount: '385000',
  interestRate: '6.125',
  termYears: '30',
  status: 'PENDING',
}

export const defaultProductForm = {
  programCode: 'CONVENTIONAL',
  productName: 'Demo 30-Year Fixed',
  baseRate: '6.1250',
  active: 'true',
}

export const defaultReportForm = {
  reportType: 'PRODUCTS',
  search: '',
  activeOnly: 'false',
  status: '',
  minCreditScore: '',
  maxCreditScore: '',
  programCode: '',
}

export function getStoredAuth() {
  if (typeof window === 'undefined') {
    return null
  }

  const raw = window.localStorage.getItem(AUTH_STORAGE_KEY)
  if (!raw) {
    return null
  }

  try {
    return JSON.parse(raw)
  } catch {
    window.localStorage.removeItem(AUTH_STORAGE_KEY)
    return null
  }
}

export function getOrCreateSessionId() {
  if (typeof window === 'undefined') {
    return 'server-session'
  }

  const existing = window.localStorage.getItem(SESSION_STORAGE_KEY)
  if (existing) {
    return existing
  }

  const created = crypto.randomUUID()
  window.localStorage.setItem(SESSION_STORAGE_KEY, created)
  return created
}

export function formatCurrency(value) {
  if (value === undefined || value === null || Number.isNaN(Number(value))) {
    return '--'
  }

  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 2,
  }).format(Number(value))
}
