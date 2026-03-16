import { formatCurrency } from '../lib/adminApp'

export default function QuoteLabView({
  quoteForm,
  setQuoteForm,
  quoteMonitorId,
  setQuoteMonitorId,
  quoteResult,
  loadingTarget,
  handleAdminQuoteSubmit,
  fetchQuote,
  handleInput,
}) {
  return (
    <div className="row g-4">
      <div className="col-xl-6">
        <div className="admin-panel h-100">
          <h2 className="h4 mb-3">Trigger a public quote</h2>
          <p className="text-secondary mb-4">Use the same public quote flow as the borrower demo, but from the internal console.</p>
          <form className="row g-3" onSubmit={handleAdminQuoteSubmit}>
            <div className="col-md-6">
              <label className="form-label">Home price</label>
              <input className="form-control" name="homePrice" value={quoteForm.homePrice} onChange={handleInput(setQuoteForm)} required />
            </div>
            <div className="col-md-6">
              <label className="form-label">Down payment</label>
              <input className="form-control" name="downPayment" value={quoteForm.downPayment} onChange={handleInput(setQuoteForm)} required />
            </div>
            <div className="col-md-6">
              <label className="form-label">ZIP code</label>
              <input className="form-control" name="zipCode" value={quoteForm.zipCode} onChange={handleInput(setQuoteForm)} required />
            </div>
            <div className="col-md-6">
              <label className="form-label">Loan program</label>
              <select className="form-select" name="loanProgram" value={quoteForm.loanProgram} onChange={handleInput(setQuoteForm)}>
                <option value="CONVENTIONAL">Conventional</option>
                <option value="FHA">FHA</option>
                <option value="VA">VA</option>
                <option value="JUMBO">Jumbo</option>
              </select>
            </div>
            <div className="col-md-6">
              <label className="form-label">Property use</label>
              <select className="form-select" name="propertyUse" value={quoteForm.propertyUse} onChange={handleInput(setQuoteForm)}>
                <option value="PRIMARY_RESIDENCE">Primary residence</option>
                <option value="SECOND_HOME">Second home</option>
                <option value="INVESTMENT">Investment</option>
              </select>
            </div>
            <div className="col-md-6">
              <label className="form-label">Term years</label>
              <select className="form-select" name="termYears" value={quoteForm.termYears} onChange={handleInput(setQuoteForm)}>
                <option value="15">15</option>
                <option value="20">20</option>
                <option value="30">30</option>
              </select>
            </div>
            <div className="col-12 d-grid d-sm-flex gap-2">
              <button className="btn btn-primary" type="submit" disabled={loadingTarget === 'admin-public-quote'}>
                {loadingTarget === 'admin-public-quote' ? 'Creating quote...' : 'Create quote'}
              </button>
              <button className="btn btn-outline-secondary" type="button" onClick={fetchQuote}>
                Refresh quote snapshot
              </button>
            </div>
          </form>
        </div>
      </div>
      <div className="col-xl-6">
        <div className="admin-panel h-100">
          <h2 className="h4 mb-3">Quote monitor</h2>
          <div className="row g-3 mb-4">
            <div className="col-sm-8">
              <label className="form-label">Quote ID</label>
              <input className="form-control" value={quoteMonitorId} onChange={(event) => setQuoteMonitorId(event.target.value)} placeholder="Enter quote ID" />
            </div>
            <div className="col-sm-4 d-grid align-content-end">
              <button className="btn btn-outline-primary" type="button" onClick={fetchQuote} disabled={loadingTarget === 'quote-monitor'}>
                {loadingTarget === 'quote-monitor' ? 'Loading...' : 'Load quote'}
              </button>
            </div>
          </div>
          <div className="quote-monitor-box">
            <strong>{quoteResult ? `Quote #${quoteResult.id}` : 'No quote loaded'}</strong>
            <ul className="list-unstyled admin-detail-list mt-3 mb-0">
              <li><span>Processing</span><strong>{quoteResult?.processingStatus ?? '--'}</strong></li>
              <li><span>Stage</span><strong>{quoteResult?.quoteStage ?? '--'}</strong></li>
              <li><span>Status</span><strong>{quoteResult?.quoteStatus ?? '--'}</strong></li>
              <li><span>Monthly payment</span><strong>{formatCurrency(quoteResult?.estimatedMonthlyPayment)}</strong></li>
              <li><span>Rate</span><strong>{quoteResult?.estimatedRate ?? '--'}</strong></li>
              <li><span>Lead captured</span><strong>{quoteResult ? (quoteResult.leadCaptured ? 'Yes' : 'No') : '--'}</strong></li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  )
}
