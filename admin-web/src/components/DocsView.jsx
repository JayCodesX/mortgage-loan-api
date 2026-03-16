export default function DocsView({ endpointPreview }) {
  const endpoints = [
    `POST ${endpointPreview.login}`,
    `GET ${endpointPreview.adminSummary}`,
    `POST ${endpointPreview.publicQuote}`,
    `GET ${endpointPreview.quoteById}/1`,
    `GET ${endpointPreview.borrowers}`,
    `POST ${endpointPreview.borrowers}`,
    `POST ${endpointPreview.loans}`,
    `GET ${endpointPreview.loans}/1`,
    `GET ${endpointPreview.mortgage}?loanAmount=450000&downPayment=90000&annualInterestRate=6.5&termYears=30`,
    `GET ${endpointPreview.amortization}?principal=350000&annualInterestRate=6.5&termYears=30`,
  ]

  return (
    <div className="admin-panel">
      <h2 className="h4 mb-3">Internal API catalog</h2>
      <p className="text-secondary mb-4">These endpoints stay out of the public navigation and are available from the internal console instead.</p>
      <div className="row g-3 docs-grid">
        {endpoints.map((endpoint) => (
          <div key={endpoint} className="col-lg-6">
            <div className="doc-card">{endpoint}</div>
          </div>
        ))}
      </div>
    </div>
  )
}
