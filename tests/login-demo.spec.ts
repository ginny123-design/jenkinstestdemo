import { test, expect } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

test.describe('Sample Login Web Automation & Visual Reporting', () => {

  test.beforeAll(async () => {
    // Ensure screenshot output folder exists
    const dir = path.join(process.cwd(), 'test-results', 'screenshots');
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir, { recursive: true });
    }
  });

  test('Successful Login Scenario - Visual Verification', async ({ page }) => {
    console.log('1. Navigating to sample login page...');
    await page.goto('https://the-internet.herokuapp.com/login');
    
    // Take screenshot of initial login page
    await page.screenshot({ path: 'test-results/screenshots/01-login-page.png', fullPage: true });

    console.log('2. Entering valid credentials...');
    await page.fill('#username', 'tomsmith');
    await page.fill('#password', 'SuperSecretPassword!');

    // Take screenshot with filled credentials
    await page.screenshot({ path: 'test-results/screenshots/02-credentials-entered.png' });

    console.log('3. Submitting login form...');
    await page.click('button[type="submit"]');

    console.log('4. Verifying successful login dashboard...');
    await expect(page.locator('#flash')).toContainText('You logged into a secure area!');
    await expect(page.locator('h2')).toContainText('Secure Area');

    // Take screenshot of secure dashboard
    await page.screenshot({ path: 'test-results/screenshots/03-login-success-dashboard.png', fullPage: true });
    console.log('Successfully logged in and captured visual evidence!');
  });

  test('Invalid Credentials Scenario - Error State Verification', async ({ page }) => {
    console.log('1. Navigating to sample login page...');
    await page.goto('https://the-internet.herokuapp.com/login');

    console.log('2. Entering invalid password...');
    await page.fill('#username', 'tomsmith');
    await page.fill('#password', 'WrongPassword123!');

    console.log('3. Submitting login form...');
    await page.click('button[type="submit"]');

    console.log('4. Verifying error banner display...');
    await expect(page.locator('#flash')).toContainText('Your password is invalid!');

    // Take screenshot of error state
    await page.screenshot({ path: 'test-results/screenshots/04-login-error-state.png', fullPage: true });
    console.log('Successfully verified invalid login error handling & captured screenshot!');
  });

});
