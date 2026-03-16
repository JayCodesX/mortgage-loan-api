import { expect, test } from '@playwright/test'

test('admin console supports direct seeded login and summary access', async ({ page }) => {
  await page.goto('/admin/')

  await expect(page.getByRole('heading', { name: /Sign in to the admin console/i })).toBeVisible()
  await page.getByRole('button', { name: /Sign in as admin/i }).click()

  await expect(page.getByRole('heading', { name: /Run the complete borrower quote demo from the admin console/i })).toBeVisible()
  await expect(page.getByText(/System snapshot/i)).toBeVisible()
  await expect(page.getByText(/Quote funnel/i)).toBeVisible()
})

test('admin workspace is available after login', async ({ page }) => {
  await page.goto('/admin/')
  await page.getByRole('button', { name: /Sign in as admin/i }).click()
  await expect(page.getByRole('heading', { name: /Run the complete borrower quote demo from the admin console/i })).toBeVisible()

  await page.getByRole('button', { name: /^Workspace$/i }).click()
  await expect(page.getByRole('heading', { name: /Borrower workspace/i })).toBeVisible()
  await expect(page.getByRole('heading', { name: /Loan workspace/i })).toBeVisible()
})
