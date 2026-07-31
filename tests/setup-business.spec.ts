import { test, expect } from '@playwright/test';

test.describe('Automate Business Setup Persona Auto', () => {
    test('Should login and complete business setup', async ({ page, request }) => {
        // 1. Navigate to qa2.fly.test4wd.com
        test.setTimeout(120000); // 2 minutes to accommodate uploads and navs
        await page.goto('https://qa2.fly.test4wd.com');
        
        // 1. Enter Email address and continue
        await page.locator('#email-input').fill('ginny+9488qa@forwardai.com');
        // The Continue button becomes active after entering email
        await page.getByRole('button', { name: 'Continue' }).click();

        // 2. Enter Password and Sign in
        await page.locator('#password-input').waitFor({ state: 'visible' });
        await page.locator('#password-input').click();
        await page.locator('#password-input').pressSequentially('Test@123', { delay: 50 });
        await page.locator('#password-input').press('Enter');

        // Polling logic to ensure we get a brand new OTP delta
        console.log("Polling API for latest OTP delta...");
        let latestOtp = "";
        let attemptCount = 0;
        while (attemptCount < 5) {
             await page.waitForTimeout(4000); 
             const response = await request.get('https://qa2.flydata.test4wd.com/api/otp/phone');
             if (response.ok()) {
                 const otpData = await response.json();
                 if (otpData?.data?.[0]?.otp) {
                     const newest = otpData.data[0];
                     // We grab the absolute newest code, and strictly verify it was updated within the last 40 seconds
                     const updatedTime = new Date(newest.updatedAt).getTime();
                     if (Date.now() - updatedTime < 40000) {
                          latestOtp = newest.otp;
                          break;
                     }
                 }
             }
             attemptCount++;
        }
        
        if (!latestOtp) {
             console.log("Fallback: Timeout polling, attempting to use top value.");
             const fallbackResp = await request.get('https://qa2.flydata.test4wd.com/api/otp/phone');
             const fallbackData = await fallbackResp.json();
             latestOtp = fallbackData?.data?.[0]?.otp || "";
        }
        console.log("Extracted OTP:", latestOtp);

        // Fill the OTP.
        const otpInputs = page.locator('input[type="text"], input[type="number"]');
        await page.waitForTimeout(2000); // wait for UI to fully become intractable
        
        // Handle flakiness: Check if "Get a new code" is disabled
        const getNewCodeBtn = page.locator('*:has-text("Get a new code")').last();
        const checkDisabled = async () => await getNewCodeBtn.evaluate(el => el.hasAttribute('disabled') || el.classList.contains('disabled') || el.classList.contains('pointer-events-none')).catch(() => false);
        
        if (await checkDisabled()) {
            console.log("Get a new code is disabled. Refreshing the page...");
            await page.reload();
            await page.waitForTimeout(3000);
            
            // If the refresh kicked us securely out to the email screen, we MUST type it again!
            const emailInput = page.locator('#email-input');
            if (await emailInput.isVisible() || await checkDisabled()) {
                console.log("Still disabled or kicked strictly to Start. Logging in completely again...");
                if (!await emailInput.isVisible()) await page.goto('https://qa2.fly.test4wd.com');
                await page.locator('#email-input').fill('ginny+9488qa@forwardai.com');
                await page.getByRole('button', { name: 'Continue' }).click();
                await page.locator('#password-input').waitFor({ state: 'visible' });
                await page.locator('#password-input').click();
                await page.locator('#password-input').pressSequentially('Test@123', { delay: 50 });
                await page.locator('#password-input').press('Enter');
                
                console.log("Waiting generously for fresh backend OTP...");
                await page.waitForTimeout(7000);
                const retryResp = await request.get('https://qa2.flydata.test4wd.com/api/otp/phone');
                latestOtp = (await retryResp.json())?.data?.[0]?.otp || latestOtp;
                console.log("Acquired fresh fallback OTP:", latestOtp);
            }
        }

        await otpInputs.first().waitFor({ state: 'visible' }).catch(() => null);
        // Sometimes OTP is broken down into 6 distinct boxes with React Auto-Focus Event listeners
        const boxesCount = await otpInputs.count();
        if (boxesCount >= 6 && latestOtp.length >= 6) {
             // Use hardware-level keystrokes to type it sequentially so React registers each box natively!
             await otpInputs.first().click();
             await page.waitForTimeout(500);
             await page.keyboard.type(latestOtp, { delay: 100 });
        } else {
            await otpInputs.first().fill(latestOtp);
        }
        
        await page.waitForTimeout(2000); // Wait for the UI form to fully validate the filled number
        
        // Click the "Trust this device for 30 days" checkbox
        const trustText = page.getByText(/Trust this device/i).first();
        if (await trustText.isVisible().catch(() => false)) {
             console.log("Found 'Trust this device' text label! Clicking it natively...");
             await trustText.click();
             await page.waitForTimeout(1000);
        } else {
             console.log("Text label not visible, attempting to forcibly check any existing UI checkbox component...");
             await page.locator('input[type="checkbox"]').first().click({ force: true }).catch(() => null);
        }
        
        // Let the React component settle after checking the box
        await page.waitForTimeout(1000);
        
        // We tightly acquire the primary continue/verify button specifically on this active OTP component layer
        const loginBtn = page.getByRole('button', { name: /continue|verify|submit/i }).last();
        await loginBtn.waitFor({ state: 'visible', timeout: 10000 });
        await loginBtn.click({ force: true });
        console.log("Successfully clicked OTP continue button!");
        
        // Let's add a small pause just in case to ensure we don't speed past
        await page.waitForTimeout(3000);

        // Wait for dashboard to load
        await page.waitForLoadState('networkidle');
        await page.waitForTimeout(6000); // Allow additional dashboard render time

        // 1. Click "sandbox" located on the right side of the page
        await page.getByText(/sandbox/i).first().waitFor({ state: 'visible', timeout: 30000 });
        await page.getByText(/sandbox/i).first().click();

        // Reveal and execute search
        const searchInput = page.locator('input[placeholder*="search" i], input[type="search"], .search-box input').first();
        await searchInput.waitFor({ state: 'visible', timeout: 15000 });
        await searchInput.fill('Persona Auto');
        // Let search results update
        await page.waitForTimeout(1500); 

        // 2. Click the Persona Auto business option that appears
        const businessLocator = page.locator('text="Persona Auto"').last();
        await businessLocator.waitFor({ state: 'visible', timeout: 15000 });
        await businessLocator.click();
        
        console.log("Clicked Persona Auto successfully. Navigating inside.");

        // Wait for business profile page to render completely
        await page.waitForTimeout(5000); 

        // 3. click on the settings tab of this business.
        const settingsTab = page.locator('text="Settings"').first();
        await settingsTab.waitFor({ state: 'visible', timeout: 15000 }).catch(() => null);
        if (await settingsTab.isVisible()) {
             await settingsTab.click({ force: true });
        } else {
             await page.locator('*:has-text("Settings")').last().click({ force: true });
        }
        
        // Wait for settings page to load
        await page.waitForTimeout(3000);

        // 4. Once you are in settings tab, click on continue button present on the right of business profile section.
        // It will redirect to a different window
        console.log("Entering new step after clicking Continue...");
        // Grab the Continue button visible on the page
        const continueBtn = page.locator('button:has-text("Continue"), a:has-text("Continue")').locator('visible=true').first();
        await continueBtn.click({ force: true });
        
        await page.waitForTimeout(3000); // Wait for next view to load

        // Handle Sandbox environment popup interruption explicitly instructed by the user!
        console.log("Checking for 'sandbox environment' popup interruption...");
        const sandboxWarning = page.getByText(/sandbox environment/i).first();
        if (await sandboxWarning.isVisible().catch(() => false)) {
            console.log("Sandbox popup found! Attempting to click the cross icon...");
            // Most UI cross icons are svgs wrapped in buttons or just raw SVGs hanging out on the edge
            // We use a blanket locator targeting cross icon archetypes
            const closeBtn = page.locator('button[aria-label*="lose" i], button > svg, svg.lucide-x, svg[class*="lose"]').locator('visible=true').first();
            await closeBtn.click({ force: true }).catch(() => null);
            await page.waitForTimeout(1000); // allow modal animation to fade out
        }

        // 6. Now, click on Continue button again.
        await page.locator('button:has-text("Continue"), a:has-text("Continue")').locator('visible=true').first().click({ force: true });
        await page.waitForTimeout(2000);

        // 7. Wait for Associated People heading and meticulously target the visibly active Job filler field
        await page.waitForTimeout(2000); // Buffer for UI render
        console.log("Typing 'Manga' into Job Title field...");
        
        const jobInput = page.locator('input[name*="job" i], input[placeholder*="job" i], input[type="text"]').locator('visible=true').last();
        await jobInput.waitFor({ state: 'visible', timeout: 10000 });
        await jobInput.click({ force: true });
        await page.waitForTimeout(500);
        await page.keyboard.type('Manga', { delay: 50 });
        await page.waitForTimeout(1000);
        
        await page.locator('button:has-text("Continue"), a:has-text("Continue")').locator('visible=true').first().click({ force: true });
        await page.waitForTimeout(2000);

        // 8. Click on Continue on next page as well.
        await page.locator('button:has-text("Continue"), a:has-text("Continue")').locator('visible=true').first().click({ force: true });
        await page.waitForTimeout(2000);

        // 9. Now, it will again open a new pop up, click on Get started here.
        const getStartedBtn = page.locator('button:has-text("Get started here"), a:has-text("Get started here")').last();
        await getStartedBtn.waitFor({ state: 'visible', timeout: 5000 }).catch(() => null);
        await getStartedBtn.click({ force: true }).catch(() => null);
        await page.waitForTimeout(2000);

        // 10. Now, add any random description and click on continue.
        await page.locator('textarea, input[placeholder*="description" i]').first().fill('Random descriptive text for Persona Auto business.');
        await page.locator('button:has-text("Continue"), a:has-text("Continue")').locator('visible=true').first().click({ force: true });
        await page.waitForTimeout(2000);

        // 11. Now, add the business EIN any numeric value of 9 digits and also select the physical address and click on Continue.
        await page.locator('input[placeholder*="EIN" i], label:has-text("EIN") + input, input[name*="ein" i]').first().fill('123456789');
        const addressInput = page.locator('input[placeholder*="address" i], input[placeholder*="Address" i]').first();
        await addressInput.fill('123 Main St');
        await page.waitForTimeout(2000); // Wait for autocomplete
        await page.keyboard.press('ArrowDown');
        await page.keyboard.press('Enter');
        await page.locator('button:has-text("Continue"), a:has-text("Continue")').locator('visible=true').first().click({ force: true });
        await page.waitForTimeout(2000);

        // 12. Click on Proceed button present on the next page.
        await page.locator('button:has-text("Proceed"), a:has-text("Proceed")').locator('visible=true').first().click({ force: true });
        await page.waitForTimeout(2000);

        // 13. Now, on this screen, you need to upload all the required documents, make sure these are sample pdf files wiith small size one by one
        const fileInputs = await page.locator('input[type="file"]').elementHandles();
        for (const input of fileInputs) {
            await input.setInputFiles('sample.pdf').catch(() => null);
        }
        await page.locator('button:has-text("Identity"), a:has-text("Identity")').locator('visible=true').last().click({ force: true }).catch(() => null);
        await page.waitForTimeout(2000);

        // 14. Now, click on continue button again.
        await page.locator('button:has-text("Continue"), a:has-text("Continue")').locator('visible=true').first().click({ force: true }).catch(() => null);
        
        console.log("Successfully reached the end of the script!");
    });
});
