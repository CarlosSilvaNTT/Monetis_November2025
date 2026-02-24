require('dotenv').config();

const {
  MONETIS_DASHBOARD_READY_SELECTOR,
  MONETIS_OPTIONAL_POPUP_CLOSE_SELECTOR
} = process.env;

module.exports = async (page, scenario, vp) => {
  console.log("Monetis | onReady: Preparing dashboard for screenshot...");

  // Optional popup (cookie consent, modals, etc.)
  if (MONETIS_OPTIONAL_POPUP_CLOSE_SELECTOR) {
    const popup = await page.$(MONETIS_OPTIONAL_POPUP_CLOSE_SELECTOR);
    if (popup) {
      await popup.click();
      console.log("Monetis | onReady: Closed popup.");
    }
  }

  // Wait for dashboard container
  if (MONETIS_DASHBOARD_READY_SELECTOR) {
    await page.waitForSelector(MONETIS_DASHBOARD_READY_SELECTOR, {
      visible: true,
      timeout: 15000
    });
    console.log(`Monetis | onReady: '${MONETIS_DASHBOARD_READY_SELECTOR}' visible.`);
  }
};