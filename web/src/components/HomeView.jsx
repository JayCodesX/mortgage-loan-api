import { formatCurrency, formatPercent, isQuoteInFlight } from '../lib/demoApp'

export default function HomeView({
  activeView,
  setActiveView,
  publicQuoteForm,
  setPublicQuoteForm,
  quoteStatus,
  handlePublicQuoteSubmit,
  loadingTarget,
  quoteResult,
  fetchQuoteStatus,
  sessionId,
  refineForm,
  setRefineForm,
  handleRefineQuoteSubmit,
  authState,
  handleInput,
}) {
  return (
    <div className="d-grid gap-4">
      <section className="hero-shell row g-4 align-items-stretch">
        <div className="col-xl-6">
          <div className="hero-copy-panel h-100">
            <span className="eyebrow-chip">Get a loan quote</span>
            <h1 className="display-5 fw-bold mt-3">See an estimated mortgage quote in minutes.</h1>
            <p className="lead text-secondary mt-3 mb-4">
              Start with a fast borrower-facing estimate, then personalize the scenario when you are ready to turn it into a mortgage lead.
            </p>
            <div className="trust-strip row row-cols-1 row-cols-sm-3 g-3 mb-4">
              <div className="col"><div className="trust-card"><strong>Fast pricing</strong><span>Public quote with no account required</span></div></div>
              <div className="col"><div className="trust-card"><strong>Personalize later</strong><span>Income, debts, credit, and reserves</span></div></div>
              <div className="col"><div className="trust-card"><strong>Demo-ready</strong><span>Real async pricing and lead capture flow</span></div></div>
            </div>
            <div className="stepper-strip d-flex flex-wrap gap-2">
              <span className="step-pill active">1. Property details</span>
              <span className={`step-pill ${quoteResult ? 'active' : ''}`}>2. Review quote</span>
              <span className={`step-pill ${quoteResult?.processingStatus === 'COMPLETED' ? 'active' : ''}`}>3. Personalize</span>
            </div>
          </div>
        </div>
        <div className="col-xl-6">
          <div className="quote-form-panel h-100">
            <div className="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-3">
              <div>
                <p className="text-uppercase small fw-bold text-primary mb-1">Quote request</p>
                <h2 className="h3 mb-1">Get a loan quote</h2>
                <p className="text-secondary mb-0">Enter the basics a borrower would expect from a consumer mortgage flow.</p>
              </div>
              <span className={`status-badge ${quoteStatus.tone}`}>{quoteStatus.badge}</span>
            </div>

            <form className="row g-3" onSubmit={handlePublicQuoteSubmit}>
              <div className="col-md-6">
                <label className="form-label">Home price</label>
                <input className="form-control" name="homePrice" inputMode="decimal" value={publicQuoteForm.homePrice} onChange={handleInput(setPublicQuoteForm)} required />
              </div>
              <div className="col-md-6">
                <label className="form-label">Down payment</label>
                <input className="form-control" name="downPayment" inputMode="decimal" value={publicQuoteForm.downPayment} onChange={handleInput(setPublicQuoteForm)} required />
              </div>
              <div className="col-md-6">
                <label className="form-label">ZIP code</label>
                <input className="form-control" name="zipCode" inputMode="numeric" value={publicQuoteForm.zipCode} onChange={handleInput(setPublicQuoteForm)} required />
              </div>
              <div className="col-md-6">
                <label className="form-label">Loan program</label>
                <select className="form-select" name="loanProgram" value={publicQuoteForm.loanProgram} onChange={handleInput(setPublicQuoteForm)}>
                  <option value="CONVENTIONAL">Conventional</option>
                  <option value="FHA">FHA</option>
                  <option value="VA">VA</option>
                  <option value="JUMBO">Jumbo</option>
                </select>
              </div>
              <div className="col-md-6">
                <label className="form-label">Property use</label>
                <select className="form-select" name="propertyUse" value={publicQuoteForm.propertyUse} onChange={handleInput(setPublicQuoteForm)}>
                  <option value="PRIMARY_RESIDENCE">Primary residence</option>
                  <option value="SECOND_HOME">Second home</option>
                  <option value="INVESTMENT">Investment</option>
                </select>
              </div>
              <div className="col-md-6">
                <label className="form-label">Term</label>
                <select className="form-select" name="termYears" value={publicQuoteForm.termYears} onChange={handleInput(setPublicQuoteForm)}>
                  <option value="15">15 years</option>
                  <option value="20">20 years</option>
                  <option value="30">30 years</option>
                </select>
              </div>
              <div className="col-12 d-grid d-sm-flex gap-2 pt-2">
                <button className="btn btn-primary btn-lg px-4" type="submit" disabled={loadingTarget === 'public-quote'}>
                  {loadingTarget === 'public-quote' ? 'Pricing your quote...' : 'See my quote'}
                </button>
                <button className="btn btn-outline-secondary btn-lg px-4" type="button" onClick={() => setActiveView('tools')}>
                  Use free calculators
                </button>
              </div>
            </form>
          </div>
        </div>
      </section>

      <section className="quote-result-shell row g-4">
        <div className="col-lg-7">
          <div className={`result-panel ${quoteStatus.tone}`}>
            <div className="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-3">
              <div>
                <p className="text-uppercase small fw-bold mb-1">Quote status</p>
                <h2 className="h3 mb-1">{quoteStatus.headline}</h2>
                <p className="text-secondary mb-0">{quoteStatus.detail}</p>
              </div>
              <div className="quote-id-badge">{quoteResult ? `Quote #${quoteResult.id}` : 'No quote yet'}</div>
            </div>
            <div className="row g-3 metric-grid">
              <div className="col-sm-6 col-xl-4"><div className="metric-card"><span>Monthly payment</span><strong>{formatCurrency(quoteResult?.estimatedMonthlyPayment)}</strong></div></div>
              <div className="col-sm-6 col-xl-4"><div className="metric-card"><span>Estimated rate</span><strong>{formatPercent(quoteResult?.estimatedRate)}</strong></div></div>
              <div className="col-sm-6 col-xl-4"><div className="metric-card"><span>APR</span><strong>{formatPercent(quoteResult?.estimatedApr)}</strong></div></div>
              <div className="col-sm-6 col-xl-4"><div className="metric-card"><span>Cash to close</span><strong>{formatCurrency(quoteResult?.estimatedCashToClose)}</strong></div></div>
              <div className="col-sm-6 col-xl-4"><div className="metric-card"><span>Qualification tier</span><strong>{quoteResult?.qualificationTier ?? '--'}</strong></div></div>
              <div className="col-sm-6 col-xl-4"><div className="metric-card"><span>Next step</span><strong>{quoteResult?.nextStep ?? '--'}</strong></div></div>
            </div>
            <div className="d-flex flex-wrap gap-2 mt-4">
              <button className="btn btn-outline-primary" type="button" onClick={() => quoteResult && fetchQuoteStatus(quoteResult.id)} disabled={!quoteResult}>
                Refresh quote
              </button>
              <button className="btn btn-outline-secondary" type="button" onClick={handlePublicQuoteSubmit} disabled={loadingTarget === 'public-quote' || isQuoteInFlight(quoteResult)}>
                Retry quote
              </button>
            </div>
          </div>
        </div>
        <div className="col-lg-5">
          <div className="result-side-panel h-100">
            <p className="text-uppercase small fw-bold text-primary mb-1">Session snapshot</p>
            <h3 className="h4 mb-3">Borrower handoff</h3>
            <ul className="list-unstyled quote-details mb-4">
              <li><span>Session</span><strong>{quoteResult?.sessionId ?? sessionId}</strong></li>
              <li><span>Processing</span><strong>{quoteResult?.processingStatus ?? '--'}</strong></li>
              <li><span>Stage</span><strong>{quoteResult?.quoteStage ?? '--'}</strong></li>
              <li><span>Status</span><strong>{quoteResult?.quoteStatus ?? '--'}</strong></li>
              <li><span>Duplicate reused</span><strong>{quoteResult ? (quoteResult.duplicate ? 'Yes' : 'No') : '--'}</strong></li>
              <li><span>Lead captured</span><strong>{quoteResult ? (quoteResult.leadCaptured ? 'Yes' : 'No') : '--'}</strong></li>
            </ul>
            {quoteResult?.lead ? (
              <div className="lead-record-box">
                <h4 className="h6 mb-2">Lead record</h4>
                <p className="mb-1">Lead ID: <strong>{quoteResult.lead.id}</strong></p>
                <p className="mb-1">Status: <strong>{quoteResult.lead.leadStatus}</strong></p>
                <p className="mb-0">Source: <strong>{quoteResult.lead.leadSource}</strong></p>
              </div>
            ) : (
              <p className="text-secondary mb-0">Once the quote is refined, the borrower profile converts into a lead-ready scenario.</p>
            )}
          </div>
        </div>
      </section>

      <section className="personalize-shell row g-4 align-items-stretch">
        <div className="col-lg-4">
          <div className="personalize-copy h-100">
            <span className="eyebrow-chip">Personalize the quote</span>
            <h2 className="h3 mt-3">Refine the estimate with borrower details.</h2>
            <p className="text-secondary mb-4">
              This mirrors the transition from public estimate to borrower-specific pricing by collecting the income, debt, and credit inputs lenders care about.
            </p>
            {!authState ? (
              <button className="btn btn-primary" type="button" onClick={() => setActiveView('auth')}>
                Sign in to personalize
              </button>
            ) : (
              <div className="signed-in-box">
                <strong>{authState.email}</strong>
                <span>{authState.role}</span>
              </div>
            )}
          </div>
        </div>
        <div className="col-lg-8">
          <div className="personalize-form-panel h-100">
            <form className="row g-3" onSubmit={handleRefineQuoteSubmit} noValidate>
              <div className="col-md-4">
                <label className="form-label">Quote ID</label>
                <input className="form-control" name="quoteId" inputMode="numeric" value={refineForm.quoteId} onChange={handleInput(setRefineForm)} required />
              </div>
              <div className="col-md-4">
                <label className="form-label">First name</label>
                <input className="form-control" name="firstName" value={refineForm.firstName} onChange={handleInput(setRefineForm)} required />
              </div>
              <div className="col-md-4">
                <label className="form-label">Last name</label>
                <input className="form-control" name="lastName" value={refineForm.lastName} onChange={handleInput(setRefineForm)} required />
              </div>
              <div className="col-md-6">
                <label className="form-label">Email</label>
                <input className="form-control" type="email" name="email" value={refineForm.email} onChange={handleInput(setRefineForm)} required />
              </div>
              <div className="col-md-6">
                <label className="form-label">Phone</label>
                <input className="form-control" name="phone" value={refineForm.phone} onChange={handleInput(setRefineForm)} required />
              </div>
              <div className="col-md-4">
                <label className="form-label">Annual income</label>
                <input className="form-control" name="annualIncome" inputMode="decimal" value={refineForm.annualIncome} onChange={handleInput(setRefineForm)} required />
              </div>
              <div className="col-md-4">
                <label className="form-label">Monthly debts</label>
                <input className="form-control" name="monthlyDebts" inputMode="decimal" value={refineForm.monthlyDebts} onChange={handleInput(setRefineForm)} required />
              </div>
              <div className="col-md-4">
                <label className="form-label">Credit score</label>
                <input className="form-control" name="creditScore" inputMode="numeric" value={refineForm.creditScore} onChange={handleInput(setRefineForm)} required />
              </div>
              <div className="col-md-6">
                <label className="form-label">Cash reserves</label>
                <input className="form-control" name="cashReserves" inputMode="decimal" value={refineForm.cashReserves} onChange={handleInput(setRefineForm)} required />
              </div>
              <div className="col-md-3">
                <label className="form-label">First-time buyer</label>
                <select className="form-select" name="firstTimeBuyer" value={refineForm.firstTimeBuyer} onChange={handleInput(setRefineForm)}>
                  <option value="true">Yes</option>
                  <option value="false">No</option>
                </select>
              </div>
              <div className="col-md-3">
                <label className="form-label">VA eligible</label>
                <select className="form-select" name="vaEligible" value={refineForm.vaEligible} onChange={handleInput(setRefineForm)}>
                  <option value="false">No</option>
                  <option value="true">Yes</option>
                </select>
              </div>
              <div className="col-12 d-grid d-sm-flex gap-2 pt-2">
                <button className="btn btn-dark btn-lg" type="submit" disabled={loadingTarget === 'refine-quote'}>
                  {loadingTarget === 'refine-quote' ? 'Personalizing...' : 'Personalize my quote'}
                </button>
                <button className="btn btn-outline-secondary btn-lg" type="button" onClick={() => setActiveView('auth')}>
                  Manage sign in
                </button>
              </div>
            </form>
          </div>
        </div>
      </section>
    </div>
  )
}
