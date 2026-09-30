# Costco Travel site findings

Reviewed 30 September 2026 on the public homepage, search tabs, empty-search validation, the Careers control, and Help Center. No account pages were opened. This file has no usernames, passwords, or request ids.

axe-core (WCAG 2.x A/AA) reported no serious or critical violations for the header plus search widget, and none for the sign-in page. Those scans are in `accessibility-homepage.txt` and `accessibility-signin.txt`. The notes below are behaviors those scoped scans did not fail.

## Findings

1. **Search tabs rewrite the address.** Hotels changes the URL to `/h=4006`, Cruises to `/h=4001`, and Rental Cars to `/h=4005`. There is no `?` or `#`. Reloading restores that tab, so the path works as a deep link. Back steps through the tab changes.

2. **“Skip to main content” stays off-screen.** The link remains at `left: -10000px` after it receives focus, so a keyboard user cannot see it. Its `href` is `javascript:void(0)`. Activating it moves focus to the first control in `<main>`, the Packages tab.

3. **Calendar buttons have no accessible name.** The date-picker triggers beside the date fields are empty `ui-datepicker-trigger` buttons, with no text, title, or `aria-label`.

4. **The package destination announces an open list when none is open.** On load, the destination field has `aria-expanded="true"` and no suggestion list.

5. **The same id is reused.** `heroImageDetailsLink` is on 133 elements. `destination-label` is on 3. Unrendered template ids such as `vp_room_%{=index}_label` are in the live page. A lookup by id hits only the first copy.

6. **Empty-search messages disagree.** On Packages, a blank destination says it is required, while blank departure, return, and airport say “Invalid” even though nothing was typed. On Hotels, the same empty search reports only that the destination is required and does not mention the empty dates.

7. **Careers has no address.** The footer control is an anchor with no `href`. A click opens a dialog about leaving Costcotravel.com. Open-in-new-tab and copy-link have nowhere to go.

8. **The homepage phone number does not dial.** The header number links to `/info/contact-us`. On the Help Center site, that number is a `tel:` link, and a second number is labeled for help while traveling.

9. **Renters under 25 are sent to the phone.** Clearing “Yes, I am at least 25 years old” shows a dialog that says to call. There is no age field after that.

10. **A default headless Chrome is rejected.** The page title is Access Denied and the response comes from the site edge (Akamai, `errors.edgesuite.net`). With the Chrome options this suite already uses, the homepage loads. The four homepage tests passed with `HEADLESS=true`. Details are in `headless-probe.txt`.

## Accessibility test result

`AccessibilityTests` passed: skip link present, logo alt text present, hotel search fields visible, and no serious or critical axe violations in the scanned regions. The sign-in form exposes labels for email and password. See `AccessibilityTests.txt`.
