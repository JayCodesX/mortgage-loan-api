export default function DocsView({ endpointPreview }) {
  return (
    <div className="row g-4 docs-grid">
      <div className="col-md-6 col-xl-3"><article className="doc-card"><strong>Auth</strong><div>{endpointPreview.login}</div></article></div>
      <div className="col-md-6 col-xl-3"><article className="doc-card"><strong>Admin summary</strong><div>{endpointPreview.adminSummary}</div></article></div>
      <div className="col-md-6 col-xl-3"><article className="doc-card"><strong>Public quote</strong><div>{endpointPreview.publicQuote}</div></article></div>
      <div className="col-md-6 col-xl-3"><article className="doc-card"><strong>Quote lookup</strong><div>{endpointPreview.quoteById}</div></article></div>
      <div className="col-md-6 col-xl-3"><article className="doc-card"><strong>Borrowers</strong><div>{endpointPreview.borrowers}</div></article></div>
      <div className="col-md-6 col-xl-3"><article className="doc-card"><strong>Loans</strong><div>{endpointPreview.loans}</div></article></div>
      <div className="col-md-6 col-xl-3"><article className="doc-card"><strong>Admin products</strong><div>{endpointPreview.products}</div></article></div>
      <div className="col-md-6 col-xl-3"><article className="doc-card"><strong>Admin reports</strong><div>{endpointPreview.reports}</div></article></div>
      <div className="col-md-6 col-xl-3"><article className="doc-card"><strong>Mortgage calculator</strong><div>{endpointPreview.mortgage}</div></article></div>
      <div className="col-md-6 col-xl-3"><article className="doc-card"><strong>Amortization</strong><div>{endpointPreview.amortization}</div></article></div>
    </div>
  )
}
