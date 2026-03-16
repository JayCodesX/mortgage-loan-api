import { formatCurrency } from '../lib/adminApp'

export default function WorkspaceView({
  borrowerForm,
  setBorrowerForm,
  borrowers,
  createdBorrower,
  loanForm,
  setLoanForm,
  loanLookupId,
  setLoanLookupId,
  loanResult,
  loadingTarget,
  fetchBorrowers,
  createBorrower,
  createLoan,
  fetchLoan,
  handleInput,
}) {
  return (
    <div className="row g-4">
      <div className="col-xl-5">
        <div className="admin-panel h-100">
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h2 className="h4 mb-0">Borrower workspace</h2>
            <button className="btn btn-outline-primary btn-sm" type="button" onClick={fetchBorrowers} disabled={loadingTarget === 'borrowers'}>
              {loadingTarget === 'borrowers' ? 'Loading...' : 'Load borrowers'}
            </button>
          </div>
          <form className="row g-3 mb-4" onSubmit={createBorrower}>
            <div className="col-md-6">
              <label className="form-label">First name</label>
              <input className="form-control" name="firstName" value={borrowerForm.firstName} onChange={handleInput(setBorrowerForm)} required />
            </div>
            <div className="col-md-6">
              <label className="form-label">Last name</label>
              <input className="form-control" name="lastName" value={borrowerForm.lastName} onChange={handleInput(setBorrowerForm)} required />
            </div>
            <div className="col-md-8">
              <label className="form-label">Email</label>
              <input className="form-control" type="email" name="email" value={borrowerForm.email} onChange={handleInput(setBorrowerForm)} required />
            </div>
            <div className="col-md-4">
              <label className="form-label">Credit score</label>
              <input className="form-control" name="creditScore" value={borrowerForm.creditScore} onChange={handleInput(setBorrowerForm)} required />
            </div>
            <div className="col-12 d-grid">
              <button className="btn btn-primary" type="submit" disabled={loadingTarget === 'create-borrower'}>
                {loadingTarget === 'create-borrower' ? 'Creating borrower...' : 'Create borrower'}
              </button>
            </div>
          </form>
          <div className="workspace-card">
            <strong>{createdBorrower ? `${createdBorrower.firstName} ${createdBorrower.lastName}` : 'No borrower created yet'}</strong>
            <span>ID: {createdBorrower?.id ?? '--'} · Score: {createdBorrower?.creditScore ?? '--'}</span>
          </div>
          <div className="workspace-list mt-4">
            {borrowers.length === 0 ? (
              <p className="text-secondary mb-0">No borrowers loaded.</p>
            ) : borrowers.map((borrower) => (
              <div key={borrower.id} className="workspace-row">
                <strong>{borrower.firstName} {borrower.lastName}</strong>
                <span>ID {borrower.id} · {borrower.email} · Score {borrower.creditScore}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
      <div className="col-xl-7">
        <div className="admin-panel h-100">
          <h2 className="h4 mb-3">Loan workspace</h2>
          <form className="row g-3 mb-4" onSubmit={createLoan}>
            <div className="col-md-4">
              <label className="form-label">Borrower ID</label>
              <input className="form-control" name="borrowerId" value={loanForm.borrowerId} onChange={handleInput(setLoanForm)} required />
            </div>
            <div className="col-md-4">
              <label className="form-label">Loan amount</label>
              <input className="form-control" name="loanAmount" value={loanForm.loanAmount} onChange={handleInput(setLoanForm)} required />
            </div>
            <div className="col-md-4">
              <label className="form-label">Interest rate</label>
              <input className="form-control" name="interestRate" value={loanForm.interestRate} onChange={handleInput(setLoanForm)} required />
            </div>
            <div className="col-md-4">
              <label className="form-label">Term years</label>
              <input className="form-control" name="termYears" value={loanForm.termYears} onChange={handleInput(setLoanForm)} required />
            </div>
            <div className="col-md-4">
              <label className="form-label">Status</label>
              <input className="form-control" name="status" value={loanForm.status} onChange={handleInput(setLoanForm)} required />
            </div>
            <div className="col-md-4 d-grid align-content-end">
              <button className="btn btn-primary" type="submit" disabled={loadingTarget === 'create-loan'}>
                {loadingTarget === 'create-loan' ? 'Creating loan...' : 'Create loan'}
              </button>
            </div>
          </form>
          <div className="row g-3 mb-4">
            <div className="col-sm-8">
              <label className="form-label">Loan ID</label>
              <input className="form-control" value={loanLookupId} onChange={(event) => setLoanLookupId(event.target.value)} placeholder="Enter loan ID" />
            </div>
            <div className="col-sm-4 d-grid align-content-end">
              <button className="btn btn-outline-secondary" type="button" onClick={fetchLoan} disabled={loadingTarget === 'loan-monitor'}>
                {loadingTarget === 'loan-monitor' ? 'Loading...' : 'Load loan'}
              </button>
            </div>
          </div>
          <div className="workspace-card loan-card">
            <strong>{loanResult ? `Loan #${loanResult.id}` : 'No loan loaded'}</strong>
            <span>Borrower ID: {loanResult?.borrowerId ?? '--'} · Amount: {formatCurrency(loanResult?.loanAmount)} · Rate: {loanResult?.interestRate ?? '--'}%</span>
          </div>
        </div>
      </div>
    </div>
  )
}
