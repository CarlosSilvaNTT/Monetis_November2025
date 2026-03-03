require('dotenv').config();

const {
  MONETIS_LOGIN_URL,
  MONETIS_USERNAME,
  MONETIS_PASSWORD,
  MONETIS_USERNAME_SELECTOR,
  MONETIS_PASSWORD_SELECTOR,
  MONETIS_LOGIN_BUTTON_SELECTOR
} = process.env;

module.exports = async (page, scenario, vp) => {
  console.log("Monetis | onBefore: Starting login sequence...");


   if (!process.env.MONETIS_LOGIN_URL) {
      require('dotenv').config();
   }

  if (!MONETIS_LOGIN_URL) {
    throw new Error("MONETIS_LOGIN_URL missing in .env");
  }

  // Navigate to login page
  await page.goto(MONETIS_LOGIN_URL, { waitUntil: "networkidle0" });

  // Ensure fields exist
  await page.waitForSelector(MONETIS_USERNAME_SELECTOR, { visible: true });
  await page.waitForSelector(MONETIS_PASSWORD_SELECTOR, { visible: true });

  // Type credentials
  await page.type(MONETIS_USERNAME_SELECTOR, MONETIS_USERNAME || "");
  await page.type(MONETIS_PASSWORD_SELECTOR, MONETIS_PASSWORD || "");

  // Submit and wait
  await Promise.all([
    page.click(MONETIS_LOGIN_BUTTON_SELECTOR),
    page.waitForNavigation({ waitUntil: "networkidle0" })
  ]);

  console.log("Monetis | onBefore: Login successful.");
};