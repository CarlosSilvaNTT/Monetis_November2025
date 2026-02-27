require('dotenv').config();

module.exports = async (page) => {
  console.log("Monetis | onReady: Preparing dashboard for screenshot...");

  // Wait for main dashboard container
  await page.waitForSelector(".dashboard", { visible: true, timeout: 15000 });

  // HARD-KILL dynamic elements across entire page AFTER React re-renders
  await page.addStyleTag({
    content: `
      .loading_screen,
      .loading_screen *,
      .Toastify,
      .Toastify *,
      canvas,
      .chart canvas,
      .fillPercent,
      .fillPercent *,
      .transaction-list,
      .transaction-list *,
      .overview-list,
      .overview-list * {
        visibility: hidden !important;
        opacity: 0 !important;
        display: none !important;
      }
      * {
        animation: none !important;
        transition: none !important;
      }
    `
  });

  console.log("Monetis | onReady: Dashboard stabilized.");
};