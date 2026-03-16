import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import App from './App'

function mockSummary() {
  return {
    auth: { totalUsers: 3, adminUsers: 1, standardUsers: 2 },
    borrowers: {
      totalBorrowers: 4,
      averageCreditScore: 716.5,
      creditScoreBands: [
        { label: 'Prime 740+', value: 1 },
        { label: 'Near Prime 680-739', value: 1 },
      ],
    },
    pricing: {
      totalProducts: 4,
      activeProducts: 4,
      activeRateSheets: 5,
      activeAdjustmentRules: 16,
      programDistribution: [
        { label: 'CONVENTIONAL', value: 1 },
        { label: 'FHA', value: 1 },
      ],
    },
    leads: {
      totalLeads: 3,
      leadStatusDistribution: [{ label: 'NEW', value: 1 }],
      leadSourceDistribution: [{ label: 'PUBLIC_QUOTE_FUNNEL', value: 1 }],
    },
    notifications: { cachedQuoteSnapshots: 1 },
    quotes: {
      quotesStarted: 8,
      quotesCompleted: 11,
      quotesFailed: 0,
      quotesDeduped: 0,
      sessionsWithQuotes: 1,
      authenticatedSessions: 0,
      sessionsWithRefinements: 0,
      leadConvertedSessions: 0,
      averagePricingDurationMs: 1987,
      averageLeadDurationMs: 2636,
    },
  }
}

function mockJsonResponse(payload, overrides = {}) {
  return {
    ok: true,
    status: 200,
    json: async () => payload,
    text: async () => '',
    ...overrides,
  }
}

describe('Admin console', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', vi.fn())
    vi.stubGlobal('crypto', { randomUUID: () => 'admin-session-id' })
    window.localStorage.clear()
  })

  afterEach(() => {
    cleanup()
    vi.unstubAllGlobals()
  })

  it('shows a direct admin sign-in form when there is no session', () => {
    render(<App />)

    expect(screen.getByText(/Sign in to the admin console/i)).toBeInTheDocument()
    expect(screen.getAllByText('admin@jaycodesx.dev').length).toBeGreaterThan(0)
  })

  it('signs in directly and loads the dashboard', async () => {
    fetch
      .mockResolvedValueOnce(mockJsonResponse({
        accessToken: 'admin-token',
        tokenType: 'Bearer',
        expiresIn: 900,
        email: 'admin@jaycodesx.dev',
        role: 'ADMIN',
      }))
      .mockResolvedValueOnce(mockJsonResponse(mockSummary()))

    render(<App />)

    fireEvent.click(screen.getByRole('button', { name: /sign in as admin/i }))

    await waitFor(() => expect(screen.getByText(/Run the complete borrower quote demo from the admin console/i)).toBeInTheDocument())
    expect(screen.getByText(/Quote funnel/i)).toBeInTheDocument()
    expect(screen.getByText(/Auth users/i)).toBeInTheDocument()
  })

  it('rejects non-admin sessions', () => {
    window.localStorage.setItem('mortgage-loan-api-auth', JSON.stringify({
      accessToken: 'user-token',
      role: 'USER',
      email: 'jay@jaycodesx.dev',
    }))

    render(<App />)

    expect(screen.getByText(/does not have the `ADMIN` role/i)).toBeInTheDocument()
  })

  it('shows the quote lab and can create a quote', async () => {
    window.localStorage.setItem('mortgage-loan-api-auth', JSON.stringify({
      accessToken: 'admin-token',
      role: 'ADMIN',
      email: 'admin@jaycodesx.dev',
    }))

    fetch
      .mockResolvedValueOnce(mockJsonResponse(mockSummary()))
      .mockResolvedValueOnce(mockJsonResponse({
        id: 44,
        processingStatus: 'QUEUED',
        quoteStage: 'PUBLIC',
        quoteStatus: 'PENDING_PRICING',
      }))

    render(<App />)

    await waitFor(() => expect(screen.getByText(/Run the complete borrower quote demo from the admin console/i)).toBeInTheDocument())
    fireEvent.click(screen.getByRole('button', { name: /open quote lab/i }))
    fireEvent.click(screen.getByRole('button', { name: /create quote/i }))

    await waitFor(() => expect(screen.getByText(/Quote #44/i)).toBeInTheDocument())
    expect(fetch).toHaveBeenCalledWith('/api/loan-quotes/public', expect.any(Object))
  })

  it('shows workspace and docs tabs for internal tooling', async () => {
    window.localStorage.setItem('mortgage-loan-api-auth', JSON.stringify({
      accessToken: 'admin-token',
      role: 'ADMIN',
      email: 'admin@jaycodesx.dev',
    }))

    fetch
      .mockResolvedValueOnce(mockJsonResponse(mockSummary()))
      .mockResolvedValueOnce(mockJsonResponse([
        { id: 1, firstName: 'Jay', lastName: 'Carter', email: 'jay.carter@jaycodesx.dev', creditScore: 741 },
      ]))

    render(<App />)

    await waitFor(() => expect(screen.getByText(/Run the complete borrower quote demo from the admin console/i)).toBeInTheDocument())
    fireEvent.click(screen.getByRole('button', { name: /workspace/i }))
    fireEvent.click(screen.getByRole('button', { name: /load borrowers/i }))

    await waitFor(() => expect(screen.getByText(/Jay Carter/i)).toBeInTheDocument())

    fireEvent.click(screen.getByRole('button', { name: /docs/i }))
    expect(screen.getByText(/GET \/api\/metrics\/admin\/summary/)).toBeInTheDocument()
  })
})
