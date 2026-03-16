export default function ReportsView({
  reportForm,
  setReportForm,
  reportResult,
  loadingTarget,
  runReport,
  handleInput,
}) {
  return (
    <div className="row g-4">
      <div className="col-xl-4">
        <div className="admin-panel h-100">
          <h2 className="h4 mb-3">Custom reports</h2>
          <form className="row g-3" onSubmit={runReport}>
            <div className="col-12">
              <label className="form-label">Report type</label>
              <select className="form-select" name="reportType" value={reportForm.reportType} onChange={handleInput(setReportForm)}>
                <option value="PRODUCTS">Products</option>
                <option value="BORROWERS">Borrowers</option>
                <option value="LOANS">Loans and quotes</option>
              </select>
            </div>
            <div className="col-12">
              <label className="form-label">Search</label>
              <input className="form-control" name="search" value={reportForm.search} onChange={handleInput(setReportForm)} placeholder="program, name, email, status" />
            </div>
            <div className="col-md-6">
              <label className="form-label">Program code</label>
              <input className="form-control" name="programCode" value={reportForm.programCode} onChange={handleInput(setReportForm)} />
            </div>
            <div className="col-md-6">
              <label className="form-label">Status</label>
              <input className="form-control" name="status" value={reportForm.status} onChange={handleInput(setReportForm)} />
            </div>
            <div className="col-md-6">
              <label className="form-label">Min credit score</label>
              <input className="form-control" name="minCreditScore" value={reportForm.minCreditScore} onChange={handleInput(setReportForm)} />
            </div>
            <div className="col-md-6">
              <label className="form-label">Max credit score</label>
              <input className="form-control" name="maxCreditScore" value={reportForm.maxCreditScore} onChange={handleInput(setReportForm)} />
            </div>
            <div className="col-12">
              <div className="form-check">
                <input className="form-check-input" type="checkbox" id="activeOnly" checked={reportForm.activeOnly === 'true'} onChange={(event) => setReportForm((current) => ({ ...current, activeOnly: String(event.target.checked) }))} />
                <label className="form-check-label" htmlFor="activeOnly">Only active products</label>
              </div>
            </div>
            <div className="col-12 d-grid">
              <button className="btn btn-primary" type="submit" disabled={loadingTarget === 'run-report'}>
                {loadingTarget === 'run-report' ? 'Running report...' : 'Run report'}
              </button>
            </div>
          </form>
        </div>
      </div>
      <div className="col-xl-8">
        <div className="admin-panel h-100">
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h2 className="h4 mb-0">{reportResult?.title || 'Report results'}</h2>
            <span className="text-secondary small">{reportResult ? `${reportResult.totalRows} rows` : 'No report yet'}</span>
          </div>
          {reportResult ? (
            <div className="table-responsive">
              <table className="table admin-table align-middle mb-0">
                <thead>
                  <tr>
                    {reportResult.columns.map((column) => <th key={column}>{column}</th>)}
                  </tr>
                </thead>
                <tbody>
                  {reportResult.rows.length === 0 ? (
                    <tr><td colSpan={reportResult.columns.length} className="text-secondary py-4">No matching rows for this filter set.</td></tr>
                  ) : reportResult.rows.map((row, index) => (
                    <tr key={`${row.id ?? index}-${index}`}>
                      {reportResult.columns.map((column) => <td key={`${column}-${index}`}>{String(row[column] ?? '--')}</td>)}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : <p className="text-secondary mb-0">Select a report type and run a query to view internal data.</p>}
        </div>
      </div>
    </div>
  )
}
