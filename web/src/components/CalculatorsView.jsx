import { formatCurrency } from '../lib/demoApp'

export default function CalculatorsView({
  activeCalculator,
  setActiveCalculator,
  mortgageForm,
  setMortgageForm,
  mortgageResult,
  amortizationForm,
  setAmortizationForm,
  amortizationResult,
  loadingTarget,
  handleMortgageSubmit,
  handleAmortizationSubmit,
  handleInput,
}) {
  return (
    <section className="calculator-shell">
      <div className="section-heading d-flex flex-column flex-lg-row justify-content-between align-items-lg-end gap-3 mb-4">
        <div>
          <span className="eyebrow-chip">Free tools</span>
          <h2 className="display-6 fw-bold mt-3 mb-2">Consumer calculators that stay public.</h2>
          <p className="text-secondary mb-0">Keep the utility tools open while the main demo flow stays focused on borrower quote conversion.</p>
        </div>
        <div className="btn-group rounded-pill calculator-switcher">
          <button className={`btn ${activeCalculator === 'mortgage' ? 'btn-primary' : 'btn-outline-secondary'}`} type="button" onClick={() => setActiveCalculator('mortgage')}>
            Mortgage payment
          </button>
          <button className={`btn ${activeCalculator === 'amortization' ? 'btn-primary' : 'btn-outline-secondary'}`} type="button" onClick={() => setActiveCalculator('amortization')}>
            Amortization
          </button>
        </div>
      </div>

      <div className="row g-4">
        {activeCalculator === 'mortgage' ? (
          <>
            <div className="col-lg-7">
              <div className="calc-shell h-100">
                <h3 className="h4 mb-2">Monthly mortgage payment calculator</h3>
                <p className="text-secondary mb-4">Estimate your monthly payment based on home price, down payment, rate, and term.</p>
                <form className="row g-3" onSubmit={handleMortgageSubmit}>
                  <div className="col-md-6">
                    <label className="form-label">Home price</label>
                    <input className="form-control" name="loanAmount" inputMode="decimal" value={mortgageForm.loanAmount} onChange={handleInput(setMortgageForm)} required />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label">Down payment</label>
                    <input className="form-control" name="downPayment" inputMode="decimal" value={mortgageForm.downPayment} onChange={handleInput(setMortgageForm)} required />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label">Interest rate</label>
                    <input className="form-control" name="annualInterestRate" inputMode="decimal" value={mortgageForm.annualInterestRate} onChange={handleInput(setMortgageForm)} required />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label">Loan term</label>
                    <input className="form-control" name="termYears" inputMode="numeric" value={mortgageForm.termYears} onChange={handleInput(setMortgageForm)} required />
                  </div>
                  <div className="col-12 d-grid">
                    <button className="btn btn-primary btn-lg" type="submit" disabled={loadingTarget === 'mortgage'}>
                      {loadingTarget === 'mortgage' ? 'Calculating...' : 'Calculate payment'}
                    </button>
                  </div>
                </form>
              </div>
            </div>
            <div className="col-lg-5">
              <div className="calc-result-card h-100">
                <span className="eyebrow-chip">Payment result</span>
                <h4 className="display-6 fw-bold mt-3">{formatCurrency(mortgageResult?.monthlyPayment)}</h4>
                <p className="text-secondary">Estimated monthly payment.</p>
                <ul className="list-unstyled quote-details mb-0">
                  <li><span>Financed principal</span><strong>{formatCurrency(mortgageResult?.financedPrincipal)}</strong></li>
                  <li><span>Payments</span><strong>{mortgageResult?.numberOfPayments ?? '--'}</strong></li>
                </ul>
              </div>
            </div>
          </>
        ) : (
          <>
            <div className="col-lg-7">
              <div className="calc-shell h-100">
                <h3 className="h4 mb-2">Amortization preview</h3>
                <p className="text-secondary mb-4">Use principal, rate, and term to preview payment amount and payment count.</p>
                <form className="row g-3" onSubmit={handleAmortizationSubmit}>
                  <div className="col-md-4">
                    <label className="form-label">Principal</label>
                    <input className="form-control" name="principal" inputMode="decimal" value={amortizationForm.principal} onChange={handleInput(setAmortizationForm)} required />
                  </div>
                  <div className="col-md-4">
                    <label className="form-label">Interest rate</label>
                    <input className="form-control" name="annualInterestRate" inputMode="decimal" value={amortizationForm.annualInterestRate} onChange={handleInput(setAmortizationForm)} required />
                  </div>
                  <div className="col-md-4">
                    <label className="form-label">Loan term</label>
                    <input className="form-control" name="termYears" inputMode="numeric" value={amortizationForm.termYears} onChange={handleInput(setAmortizationForm)} required />
                  </div>
                  <div className="col-12 d-grid">
                    <button className="btn btn-primary btn-lg" type="submit" disabled={loadingTarget === 'amortization'}>
                      {loadingTarget === 'amortization' ? 'Calculating...' : 'Calculate amortization'}
                    </button>
                  </div>
                </form>
              </div>
            </div>
            <div className="col-lg-5">
              <div className="calc-result-card h-100">
                <span className="eyebrow-chip">Amortization result</span>
                <h4 className="display-6 fw-bold mt-3">{formatCurrency(amortizationResult?.monthlyPayment)}</h4>
                <p className="text-secondary">Monthly payment from principal only.</p>
                <ul className="list-unstyled quote-details mb-0">
                  <li><span>Principal</span><strong>{formatCurrency(amortizationResult?.principal)}</strong></li>
                  <li><span>Payments</span><strong>{amortizationResult?.numberOfPayments ?? '--'}</strong></li>
                </ul>
              </div>
            </div>
          </>
        )}
      </div>
    </section>
  )
}
