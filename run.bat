@echo off
echo =========================================
echo Setting up Forwardly Automation Suite...
echo =========================================

echo Installing Node.js packages...
call npm install

echo.
echo Installing Playwright Browsers...
call npx playwright install chromium

echo.
echo Running the Business Setup Automation...
echo (A Chromium browser will open automatically)
call npx playwright test tests/setup-business.spec.ts --headed

echo.
echo Automation finished! Press any key to exit.
pause
