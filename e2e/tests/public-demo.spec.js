import { expect, test } from '@playwright/test'

test('borrower demo loads and produces a quote', async ({ page }) => {
  await page.goto('/')

  await expect(page.getByRole('heading', { name: /See an estimated mortgage quote in minutes/i })).toBeVisible()
  await page.getByRole('button', { name: /See my quote/i }).click()

  await expect(page.getByText(/Quote #/)).toBeVisible()
  await expect(page.getByText(/Quote status/i)).toBeVisible()

  await page.waitForFunction(() => {
    const body = document.body.innerText
    return body.includes('Quote ready') || body.includes('Your estimate is ready') || body.includes('Lead ready')
  })
})

test('borrower demo redirects users to auth before personalization', async ({ page }) => {
  await page.goto('/')
  await page.getByRole('button', { name: /Personalize my quote/i }).click()

  await expect(page.getByRole('heading', { name: /Continue your quote when you are ready/i })).toBeVisible()
})
