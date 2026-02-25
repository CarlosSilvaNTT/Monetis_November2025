// backstop/backstop_data/engine_scripts/onRegisterFill.js
require('dotenv').config();

const {
  MONETIS_REG_NAME,
  MONETIS_REG_SURNAME,
  MONETIS_REG_EMAIL,
  MONETIS_REG_PHONE,
  MONETIS_REG_STREET,
  MONETIS_REG_POSTAL,
  MONETIS_REG_CITY,
  MONETIS_REG_COUNTRY_TEXT,
  MONETIS_REG_SUBMIT
} = process.env;

function uniqueEmail(baseEmail) {
  // If submit=true, append a timestamp to avoid "email already exists"
  if (!String(MONETIS_REG_SUBMIT).toLowerCase().includes('true')) return baseEmail;
  const [local, domain] = baseEmail.split('@');
  return `${local}+${Date.now()}@${domain || 'example.com'}`;
}

module.exports = async (page, scenario, vp) => {
  console.log('Monetis | onRegisterFill: Waiting for register form...');

  // Wait for the main form
  await page.waitForSelector('.registerContainer form', { visible: true, timeout: 15000 });

  // Disable animations, hide noisy elements
  await page.addStyleTag({
    content: `
      .Toastify, .loading_screen { visibility: hidden !important; }
      * { animation: none !important; transition: none !important; }
    `
  });

  // Fill basic fields (from your real markup)
  await page.type('input[name="name"]', MONETIS_REG_NAME || 'TestName', { delay: 10 });
  await page.type('input[name="surname"]', MONETIS_REG_SURNAME || 'TestSurname', { delay: 10 });
  await page.type('input[name="email"]', uniqueEmail(MONETIS_REG_EMAIL || 'test@example.com'), { delay: 10 });
  if (MONETIS_REG_PHONE) {
    await page.type('input[name="phone_number"]', MONETIS_REG_PHONE, { delay: 10 });
  }
  await page.type('input[name="street_address"]', MONETIS_REG_STREET || 'Some Street 123', { delay: 10 });
  await page.type('input[name="postal_code"]', MONETIS_REG_POSTAL || '1000-000', { delay: 10 });
  await page.type('input[name="city"]', MONETIS_REG_CITY || 'Lisbon', { delay: 10 });

  // Country (React-Select)
  // Click the select control, then type the country and press Enter.
  try {
    await page.click('.countrySelect .css-13cymwt-control', { delay: 10 });
    // Use a robust selector for the react-select text input
    const reactSelectInput = '[id^="react-select-"][id$="-input"]';
    await page.waitForSelector(reactSelectInput, { visible: true, timeout: 5000 });
    await page.type(reactSelectInput, (MONETIS_REG_COUNTRY_TEXT || 'Portugal'), { delay: 20 });
    await page.keyboard.press('Enter');
  } catch (e) {
    console.warn('Monetis | onRegisterFill: Country select interaction failed or not present:', e.message);
  }

  // Security section
  // Use the same password for password and confirmation
  const password = process.env.MONETIS_PASSWORD || 'testingPassword!1';
  await page.type('input[name="password"]', password, { delay: 10 });
  await page.type('input[name="confirmPassword"]', password, { delay: 10 });

  // Accept terms
  const termsSelector = '#terms';
  const terms = await page.$(termsSelector);
  if (terms) {
    const isChecked = await page.$eval(termsSelector, el => el.checked);
    if (!isChecked) await page.click(termsSelector);
  }

  // Optionally submit (usually disabled for VRT to keep snapshots deterministic)
  if (String(MONETIS_REG_SUBMIT).toLowerCase().includes('true')) {
    console.log('Monetis | onRegisterFill: Submitting registration form...');
    await Promise.all([
      page.click('button[type="submit"]'),
      page.waitForNavigation({ waitUntil: 'networkidle0', timeout: 15000 }).catch(() => {})
    ]);
  }

  console.log('Monetis | onRegisterFill: Register form filled.');
};