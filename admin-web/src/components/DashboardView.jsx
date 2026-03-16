import { Bar, BarChart, CartesianGrid, Cell, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { palette } from '../lib/adminApp'

export default function DashboardView({ summary, funnelData, quoteOutcomeData, fetchSummary, loadingTarget, setActiveTab }) {
  return (
    <div className="d-grid gap-4">
      <section className="dashboard-hero row g-4 align-items-stretch">
        <div className="col-lg-7">
          <div className="admin-panel h-100 hero-panel">
            <span className="eyebrow-chip">Overview</span>
            <h1 className="display-6 fw-bold mt-3">Run the complete borrower quote demo from the admin console.</h1>
            <p className="text-secondary mt-3 mb-4">
              This view consolidates the internal tooling that was previously exposed in the borrower-facing UI: quote metrics, borrower operations, loan testing, and service-level verification.
            </p>
            <div className="d-flex flex-wrap gap-2">
              <button className="btn btn-primary" type="button" onClick={fetchSummary} disabled={loadingTarget === 'summary'}>
                {loadingTarget === 'summary' ? 'Refreshing...' : 'Refresh summary'}
              </button>
              <button className="btn btn-outline-secondary" type="button" onClick={() => setActiveTab('quote-lab')}>
                Open quote lab
              </button>
            </div>
          </div>
        </div>
        <div className="col-lg-5">
          <div className="admin-panel h-100 system-panel">
            <h2 className="h4 mb-3">System snapshot</h2>
            <ul className="list-unstyled admin-detail-list mb-0">
              <li><span>Auth users</span><strong>{summary?.auth.totalUsers ?? '--'}</strong></li>
              <li><span>Borrowers</span><strong>{summary?.borrowers.totalBorrowers ?? '--'}</strong></li>
              <li><span>Products</span><strong>{summary?.pricing.totalProducts ?? '--'}</strong></li>
              <li><span>Leads</span><strong>{summary?.leads.totalLeads ?? '--'}</strong></li>
              <li><span>Notifications</span><strong>{summary?.notifications.cachedQuoteSnapshots ?? '--'}</strong></li>
            </ul>
          </div>
        </div>
      </section>

      <section className="row g-4">
        <div className="col-xl-7">
          <div className="admin-panel h-100">
            <h2 className="h4 mb-3">Quote funnel</h2>
            {summary ? (
              <ResponsiveContainer width="100%" height={280}>
                <BarChart data={funnelData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="label" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="value" fill="#0b5ed7" radius={[10, 10, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            ) : <p className="text-secondary mb-0">Refresh the summary to load the dashboard charts.</p>}
          </div>
        </div>
        <div className="col-xl-5">
          <div className="admin-panel h-100">
            <h2 className="h4 mb-3">Quote outcomes</h2>
            {summary ? (
              <ResponsiveContainer width="100%" height={280}>
                <PieChart>
                  <Pie data={quoteOutcomeData} dataKey="value" nameKey="label" innerRadius={60} outerRadius={100}>
                    {quoteOutcomeData.map((entry, index) => <Cell key={entry.label} fill={palette[index % palette.length]} />)}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            ) : <p className="text-secondary mb-0">Refresh the summary to load the dashboard charts.</p>}
          </div>
        </div>
      </section>

      <section className="stats-grid">
        <article><span>Quotes Started</span><strong>{summary?.quotes.quotesStarted ?? '--'}</strong></article>
        <article><span>Completed</span><strong>{summary?.quotes.quotesCompleted ?? '--'}</strong></article>
        <article><span>Failed</span><strong>{summary?.quotes.quotesFailed ?? '--'}</strong></article>
        <article><span>Refinements</span><strong>{summary?.quotes.quoteRefinementsRequested ?? '--'}</strong></article>
        <article><span>Average Pricing</span><strong>{summary ? `${summary.quotes.averagePricingDurationMs} ms` : '--'}</strong></article>
        <article><span>Average Lead</span><strong>{summary ? `${summary.quotes.averageLeadDurationMs} ms` : '--'}</strong></article>
        <article><span>Admin Users</span><strong>{summary?.auth.adminUsers ?? '--'}</strong></article>
        <article><span>Active Rate Sheets</span><strong>{summary?.pricing.activeRateSheets ?? '--'}</strong></article>
        <article><span>Lead Snapshots</span><strong>{summary?.notifications.cachedQuoteSnapshots ?? '--'}</strong></article>
      </section>
    </div>
  )
}
