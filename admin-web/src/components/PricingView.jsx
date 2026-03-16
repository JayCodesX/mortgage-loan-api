export default function PricingView({
  productForm,
  setProductForm,
  products,
  editingProductId,
  loadingTarget,
  loadProducts,
  saveProduct,
  startEditProduct,
  resetProductForm,
  deleteProduct,
  handleInput,
}) {
  return (
    <div className="row g-4">
      <div className="col-xl-4">
        <div className="admin-panel h-100">
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h2 className="h4 mb-0">Pricing product editor</h2>
            <button className="btn btn-outline-primary btn-sm" type="button" onClick={resetProductForm}>
              New product
            </button>
          </div>
          <form className="row g-3" onSubmit={saveProduct}>
            <div className="col-12">
              <label className="form-label">Program code</label>
              <input className="form-control" name="programCode" value={productForm.programCode} onChange={handleInput(setProductForm)} required />
            </div>
            <div className="col-12">
              <label className="form-label">Product name</label>
              <input className="form-control" name="productName" value={productForm.productName} onChange={handleInput(setProductForm)} required />
            </div>
            <div className="col-md-6">
              <label className="form-label">Base rate</label>
              <input className="form-control" name="baseRate" value={productForm.baseRate} onChange={handleInput(setProductForm)} required />
            </div>
            <div className="col-md-6">
              <label className="form-label">Active</label>
              <select className="form-select" name="active" value={productForm.active} onChange={handleInput(setProductForm)}>
                <option value="true">Active</option>
                <option value="false">Inactive</option>
              </select>
            </div>
            <div className="col-12 d-grid gap-2">
              <button className="btn btn-primary" type="submit" disabled={loadingTarget === 'save-product'}>
                {loadingTarget === 'save-product' ? 'Saving product...' : editingProductId ? 'Update product' : 'Create product'}
              </button>
              <button className="btn btn-outline-secondary" type="button" onClick={loadProducts} disabled={loadingTarget === 'products'}>
                {loadingTarget === 'products' ? 'Loading...' : 'Refresh product list'}
              </button>
            </div>
          </form>
        </div>
      </div>
      <div className="col-xl-8">
        <div className="admin-panel h-100">
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h2 className="h4 mb-0">Available products</h2>
            <span className="text-secondary small">{products.length} loaded</span>
          </div>
          <div className="table-responsive">
            <table className="table admin-table align-middle mb-0">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Program</th>
                  <th>Product</th>
                  <th>Base rate</th>
                  <th>Status</th>
                  <th className="text-end">Actions</th>
                </tr>
              </thead>
              <tbody>
                {products.length === 0 ? (
                  <tr><td colSpan="6" className="text-secondary py-4">Load the pricing catalog to manage products.</td></tr>
                ) : products.map((product) => (
                  <tr key={product.id}>
                    <td>{product.id}</td>
                    <td>{product.programCode}</td>
                    <td>{product.productName}</td>
                    <td>{product.baseRate}%</td>
                    <td><span className={`badge rounded-pill ${product.active ? 'text-bg-success' : 'text-bg-secondary'}`}>{product.active ? 'Active' : 'Inactive'}</span></td>
                    <td className="text-end">
                      <div className="d-inline-flex gap-2">
                        <button className="btn btn-sm btn-outline-primary" type="button" onClick={() => startEditProduct(product)}>Edit</button>
                        <button className="btn btn-sm btn-outline-danger" type="button" onClick={() => deleteProduct(product.id)} disabled={loadingTarget === `delete-product-${product.id}`}>Remove</button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  )
}
