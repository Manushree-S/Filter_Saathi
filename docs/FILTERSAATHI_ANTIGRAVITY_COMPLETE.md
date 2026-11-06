# FILTERSAATHI: COMPLETE ANTIGRAVITY PROMPT (Phase 1: frontend first)

HOW TO USE THIS FILE
1. Paste PART A (this section, down to the line "END OF PART A") into Antigravity as your first message.
2. Then paste each FILE block below it (Parts B to F) as follow-up messages, or save them into the folders shown. Each file block starts with `FILE:` and the path.
3. Antigravity must reply with a plan first and wait for approval.

If your chat box has a size limit, paste Part A first, then send Parts B, C, D, E and F one at a time.

================================================================
PART A: THE PROMPT
================================================================

## ROLE
You are a senior Android engineer and product designer. Build the complete FRONTEND of an Android app called FilterSaathi in the existing repository https://github.com/Manushree-S/Filter_Saathi.git (package `org.filtersaathi.app`). Match the approved design prototype exactly. This is Phase 1: UI plus sample data only. The backend comes later.

Design prototype (open in a browser and use as the visual reference): https://claude.ai/artifact/St4LmzY4HkHqh5z3FckwW7

## WHAT THE APP IS
- Name: FilterSaathi. Tagline: "Every drop, verified." (Kannada: ಪ್ರತಿ ಹನಿ, ಸುರಕ್ಷಿತ).
- Purpose: help Gram Panchayats, schools, Anganwadis, PHCs and lower-level BWSSB staff install, monitor and maintain community RO/UV water filters and sump water quality, aligned with the Jal Jeevan Mission.
- Our role: we are the MEDIATOR between the government (Gram Panchayat / Zilla Panchayat) and BWSSB or approved sump installers. The panchayat sends a request, FilterSaathi verifies and routes it, and the installer surveys and installs. Because we only mediate, there must be NO payment screens, payment SDKs or payment wording anywhere. Show "No payments in this app." where the prototype does.
- Users: (1) caretaker or villager with low literacy, (2) panchayat officer, (3) BWSSB or PHC engineer, (4) any citizen checking water safety (no login).
- Languages: English and Kannada everywhere. Offline-friendly. Presented to the Government of Karnataka for approval, so it must look professional and official (tricolour strip, navy/blue palette), not playful.
- Logo: a water drop with a check mark inside and a filter ring, gradient aqua `#14B8D4` to deep blue `#1D4896`. Provide it as vector drawables (full logo, icon-only for launcher and splash). Do NOT use the national emblem or the Karnataka state seal.

## ALREADY DONE: DO NOT REDO OR BREAK
- The Gradle wrapper was repaired (official Gradle 8.6 `gradlew`, `gradlew.bat`, `gradle-wrapper.jar`, `.gitattributes`).
- `gradle.properties` contains `android.useAndroidX=true`.
- In `strings.xml` (values and values-kn), the strings `health_score_delta_positive`, `health_score_delta_negative`, `alert_sample_message` have `formatted="false"` because they contain a `%`. Keep that. Any new string containing `%` needs `formatted="false"`.
- CI (`.github/workflows/android-ci.yml`) assembles the debug APK first, uploads it, then runs lint with continue-on-error, uploads the lint report, then runs unit tests. The build is green.
- Do not touch: `gradlew`, `gradlew.bat`, `gradle/wrapper/*`, `gradle.properties`, JDK or SDK versions, `.github/workflows/*`, dependencies (add only what a screen strictly needs and tell me first).

## RULES
1. Do not rewrite the project. Extend it. Inspect the current structure first and reuse existing packages, theme and Hilt setup.
2. LANGUAGE SPLIT: **Java for the UI layer** (Activity, Fragments, adapters, custom views, dialogs). **Kotlin for state and logic** (ViewModels, UI state, FakeRepository, score and status helpers). Show Java calling Kotlin.
3. UI toolkit: XML layouts, Material Components, ViewBinding, Navigation Component, Hilt. No Jetpack Compose.
4. Phase 1 data comes from a Kotlin `FakeRepository` (Hilt-injected so a real repository can replace it later). No network, no Room, no payments.
5. Every UI string comes from resources. Use the provided `ui_strings.xml` files (English and Kannada). Never hard-code text. Add new strings to both languages.
6. All demo data must show a visible "Sample data" label, like the prototype. Never present sample numbers as real.
7. Git: work on branch `feature/frontend-phase1`, use Conventional Commits (feat:, fix:, docs:, chore:), open a PR into `develop`. **Do not commit or push until I approve.** Never commit keys, keystores or `google-services.json`.
8. Keep `./gradlew assembleDebug` passing after every screen. This laptop has JDK 17 but no Android SDK, so CI is the real build check: tell me when to push and what the CI run should show.
9. After each screen, tell me which files you added or changed, then continue.
10. Make the smallest correct change. If something is ambiguous, ask one question, do not guess.

## FILES PROVIDED (create them exactly as given in Parts B to F)
- `app/src/main/res/values/ui_strings.xml` (Part B)
- `app/src/main/res/values-kn/ui_strings.xml` (Part C)
- `app/src/main/res/values/ui_arrays.xml` (Part D)
- `app/src/main/java/org/filtersaathi/app/ui/widget/DistrictMapView.java` (Part E, a reference starter that was never compiled: review, fix and extend it)
- `app/src/main/assets/karnataka_districts.json` (Part F)
Strings are prefixed `ui_` so they will not clash with existing ones. Keys are snake_case of the prototype keys (for example `heroTitle` becomes `ui_hero_title`).

## DESIGN TOKENS
Colours (`colors.xml` plus `values-night`):
- Navy `#0F2A5C`, Blue `#1D4896`, Aqua `#14B8D4`
- Saffron accent for primary buttons `#B45309`, saffron light `#E8801A`
- Success `#18803A`, Warning `#D99A0B`, Danger `#C62828`
- Light: page `#F3F6FB`, card `#FFFFFF`, ink `#17213A`, muted `#4A5670`, line `#D9E0EC`, soft `#F1F5FB`, selected `#E4EDFB`, track `#E3E9F4`
- Dark: page `#0E1524`, card `#17213A`, ink `#E7ECF6`, muted `#9AA8C2`, line `#2B3852`, soft `#1D2942`, selected `#23406E`, headings `#BBD0F8`
- High contrast: black on white, black borders, selected `#FFE9A8`
- Map palette: `#CFE0F7 #D4EBDD #FBE7C0 #E1D8F2`
- Status tones: red bg `#FDECEC` fg `#8A1C1C`; amber bg `#FFF1D6` fg `#7A4B00`; green bg `#E3F4E8` fg `#135B2A`
- Gradients as shape drawables: navy to blue at 135 degrees for header, hero, impact and CTA cards; the hero adds an aqua radial glow top-right and a faint saffron glow bottom-left.
- A 6dp tricolour strip (saffron, white, green) above every toolbar.

Typography: Noto Sans (body), Noto Serif (headings), Noto Sans Kannada (Kannada). Bundle as font resources. Body 15-16sp, headings 28-32sp bold. All touch targets at least 48dp.
Shape and depth: 14-18dp rounded cards, soft elevation, 8dp rounded buttons, hover or press lift on cards.
Motion (respect the system "remove animations" setting): screens fade in 450ms; cards lift on press; buttons scale to 0.98 on press; count-up numbers 1.8s; pulsing ring on the current stage; booked banner slides down; scan line sweeps; water ripples; flowing dashes.

## NAVIGATION
- First launch: Welcome screen with two big buttons, "ಕನ್ನಡ" and "English". The choice sets the language.
- Bottom navigation (5): Home, Book, Test strip, Water check, Report.
- Toolbar: logo and name; offline chip (Online/Offline); notification bell with badge; accessibility "Aa"; dark-mode toggle; EN | ಕನ್ನಡ toggle; Register / Login button (shows "Hi, <name>" and Log out when signed in).
- Overflow menu: Officer overview. Track opens after a booking. Certificate opens from Track.
- A floating speaker button on every screen reads the current screen aloud.
- If a signed-out user taps Book, go to Register first, then OTP, then Book.

## SCREENS (build in this order, one at a time)
1. **Welcome:** gradient logo circle, "ಫಿಲ್ಟರ್ ಸಾಥಿ · FilterSaathi", tagline in both languages, "ಭಾಷೆ ಆಯ್ಕೆಮಾಡಿ · Choose language", two big buttons.
2. **Home** (scrolling):
   - Hero (navy-blue gradient): headline "Safe water for every village.", one-line sub, primary button "Request installation", ghost button "Check my village water", green pill "No payments in this app".
   - `SumpHeroView` (Java custom View): 3D-style animated sump tank with rippling water; a sensor with pulsing rings sending a dashed animated signal to a "Live readings: TDS, pH, level" box; water flowing through a four-stage filter (Sediment, Carbon, RO filter, UV light with a glowing UV stage) to a "Safe water" drop. Labels "Sump tank", "Filter and UV", "Safe water". Localised. Inside a glass-style rounded panel that floats gently. Animated wave divider at the bottom of the hero.
   - "How it works": three cards with icons and arrows: Panchayat (sends the request) -> FilterSaathi (verifies and routes it; highlighted navy gradient card) -> BWSSB or installer (surveys and installs). Then a green notice "No payments in this app."
   - Test strip promo banner: four coloured pads (`#E6C84A #A8C25C #D88BA8 #C4572A`), title "Test strip scanner", hint, "Scan strip" button.
   - "Our impact" (label "Sample data"): four count-up cards (18,400 litres of safe water daily; 24,600 people reached; 214 villages covered; 37 unsafe readings fixed); a before/after water slider (`BeforeAfterView`, draggable: cloudy brown vs clear aqua with bubbles, handle with arrows); four SDG cards using OUR OWN coloured badges (not the official UN icons): SDG 6 Clean water (`#0E8A9E`), SDG 3 Good health (`#18803A`), SDG 10 Fair access (`#B45309`), SDG 17 Partnerships (`#1D4896`); note "UN Sustainable Development Goals."
   - "Karnataka district map": `DistrictMapView` (real district outlines, four pastel colours, sea and neighbouring state names, Bengaluru marker, north arrow, labels). Beside or below it: a navy gradient card showing the selected district, a sample status chip, and a "Book installation · <district>" button that opens Book with the district pre-filled; a search box; a scrollable list of all 31 districts. Tapping the Ballari outline a second time switches to Vijayanagara. Show the note that Ballari and Vijayanagara share one outline and Bengaluru South uses the old Ramanagara outline.
3. **Register:** full name and +91 mobile. Mobile must match `^[6-9]\d{9}$`. **OTP:** 6 digits. In the demo any 6 digits work and the screen says so. Errors use the `ui_err_*` strings. After OTP the user is signed in.
4. **Book:** district spinner (31, localised from `ui_district_names`, keys from `ui_district_keys`), taluk, village or Gram Panchayat, sump location (building, street, landmark), PIN (6 digits). All required. On submit: create request number `FS-2026-####`, show a green banner at the top "Booked. Request no. ...", then open Track. Step indicator "1 Register, 2 Book, 3 Track". Form card has a tricolour top bar.
5. **Track:** request number, district and village, a progress bar. Six stages (Request received, Verified, Installer assigned, Site survey, Installation, Live monitoring) as a vertical stepper: done = green tick, current = saffron ring that pulses, pending = grey. From stage 3: installer card with initial avatar, name, phone, experience and a "Call installer" button (ACTION_DIAL) tagged "Sample details". From stage 6: 5-star rating with thank-you text, and "View certificate". Buttons: "Next stage (demo)" (moves one stage) and "New request".
6. **Certificate:** paper-style card (double border, tricolour bar, logo): "Water Safety Certificate", body line, request number, district, village, installer, date, readings, a decorative QR-style box tagged "Sample QR" ("Scan to verify"), Print / Save (PrintManager), "Back to tracking". Always white, even in dark mode.
7. **Test strip scanner:** dark viewfinder with four orange corner brackets and a strip. "Scan strip" shows a sweeping scan line for 1.8 seconds, then four result rings (TDS 312 ppm Safe, pH 7.2 Safe, Chlorine 0.1 mg/L Low, Nitrate 62 mg/L Unsafe) and a red warning "Nitrate is above the limit. Do not drink this water. The panchayat has been notified." Phase 1 is a simulation: no real camera.
8. **Water check (public, no login):** a district spinner comes first. Before a district is chosen show only the hint "Select your district to see your village water." After that show ONLY that district's villages as chips. Sample mapping: Mysuru -> Hosahalli, Tumakuru -> Chikkanahalli, Chikkaballapura -> Doddabele, Shivamogga -> Bettahalli, Mandya -> Kyathanahalli. Other districts show "No readings yet for this district." Never list villages from other districts. For a village show: a big status banner (score >= 80 "Safe to drink" green; 50-79 "Use with care" amber; below 50 "Do not drink" red, with a calm next-step line), last tested "10 October 2026", four reading rings, and the animated "How your water becomes safe" diagram (Source -> Sump -> Filter -> Your tap with flowing dashes and TDS 780, 780, 312, 312). Pre-fill the district if the user already booked one.
9. **Report a problem (public):** problem spinner (Bad taste or smell, Filter not working, Leak, Other), village, optional details, an "Add photo" toggle (simulated), "Send report". Result: ticket `FS-R-####`, a top banner "Report sent. Ticket no. ...", and a Received -> In review -> Fixed row with In review pulsing.
10. **Officer overview:** four KPI cards (1,248 filters monitored; 90.2% working; 94.2% safe tests; 37 open alerts), a 6-month safe-test bar chart that grows on load (`BarChartView`, Java; values May-Oct 88, 90, 91, 92, 93, 94; latest bar darker), "Needs action" alerts with tags (Nitrate above limit Unsafe; Filter offline 3 days Urgent; UV lamp due in 7 days Soon), a village status table (RecyclerView: Hosahalli 14 filters score 91; Chikkanahalli 9, 68; Doddabele 12, 41; Bettahalli 6, 86; Kyathanahalli 7, 88; score bars and tags: >= 80 Good, 50-79 Watch, below 50 Action needed), "Sump installations" progress bars (Requested 42, Scheduled 18, Installed 96, total 156) with an animated wave tank gauge (78%, `WaveTankView`), and a district coverage map using `DistrictMapView.setFillOverrides` with a legend (Installed `#18803A`, Scheduled `#1D4896`, Requested `#D99A0B`, No request `#E3E9F4`). Label everything "Sample data".

## SAMPLE DATA (FakeRepository)
- Installer: Ramesh Gowda (ರಮೇಶ್ ಗೌಡ), +91 98765 43210, "8 years · 120+ installations" (tagged Sample details).
- Certificate date "11 October 2026"; readings "TDS 312 ppm · pH 7.2".
- Notifications: "Your request is verified" (2 hours ago), "UV lamp due in 7 days" (1 day ago), "Monthly report is ready" (3 days ago).
- Water check readings by status: Safe (TDS 312, pH 7.2, Chlorine 0.5, Nitrate 12); Watch (420, 7.4, 0.1 low, 18); Unsafe (480, 7.6, 0.3, 62 unsafe). Village scores: Hosahalli 91, Chikkanahalli 68, Doddabele 41, Bettahalli 86, Kyathanahalli 88.
- Coverage statuses are generated deterministically from the district index (sample).

## CROSS-CUTTING FEATURES
- **Language:** per-app locales with `AppCompatDelegate.setApplicationLocales` and `locales_config.xml` (English, Kannada). Every screen switches instantly. The map uses `setKannada`.
- **Dark mode:** DayNight with `values-night`. The map keeps its pastel colours. The certificate stays white.
- **Offline:** the chip toggles Online/Offline. Offline shows an amber bar "You are offline. Changes are saved on this phone." Booking while offline shows an amber banner "Saved offline. Will sync. Request no. ..."; going online shows "Synced. Request no. ...".
- **Notifications bell:** orange badge with 3 until opened; bottom sheet with the three items above.
- **Accessibility sheet** (the "Aa" button): switches for Large text (about 1.2x via a theme overlay) and High contrast (theme overlay), plus "Read this page aloud" using `TextToSpeech` with `kn-IN` or `en-IN`. If no voice is installed show "Voice depends on your phone." The floating speaker button does the same. Add `contentDescription` everywhere, logical TalkBack order, 48dp targets, status never shown by colour alone.
- **Easy access for everyone:** icon-first layouts, short plain sentences, large buttons.
- **Banner system:** one top banner for Booked (green), Saved offline (amber), Synced (green), Report sent (green), each with a Close button.

## ARCHITECTURE (Phase 1)
- `ui/` (Java): `MainActivity` (single activity, nav host); one Fragment per screen; RecyclerView adapters; custom views `SumpHeroView`, `DistrictMapView`, `BeforeAfterView`, `BarChartView`, `WaveTankView`, `RingView`, `ScanOverlayView`.
- `ui/state/` (Kotlin): one ViewModel per screen exposing immutable UI state through StateFlow or LiveData.
- `data/` (Kotlin): `FakeRepository` plus models.
- `util/`: score and status helpers (Kotlin), `PrintHelper` (Java).
- Layers for the report: Presentation (Java) -> Domain and logic (Kotlin) -> Data (Kotlin; Room and Retrofit later) -> External services (backend, Firebase FCM, device camera), with Hilt across all layers.

## DOCS TO PRODUCE
- Update `README.md`: app name and tagline, what it does, the mediator model, screenshots (both languages), tech stack (Kotlin + Java, MVVM, Hilt, Navigation, Material), how to build, the CI status note ("lint errors are logged in the lint-report artifact; planned for fixing"), and a clear statement that Phase 1 uses sample data.
- A short `docs/ARCHITECTURE.md` with the layer list above.

## LATER PHASES (do NOT build now; keep the code ready for them)
- Phase 2: Room offline database, WorkManager sync, Retrofit client with a backend (FastAPI or Node + PostgreSQL), real phone-OTP authentication.
- Phase 3: real CameraX test-strip analyser (find pads, average RGB, correct lighting with a printed reference patch, match to the strip's colour chart, label Safe/Low/Unsafe), FCM push reminders, real verifiable certificate QR with signed records, PDF reports.
- Phase 4 ideas: predictive filter-maintenance model, anomaly detection on readings, tamper-evident records, ESP32 sensor option, on-device Kannada voice assistant, federated learning, offline mesh sync.

## ACCEPTANCE CHECKLIST (report against this at the end)
- [ ] `./gradlew assembleDebug` passes locally and in CI
- [ ] Welcome -> Home -> Register -> OTP -> Book -> Track -> Certificate works end to end
- [ ] Every screen exists and matches the prototype
- [ ] English and Kannada complete everywhere; no hard-coded strings
- [ ] Dark mode, Large text and High contrast work on every screen
- [ ] Water check never shows another district's villages
- [ ] No payment text, screens or SDKs anywhere
- [ ] All demo data is labelled "Sample data"
- [ ] Offline chip, bell, accessibility sheet, speaker button all work
- [ ] README and docs updated; screenshots of each screen in both languages

## START NOW
1. Inspect the repo structure and tell me what already exists (packages, theme, nav graph, Hilt modules).
2. Reply with a short plan: files per screen, in the order above, and which existing files you will change.
3. Wait for my approval before writing any code.

END OF PART A


================================================================
PART B (English strings)
FILE: app/src/main/res/values/ui_strings.xml
================================================================

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- Generated from the FilterSaathi design prototype. Prefix ui_ avoids clashes with existing strings. -->
<resources>
    <string name="ui_brand">FilterSaathi</string>
    <string name="ui_tagline">Every drop, verified.</string>
    <string name="ui_nav_home">Home</string>
    <string name="ui_nav_book">Book</string>
    <string name="ui_nav_track">Track</string>
    <string name="ui_nav_overview">Overview</string>
    <string name="ui_login">Register / Login</string>
    <string name="ui_logout">Log out</string>
    <string name="ui_hi">Hi,</string>
    <string name="ui_hero_title">Safe water for every village.</string>
    <string name="ui_hero_sub">Request a sump sensor and filter for your panchayat.</string>
    <string name="ui_hero_btn">Request installation</string>
    <string name="ui_how">How it works</string>
    <string name="ui_c1">Panchayat</string>
    <string name="ui_c1t">Sends the request.</string>
    <string name="ui_c2t">Verifies and routes it.</string>
    <string name="ui_c3">BWSSB or installer</string>
    <string name="ui_c3t">Surveys and installs.</string>
    <string name="ui_no_pay">No payments in this app.</string>
    <string name="ui_g_live">Live readings</string>
    <string name="ui_g_tds">TDS · pH · level</string>
    <string name="ui_g_sed">Sediment</string>
    <string name="ui_g_car">Carbon</string>
    <string name="ui_g_ro">RO filter</string>
    <string name="ui_g_uv">UV light</string>
    <string name="ui_g_tank">Sump tank</string>
    <string name="ui_g_fil">Filter and UV</string>
    <string name="ui_g_safe">Safe water</string>
    <string name="ui_s1">Register</string>
    <string name="ui_s2">Book</string>
    <string name="ui_s3">Track</string>
    <string name="ui_reg_title">Register or log in</string>
    <string name="ui_name">Full name</string>
    <string name="ui_phone">Mobile number</string>
    <string name="ui_send_otp">Send OTP</string>
    <string name="ui_otp_title">Verify your number</string>
    <string name="ui_otp_sent">OTP sent to</string>
    <string name="ui_otp_label">6-digit OTP</string>
    <string name="ui_verify">Verify</string>
    <string name="ui_demo_note">Demo: any 6 digits work.</string>
    <string name="ui_err_reg">Enter your name and a valid 10-digit mobile number.</string>
    <string name="ui_err_otp">Enter the 6-digit OTP.</string>
    <string name="ui_err_book">Fill in all fields. PIN must be 6 digits.</string>
    <string name="ui_book_title">Book installation</string>
    <string name="ui_district">District</string>
    <string name="ui_select_district">Select district</string>
    <string name="ui_taluk">Taluk</string>
    <string name="ui_village">Village or Gram Panchayat</string>
    <string name="ui_sump">Sump location</string>
    <string name="ui_sump_hint">Building, street, landmark</string>
    <string name="ui_pin">PIN code</string>
    <string name="ui_pin_hint">6 digits</string>
    <string name="ui_book_btn">Book installation</string>
    <string name="ui_banner">Booked. Request no.</string>
    <string name="ui_close">Close</string>
    <string name="ui_track_title">Track your request</string>
    <string name="ui_req_no">Request no.</string>
    <string name="ui_stg1">Request received</string>
    <string name="ui_stg2">Verified</string>
    <string name="ui_stg3">Installer assigned</string>
    <string name="ui_stg4">Site survey</string>
    <string name="ui_stg5">Installation</string>
    <string name="ui_stg6">Live monitoring</string>
    <string name="ui_done">Done</string>
    <string name="ui_current">In progress</string>
    <string name="ui_pending">Pending</string>
    <string name="ui_installer_title">Your installer</string>
    <string name="ui_i_name">Name</string>
    <string name="ui_i_contact">Contact</string>
    <string name="ui_i_exp">Experience</string>
    <string name="ui_call">Call installer</string>
    <string name="ui_installer_name">Ramesh Gowda</string>
    <string name="ui_initial">R</string>
    <string name="ui_installer_exp">8 years · 120+ installations</string>
    <string name="ui_assigning">An installer will be assigned soon.</string>
    <string name="ui_sample_tag">Sample details</string>
    <string name="ui_next">Next stage (demo)</string>
    <string name="ui_new_req">New request</string>
    <string name="ui_ov_title">District overview</string>
    <string name="ui_ov_sub">Officer view · October 2026</string>
    <string name="ui_sample">Sample data</string>
    <string name="ui_k1">Filters monitored</string>
    <string name="ui_k2">Filters working</string>
    <string name="ui_k3">Safe test results</string>
    <string name="ui_k4">Open alerts</string>
    <string name="ui_chart_title">Safe water test results</string>
    <string name="ui_chart_sub">Last 6 months</string>
    <string name="ui_alerts_title">Needs action</string>
    <string name="ui_a1">Nitrate above limit</string>
    <string name="ui_a2">Filter offline for 3 days</string>
    <string name="ui_a3">UV lamp due in 7 days</string>
    <string name="ui_tag_unsafe">Unsafe</string>
    <string name="ui_tag_urgent">Urgent</string>
    <string name="ui_tag_soon">Soon</string>
    <string name="ui_vs_title">Village status</string>
    <string name="ui_th_village">Village</string>
    <string name="ui_th_filters">Filters</string>
    <string name="ui_th_score">Health score</string>
    <string name="ui_th_status">Status</string>
    <string name="ui_st_good">Good</string>
    <string name="ui_st_watch">Watch</string>
    <string name="ui_st_action">Action needed</string>
    <string name="ui_sump_title">Sump installations</string>
    <string name="ui_map_title">Karnataka district map</string>
    <string name="ui_map_note">Tap a district to start. Outlines: Census 2011, simplified.</string>
    <string name="ui_map_hint">Tap a district on the map.</string>
    <string name="ui_map_bv">Ballari and Vijayanagara share one outline. Tap again to switch.</string>
    <string name="ui_search">Search district</string>
    <string name="ui_no_res">No match.</string>
    <string name="ui_c_blr">Bengaluru</string>
    <string name="ui_n_mh">MAHARASHTRA</string>
    <string name="ui_n_ts">TELANGANA</string>
    <string name="ui_n_ap">ANDHRA PRADESH</string>
    <string name="ui_n_tn">TAMIL NADU</string>
    <string name="ui_n_kl">KERALA</string>
    <string name="ui_n_ga">GOA</string>
    <string name="ui_n_sea">Arabian Sea</string>
    <string name="ui_online">Online</string>
    <string name="ui_offline">Offline</string>
    <string name="ui_off_bar">You are offline. Changes are saved on this phone.</string>
    <string name="ui_saved_offline">Saved offline. Will sync. Request no.</string>
    <string name="ui_synced">Synced. Request no.</string>
    <string name="ui_notif">Notifications</string>
    <string name="ui_nt1">Your request is verified</string>
    <string name="ui_nt2">UV lamp due in 7 days</string>
    <string name="ui_nt3">Monthly report is ready</string>
    <string name="ui_tm1">2 hours ago</string>
    <string name="ui_tm2">1 day ago</string>
    <string name="ui_tm3">3 days ago</string>
    <string name="ui_theme_btn">Dark mode</string>
    <string name="ui_nav_scan">Test strip</string>
    <string name="ui_scan_title">Test strip scanner</string>
    <string name="ui_scan_hint">Place the strip inside the frame</string>
    <string name="ui_scan_btn">Scan strip</string>
    <string name="ui_scanning">Reading…</string>
    <string name="ui_scan_again">Scan again</string>
    <string name="ui_nav_public">Water check</string>
    <string name="ui_nav_report">Report</string>
    <string name="ui_hero_check">Check my village water</string>
    <string name="ui_imp_title">Our impact</string>
    <string name="ui_i_lit">Litres of safe water daily</string>
    <string name="ui_i_people">People reached</string>
    <string name="ui_i_vill">Villages covered</string>
    <string name="ui_i_fix">Unsafe readings fixed</string>
    <string name="ui_g6">Clean water</string>
    <string name="ui_d6">Safe drinking water for every village.</string>
    <string name="ui_g3">Good health</string>
    <string name="ui_d3">Fewer water-borne illnesses.</string>
    <string name="ui_g10">Fair access</string>
    <string name="ui_d10">Kannada, voice and large text for everyone.</string>
    <string name="ui_g17">Partnerships</string>
    <string name="ui_d17">Government and installers working together.</string>
    <string name="ui_sdg_note">UN Sustainable Development Goals.</string>
    <string name="ui_ba_title">Before and after</string>
    <string name="ui_ba_before">Before</string>
    <string name="ui_ba_after">After</string>
    <string name="ui_ba_hint">Drag to compare</string>
    <string name="ui_pub_title">Is my village water safe?</string>
    <string name="ui_pub_sub">Select your own district first. No login needed.</string>
    <string name="ui_pub_district">Your district</string>
    <string name="ui_pub_pick">Select your district to see your village water.</string>
    <string name="ui_pub_none">No readings yet for this district.</string>
    <string name="ui_pub_last">Last tested</string>
    <string name="ui_pub_date">10 October 2026</string>
    <string name="ui_pub_safe">Safe to drink</string>
    <string name="ui_pub_watch">Use with care</string>
    <string name="ui_pub_unsafe">Do not drink</string>
    <string name="ui_pub_safe_txt">All readings are within safe limits.</string>
    <string name="ui_pub_watch_txt">One reading is low. A check is scheduled.</string>
    <string name="ui_pub_unsafe_txt">A reading is above the limit. A repair is scheduled.</string>
    <string name="ui_journey">How your water becomes safe</string>
    <string name="ui_j_src">Source</string>
    <string name="ui_j_sump">Sump</string>
    <string name="ui_j_fil">Filter</string>
    <string name="ui_j_tap">Your tap</string>
    <string name="ui_rep_title">Report a problem</string>
    <string name="ui_rep_type">Problem</string>
    <string name="ui_rep_sel">Select problem</string>
    <string name="ui_ty1">Bad taste or smell</string>
    <string name="ui_ty2">Filter not working</string>
    <string name="ui_ty3">Leak</string>
    <string name="ui_ty4">Other</string>
    <string name="ui_rep_desc">Details (optional)</string>
    <string name="ui_rep_photo">Add photo</string>
    <string name="ui_rep_photo_on">Photo added</string>
    <string name="ui_rep_btn">Send report</string>
    <string name="ui_err_rep">Choose the problem and enter your village.</string>
    <string name="ui_rep_done">Report sent. Ticket no.</string>
    <string name="ui_ticket">Ticket no.</string>
    <string name="ui_rs1">Received</string>
    <string name="ui_rs2">In review</string>
    <string name="ui_rs3">Fixed</string>
    <string name="ui_rep_new">Send another</string>
    <string name="ui_acc">Accessibility</string>
    <string name="ui_acc_big">Large text</string>
    <string name="ui_acc_hc">High contrast</string>
    <string name="ui_acc_read">Read this page aloud</string>
    <string name="ui_voice_note">Voice depends on your phone.</string>
    <string name="ui_speak">Read aloud</string>
    <string name="ui_a_on">On</string>
    <string name="ui_a_off">Off</string>
    <string name="ui_scan_warn">Nitrate is above the limit. Do not drink this water. The panchayat has been notified.</string>
    <string name="ui_s_safe">Safe</string>
    <string name="ui_s_low">Low</string>
    <string name="ui_r_tds">TDS</string>
    <string name="ui_r_ph">pH</string>
    <string name="ui_r_cl">Chlorine</string>
    <string name="ui_r_ni">Nitrate</string>
    <string name="ui_rate_title">Rate your installer</string>
    <string name="ui_thanks">Thank you for your feedback.</string>
    <string name="ui_view_cert">View certificate</string>
    <string name="ui_cert_title">Water Safety Certificate</string>
    <string name="ui_cert_body">A FilterSaathi sump sensor and filter has been installed and is reporting.</string>
    <string name="ui_cert_date">Date</string>
    <string name="ui_date_val">11 October 2026</string>
    <string name="ui_cert_read">Readings</string>
    <string name="ui_read_val">TDS 312 ppm · pH 7.2</string>
    <string name="ui_cert_inst">Installer</string>
    <string name="ui_cert_verify">Scan to verify</string>
    <string name="ui_qr_sample">Sample QR</string>
    <string name="ui_print">Print / Save</string>
    <string name="ui_back">Back to tracking</string>
    <string name="ui_cov_title">District coverage</string>
    <string name="ui_none">No request</string>
    <string name="ui_sump_level">Live sump level</string>
    <string name="ui_s_req">Requested</string>
    <string name="ui_s_sch">Scheduled</string>
    <string name="ui_s_ins">Installed</string>
    <string-array name="ui_district_names">
        <item>Bagalkot</item>
        <item>Ballari</item>
        <item>Belagavi</item>
        <item>Bengaluru Rural</item>
        <item>Bengaluru South</item>
        <item>Bengaluru Urban</item>
        <item>Bidar</item>
        <item>Chamarajanagar</item>
        <item>Chikkaballapura</item>
        <item>Chikkamagaluru</item>
        <item>Chitradurga</item>
        <item>Dakshina Kannada</item>
        <item>Davanagere</item>
        <item>Dharwad</item>
        <item>Gadag</item>
        <item>Hassan</item>
        <item>Haveri</item>
        <item>Kalaburagi</item>
        <item>Kodagu</item>
        <item>Kolar</item>
        <item>Koppal</item>
        <item>Mandya</item>
        <item>Mysuru</item>
        <item>Raichur</item>
        <item>Shivamogga</item>
        <item>Tumakuru</item>
        <item>Udupi</item>
        <item>Uttara Kannada</item>
        <item>Vijayanagara</item>
        <item>Vijayapura</item>
        <item>Yadgir</item>
    </string-array>
</resources>
```


================================================================
PART C (Kannada strings)
FILE: app/src/main/res/values-kn/ui_strings.xml
================================================================

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- Generated from the FilterSaathi design prototype. Prefix ui_ avoids clashes with existing strings. -->
<resources>
    <string name="ui_brand">ಫಿಲ್ಟರ್ ಸಾಥಿ</string>
    <string name="ui_tagline">ಪ್ರತಿ ಹನಿ, ಸುರಕ್ಷಿತ</string>
    <string name="ui_nav_home">ಮುಖಪುಟ</string>
    <string name="ui_nav_book">ಬುಕ್ ಮಾಡಿ</string>
    <string name="ui_nav_track">ಟ್ರ್ಯಾಕ್</string>
    <string name="ui_nav_overview">ಅವಲೋಕನ</string>
    <string name="ui_login">ನೋಂದಣಿ / ಲಾಗಿನ್</string>
    <string name="ui_logout">ಲಾಗ್ ಔಟ್</string>
    <string name="ui_hi">ನಮಸ್ಕಾರ,</string>
    <string name="ui_hero_title">ಪ್ರತಿ ಹಳ್ಳಿಗೂ ಸುರಕ್ಷಿತ ನೀರು.</string>
    <string name="ui_hero_sub">ನಿಮ್ಮ ಪಂಚಾಯತ್‌ಗೆ ಸಂಪ್ ಸೆನ್ಸರ್ ಮತ್ತು ಫಿಲ್ಟರ್‌ಗಾಗಿ ಮನವಿ ಮಾಡಿ.</string>
    <string name="ui_hero_btn">ಸ್ಥಾಪನೆಗೆ ಮನವಿ ಮಾಡಿ</string>
    <string name="ui_how">ಇದು ಹೇಗೆ ಕೆಲಸ ಮಾಡುತ್ತದೆ</string>
    <string name="ui_c1">ಪಂಚಾಯತ್</string>
    <string name="ui_c1t">ಮನವಿ ಕಳುಹಿಸುತ್ತದೆ.</string>
    <string name="ui_c2t">ಪರಿಶೀಲಿಸಿ ಕಳುಹಿಸುತ್ತದೆ.</string>
    <string name="ui_c3">BWSSB ಅಥವಾ ಸ್ಥಾಪಕರು</string>
    <string name="ui_c3t">ಸಮೀಕ್ಷೆ ಮತ್ತು ಸ್ಥಾಪನೆ ಮಾಡುತ್ತಾರೆ.</string>
    <string name="ui_no_pay">ಈ ಆ್ಯಪ್‌ನಲ್ಲಿ ಯಾವುದೇ ಪಾವತಿ ಇಲ್ಲ.</string>
    <string name="ui_g_live">ನೇರ ಮಾಪನ</string>
    <string name="ui_g_tds">TDS · pH · ಮಟ್ಟ</string>
    <string name="ui_g_sed">ಕೆಸರು</string>
    <string name="ui_g_car">ಕಾರ್ಬನ್</string>
    <string name="ui_g_ro">RO ಫಿಲ್ಟರ್</string>
    <string name="ui_g_uv">UV ಬೆಳಕು</string>
    <string name="ui_g_tank">ಸಂಪ್ ಟ್ಯಾಂಕ್</string>
    <string name="ui_g_fil">ಫಿಲ್ಟರ್ ಮತ್ತು UV</string>
    <string name="ui_g_safe">ಸುರಕ್ಷಿತ ನೀರು</string>
    <string name="ui_s1">ನೋಂದಣಿ</string>
    <string name="ui_s2">ಬುಕ್</string>
    <string name="ui_s3">ಟ್ರ್ಯಾಕ್</string>
    <string name="ui_reg_title">ನೋಂದಣಿ ಅಥವಾ ಲಾಗಿನ್</string>
    <string name="ui_name">ಪೂರ್ಣ ಹೆಸರು</string>
    <string name="ui_phone">ಮೊಬೈಲ್ ಸಂಖ್ಯೆ</string>
    <string name="ui_send_otp">OTP ಕಳುಹಿಸಿ</string>
    <string name="ui_otp_title">ನಿಮ್ಮ ಸಂಖ್ಯೆಯನ್ನು ಪರಿಶೀಲಿಸಿ</string>
    <string name="ui_otp_sent">OTP ಕಳುಹಿಸಲಾಗಿದೆ:</string>
    <string name="ui_otp_label">6 ಅಂಕಿಯ OTP</string>
    <string name="ui_verify">ಪರಿಶೀಲಿಸಿ</string>
    <string name="ui_demo_note">ಡೆಮೊ: ಯಾವುದೇ 6 ಅಂಕಿ ಸಾಕು.</string>
    <string name="ui_err_reg">ಹೆಸರು ಮತ್ತು ಸರಿಯಾದ 10 ಅಂಕಿಯ ಮೊಬೈಲ್ ಸಂಖ್ಯೆ ನಮೂದಿಸಿ.</string>
    <string name="ui_err_otp">6 ಅಂಕಿಯ OTP ನಮೂದಿಸಿ.</string>
    <string name="ui_err_book">ಎಲ್ಲಾ ವಿವರಗಳನ್ನು ತುಂಬಿ. ಪಿನ್ 6 ಅಂಕಿ ಇರಬೇಕು.</string>
    <string name="ui_book_title">ಸ್ಥಾಪನೆ ಬುಕ್ ಮಾಡಿ</string>
    <string name="ui_district">ಜಿಲ್ಲೆ</string>
    <string name="ui_select_district">ಜಿಲ್ಲೆ ಆಯ್ಕೆಮಾಡಿ</string>
    <string name="ui_taluk">ತಾಲ್ಲೂಕು</string>
    <string name="ui_village">ಗ್ರಾಮ ಅಥವಾ ಗ್ರಾಮ ಪಂಚಾಯತ್</string>
    <string name="ui_sump">ಸಂಪ್ ಇರುವ ಸ್ಥಳ</string>
    <string name="ui_sump_hint">ಕಟ್ಟಡ, ರಸ್ತೆ, ಗುರುತು ಸ್ಥಳ</string>
    <string name="ui_pin">ಪಿನ್ ಕೋಡ್</string>
    <string name="ui_pin_hint">6 ಅಂಕಿ</string>
    <string name="ui_book_btn">ಸ್ಥಾಪನೆ ಬುಕ್ ಮಾಡಿ</string>
    <string name="ui_banner">ಬುಕ್ ಆಗಿದೆ. ವಿನಂತಿ ಸಂಖ್ಯೆ</string>
    <string name="ui_close">ಮುಚ್ಚಿ</string>
    <string name="ui_track_title">ನಿಮ್ಮ ವಿನಂತಿಯನ್ನು ಟ್ರ್ಯಾಕ್ ಮಾಡಿ</string>
    <string name="ui_req_no">ವಿನಂತಿ ಸಂಖ್ಯೆ</string>
    <string name="ui_stg1">ವಿನಂತಿ ಸ್ವೀಕರಿಸಲಾಗಿದೆ</string>
    <string name="ui_stg2">ಪರಿಶೀಲಿಸಲಾಗಿದೆ</string>
    <string name="ui_stg3">ಸ್ಥಾಪಕರ ನೇಮಕ</string>
    <string name="ui_stg4">ಸ್ಥಳ ಸಮೀಕ್ಷೆ</string>
    <string name="ui_stg5">ಸ್ಥಾಪನೆ</string>
    <string name="ui_stg6">ನೇರ ಮೇಲ್ವಿಚಾರಣೆ</string>
    <string name="ui_done">ಮುಗಿದಿದೆ</string>
    <string name="ui_current">ನಡೆಯುತ್ತಿದೆ</string>
    <string name="ui_pending">ಬಾಕಿ</string>
    <string name="ui_installer_title">ನಿಮ್ಮ ಸ್ಥಾಪಕರು</string>
    <string name="ui_i_name">ಹೆಸರು</string>
    <string name="ui_i_contact">ಸಂಪರ್ಕ</string>
    <string name="ui_i_exp">ಅನುಭವ</string>
    <string name="ui_call">ಸ್ಥಾಪಕರಿಗೆ ಕರೆ ಮಾಡಿ</string>
    <string name="ui_installer_name">ರಮೇಶ್ ಗೌಡ</string>
    <string name="ui_initial">ರ</string>
    <string name="ui_installer_exp">8 ವರ್ಷ · 120+ ಸ್ಥಾಪನೆಗಳು</string>
    <string name="ui_assigning">ಶೀಘ್ರದಲ್ಲೇ ಸ್ಥಾಪಕರನ್ನು ನೇಮಿಸಲಾಗುವುದು.</string>
    <string name="ui_sample_tag">ಮಾದರಿ ವಿವರಗಳು</string>
    <string name="ui_next">ಮುಂದಿನ ಹಂತ (ಡೆಮೊ)</string>
    <string name="ui_new_req">ಹೊಸ ವಿನಂತಿ</string>
    <string name="ui_ov_title">ಜಿಲ್ಲಾ ಅವಲೋಕನ</string>
    <string name="ui_ov_sub">ಅಧಿಕಾರಿ ವೀಕ್ಷಣೆ · ಅಕ್ಟೋಬರ್ 2026</string>
    <string name="ui_sample">ಮಾದರಿ ಅಂಕಿಅಂಶ</string>
    <string name="ui_k1">ಮೇಲ್ವಿಚಾರಣೆಯ ಫಿಲ್ಟರ್‌ಗಳು</string>
    <string name="ui_k2">ಕಾರ್ಯನಿರತ ಫಿಲ್ಟರ್‌ಗಳು</string>
    <string name="ui_k3">ಸುರಕ್ಷಿತ ಪರೀಕ್ಷಾ ಫಲಿತಾಂಶ</string>
    <string name="ui_k4">ಬಾಕಿ ಎಚ್ಚರಿಕೆಗಳು</string>
    <string name="ui_chart_title">ಸುರಕ್ಷಿತ ನೀರಿನ ಪರೀಕ್ಷೆ</string>
    <string name="ui_chart_sub">ಕಳೆದ 6 ತಿಂಗಳು</string>
    <string name="ui_alerts_title">ಗಮನ ಅಗತ್ಯ</string>
    <string name="ui_a1">ನೈಟ್ರೇಟ್ ಮಿತಿ ಮೀರಿದೆ</string>
    <string name="ui_a2">ಫಿಲ್ಟರ್ 3 ದಿನ ಸ್ಥಗಿತ</string>
    <string name="ui_a3">UV ದೀಪ 7 ದಿನದಲ್ಲಿ ಬದಲಿಸಿ</string>
    <string name="ui_tag_unsafe">ಅಸುರಕ್ಷಿತ</string>
    <string name="ui_tag_urgent">ತುರ್ತು</string>
    <string name="ui_tag_soon">ಶೀಘ್ರದಲ್ಲಿ</string>
    <string name="ui_vs_title">ಹಳ್ಳಿಯ ಸ್ಥಿತಿ</string>
    <string name="ui_th_village">ಹಳ್ಳಿ</string>
    <string name="ui_th_filters">ಫಿಲ್ಟರ್‌ಗಳು</string>
    <string name="ui_th_score">ಆರೋಗ್ಯ ಅಂಕ</string>
    <string name="ui_th_status">ಸ್ಥಿತಿ</string>
    <string name="ui_st_good">ಉತ್ತಮ</string>
    <string name="ui_st_watch">ಗಮನಿಸಿ</string>
    <string name="ui_st_action">ಕ್ರಮ ಬೇಕು</string>
    <string name="ui_sump_title">ಸಂಪ್ ಸ್ಥಾಪನೆಗಳು</string>
    <string name="ui_map_title">ಕರ್ನಾಟಕ ಜಿಲ್ಲಾ ನಕ್ಷೆ</string>
    <string name="ui_map_note">ಪ್ರಾರಂಭಿಸಲು ಜಿಲ್ಲೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ. ರೂಪರೇಖೆ: ಜನಗಣತಿ 2011, ಸರಳೀಕೃತ.</string>
    <string name="ui_map_hint">ನಕ್ಷೆಯಲ್ಲಿ ಜಿಲ್ಲೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ.</string>
    <string name="ui_map_bv">ಬಳ್ಳಾರಿ ಮತ್ತು ವಿಜಯನಗರ ಒಂದೇ ರೂಪರೇಖೆ ಹಂಚಿಕೊಂಡಿವೆ. ಬದಲಿಸಲು ಮತ್ತೆ ಒತ್ತಿ.</string>
    <string name="ui_search">ಜಿಲ್ಲೆ ಹುಡುಕಿ</string>
    <string name="ui_no_res">ಹೊಂದಾಣಿಕೆ ಇಲ್ಲ.</string>
    <string name="ui_c_blr">ಬೆಂಗಳೂರು</string>
    <string name="ui_n_mh">ಮಹಾರಾಷ್ಟ್ರ</string>
    <string name="ui_n_ts">ತೆಲಂಗಾಣ</string>
    <string name="ui_n_ap">ಆಂಧ್ರ ಪ್ರದೇಶ</string>
    <string name="ui_n_tn">ತಮಿಳುನಾಡು</string>
    <string name="ui_n_kl">ಕೇರಳ</string>
    <string name="ui_n_ga">ಗೋವಾ</string>
    <string name="ui_n_sea">ಅರಬ್ಬೀ ಸಮುದ್ರ</string>
    <string name="ui_online">ಆನ್‌ಲೈನ್</string>
    <string name="ui_offline">ಆಫ್‌ಲೈನ್</string>
    <string name="ui_off_bar">ನೀವು ಆಫ್‌ಲೈನ್ ಇದ್ದೀರಿ. ಬದಲಾವಣೆಗಳು ಈ ಫೋನ್‌ನಲ್ಲಿ ಉಳಿಯುತ್ತವೆ.</string>
    <string name="ui_saved_offline">ಆಫ್‌ಲೈನ್‌ನಲ್ಲಿ ಉಳಿಸಲಾಗಿದೆ. ಸಂಪರ್ಕ ಸಿಕ್ಕಾಗ ಕಳುಹಿಸಲಾಗುತ್ತದೆ. ವಿನಂತಿ ಸಂಖ್ಯೆ</string>
    <string name="ui_synced">ಸಿಂಕ್ ಆಗಿದೆ. ವಿನಂತಿ ಸಂಖ್ಯೆ</string>
    <string name="ui_notif">ಅಧಿಸೂಚನೆಗಳು</string>
    <string name="ui_nt1">ನಿಮ್ಮ ವಿನಂತಿ ಪರಿಶೀಲಿಸಲಾಗಿದೆ</string>
    <string name="ui_nt2">UV ದೀಪ 7 ದಿನದಲ್ಲಿ ಬದಲಿಸಿ</string>
    <string name="ui_nt3">ಮಾಸಿಕ ವರದಿ ಸಿದ್ಧವಾಗಿದೆ</string>
    <string name="ui_tm1">2 ಗಂಟೆ ಹಿಂದೆ</string>
    <string name="ui_tm2">1 ದಿನ ಹಿಂದೆ</string>
    <string name="ui_tm3">3 ದಿನ ಹಿಂದೆ</string>
    <string name="ui_theme_btn">ಡಾರ್ಕ್ ಮೋಡ್</string>
    <string name="ui_nav_scan">ಟೆಸ್ಟ್ ಸ್ಟ್ರಿಪ್</string>
    <string name="ui_scan_title">ಟೆಸ್ಟ್ ಸ್ಟ್ರಿಪ್ ಸ್ಕ್ಯಾನರ್</string>
    <string name="ui_scan_hint">ಸ್ಟ್ರಿಪ್ ಅನ್ನು ಚೌಕಟ್ಟಿನೊಳಗೆ ಇರಿಸಿ</string>
    <string name="ui_scan_btn">ಸ್ಟ್ರಿಪ್ ಸ್ಕ್ಯಾನ್ ಮಾಡಿ</string>
    <string name="ui_scanning">ಓದಲಾಗುತ್ತಿದೆ…</string>
    <string name="ui_scan_again">ಮತ್ತೆ ಸ್ಕ್ಯಾನ್ ಮಾಡಿ</string>
    <string name="ui_nav_public">ನೀರಿನ ಪರಿಶೀಲನೆ</string>
    <string name="ui_nav_report">ದೂರು</string>
    <string name="ui_hero_check">ನನ್ನ ಹಳ್ಳಿಯ ನೀರು ಪರಿಶೀಲಿಸಿ</string>
    <string name="ui_imp_title">ನಮ್ಮ ಪರಿಣಾಮ</string>
    <string name="ui_i_lit">ಪ್ರತಿದಿನ ಸುರಕ್ಷಿತ ನೀರು (ಲೀಟರ್)</string>
    <string name="ui_i_people">ತಲುಪಿದ ಜನರು</string>
    <string name="ui_i_vill">ಒಳಗೊಂಡ ಹಳ್ಳಿಗಳು</string>
    <string name="ui_i_fix">ಸರಿಪಡಿಸಿದ ಅಸುರಕ್ಷಿತ ಮಾಪನಗಳು</string>
    <string name="ui_g6">ಶುದ್ಧ ನೀರು</string>
    <string name="ui_d6">ಪ್ರತಿ ಹಳ್ಳಿಗೂ ಸುರಕ್ಷಿತ ಕುಡಿಯುವ ನೀರು.</string>
    <string name="ui_g3">ಉತ್ತಮ ಆರೋಗ್ಯ</string>
    <string name="ui_d3">ನೀರಿನಿಂದ ಹರಡುವ ರೋಗಗಳು ಕಡಿಮೆ.</string>
    <string name="ui_g10">ಎಲ್ಲರಿಗೂ ಸಮಾನ ಪ್ರವೇಶ</string>
    <string name="ui_d10">ಎಲ್ಲರಿಗೂ ಕನ್ನಡ, ಧ್ವನಿ ಮತ್ತು ದೊಡ್ಡ ಅಕ್ಷರ.</string>
    <string name="ui_g17">ಪಾಲುದಾರಿಕೆ</string>
    <string name="ui_d17">ಸರ್ಕಾರ ಮತ್ತು ಸ್ಥಾಪಕರು ಒಟ್ಟಾಗಿ.</string>
    <string name="ui_sdg_note">ವಿಶ್ವಸಂಸ್ಥೆಯ ಸುಸ್ಥಿರ ಅಭಿವೃದ್ಧಿ ಗುರಿಗಳು.</string>
    <string name="ui_ba_title">ಮೊದಲು ಮತ್ತು ನಂತರ</string>
    <string name="ui_ba_before">ಮೊದಲು</string>
    <string name="ui_ba_after">ನಂತರ</string>
    <string name="ui_ba_hint">ಹೋಲಿಸಲು ಎಳೆಯಿರಿ</string>
    <string name="ui_pub_title">ನನ್ನ ಹಳ್ಳಿಯ ನೀರು ಸುರಕ್ಷಿತವೇ?</string>
    <string name="ui_pub_sub">ಮೊದಲು ನಿಮ್ಮ ಜಿಲ್ಲೆ ಆಯ್ಕೆಮಾಡಿ. ಲಾಗಿನ್ ಬೇಕಿಲ್ಲ.</string>
    <string name="ui_pub_district">ನಿಮ್ಮ ಜಿಲ್ಲೆ</string>
    <string name="ui_pub_pick">ನಿಮ್ಮ ಹಳ್ಳಿಯ ನೀರು ನೋಡಲು ನಿಮ್ಮ ಜಿಲ್ಲೆ ಆಯ್ಕೆಮಾಡಿ.</string>
    <string name="ui_pub_none">ಈ ಜಿಲ್ಲೆಗೆ ಇನ್ನೂ ಮಾಪನಗಳಿಲ್ಲ.</string>
    <string name="ui_pub_last">ಕೊನೆಯ ಪರೀಕ್ಷೆ</string>
    <string name="ui_pub_date">10 ಅಕ್ಟೋಬರ್ 2026</string>
    <string name="ui_pub_safe">ಕುಡಿಯಲು ಸುರಕ್ಷಿತ</string>
    <string name="ui_pub_watch">ಎಚ್ಚರಿಕೆಯಿಂದ ಬಳಸಿ</string>
    <string name="ui_pub_unsafe">ಕುಡಿಯಬೇಡಿ</string>
    <string name="ui_pub_safe_txt">ಎಲ್ಲಾ ಮಾಪನಗಳು ಸುರಕ್ಷಿತ ಮಿತಿಯೊಳಗಿವೆ.</string>
    <string name="ui_pub_watch_txt">ಒಂದು ಮಾಪನ ಕಡಿಮೆ ಇದೆ. ಪರಿಶೀಲನೆ ನಿಗದಿಯಾಗಿದೆ.</string>
    <string name="ui_pub_unsafe_txt">ಒಂದು ಮಾಪನ ಮಿತಿ ಮೀರಿದೆ. ದುರಸ್ತಿ ನಿಗದಿಯಾಗಿದೆ.</string>
    <string name="ui_journey">ನಿಮ್ಮ ನೀರು ಹೇಗೆ ಸುರಕ್ಷಿತವಾಗುತ್ತದೆ</string>
    <string name="ui_j_src">ಮೂಲ</string>
    <string name="ui_j_sump">ಸಂಪ್</string>
    <string name="ui_j_fil">ಫಿಲ್ಟರ್</string>
    <string name="ui_j_tap">ನಿಮ್ಮ ನಲ್ಲಿ</string>
    <string name="ui_rep_title">ಸಮಸ್ಯೆ ವರದಿ ಮಾಡಿ</string>
    <string name="ui_rep_type">ಸಮಸ್ಯೆ</string>
    <string name="ui_rep_sel">ಸಮಸ್ಯೆ ಆಯ್ಕೆಮಾಡಿ</string>
    <string name="ui_ty1">ಕೆಟ್ಟ ರುಚಿ ಅಥವಾ ವಾಸನೆ</string>
    <string name="ui_ty2">ಫಿಲ್ಟರ್ ಕೆಲಸ ಮಾಡುತ್ತಿಲ್ಲ</string>
    <string name="ui_ty3">ಸೋರಿಕೆ</string>
    <string name="ui_ty4">ಇತರೆ</string>
    <string name="ui_rep_desc">ವಿವರಗಳು (ಐಚ್ಛಿಕ)</string>
    <string name="ui_rep_photo">ಫೋಟೋ ಸೇರಿಸಿ</string>
    <string name="ui_rep_photo_on">ಫೋಟೋ ಸೇರಿಸಲಾಗಿದೆ</string>
    <string name="ui_rep_btn">ವರದಿ ಕಳುಹಿಸಿ</string>
    <string name="ui_err_rep">ಸಮಸ್ಯೆ ಆಯ್ಕೆಮಾಡಿ ಮತ್ತು ನಿಮ್ಮ ಹಳ್ಳಿ ನಮೂದಿಸಿ.</string>
    <string name="ui_rep_done">ವರದಿ ಕಳುಹಿಸಲಾಗಿದೆ. ಟಿಕೆಟ್ ಸಂಖ್ಯೆ</string>
    <string name="ui_ticket">ಟಿಕೆಟ್ ಸಂಖ್ಯೆ</string>
    <string name="ui_rs1">ಸ್ವೀಕರಿಸಲಾಗಿದೆ</string>
    <string name="ui_rs2">ಪರಿಶೀಲನೆಯಲ್ಲಿ</string>
    <string name="ui_rs3">ಸರಿಪಡಿಸಲಾಗಿದೆ</string>
    <string name="ui_rep_new">ಇನ್ನೊಂದು ಕಳುಹಿಸಿ</string>
    <string name="ui_acc">ಪ್ರವೇಶಸಾಧ್ಯತೆ</string>
    <string name="ui_acc_big">ದೊಡ್ಡ ಅಕ್ಷರ</string>
    <string name="ui_acc_hc">ಹೆಚ್ಚಿನ ವ್ಯತಿರಿಕ್ತತೆ</string>
    <string name="ui_acc_read">ಈ ಪುಟವನ್ನು ಓದಿ ಹೇಳಿ</string>
    <string name="ui_voice_note">ಧ್ವನಿ ನಿಮ್ಮ ಫೋನ್ ಅನ್ನು ಅವಲಂಬಿಸಿದೆ.</string>
    <string name="ui_speak">ಓದಿ ಹೇಳಿ</string>
    <string name="ui_a_on">ಆನ್</string>
    <string name="ui_a_off">ಆಫ್</string>
    <string name="ui_scan_warn">ನೈಟ್ರೇಟ್ ಮಿತಿ ಮೀರಿದೆ. ಈ ನೀರನ್ನು ಕುಡಿಯಬೇಡಿ. ಪಂಚಾಯತ್‌ಗೆ ತಿಳಿಸಲಾಗಿದೆ.</string>
    <string name="ui_s_safe">ಸುರಕ್ಷಿತ</string>
    <string name="ui_s_low">ಕಡಿಮೆ</string>
    <string name="ui_r_tds">TDS</string>
    <string name="ui_r_ph">pH</string>
    <string name="ui_r_cl">ಕ್ಲೋರಿನ್</string>
    <string name="ui_r_ni">ನೈಟ್ರೇಟ್</string>
    <string name="ui_rate_title">ಸ್ಥಾಪಕರಿಗೆ ರೇಟಿಂಗ್ ನೀಡಿ</string>
    <string name="ui_thanks">ನಿಮ್ಮ ಪ್ರತಿಕ್ರಿಯೆಗೆ ಧನ್ಯವಾದ.</string>
    <string name="ui_view_cert">ಪ್ರಮಾಣಪತ್ರ ನೋಡಿ</string>
    <string name="ui_cert_title">ಜಲ ಸುರಕ್ಷತಾ ಪ್ರಮಾಣಪತ್ರ</string>
    <string name="ui_cert_body">ಫಿಲ್ಟರ್ ಸಾಥಿ ಸಂಪ್ ಸೆನ್ಸರ್ ಮತ್ತು ಫಿಲ್ಟರ್ ಸ್ಥಾಪಿಸಲಾಗಿದೆ ಮತ್ತು ಮಾಹಿತಿ ಕಳುಹಿಸುತ್ತಿದೆ.</string>
    <string name="ui_cert_date">ದಿನಾಂಕ</string>
    <string name="ui_date_val">11 ಅಕ್ಟೋಬರ್ 2026</string>
    <string name="ui_cert_read">ಮಾಪನಗಳು</string>
    <string name="ui_read_val">TDS 312 ppm · pH 7.2</string>
    <string name="ui_cert_inst">ಸ್ಥಾಪಕರು</string>
    <string name="ui_cert_verify">ಪರಿಶೀಲಿಸಲು ಸ್ಕ್ಯಾನ್ ಮಾಡಿ</string>
    <string name="ui_qr_sample">ಮಾದರಿ QR</string>
    <string name="ui_print">ಮುದ್ರಿಸಿ / ಉಳಿಸಿ</string>
    <string name="ui_back">ಟ್ರ್ಯಾಕಿಂಗ್‌ಗೆ ಹಿಂತಿರುಗಿ</string>
    <string name="ui_cov_title">ಜಿಲ್ಲಾ ವ್ಯಾಪ್ತಿ</string>
    <string name="ui_none">ಮನವಿ ಇಲ್ಲ</string>
    <string name="ui_sump_level">ನೇರ ಸಂಪ್ ಮಟ್ಟ</string>
    <string name="ui_s_req">ಮನವಿ</string>
    <string name="ui_s_sch">ನಿಗದಿ</string>
    <string name="ui_s_ins">ಸ್ಥಾಪಿಸಲಾಗಿದೆ</string>
    <string-array name="ui_district_names">
        <item>ಬಾಗಲಕೋಟೆ</item>
        <item>ಬಳ್ಳಾರಿ</item>
        <item>ಬೆಳಗಾವಿ</item>
        <item>ಬೆಂಗಳೂರು ಗ್ರಾಮಾಂತರ</item>
        <item>ಬೆಂಗಳೂರು ದಕ್ಷಿಣ</item>
        <item>ಬೆಂಗಳೂರು ನಗರ</item>
        <item>ಬೀದರ್</item>
        <item>ಚಾಮರಾಜನಗರ</item>
        <item>ಚಿಕ್ಕಬಳ್ಳಾಪುರ</item>
        <item>ಚಿಕ್ಕಮಗಳೂರು</item>
        <item>ಚಿತ್ರದುರ್ಗ</item>
        <item>ದಕ್ಷಿಣ ಕನ್ನಡ</item>
        <item>ದಾವಣಗೆರೆ</item>
        <item>ಧಾರವಾಡ</item>
        <item>ಗದಗ</item>
        <item>ಹಾಸನ</item>
        <item>ಹಾವೇರಿ</item>
        <item>ಕಲಬುರಗಿ</item>
        <item>ಕೊಡಗು</item>
        <item>ಕೋಲಾರ</item>
        <item>ಕೊಪ್ಪಳ</item>
        <item>ಮಂಡ್ಯ</item>
        <item>ಮೈಸೂರು</item>
        <item>ರಾಯಚೂರು</item>
        <item>ಶಿವಮೊಗ್ಗ</item>
        <item>ತುಮಕೂರು</item>
        <item>ಉಡುಪಿ</item>
        <item>ಉತ್ತರ ಕನ್ನಡ</item>
        <item>ವಿಜಯನಗರ</item>
        <item>ವಿಜಯಪುರ</item>
        <item>ಯಾದಗಿರಿ</item>
    </string-array>
</resources>
```


================================================================
PART D (district keys)
FILE: app/src/main/res/values/ui_arrays.xml
================================================================

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string-array name="ui_district_keys" translatable="false">
        <item>Bagalkot</item>
        <item>Ballari</item>
        <item>Belagavi</item>
        <item>Bengaluru Rural</item>
        <item>Bengaluru South</item>
        <item>Bengaluru Urban</item>
        <item>Bidar</item>
        <item>Chamarajanagar</item>
        <item>Chikkaballapura</item>
        <item>Chikkamagaluru</item>
        <item>Chitradurga</item>
        <item>Dakshina Kannada</item>
        <item>Davanagere</item>
        <item>Dharwad</item>
        <item>Gadag</item>
        <item>Hassan</item>
        <item>Haveri</item>
        <item>Kalaburagi</item>
        <item>Kodagu</item>
        <item>Kolar</item>
        <item>Koppal</item>
        <item>Mandya</item>
        <item>Mysuru</item>
        <item>Raichur</item>
        <item>Shivamogga</item>
        <item>Tumakuru</item>
        <item>Udupi</item>
        <item>Uttara Kannada</item>
        <item>Vijayanagara</item>
        <item>Vijayapura</item>
        <item>Yadgir</item>
    </string-array>
</resources>
```


================================================================
PART E (district map view, reference starter, never compiled)
FILE: app/src/main/java/org/filtersaathi/app/ui/widget/DistrictMapView.java
================================================================

```java
package org.filtersaathi.app.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Draws the Karnataka district map from assets/karnataka_districts.json.
 * Reference starter: written without an Android SDK to compile against, so review and adapt it.
 * Java is used on purpose: this is UI-layer code.
 */
public class DistrictMapView extends View {

    public interface Listener {
        void onDistrictSelected(@NonNull String districtKey);
    }

    private static final class District {
        final List<String> keys = new ArrayList<>();   // first key is the polygon's own district
        final List<String> namesEn = new ArrayList<>();
        final List<String> namesKn = new ArrayList<>();
        Path source;
        final Path scaled = new Path();
        final Region region = new Region();
        float labelX, labelY;
        int colorIndex;
        boolean showLabel;
    }

    private final List<District> districts = new ArrayList<>();
    private int[] palette = {0xFFCFE0F7, 0xFFD4EBDD, 0xFFFBE7C0, 0xFFE1D8F2};
    private float viewBoxW = 624f, viewBoxH = 820f, scale = 1f;

    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint selectedStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint haloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Matrix matrix = new Matrix();

    private String selectedKey;
    private boolean kannada;
    private Listener listener;
    @Nullable private Map<String, Integer> fillOverrides; // used by the officer coverage map

    public DistrictMapView(Context context) { this(context, null); }

    public DistrictMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        float d = getResources().getDisplayMetrics().density;
        fillPaint.setStyle(Paint.Style.FILL);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.WHITE);
        strokePaint.setStrokeWidth(1.5f * d);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);
        selectedStroke.setStyle(Paint.Style.STROKE);
        selectedStroke.setColor(0xFFE8801A);
        selectedStroke.setStrokeWidth(2.5f * d);
        selectedStroke.setStrokeJoin(Paint.Join.ROUND);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        haloPaint.setTextAlign(Paint.Align.CENTER);
        haloPaint.setFakeBoldText(true);
        haloPaint.setStyle(Paint.Style.STROKE);
        haloPaint.setStrokeWidth(2.6f * d);
        haloPaint.setStrokeJoin(Paint.Join.ROUND);
        loadFromAssets("karnataka_districts.json");
    }

    private void loadFromAssets(String fileName) {
        try (InputStream in = getContext().getAssets().open(fileName)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
            JSONObject root = new JSONObject(new String(out.toByteArray(), StandardCharsets.UTF_8));
            viewBoxW = (float) root.getDouble("viewBoxWidth");
            viewBoxH = (float) root.getDouble("viewBoxHeight");
            JSONArray pal = root.getJSONArray("palette");
            palette = new int[pal.length()];
            for (int i = 0; i < pal.length(); i++) palette[i] = Color.parseColor(pal.getString(i));
            JSONArray arr = root.getJSONArray("districts");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                District d = new District();
                d.keys.add(o.getString("key"));
                JSONArray also = o.getJSONArray("alsoCovers");
                for (int k = 0; k < also.length(); k++) d.keys.add(also.getString(k));
                JSONArray en = o.getJSONArray("nameEn");
                JSONArray kn = o.getJSONArray("nameKn");
                for (int k = 0; k < en.length(); k++) { d.namesEn.add(en.getString(k)); d.namesKn.add(kn.getString(k)); }
                d.source = PathParser.createPathFromPathData(o.getString("path"));
                d.labelX = (float) o.getDouble("labelX");
                d.labelY = (float) o.getDouble("labelY");
                d.colorIndex = o.getInt("colorIndex");
                d.showLabel = o.getBoolean("showLabel");
                districts.add(d);
            }
        } catch (Exception e) {
            // Leave the map empty if the asset is missing or malformed.
        }
    }

    // ---- public API ----
    public void setListener(@Nullable Listener l) { listener = l; }

    public void setKannada(boolean value) { kannada = value; invalidate(); }

    public void setSelectedKey(@Nullable String key) { selectedKey = key; invalidate(); }

    @Nullable public String getSelectedKey() { return selectedKey; }

    /** Colours by district key. Pass null to go back to the political palette. */
    public void setFillOverrides(@Nullable Map<String, Integer> colors) { fillOverrides = colors; invalidate(); }

    // ---- layout ----
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int w = MeasureSpec.getSize(widthSpec);
        setMeasuredDimension(w, Math.round(w * viewBoxH / viewBoxW));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        scale = Math.min(w / viewBoxW, h / viewBoxH);
        matrix.setScale(scale, scale);
        Region clip = new Region(0, 0, w, h);
        for (District d : districts) {
            d.source.transform(matrix, d.scaled);
            d.region.setPath(d.scaled, clip);
        }
        float sp = getResources().getDisplayMetrics().scaledDensity;
        float size = Math.max(9f * sp, 10f * scale);
        textPaint.setTextSize(size);
        haloPaint.setTextSize(size);
    }

    // ---- drawing ----
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        District selected = null;
        for (District d : districts) {
            if (selectedKey != null && d.keys.contains(selectedKey)) { selected = d; continue; }
            Integer override = fillOverrides == null ? null : fillOverrides.get(d.keys.get(0));
            fillPaint.setColor(override != null ? override : palette[d.colorIndex % palette.length]);
            canvas.drawPath(d.scaled, fillPaint);
            canvas.drawPath(d.scaled, strokePaint);
        }
        if (selected != null) {
            fillPaint.setColor(0xFF0F2A5C);
            canvas.drawPath(selected.scaled, fillPaint);
            canvas.drawPath(selected.scaled, selectedStroke);
        }
        for (District d : districts) {
            if (!d.showLabel) continue;
            boolean on = d == selected;
            haloPaint.setColor(on ? 0xFF0F2A5C : Color.WHITE);
            textPaint.setColor(on ? Color.WHITE : 0xFF17213A);
            String label = kannada ? d.namesKn.get(0) : d.namesEn.get(0);
            float x = d.labelX * scale;
            float y = d.labelY * scale - (textPaint.ascent() + textPaint.descent()) / 2f;
            canvas.drawText(label, x, y, haloPaint);
            canvas.drawText(label, x, y, textPaint);
        }
    }

    // ---- touch: tap selects; tapping a shared outline (Ballari / Vijayanagara) cycles through its districts ----
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) return true;
        if (e.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) e.getX(), y = (int) e.getY();
            for (District d : districts) {
                if (d.region.contains(x, y)) {
                    int i = selectedKey == null ? -1 : d.keys.indexOf(selectedKey);
                    selectedKey = d.keys.get((i + 1) % d.keys.size());
                    invalidate();
                    performClick();
                    if (listener != null) listener.onDistrictSelected(selectedKey);
                    return true;
                }
            }
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean performClick() { return super.performClick(); }
}
```


================================================================
PART F (district outlines, keep as one line)
FILE: app/src/main/assets/karnataka_districts.json
================================================================

```json
{"viewBoxWidth":624,"viewBoxHeight":820,"palette":["#CFE0F7","#D4EBDD","#FBE7C0","#E1D8F2"],"districts":[{"key":"Bidar","alsoCovers":[],"nameEn":["Bidar"],"nameKn":["ಬೀದರ್"],"path":"M435.0 138.6L428.5 138.6L428.5 137.5L426.5 136.9L424.6 138.2L421.6 137.4L417.8 134.1L410.1 133.6L409.2 135.3L405.3 133.5L402.8 127.8L398.7 130.7L393.6 129.6L393.5 128.5L391.9 128.8L389.1 126.9L387.7 128.6L388.8 130.8L384.0 136.0L382.1 135.8L382.9 133.0L380.7 133.3L379.8 132.2L369.1 131.4L367.6 133.2L367.1 129.9L363.0 130.1L361.1 132.9L359.5 132.5L360.8 130.5L359.1 130.4L358.2 128.3L359.6 123.6L362.5 122.5L361.1 118.0L365.9 117.3L368.8 113.2L366.5 110.5L363.5 110.0L363.7 105.5L366.6 105.3L367.3 107.8L370.2 108.5L370.8 106.8L372.9 107.0L373.8 105.6L378.3 106.0L380.1 103.6L381.9 103.8L380.5 93.9L381.4 91.2L385.2 90.7L384.5 86.2L386.7 84.6L382.0 82.5L385.2 75.2L389.1 75.0L390.2 78.1L392.4 78.3L393.4 75.8L396.6 79.1L401.3 78.6L401.9 77.1L400.0 74.3L405.5 72.2L405.2 69.9L407.8 65.0L411.7 65.5L410.3 61.8L412.3 60.7L413.0 56.6L415.1 56.0L414.0 53.5L414.6 51.6L422.2 48.5L422.9 47.2L425.1 49.4L427.2 47.6L427.9 52.8L432.0 53.7L430.1 56.3L430.7 58.5L426.4 61.7L427.2 62.7L431.6 63.3L434.5 62.3L434.5 64.9L437.2 67.4L442.0 65.3L442.1 62.5L447.0 65.0L448.2 64.2L451.5 65.4L448.1 69.3L449.5 71.9L447.2 74.4L449.0 77.9L450.6 78.6L450.8 85.1L445.6 89.1L446.2 91.2L448.9 91.6L449.8 93.7L451.5 92.8L453.7 95.3L455.9 95.1L456.6 98.2L455.9 100.2L454.3 98.4L453.4 99.2L454.2 100.5L452.8 101.3L452.8 105.0L449.5 104.6L450.8 108.3L448.5 108.0L446.3 113.8L443.6 113.0L443.7 114.6L441.4 117.4L447.8 119.5L447.7 121.3L445.5 123.6L436.0 127.0L436.3 133.4Z","labelX":417,"labelY":102.9,"colorIndex":1,"showLabel":true},{"key":"Kalaburagi","alsoCovers":[],"nameEn":["Kalaburagi"],"nameKn":["ಕಲಬುರಗಿ"],"path":"M358.2 128.3L359.1 130.4L360.8 130.5L359.5 132.5L361.1 132.9L363.0 130.1L367.1 129.9L367.6 133.2L369.1 131.4L379.8 132.2L380.7 133.3L382.9 133.0L382.1 135.8L384.0 136.0L388.8 130.8L387.7 128.6L389.1 126.9L391.9 128.8L393.5 128.5L393.6 129.6L398.7 130.7L402.8 127.8L405.3 133.5L409.2 135.3L410.1 133.6L417.8 134.1L421.6 137.4L424.6 138.2L426.5 136.9L428.5 137.5L428.5 138.6L436.9 139.0L440.2 140.3L441.5 143.0L443.6 139.5L445.9 142.0L450.3 140.7L450.6 143.9L459.9 146.3L459.9 150.0L456.2 149.3L456.7 146.8L455.5 149.8L452.5 150.3L452.4 153.1L448.5 152.1L448.5 154.7L444.6 153.4L442.1 154.7L442.2 156.5L444.6 157.3L443.2 159.2L443.8 161.0L439.1 162.7L438.6 161.0L436.1 160.7L436.3 165.2L434.5 169.3L436.6 170.6L433.3 173.0L432.7 175.0L431.5 174.6L428.6 176.6L431.0 177.9L430.0 179.5L427.8 179.4L427.0 183.3L431.3 186.9L437.1 188.4L437.0 190.1L440.5 195.5L441.0 199.0L438.7 200.4L437.7 208.4L436.1 209.0L436.6 212.0L431.8 213.1L430.8 212.2L431.3 210.2L428.3 211.0L425.7 209.6L424.7 206.8L416.7 207.5L414.6 205.9L411.5 205.7L410.0 207.6L408.7 206.0L406.9 206.4L406.3 208.8L399.7 209.8L398.8 206.6L396.7 204.8L394.2 208.0L392.1 207.6L391.9 210.6L393.5 211.2L394.3 213.6L388.0 214.5L388.4 216.8L389.6 217.0L388.3 219.9L384.4 217.7L381.0 219.2L380.1 215.2L382.0 213.6L380.1 212.7L380.1 211.2L376.9 210.9L374.0 214.0L371.5 213.2L368.7 217.0L365.7 216.3L364.2 219.3L364.2 226.0L362.6 225.8L363.2 223.2L360.6 223.0L359.8 221.7L357.6 222.5L357.2 224.6L355.7 224.4L354.8 226.1L355.5 228.8L353.1 227.8L353.1 226.6L351.8 227.0L351.9 224.5L349.1 224.5L344.1 226.4L341.0 226.1L339.6 229.0L337.5 230.2L335.4 230.3L334.5 229.1L333.4 230.0L331.5 229.7L333.3 227.0L332.9 224.7L326.3 224.2L326.9 220.8L330.3 221.1L330.6 219.5L324.4 218.9L326.6 214.8L332.2 215.6L332.2 217.0L336.4 216.9L335.2 213.9L333.1 213.8L332.6 212.3L332.6 210.7L335.9 210.2L335.2 207.5L332.7 205.9L332.1 207.6L329.1 206.7L329.2 202.3L332.9 201.9L333.3 200.3L332.2 189.5L329.0 192.6L328.0 192.3L328.2 189.7L325.4 187.8L328.7 184.9L325.1 185.2L326.1 182.4L324.8 180.1L322.5 182.0L316.7 182.4L315.4 184.1L313.3 184.1L311.0 180.5L307.7 180.4L301.6 174.2L297.6 173.0L298.7 169.4L295.2 167.2L296.1 164.6L300.7 161.0L305.1 163.7L305.4 168.1L306.5 168.4L307.2 166.4L305.9 162.9L311.9 161.6L311.8 160.5L313.2 160.2L314.1 162.1L316.3 162.1L316.7 164.8L320.9 165.0L320.5 163.4L323.7 162.7L324.5 166.9L327.5 166.9L328.9 163.5L330.5 163.1L330.1 160.9L325.5 160.3L326.3 154.1L322.5 154.4L322.2 150.6L326.7 148.5L323.7 148.2L323.8 146.1L325.9 145.4L325.5 143.5L323.2 143.0L322.3 137.3L331.2 136.5L332.2 132.2L333.8 132.7L334.6 131.1L338.3 130.6L338.1 124.9L341.8 124.9L341.7 120.2L346.7 120.1L346.5 125.9L347.7 125.9L351.0 119.0L352.7 120.7L352.9 123.5L355.2 124.6L356.0 127.8Z","labelX":367.9,"labelY":172.7,"colorIndex":0,"showLabel":true},{"key":"Belagavi","alsoCovers":[],"nameEn":["Belagavi"],"nameKn":["ಬೆಳಗಾವಿ"],"path":"M97.8 341.5L94.8 339.6L98.6 338.4L99.2 336.2L98.2 334.3L102.2 331.1L106.2 330.3L107.0 327.9L109.5 327.3L108.9 330.0L110.1 331.8L113.0 330.7L114.9 332.0L117.0 331.3L118.6 328.8L120.9 329.7L122.5 327.9L123.3 325.7L122.4 324.1L124.7 320.8L120.1 320.2L122.7 318.1L123.2 316.0L125.2 320.2L127.0 320.1L125.0 314.9L129.5 309.5L132.8 300.8L132.7 299.7L129.4 298.7L123.7 301.1L124.1 299.2L122.5 299.3L123.6 296.7L125.5 296.5L125.4 295.5L129.2 293.2L134.8 295.3L134.5 289.5L137.0 284.4L136.8 281.2L134.5 278.7L133.4 279.7L132.0 278.3L128.2 277.8L127.9 275.3L124.6 275.8L123.7 277.5L122.3 274.4L120.9 274.1L121.3 276.0L118.8 276.4L118.4 275.1L120.6 273.6L118.8 272.0L119.2 270.9L117.9 270.7L117.8 269.2L119.0 269.0L118.0 264.9L119.9 263.6L121.6 266.5L122.7 262.4L119.8 262.8L119.5 257.2L115.2 256.3L115.3 251.9L112.3 254.5L110.9 252.8L114.0 251.9L112.6 248.3L113.3 247.4L114.5 249.9L116.4 249.4L116.8 252.0L117.8 251.9L117.8 246.7L120.3 247.4L121.9 246.2L122.1 248.4L124.3 249.4L126.0 243.7L133.2 241.2L132.0 237.0L133.4 235.2L135.4 238.8L141.0 238.2L140.9 242.4L143.3 243.5L142.2 245.8L143.7 246.7L149.8 243.6L150.7 240.8L155.5 241.0L152.1 237.9L153.3 235.0L155.7 233.9L155.6 230.1L160.1 229.5L164.1 226.1L171.5 225.0L172.0 223.8L173.6 224.8L174.0 223.7L179.1 223.8L177.3 214.4L183.2 212.5L183.5 209.4L180.4 208.5L179.4 206.3L180.8 205.8L183.6 207.6L183.6 205.4L186.2 205.0L189.9 205.8L189.8 207.5L196.3 205.0L197.0 209.0L199.1 209.0L201.1 213.1L206.4 213.0L205.7 216.4L209.0 216.6L210.3 216.4L211.4 213.8L214.1 214.4L215.8 212.5L216.7 210.0L215.6 204.5L221.9 204.6L221.3 213.6L227.3 214.3L225.6 218.4L226.2 224.8L224.4 225.1L224.0 229.9L216.6 229.9L214.2 232.2L213.5 241.2L209.7 240.2L204.5 242.0L203.2 243.6L203.7 248.8L198.9 251.8L194.5 245.3L194.0 248.3L188.0 248.4L186.1 252.4L186.0 257.3L188.9 256.8L190.1 259.4L195.1 259.0L194.6 262.5L191.2 263.1L190.8 264.3L191.6 271.5L195.0 270.1L203.4 271.2L202.3 275.3L211.0 277.7L210.8 282.9L216.2 283.8L216.9 286.8L215.2 292.3L220.5 292.8L223.2 291.4L223.3 292.8L228.3 293.0L229.0 291.2L229.4 292.8L231.8 292.8L230.6 295.2L232.4 299.4L229.5 300.1L230.4 304.0L231.7 304.6L230.3 313.2L232.0 313.5L231.7 316.1L233.3 317.4L233.7 324.0L231.6 328.2L229.2 327.5L230.3 321.5L225.2 321.2L224.2 325.4L221.8 325.4L221.7 323.0L220.2 324.8L218.0 324.6L219.5 331.7L219.1 333.3L217.6 333.6L217.0 332.1L215.1 332.2L215.5 337.7L216.6 337.4L215.4 337.8L215.0 341.8L212.5 342.0L211.2 338.6L207.8 338.4L207.8 341.8L198.2 339.4L196.6 346.1L193.0 343.5L193.3 338.3L190.1 339.6L189.2 342.3L183.8 341.2L183.7 344.1L177.3 343.6L170.0 345.9L171.1 349.6L166.8 352.3L168.4 356.9L165.6 358.9L164.7 361.7L161.8 357.8L159.6 358.6L161.3 361.3L159.8 364.6L160.9 366.8L156.8 366.9L151.7 369.0L148.1 372.3L141.0 366.2L134.8 368.4L132.9 366.3L132.1 367.6L129.1 368.1L127.7 360.2L123.8 359.4L120.5 355.6L114.0 355.0L114.0 352.6L110.7 350.4L112.6 345.3L110.6 344.2L111.4 341.2L109.8 339.7L106.6 341.6L104.2 338.3L102.5 341.3Z","labelX":171.2,"labelY":304.8,"colorIndex":1,"showLabel":true},{"key":"Yadgir","alsoCovers":[],"nameEn":["Yadgir"],"nameKn":["ಯಾದಗಿರಿ"],"path":"M335.5 230.3L339.6 229.0L341.0 226.1L344.1 226.4L349.1 224.5L351.9 224.5L351.8 227.0L353.1 226.6L353.1 227.8L355.5 228.8L354.8 226.1L355.7 224.4L357.2 224.6L357.6 222.5L359.8 221.7L360.6 223.0L363.2 223.2L362.6 225.8L364.2 226.0L364.2 219.3L365.7 216.3L368.7 217.0L371.5 213.2L374.0 214.0L376.9 210.9L380.1 211.2L380.1 212.7L382.0 213.6L380.1 215.2L381.0 219.2L384.4 217.7L388.3 219.9L389.6 217.0L388.4 216.8L388.0 214.5L394.3 213.6L393.5 211.2L391.9 210.6L392.1 207.6L394.2 208.0L396.7 204.8L398.8 206.6L399.7 209.8L406.3 208.8L406.9 206.4L408.7 206.0L410.0 207.6L411.5 205.7L414.6 205.9L416.7 207.5L424.7 206.8L425.7 209.6L428.3 211.0L431.3 210.2L430.8 212.2L431.8 213.1L436.8 212.0L436.4 217.8L438.4 222.2L437.4 223.0L434.7 222.1L435.0 224.9L433.3 229.2L434.6 230.2L438.1 229.7L437.7 232.7L437.1 233.9L432.5 234.7L434.6 237.4L438.3 236.7L436.1 240.2L437.5 240.9L436.9 243.5L434.7 243.3L432.8 245.3L429.9 245.1L431.0 247.1L432.6 247.3L432.3 250.6L428.5 251.0L427.5 253.6L420.4 252.8L419.9 254.9L414.6 255.3L419.3 262.0L417.0 262.5L407.7 259.5L405.9 255.3L399.4 253.1L395.9 250.4L391.4 252.5L391.1 247.7L388.9 246.0L387.0 247.0L387.2 252.0L381.1 254.0L376.9 253.5L376.4 257.0L373.9 259.7L368.3 261.1L365.8 259.9L364.1 262.0L350.7 269.1L337.2 274.2L322.3 284.5L324.4 280.2L323.9 275.8L319.0 275.2L319.4 271.4L318.3 270.2L320.5 268.7L319.7 264.9L320.1 263.3L322.4 263.9L322.3 260.1L332.8 259.0L332.7 250.5L329.7 237.4L331.8 231.9Z","labelX":409.3,"labelY":232,"colorIndex":1,"showLabel":true},{"key":"Bagalkot","alsoCovers":[],"nameEn":["Bagalkot"],"nameKn":["ಬಾಗಲಕೋಟೆ"],"path":"M233.0 316.9L231.7 316.1L232.0 313.5L230.3 313.2L231.7 304.6L230.4 304.0L229.5 300.1L232.4 299.4L230.6 295.2L231.8 292.8L229.4 292.8L229.0 291.2L228.3 293.0L223.3 292.8L223.2 291.4L220.5 292.8L215.2 292.3L216.9 286.8L216.2 283.8L210.8 282.9L211.0 277.7L202.3 275.3L203.4 271.2L195.0 270.1L191.6 271.5L190.8 264.3L191.2 263.1L194.6 262.5L195.1 259.0L190.1 259.4L188.9 256.8L186.0 257.3L185.9 256.0L188.0 248.4L194.0 248.3L194.5 245.3L198.9 251.8L203.7 248.8L203.2 243.6L204.5 242.0L209.7 240.2L213.5 241.2L214.2 232.2L216.6 229.9L224.0 229.9L224.4 225.1L230.0 224.4L237.1 226.8L236.8 232.0L231.6 231.7L234.1 239.6L237.3 242.7L235.1 245.8L236.8 246.6L236.5 251.5L233.4 251.7L234.2 255.2L231.1 259.7L232.7 261.5L236.6 261.1L237.9 257.3L241.5 256.0L243.2 257.1L245.0 261.7L248.0 258.1L251.5 258.3L260.3 263.9L269.4 266.1L284.2 273.7L291.3 273.4L295.3 277.4L294.5 281.0L295.5 283.1L302.8 285.9L305.9 285.6L312.8 287.6L312.4 291.2L313.6 291.5L314.6 294.7L315.8 293.4L322.4 295.3L321.9 296.8L314.7 295.7L314.9 297.2L319.4 297.2L320.3 304.9L318.9 305.0L318.9 306.1L321.9 311.9L320.3 313.1L317.6 312.4L317.5 315.1L315.2 314.7L314.7 313.1L312.0 313.0L311.6 311.1L309.0 310.8L308.9 311.8L304.9 313.2L303.4 315.9L300.6 316.9L298.9 315.4L294.5 314.7L296.7 303.8L287.6 304.4L286.0 306.4L286.1 319.4L281.4 318.0L279.9 314.1L276.2 312.1L267.2 312.8L265.7 315.2L271.2 315.4L272.0 317.3L271.2 319.0L273.3 318.4L273.1 321.9L268.4 319.9L266.4 320.8L266.3 324.1L262.0 324.5L256.5 322.8L255.8 323.7L255.4 322.1L253.8 323.1L252.4 321.6L251.3 322.7L249.9 322.2L249.8 323.7L246.8 322.5L245.7 323.3L244.7 321.9L243.2 323.3L243.7 322.5L241.0 322.3L237.7 318.4Z","labelX":256.3,"labelY":289.9,"colorIndex":2,"showLabel":true},{"key":"Raichur","alsoCovers":[],"nameEn":["Raichur"],"nameKn":["ರಾಯಚೂರು"],"path":"M322.3 284.5L337.2 274.2L350.7 269.1L364.1 262.0L365.8 259.9L368.3 261.1L373.9 259.7L376.4 257.0L376.9 253.5L381.1 254.0L387.2 252.0L387.0 247.0L388.9 246.0L391.1 247.7L391.4 252.5L395.9 250.4L399.4 253.1L405.9 255.3L407.7 259.5L432.1 266.3L439.7 264.5L450.4 268.9L450.7 274.1L447.2 273.3L445.4 274.3L444.5 276.9L441.3 276.0L439.8 278.1L440.0 280.9L442.4 281.0L440.9 281.7L439.4 287.8L439.8 292.4L442.1 293.6L440.1 295.9L441.5 296.6L440.2 301.1L442.2 304.1L442.1 312.5L433.6 310.3L428.5 311.2L415.3 308.6L405.5 310.3L397.4 314.4L392.6 322.0L384.3 329.9L381.6 336.5L373.3 344.0L373.3 350.7L370.9 352.4L365.6 350.4L364.7 348.2L358.5 347.4L357.6 345.6L357.7 344.1L359.0 344.3L358.7 341.9L356.6 342.8L356.8 340.8L349.4 339.2L349.2 336.5L342.6 336.5L340.4 334.7L338.5 335.7L334.0 332.8L333.2 327.6L330.5 326.3L334.4 321.1L331.1 318.7L331.9 316.5L326.7 315.2L326.5 313.6L324.5 315.1L324.3 319.2L322.9 318.4L323.6 315.4L320.7 314.9L320.5 316.0L318.0 316.2L317.2 315.1L317.6 312.4L320.3 313.1L321.9 311.9L318.9 306.1L318.9 305.0L320.3 304.9L319.4 297.2L314.9 297.2L314.7 295.7L321.9 296.8L322.4 295.3L315.8 293.4L314.6 294.7L313.6 291.5L312.4 291.2L312.7 288.1L317.1 288.9L318.9 285.5Z","labelX":363.7,"labelY":302,"colorIndex":0,"showLabel":true},{"key":"Koppal","alsoCovers":[],"nameEn":["Koppal"],"nameKn":["ಕೊಪ್ಪಳ"],"path":"M273.1 321.8L273.3 318.4L271.2 319.0L272.0 317.3L271.2 315.4L265.7 315.2L267.2 312.8L276.2 312.1L279.9 314.1L281.4 318.0L286.1 319.4L286.0 306.4L287.6 304.4L296.7 303.8L294.5 314.7L298.9 315.4L300.6 316.9L303.4 315.9L304.9 313.2L308.9 311.8L309.0 310.8L311.6 311.1L312.0 313.0L314.7 313.1L315.2 314.7L318.0 316.2L320.5 316.0L320.7 314.9L323.6 315.4L322.9 318.4L324.3 319.2L324.5 315.1L326.5 313.6L326.7 315.2L331.9 316.5L331.1 318.7L334.4 321.1L330.5 326.3L333.2 327.6L334.0 332.8L338.5 335.7L340.4 334.7L342.6 336.5L349.2 336.5L349.4 339.2L356.8 340.8L356.6 342.8L358.7 341.9L359.0 344.3L357.7 344.1L357.6 345.6L358.5 347.4L364.7 348.2L365.6 350.4L370.9 352.4L371.0 356.0L368.9 357.4L360.6 358.6L356.3 362.7L350.8 363.4L348.7 366.6L344.9 368.3L338.7 373.7L325.6 375.5L322.5 382.3L319.4 383.1L318.2 386.5L313.9 389.9L310.9 389.3L312.1 385.6L307.7 385.3L304.9 387.4L304.5 389.4L300.0 389.1L298.2 392.9L292.2 394.7L286.5 393.9L283.7 395.4L283.4 391.8L281.0 390.8L277.4 379.4L277.2 372.8L278.8 372.7L278.9 367.6L275.0 367.1L274.3 363.3L270.8 362.5L269.3 358.8L270.8 357.9L270.3 356.6L274.5 356.0L275.1 347.4L278.3 346.2L277.2 339.8L275.6 339.3L276.8 332.5L278.2 331.1L279.6 331.8L280.7 333.7L280.1 336.7L282.6 337.1L282.9 338.9L285.3 338.6L285.4 340.2L289.8 340.4L290.1 338.5L287.0 339.9L287.1 337.1L289.1 334.8L291.0 335.5L292.9 334.5L292.6 329.2L288.1 328.0L287.1 325.7L284.7 327.6L281.5 326.9Z","labelX":314.2,"labelY":350.5,"colorIndex":3,"showLabel":true},{"key":"Gadag","alsoCovers":[],"nameEn":["Gadag"],"nameKn":["ಗದಗ"],"path":"M216.6 337.4L215.5 337.7L215.1 332.2L217.0 332.1L217.6 333.6L219.1 333.3L219.5 331.7L218.0 324.6L220.2 324.8L221.7 323.0L221.8 325.4L224.2 325.4L225.2 321.2L230.3 321.5L229.2 327.5L231.6 328.2L233.7 324.0L233.0 316.9L237.7 318.4L241.0 322.3L243.7 322.5L243.2 323.3L244.7 321.9L245.7 323.3L246.8 322.5L249.8 323.7L249.9 322.2L251.3 322.7L252.4 321.6L253.8 323.1L255.4 322.1L255.8 323.7L256.5 322.8L262.0 324.5L266.3 324.1L266.4 320.8L268.4 319.9L281.5 326.9L284.7 327.6L287.1 325.7L288.1 328.0L292.6 329.2L292.9 334.5L291.0 335.5L289.1 334.8L287.1 337.1L287.0 339.9L290.1 338.5L289.8 340.4L285.4 340.2L285.3 338.6L282.9 338.9L282.6 337.1L280.1 336.7L280.7 333.7L279.6 331.8L278.2 331.1L276.8 332.5L275.6 339.3L277.2 339.8L278.3 346.2L275.1 347.4L274.5 356.0L270.3 356.6L270.8 357.9L269.3 358.8L270.8 362.5L274.3 363.3L275.0 367.1L278.9 367.6L278.8 372.7L277.2 372.8L277.4 379.4L281.0 390.8L283.4 391.8L283.7 395.4L278.7 396.1L277.0 398.6L279.4 401.9L278.9 403.8L275.9 406.2L273.0 405.2L271.7 406.9L269.1 407.4L266.8 409.7L266.3 412.3L262.4 415.7L258.3 415.3L257.4 412.8L251.3 410.8L247.9 414.7L244.6 412.1L242.1 412.2L241.3 406.3L229.5 403.5L229.5 401.0L231.2 398.5L228.1 391.9L227.4 392.5L227.7 388.8L232.8 388.1L231.9 386.1L229.4 385.3L229.5 382.4L228.1 382.6L228.0 381.2L229.5 381.1L228.4 380.8L229.1 378.5L232.8 378.2L233.6 374.0L232.3 369.4L235.4 368.4L237.2 371.2L237.5 360.6L235.1 358.2L235.4 355.7L236.1 354.1L240.9 353.5L243.5 342.0L238.2 342.0L236.7 336.2L234.7 336.4L234.9 338.1L232.6 338.3L229.1 341.1L224.0 339.7L223.7 342.2L221.3 341.8L221.4 339.2Z","labelX":255.4,"labelY":386.3,"colorIndex":0,"showLabel":true},{"key":"Ballari","alsoCovers":["Vijayanagara"],"nameEn":["Ballari","Vijayanagara"],"nameKn":["ಬಳ್ಳಾರಿ","ವಿಜಯನಗರ"],"path":"M370.9 352.4L373.3 350.7L373.3 344.0L381.6 336.5L384.3 329.9L392.6 322.0L396.7 323.1L396.9 327.2L394.7 332.2L398.1 333.6L398.9 334.8L398.0 336.9L403.1 340.0L402.4 343.7L398.5 340.7L393.2 343.3L393.1 357.2L387.5 356.3L387.6 358.3L393.3 364.2L393.0 367.5L394.9 369.0L394.3 372.0L396.7 373.9L396.5 375.5L401.5 374.9L402.1 379.0L405.5 379.2L405.0 381.3L407.0 382.3L404.7 382.9L402.8 386.1L405.5 387.3L406.6 393.4L404.3 393.3L404.0 394.9L406.0 395.2L405.5 397.4L403.3 399.3L401.5 406.4L398.7 406.7L398.4 409.3L394.5 408.9L395.1 406.5L390.5 406.4L388.6 408.3L381.8 406.2L377.6 406.8L376.5 403.7L369.8 399.9L367.4 401.7L368.8 407.8L367.1 410.7L363.1 411.5L361.9 406.5L360.0 406.4L359.8 408.3L356.9 407.9L358.5 412.2L361.2 412.8L361.3 415.4L359.3 417.6L355.6 416.1L356.1 418.8L357.3 419.3L357.4 421.8L359.4 421.7L361.0 424.1L360.8 427.0L363.9 429.0L360.1 429.1L356.4 431.6L354.4 435.5L354.0 440.1L350.4 442.7L350.6 444.7L349.3 444.6L349.2 446.3L350.7 446.9L349.1 448.7L349.6 449.7L348.0 450.1L342.8 456.1L331.6 453.9L332.5 450.0L328.6 449.1L328.2 446.8L326.6 447.0L327.1 445.4L323.4 445.0L322.5 442.7L317.0 443.0L314.5 441.7L312.0 446.8L307.3 441.4L309.0 437.3L308.2 434.3L307.0 434.7L305.8 432.5L303.1 431.7L303.5 428.5L308.4 428.8L308.0 426.1L304.8 426.0L304.3 424.5L301.9 424.6L301.7 423.7L296.0 423.7L295.4 422.1L297.2 419.0L293.8 418.8L293.2 417.2L289.9 420.8L289.7 419.9L288.4 420.6L287.1 419.6L286.3 420.6L285.2 419.5L275.8 423.8L275.4 425.2L273.8 424.3L273.8 422.2L271.6 422.2L270.0 420.5L268.5 423.7L269.3 427.0L271.8 429.9L270.9 433.1L268.2 434.2L265.6 431.8L264.3 433.5L264.2 437.8L260.0 439.4L258.7 438.8L260.5 433.9L254.6 430.2L257.2 425.0L261.6 424.3L262.2 421.8L261.1 420.7L257.8 421.3L256.5 416.2L262.4 415.7L266.3 412.3L266.8 409.7L269.1 407.4L271.7 406.9L273.0 405.2L275.9 406.2L278.9 403.8L279.4 401.9L277.0 398.6L278.7 396.1L286.5 393.9L292.2 394.7L298.2 392.9L300.0 389.1L304.5 389.4L304.9 387.4L307.7 385.3L312.1 385.6L310.9 389.3L313.9 389.9L318.2 386.5L319.4 383.1L322.5 382.3L325.6 375.5L338.7 373.7L344.9 368.3L348.7 366.6L350.8 363.4L356.3 362.7L360.6 358.6L368.9 357.4L371.0 356.0Z","labelX":330.2,"labelY":410.6,"colorIndex":1,"showLabel":true},{"key":"Dharwad","alsoCovers":[],"nameEn":["Dharwad"],"nameKn":["ಧಾರವಾಡ"],"path":"M160.8 366.5L159.8 364.6L161.3 361.3L159.6 358.6L161.8 357.8L164.7 361.7L165.6 358.9L168.4 356.9L166.8 352.3L171.1 349.6L170.0 345.9L177.3 343.6L183.7 344.1L183.8 341.2L189.2 342.3L190.1 339.6L193.3 338.3L193.0 343.5L196.6 346.1L198.2 339.4L207.8 341.8L207.8 338.4L211.2 338.6L212.5 342.0L215.0 341.8L215.4 337.8L217.0 337.3L221.4 339.2L221.3 341.8L223.7 342.2L224.0 339.7L229.1 341.1L232.6 338.3L234.9 338.1L234.7 336.4L236.7 336.2L238.2 342.0L243.5 342.0L240.9 353.5L236.1 354.1L235.4 355.7L235.1 358.2L237.5 360.6L237.2 371.2L235.4 368.4L232.3 369.4L233.6 374.0L232.8 378.2L229.1 378.5L228.4 380.8L229.5 381.1L228.0 381.2L228.1 382.6L229.5 382.4L229.4 385.3L231.9 386.1L232.8 388.1L227.7 388.8L227.4 392.5L228.1 391.9L231.2 398.5L229.5 401.0L229.5 403.7L224.7 404.7L222.1 402.9L221.0 400.9L222.8 399.4L221.4 398.0L221.4 393.0L218.4 393.6L218.6 395.4L213.7 397.5L212.9 399.7L211.5 398.5L206.3 399.7L203.3 399.3L203.3 398.4L201.4 399.0L200.7 397.4L202.7 394.6L201.5 393.1L200.6 394.7L197.8 395.1L198.7 397.6L195.1 398.4L195.5 402.1L193.1 402.4L192.7 401.2L188.5 402.8L181.2 403.0L181.2 404.5L178.8 405.9L177.9 404.6L175.9 404.6L174.9 402.2L176.3 399.8L173.0 399.1L170.3 395.7L174.8 389.6L174.4 387.2L178.1 383.6L177.5 380.3L175.9 377.7L174.4 378.6L174.7 376.7L172.4 374.1L169.8 374.2L169.1 369.5L166.4 370.2L163.4 368.0L163.6 366.1Z","labelX":206.1,"labelY":369,"colorIndex":3,"showLabel":true},{"key":"Uttara Kannada","alsoCovers":[],"nameEn":["Uttara Kannada"],"nameKn":["ಉತ್ತರ ಕನ್ನಡ"],"path":"M114.0 355.0L120.5 355.6L123.8 359.4L127.7 360.2L129.1 368.1L132.1 367.6L132.9 366.3L134.8 368.4L141.0 366.2L148.1 372.3L151.7 369.0L156.8 366.9L163.6 366.1L163.4 368.0L166.4 370.2L169.1 369.5L169.8 374.2L172.4 374.1L174.7 376.7L174.4 378.6L175.9 377.7L177.5 380.3L178.1 383.6L174.4 387.2L174.8 389.6L170.3 395.7L173.0 399.1L176.3 399.8L174.9 402.2L175.9 404.6L177.9 404.6L178.8 405.9L181.2 404.5L181.2 403.0L188.5 402.8L192.7 401.2L193.1 402.4L195.5 402.1L194.3 408.3L195.7 414.5L197.3 414.5L197.7 416.8L194.1 418.5L194.2 426.8L196.5 429.8L192.9 429.4L194.3 432.7L191.4 433.7L192.0 436.8L188.8 439.3L190.5 440.6L190.4 445.1L192.4 446.7L189.9 450.8L189.6 455.4L193.5 457.9L191.0 459.3L191.3 462.4L186.2 465.3L186.3 462.3L184.4 460.3L184.9 457.3L182.6 457.0L180.7 460.5L177.2 459.4L176.0 464.6L179.2 466.7L176.1 466.4L174.5 467.9L174.9 469.2L178.8 469.6L182.0 473.0L181.1 477.3L184.6 479.5L184.9 481.4L179.4 481.8L178.0 485.2L179.8 485.9L180.7 489.4L176.1 492.2L174.8 492.3L174.4 489.6L172.4 492.7L170.2 490.8L166.1 491.1L163.3 489.9L159.8 487.2L158.4 490.5L161.6 500.0L153.3 495.6L149.2 497.9L149.6 499.8L151.2 500.0L151.3 502.9L152.3 503.0L150.4 505.0L152.8 508.8L152.3 510.8L154.5 511.3L153.3 516.8L151.1 519.4L151.5 521.2L149.7 520.7L146.4 522.1L146.2 521.1L144.9 522.7L143.5 520.2L141.5 520.4L138.1 516.3L133.4 495.2L130.6 491.4L125.8 471.0L124.6 470.7L121.8 461.5L117.3 460.2L117.7 457.6L113.2 449.1L114.4 449.1L114.8 447.1L113.4 441.4L111.8 440.6L112.5 439.4L109.6 436.4L106.6 437.6L106.3 439.3L103.8 436.1L101.2 436.5L101.8 435.1L100.2 433.1L97.9 433.6L95.2 430.5L97.3 430.4L97.3 431.3L98.5 430.4L98.5 426.7L94.5 420.4L98.4 417.8L102.2 419.2L101.4 415.9L102.4 414.3L104.0 414.5L106.2 417.8L109.8 413.5L111.4 414.3L112.6 412.2L111.5 410.2L113.0 409.8L115.9 405.6L113.5 399.7L114.7 398.0L114.7 394.3L118.0 390.4L116.4 386.4L112.2 384.6L111.3 382.8L113.8 380.2L118.9 380.2L119.9 379.0L118.0 375.4L118.6 371.3L113.8 368.8L113.8 362.8L111.3 358.3Z","labelX":149.9,"labelY":432.3,"colorIndex":0,"showLabel":true},{"key":"Haveri","alsoCovers":[],"nameEn":["Haveri"],"nameKn":["ಹಾವೇರಿ"],"path":"M229.5 401.0L229.5 403.5L241.3 406.3L242.1 412.2L244.6 412.1L247.9 414.7L251.3 410.8L257.4 412.8L258.3 415.3L256.5 416.2L257.8 421.3L261.1 420.7L262.2 421.8L261.6 424.3L257.2 425.0L254.6 430.2L260.5 433.9L258.7 438.8L260.0 439.4L264.2 437.8L264.7 439.0L262.1 442.4L264.7 444.0L268.1 443.4L269.4 446.5L268.8 452.1L270.2 455.6L268.3 458.1L268.1 461.3L264.1 460.9L259.8 465.3L257.2 463.3L254.3 467.5L251.8 466.1L251.2 468.3L252.4 469.7L250.4 478.8L251.5 481.6L249.2 481.8L246.3 485.7L241.3 485.8L241.4 484.5L238.2 482.6L238.0 478.8L235.5 478.8L235.6 481.6L222.6 474.2L223.7 472.0L225.5 472.1L226.0 469.4L221.7 469.2L222.3 464.0L219.6 461.0L214.4 460.9L214.4 459.5L217.4 458.2L214.1 454.1L211.9 454.2L211.6 456.7L210.1 455.3L210.3 453.6L208.3 454.9L205.9 453.4L205.6 452.2L208.0 452.2L208.1 448.0L206.3 449.6L204.0 448.4L203.5 450.1L197.3 446.4L192.4 446.7L190.4 445.1L190.5 440.6L189.0 440.4L188.8 439.3L192.0 436.8L191.4 433.7L194.3 432.7L192.9 429.4L196.5 429.8L194.2 426.8L194.1 418.5L197.7 416.8L197.3 414.5L195.7 414.5L194.3 408.3L195.1 398.4L198.7 397.6L197.8 395.1L200.6 394.7L201.5 393.1L202.7 394.6L200.7 397.4L201.4 399.0L203.3 398.4L203.3 399.3L206.3 399.7L211.5 398.5L212.9 399.7L213.7 397.5L218.6 395.4L218.4 393.6L221.4 393.0L221.4 398.0L222.8 399.4L221.0 400.9L222.1 402.9L224.7 404.7L229.5 403.7Z","labelX":230.2,"labelY":434.3,"colorIndex":2,"showLabel":true},{"key":"Chitradurga","alsoCovers":[],"nameEn":["Chitradurga"],"nameKn":["ಚಿತ್ರದುರ್ಗ"],"path":"M340.5 456.1L342.8 456.1L348.0 450.1L349.6 449.7L349.1 448.7L350.7 446.9L349.2 446.3L349.3 444.6L350.6 444.7L350.4 442.7L354.0 440.1L354.4 435.5L356.4 431.6L360.1 429.1L363.9 429.0L360.8 427.0L361.0 424.1L359.4 421.7L357.4 421.8L357.3 419.3L356.1 418.8L355.6 416.1L359.3 417.6L361.3 415.4L361.2 412.8L358.5 412.2L356.9 407.9L359.8 408.3L360.0 406.4L361.9 406.5L363.1 411.5L365.8 411.1L366.4 412.5L371.6 413.1L372.7 414.5L376.6 413.1L376.3 420.9L374.4 421.6L375.6 423.7L375.3 426.4L374.0 427.1L375.3 430.0L373.6 429.8L373.5 431.9L368.0 432.4L370.1 437.0L367.3 443.4L366.2 451.7L367.9 452.3L370.4 459.2L372.9 459.2L375.4 461.4L376.7 465.2L380.0 463.6L387.5 464.0L386.0 467.6L384.5 467.6L384.3 473.0L381.5 470.8L378.4 473.4L378.4 478.4L380.3 481.1L384.5 482.1L385.7 484.0L382.5 486.2L383.9 489.2L388.6 489.0L390.7 490.2L390.4 494.1L392.4 496.1L386.1 495.9L384.8 494.1L384.2 496.7L379.4 497.3L378.5 500.4L381.2 501.4L382.5 500.5L384.8 505.0L382.3 508.9L381.0 508.2L378.8 509.9L377.2 514.0L373.6 513.0L372.2 515.5L374.7 517.0L374.9 518.8L370.9 521.6L371.4 523.8L366.2 522.9L364.6 523.7L365.0 524.8L362.0 525.6L366.4 532.2L366.6 536.4L366.2 537.8L364.6 538.0L362.8 537.6L362.1 536.0L359.4 538.9L358.1 547.4L356.9 544.5L352.5 545.0L351.6 542.9L346.4 544.9L347.1 547.5L342.4 550.0L342.8 551.6L339.4 554.6L335.4 555.0L334.8 557.9L332.3 557.6L330.6 559.2L329.2 557.5L327.5 559.4L326.5 557.6L323.3 559.4L322.4 557.3L319.1 558.1L316.4 556.4L317.7 552.0L315.4 551.8L311.1 548.3L310.9 545.0L306.3 544.7L303.7 543.0L301.8 534.4L300.3 534.7L298.6 529.2L291.5 525.6L293.2 521.0L295.3 521.1L296.0 516.8L293.0 516.3L294.2 505.6L293.4 504.7L292.0 505.5L291.2 503.1L293.3 498.7L295.1 498.2L294.8 491.6L296.7 489.2L302.3 488.1L299.4 482.4L300.4 481.0L304.5 481.3L304.4 474.3L302.5 470.1L306.1 467.9L306.2 466.6L309.7 466.9L310.8 468.5L312.8 467.6L315.1 468.3L317.2 472.5L327.5 470.8L331.7 468.9L331.6 467.7L335.3 467.5L335.5 461.8L337.8 462.0L342.7 458.0Z","labelX":331.4,"labelY":508,"colorIndex":2,"showLabel":true},{"key":"Davanagere","alsoCovers":[],"nameEn":["Davanagere"],"nameKn":["ದಾವಣಗೆರೆ"],"path":"M264.2 437.8L264.3 433.5L265.6 431.8L268.2 434.2L271.0 432.9L271.8 429.9L269.3 427.0L268.5 423.7L270.0 420.5L271.6 422.2L273.8 422.2L273.8 424.3L275.4 425.2L275.8 423.8L285.2 419.5L286.3 420.6L287.1 419.6L288.4 420.6L289.7 419.9L289.9 420.8L293.2 417.2L293.8 418.8L297.2 419.0L295.4 422.1L296.0 423.7L301.7 423.7L301.9 424.6L304.3 424.5L304.8 426.0L308.0 426.1L308.4 428.8L303.5 428.5L303.1 431.7L305.8 432.5L307.0 434.7L308.2 434.3L309.0 437.3L307.3 441.4L312.0 446.8L314.5 441.7L317.0 443.0L322.5 442.7L323.4 445.0L327.1 445.4L326.6 447.0L328.2 446.8L328.6 449.1L332.5 450.0L331.6 453.9L339.9 455.6L342.7 458.0L337.8 462.0L335.5 461.8L335.3 467.5L331.6 467.7L331.7 468.9L327.5 470.8L317.2 472.5L315.1 468.3L312.8 467.6L310.8 468.5L309.7 466.9L306.2 466.6L306.1 467.9L302.5 470.1L304.4 474.3L304.5 481.3L300.4 481.0L299.4 482.4L302.3 488.1L296.7 489.2L294.8 491.6L295.1 498.2L293.3 498.7L291.2 503.1L292.0 505.5L293.4 504.7L294.2 505.6L293.0 516.3L296.0 516.8L295.3 521.1L293.2 521.0L291.2 528.7L287.5 530.8L285.6 530.0L283.5 532.4L283.8 534.2L278.1 535.3L276.4 534.9L274.0 528.3L274.4 524.3L271.3 517.9L270.8 512.9L268.2 511.4L270.5 508.1L269.8 503.8L269.0 504.6L263.9 503.4L260.4 506.2L260.8 507.0L255.5 508.1L251.6 506.5L248.4 509.0L242.3 506.7L241.1 504.7L237.0 503.9L232.7 504.9L227.8 502.6L232.2 497.8L236.4 496.4L233.6 493.3L238.6 491.9L240.6 493.6L241.3 485.8L246.3 485.7L249.2 481.8L251.5 481.6L250.4 478.8L252.4 469.7L251.2 468.3L251.8 466.1L254.3 467.5L257.2 463.3L259.8 465.3L264.1 460.9L268.1 461.3L268.3 458.1L270.2 455.6L268.8 452.1L269.4 446.5L268.1 443.4L264.7 444.0L262.1 442.4L264.7 439.0Z","labelX":274.9,"labelY":482.3,"colorIndex":3,"showLabel":true},{"key":"Shivamogga","alsoCovers":[],"nameEn":["Shivamogga"],"nameKn":["ಶಿವಮೊಗ್ಗ"],"path":"M153.4 516.3L154.5 511.3L152.3 510.8L152.8 508.8L150.4 505.0L152.3 503.0L151.3 502.9L151.2 500.0L149.6 499.8L149.2 497.9L153.3 495.6L161.6 500.0L158.4 490.5L159.8 487.2L163.3 489.9L166.1 491.1L170.2 490.8L172.4 492.7L174.4 489.6L174.8 492.3L176.1 492.2L180.7 489.4L179.8 485.9L178.0 485.2L179.4 481.8L184.9 481.4L184.6 479.5L181.1 477.3L182.0 473.0L178.8 469.6L174.9 469.2L174.5 467.9L176.1 466.4L179.2 466.7L176.0 464.6L177.2 459.4L180.7 460.5L182.6 457.0L184.9 457.3L184.4 460.3L186.3 462.3L186.2 465.3L191.3 462.4L191.0 459.3L193.5 457.9L189.6 455.4L189.9 450.8L192.4 446.7L197.3 446.4L203.5 450.1L204.0 448.4L206.3 449.6L208.1 448.0L208.0 452.2L205.6 452.2L205.9 453.4L208.3 454.9L210.3 453.6L210.1 455.3L211.6 456.7L211.9 454.2L214.1 454.1L217.4 458.2L214.4 459.5L214.4 460.9L219.6 461.0L222.3 464.0L221.7 469.2L226.0 469.4L225.5 472.1L223.7 472.0L222.6 474.2L235.6 481.6L235.5 478.8L238.0 478.8L238.2 482.6L241.4 484.5L240.6 493.6L238.6 491.9L233.6 493.3L236.4 496.4L232.2 497.8L227.8 502.6L232.7 504.9L237.0 503.9L241.1 504.7L242.3 506.7L248.4 509.0L251.6 506.5L255.5 508.1L260.8 507.0L260.4 506.2L263.9 503.4L269.0 504.6L269.8 503.8L270.5 508.1L268.2 511.4L270.8 512.9L276.4 534.9L271.0 534.0L269.1 536.5L266.1 537.2L265.0 536.2L264.8 537.9L256.9 539.4L256.9 541.2L255.1 539.7L253.7 541.6L255.6 544.0L251.6 546.3L250.4 545.8L249.8 544.0L250.9 543.0L249.0 541.6L249.2 540.0L245.2 540.6L242.1 543.5L238.6 539.5L237.8 540.3L236.6 539.3L233.3 544.0L229.7 545.8L226.1 545.3L229.3 551.4L228.5 554.2L226.4 555.5L225.0 554.2L224.7 557.2L222.2 559.0L220.3 557.7L217.2 558.5L215.5 560.7L212.1 556.4L209.9 556.7L208.6 558.5L210.2 560.2L209.1 561.4L210.1 564.5L205.1 567.5L204.4 566.6L201.0 570.3L201.5 571.5L199.0 572.0L197.6 568.6L195.3 568.2L195.0 565.9L193.3 565.4L190.8 560.4L193.4 556.7L188.8 553.3L188.5 550.9L190.1 549.9L188.3 547.4L189.4 546.6L186.3 545.4L187.1 542.4L179.9 539.2L180.5 537.2L177.9 529.9L174.9 528.9L173.6 529.9L171.0 525.7L168.9 524.6L163.2 523.8L158.0 524.9L157.6 522.6L155.8 521.9L156.7 520.1Z","labelX":200.4,"labelY":511.6,"colorIndex":1,"showLabel":true},{"key":"Udupi","alsoCovers":[],"nameEn":["Udupi"],"nameKn":["ಉಡುಪಿ"],"path":"M144.9 522.7L146.2 521.1L146.4 522.1L149.7 520.7L151.5 521.2L151.1 519.4L153.4 516.3L156.7 520.1L155.8 521.9L157.6 522.6L158.0 524.9L163.2 523.8L168.9 524.6L171.0 525.7L173.6 529.9L174.9 528.9L177.9 529.9L180.5 537.2L179.9 539.2L187.1 542.4L186.3 545.4L189.4 546.6L188.5 548.9L190.1 549.9L188.5 550.9L188.8 553.3L193.4 556.7L190.8 560.4L193.3 565.4L195.0 565.9L195.3 568.2L197.6 568.6L199.0 572.0L202.9 572.3L203.0 574.8L201.4 577.2L202.1 578.7L200.1 579.5L200.2 581.5L196.3 582.0L195.2 583.9L198.6 590.3L201.4 589.7L204.1 598.6L208.1 601.3L202.5 606.8L198.9 606.2L199.5 605.6L197.1 602.6L195.4 602.9L195.7 604.6L193.2 606.1L190.9 605.1L188.8 606.2L186.7 603.4L182.6 607.2L179.9 604.4L178.2 606.0L178.3 608.6L174.9 609.9L173.0 606.5L166.1 607.7L166.8 609.0L164.7 609.1L164.5 612.4L158.5 587.0L156.6 583.4L157.2 580.5L156.4 580.7L153.7 553.3L151.3 548.2L147.7 528.9Z","labelX":175.8,"labelY":574.1,"colorIndex":2,"showLabel":true},{"key":"Chikkamagaluru","alsoCovers":[],"nameEn":["Chikkamagaluru"],"nameKn":["ಚಿಕ್ಕಮಗಳೂರು"],"path":"M291.7 525.6L298.6 529.2L300.3 534.7L301.8 534.4L303.7 543.0L306.3 544.7L310.9 545.0L311.1 548.3L315.4 551.8L317.7 552.0L316.4 556.4L319.1 558.1L322.4 557.3L323.3 559.4L324.4 559.1L325.4 564.2L323.7 566.0L320.7 563.3L320.3 564.8L318.4 564.6L318.0 566.9L312.4 566.6L312.1 570.8L308.7 570.7L308.6 572.6L307.6 572.4L308.3 570.7L305.1 570.6L302.6 573.5L302.8 577.2L297.8 578.6L297.7 580.9L292.9 580.6L291.4 585.6L293.0 585.9L292.3 593.8L290.3 593.6L290.1 592.5L282.2 593.0L278.4 592.0L274.6 594.7L274.2 592.8L270.0 592.7L268.9 597.2L262.6 598.1L261.3 602.7L263.7 605.6L261.0 609.5L262.5 612.6L262.3 616.8L261.1 617.5L257.7 615.1L257.3 616.4L253.7 617.6L252.4 616.6L247.6 625.8L243.7 629.1L243.6 625.9L239.4 622.1L241.3 619.4L239.5 613.0L236.5 610.2L234.9 611.8L235.0 609.7L232.4 608.9L231.2 607.1L230.3 608.8L228.5 608.2L228.2 603.6L225.8 601.1L220.5 602.0L219.3 605.6L217.1 606.9L216.1 605.7L212.8 606.4L208.3 603.8L208.1 601.3L204.1 598.6L201.4 589.7L198.6 590.3L195.2 583.9L196.3 582.0L200.2 581.5L200.1 579.5L202.1 578.7L201.4 577.2L203.0 574.8L202.9 572.3L201.0 570.3L204.4 566.6L205.1 567.5L210.1 564.5L209.1 561.4L210.2 560.2L208.6 558.5L209.9 556.7L212.1 556.4L215.5 560.7L217.2 558.5L220.3 557.7L222.2 559.0L224.7 557.2L225.0 554.2L226.4 555.5L228.5 554.2L229.3 551.4L226.1 545.3L229.7 545.8L233.3 544.0L236.6 539.3L237.8 540.3L238.6 539.5L242.1 543.5L245.2 540.6L249.2 540.0L249.0 541.6L250.9 543.0L249.8 544.0L250.4 545.8L251.6 546.3L255.6 544.0L253.7 541.6L255.1 539.7L256.9 541.2L256.9 539.4L264.8 537.9L265.0 536.2L266.1 537.2L269.1 536.5L271.0 534.0L278.1 535.3L283.8 534.2L283.5 532.4L285.6 530.0L287.5 530.8L291.2 528.7Z","labelX":243.6,"labelY":577.8,"colorIndex":0,"showLabel":true},{"key":"Chikkaballapura","alsoCovers":[],"nameEn":["Chikkaballapura"],"nameKn":["ಚಿಕ್ಕಬಳ್ಳಾಪುರ"],"path":"M436.3 548.3L440.0 547.5L439.3 544.9L442.2 545.0L442.0 546.7L443.3 547.4L444.5 543.2L443.0 541.8L443.5 540.8L445.6 540.9L446.9 544.1L450.9 541.1L452.3 541.0L453.7 542.8L455.2 541.5L452.8 540.7L453.0 539.2L459.1 537.3L458.2 540.1L461.4 539.1L461.9 542.1L462.8 542.1L463.0 535.9L469.3 534.0L469.4 531.3L472.4 530.8L475.0 528.6L472.6 523.9L473.9 520.7L475.2 521.7L480.9 521.1L480.9 523.3L484.1 524.9L488.4 519.2L490.2 519.6L490.1 525.1L486.3 526.9L486.7 533.1L489.1 532.8L491.5 528.3L496.2 529.4L496.5 525.9L499.5 527.4L499.8 529.5L503.1 529.3L503.9 531.7L503.0 535.1L504.3 537.3L500.8 542.0L503.8 544.8L503.0 547.2L498.7 546.6L498.1 548.9L500.6 550.7L500.3 552.4L503.1 550.9L507.3 550.8L508.7 551.3L508.5 553.5L512.2 554.0L510.8 558.0L512.1 564.5L510.5 566.0L511.6 566.1L512.8 570.5L510.0 575.8L505.3 577.4L504.1 579.4L503.9 580.5L506.0 580.5L506.7 582.3L504.9 582.9L504.8 585.5L502.5 586.0L505.3 588.0L505.1 589.7L500.8 587.4L496.8 589.1L496.2 590.7L493.0 590.2L492.6 588.3L490.8 588.0L489.9 589.9L485.2 591.8L484.3 594.9L480.4 595.3L481.9 593.6L480.4 592.9L476.3 596.9L476.4 594.6L473.4 593.4L472.0 594.4L473.3 592.8L472.1 592.5L472.8 588.7L474.2 589.4L474.0 587.8L472.2 587.4L474.0 585.6L470.2 583.1L466.6 581.7L465.5 582.8L463.7 581.8L463.9 583.0L462.7 583.0L462.2 581.5L460.9 582.2L461.1 584.5L459.5 583.0L457.7 583.4L456.6 582.3L456.8 578.1L454.8 578.6L454.4 580.5L452.2 579.3L452.8 577.7L451.2 578.1L450.8 576.3L452.5 575.7L451.2 573.6L449.9 574.1L450.1 576.0L446.0 575.9L445.7 574.1L443.0 574.6L441.3 571.9L437.9 570.7L434.0 571.2L432.2 568.3L428.9 567.4L429.8 564.0L428.8 559.5L426.9 558.0L427.0 553.9L430.4 549.2Z","labelX":477.9,"labelY":560.8,"colorIndex":2,"showLabel":true},{"key":"Hassan","alsoCovers":[],"nameEn":["Hassan"],"nameKn":["ಹಾಸನ"],"path":"M243.9 628.8L247.6 625.8L252.4 616.6L253.7 617.6L257.3 616.4L257.7 615.1L261.1 617.5L262.3 616.8L262.5 612.6L261.0 609.5L263.7 605.6L261.3 602.7L262.6 598.1L268.9 597.2L270.0 592.7L274.2 592.8L274.6 594.7L278.4 592.0L282.2 593.0L290.1 592.5L290.3 593.6L292.3 593.8L293.0 585.9L291.4 585.6L292.9 580.6L297.7 580.9L297.8 578.6L302.8 577.2L302.6 573.5L305.1 570.6L308.3 570.7L307.6 572.4L308.6 572.6L308.7 570.7L312.1 570.8L312.4 566.6L318.0 566.9L318.4 564.6L320.3 564.8L320.7 563.3L323.7 566.0L325.6 562.5L329.3 564.7L332.0 564.8L328.9 567.0L330.8 569.6L329.9 571.7L331.2 572.9L327.3 581.7L323.8 582.1L324.8 593.8L327.2 595.6L326.3 598.9L330.5 599.8L334.8 604.2L336.6 604.2L336.6 610.2L338.8 610.0L341.0 612.7L342.1 611.7L350.5 612.7L351.5 614.3L350.1 614.2L349.7 615.7L350.8 619.3L352.8 619.5L352.9 621.9L350.7 623.6L351.2 626.5L349.1 628.2L349.1 632.4L347.4 633.2L347.2 636.0L341.4 636.6L341.2 638.2L338.3 640.3L339.3 643.0L338.6 644.4L338.3 642.4L336.8 643.9L334.8 642.9L336.5 641.1L333.9 638.5L331.8 638.3L331.8 634.9L330.6 634.2L330.1 636.3L327.8 636.2L327.5 637.7L326.1 637.9L326.0 640.0L322.4 640.1L322.6 642.2L324.1 642.4L323.4 645.0L324.9 646.2L323.3 648.1L325.5 651.3L321.5 658.3L321.9 660.7L319.7 660.9L319.1 658.9L317.1 660.2L317.0 658.4L314.5 659.8L313.6 658.0L310.9 657.3L308.1 662.6L308.6 664.3L304.1 664.1L304.0 667.5L300.2 669.6L300.3 671.1L297.8 667.6L293.9 666.7L293.3 664.3L290.0 665.1L287.9 663.1L287.5 664.6L285.5 664.4L286.2 665.8L284.8 666.0L282.5 661.0L283.5 659.4L282.1 658.8L283.4 655.2L282.6 653.9L284.5 653.2L285.1 650.3L282.8 649.3L281.9 644.7L285.7 642.6L285.5 640.4L281.6 640.1L280.5 638.0L279.6 639.3L276.5 637.4L274.8 639.1L276.3 643.4L274.3 644.1L273.4 646.2L273.9 649.0L277.2 650.9L276.3 654.4L271.7 654.4L269.1 651.3L264.4 654.2L259.5 650.6L251.5 652.5L250.7 650.9L253.2 649.4L253.8 646.7L249.5 641.2L250.2 635.1L248.4 633.0L244.4 634.0Z","labelX":304.6,"labelY":619,"colorIndex":2,"showLabel":true},{"key":"Kolar","alsoCovers":[],"nameEn":["Kolar"],"nameKn":["ಕೋಲಾರ"],"path":"M484.9 594.1L485.2 591.8L489.9 589.9L490.8 588.0L492.6 588.3L493.0 590.2L496.2 590.7L496.8 589.1L500.8 587.4L505.1 589.7L505.3 588.0L502.5 586.0L504.8 585.5L504.9 582.9L506.7 582.3L506.0 580.5L503.9 580.5L504.1 579.4L505.3 577.4L510.0 575.8L512.8 570.5L511.6 566.1L510.5 566.0L512.1 564.5L511.4 560.2L513.5 557.9L515.1 558.3L515.6 557.1L517.8 558.8L524.6 557.5L532.2 558.3L529.5 566.5L530.2 578.2L528.5 581.9L532.9 583.9L531.3 584.9L530.3 583.9L529.7 585.1L532.2 586.8L536.1 585.5L536.5 587.6L540.5 586.2L540.1 588.3L543.6 589.4L545.5 591.9L546.2 589.7L548.5 589.6L548.9 587.2L550.9 590.9L548.9 596.7L550.2 597.6L547.2 600.0L549.9 600.3L550.3 602.4L547.3 602.7L546.7 605.0L543.9 606.1L546.1 608.8L543.1 610.3L543.7 613.4L538.4 615.4L539.6 620.8L538.9 622.6L535.6 621.9L535.9 623.7L533.2 625.7L534.0 628.2L535.9 627.4L538.8 629.9L538.6 633.9L536.7 629.2L533.3 630.1L531.9 628.6L530.9 629.7L527.6 626.1L525.9 630.1L524.3 630.2L524.0 631.8L525.7 632.3L523.3 632.9L523.2 634.8L518.7 633.8L517.1 634.6L516.4 642.2L514.9 645.1L505.1 642.8L497.3 635.5L493.9 635.0L494.4 636.2L491.9 638.2L492.5 640.3L490.3 640.1L489.5 637.2L487.9 637.5L488.0 635.9L486.6 634.9L485.4 635.4L485.5 638.5L483.4 638.0L483.6 635.1L485.6 633.5L484.2 633.1L484.8 631.8L477.9 633.6L476.5 631.4L477.5 628.9L475.2 628.1L478.4 626.5L477.8 622.7L480.6 614.5L482.7 614.8L485.6 612.7L485.4 611.1L484.6 612.2L482.7 611.5L481.5 609.2L482.9 607.4L485.1 608.6L485.3 606.8L487.0 606.3L487.8 600.0L487.2 597.0L485.9 597.4L487.9 594.3Z","labelX":508.4,"labelY":612.8,"colorIndex":1,"showLabel":true},{"key":"Bengaluru Rural","alsoCovers":[],"nameEn":["Bengaluru Rural"],"nameKn":["ಬೆಂಗಳೂರು ಗ್ರಾಮಾಂತರ"],"path":"M429.2 566.0L428.9 567.4L432.2 568.3L434.0 571.2L437.9 570.7L441.3 571.9L443.0 574.6L445.7 574.1L446.0 575.9L450.1 576.0L449.9 574.1L451.2 573.6L452.5 575.7L450.8 576.3L451.2 578.1L452.8 577.7L453.0 580.5L456.8 578.1L456.6 582.3L457.7 583.4L459.5 583.0L461.1 584.5L460.9 582.2L462.2 581.5L462.7 583.0L463.9 583.0L463.7 581.8L465.5 582.8L466.6 581.7L470.2 583.1L474.0 585.6L472.2 587.4L474.0 587.8L474.2 589.4L472.8 588.7L472.1 592.5L473.3 592.8L472.0 594.4L473.4 593.4L476.4 594.6L476.3 596.9L480.4 592.9L481.9 593.6L480.4 595.3L487.9 594.3L485.9 597.4L487.2 597.0L487.8 600.0L487.0 606.3L485.3 606.8L485.1 608.6L482.9 607.4L481.5 609.2L482.7 611.5L484.6 612.2L485.4 611.1L485.6 612.7L482.7 614.8L480.6 614.5L477.8 622.7L478.4 626.5L475.2 628.1L477.5 628.9L476.5 631.4L477.9 633.6L474.4 634.0L472.8 628.9L474.5 627.6L472.3 627.0L471.0 628.5L467.7 626.0L469.1 623.1L468.3 618.5L469.3 616.4L467.2 612.2L467.6 608.6L463.5 606.6L464.4 603.2L463.6 599.7L460.9 600.0L459.5 598.6L459.7 599.6L457.3 600.6L457.0 598.8L452.7 597.8L450.9 599.3L448.3 599.1L446.8 595.6L445.4 595.5L443.0 598.0L441.9 595.9L438.7 596.5L437.5 602.9L433.8 602.6L431.5 606.9L430.0 607.5L432.4 608.9L433.0 612.5L429.0 612.9L430.0 614.4L429.5 617.2L428.1 616.9L427.8 620.7L423.6 622.6L422.7 619.1L423.6 615.4L421.6 615.1L421.9 608.8L419.8 608.4L418.9 605.8L417.3 607.5L418.0 603.9L412.1 604.7L411.1 599.3L408.6 595.9L409.6 594.4L408.7 592.9L409.2 590.9L412.3 592.3L413.3 587.0L415.4 588.3L417.4 587.3L418.6 588.7L418.6 587.3L421.4 587.3L421.7 586.1L424.4 585.4L425.2 576.1L426.9 575.8L425.4 571.9L427.9 570.6Z","labelX":436.5,"labelY":585.2,"colorIndex":0,"showLabel":false},{"key":"Dakshina Kannada","alsoCovers":[],"nameEn":["Dakshina Kannada"],"nameKn":["ದಕ್ಷಿಣ ಕನ್ನಡ"],"path":"M164.5 612.4L164.7 609.1L166.8 609.0L166.1 607.7L173.0 606.5L174.9 609.9L178.3 608.6L178.2 606.0L179.9 604.4L182.6 607.2L186.7 603.4L188.8 606.2L190.9 605.1L193.2 606.1L195.7 604.6L195.4 602.9L197.1 602.6L199.5 605.6L198.9 606.2L202.5 606.8L208.1 601.3L208.3 603.8L212.8 606.4L216.1 605.7L217.1 606.9L219.3 605.6L220.5 602.0L225.8 601.1L228.2 603.6L228.5 608.2L230.3 608.8L231.2 607.1L232.4 608.9L235.0 609.7L234.9 611.8L236.5 610.2L239.5 613.0L241.3 619.4L239.4 622.1L243.6 625.9L244.4 634.0L248.4 633.0L250.2 635.1L249.5 641.2L253.8 646.7L253.2 649.4L250.7 650.9L251.5 652.5L254.8 651.7L252.3 658.5L255.2 667.1L249.6 671.4L247.2 668.2L243.6 672.3L244.6 673.9L240.9 672.5L237.3 674.1L236.9 676.6L234.3 675.4L235.1 672.5L231.3 669.3L229.4 672.5L225.9 672.2L223.7 676.1L221.4 676.2L220.9 672.5L218.1 672.1L218.0 670.0L215.6 669.9L214.6 667.2L214.8 665.1L216.6 663.6L219.1 664.7L220.2 662.8L214.9 660.2L211.3 665.3L209.6 665.3L208.4 662.7L207.6 663.1L207.7 658.9L203.6 659.7L201.2 657.2L203.5 654.4L202.7 653.3L198.6 653.4L197.9 651.6L195.9 650.9L195.9 654.2L192.0 654.9L191.2 652.9L192.8 652.0L192.3 649.4L187.3 649.8L188.4 648.5L185.4 647.4L188.2 644.4L187.5 641.9L186.1 641.4L185.9 642.7L184.0 643.4L180.4 643.0L175.3 645.8L173.5 645.1L169.3 636.0Z","labelX":216.3,"labelY":632.2,"colorIndex":1,"showLabel":true},{"key":"Bengaluru Urban","alsoCovers":[],"nameEn":["Bengaluru Urban"],"nameKn":["ಬೆಂಗಳೂರು ನಗರ"],"path":"M423.6 622.6L427.8 620.7L428.1 616.9L429.5 617.2L430.0 614.4L429.0 612.9L433.0 612.5L432.4 608.9L430.0 607.5L431.5 606.9L433.8 602.6L437.5 602.9L438.7 596.5L441.9 595.9L443.0 598.0L445.4 595.5L446.8 595.6L448.3 599.1L450.9 599.3L452.7 597.8L457.0 598.8L457.3 600.6L459.7 599.6L459.5 598.6L460.9 600.0L463.6 599.7L464.4 603.2L463.5 606.6L467.6 608.6L467.2 612.2L469.3 616.4L468.3 618.5L469.1 623.1L467.7 626.0L471.0 628.5L472.3 627.0L474.5 627.6L472.8 628.9L474.4 634.0L470.4 636.5L472.2 637.6L471.2 640.4L472.1 641.3L469.1 644.3L470.5 646.3L467.5 647.6L468.6 648.9L467.3 651.7L464.9 651.7L465.0 654.3L462.2 653.2L461.3 655.5L458.9 655.6L458.7 653.6L457.0 653.1L451.0 654.8L452.5 650.5L450.3 649.1L447.6 651.3L446.5 648.2L447.8 648.3L448.5 646.0L450.4 645.9L450.7 644.5L448.9 642.3L447.4 645.2L445.7 640.9L446.1 643.8L444.9 646.2L444.3 645.5L442.9 648.0L442.2 646.2L441.3 648.0L438.1 645.4L437.7 641.4L439.2 640.9L438.7 640.0L435.9 639.0L434.3 639.8L432.6 638.2L434.5 635.7L433.3 635.6L432.5 633.6L433.7 633.0L431.7 632.9L428.5 635.2L427.1 633.4L424.0 632.7L424.9 631.7Z","labelX":449.5,"labelY":621.3,"colorIndex":1,"showLabel":false},{"key":"Kodagu","alsoCovers":[],"nameEn":["Kodagu"],"nameKn":["ಕೊಡಗು"],"path":"M254.8 651.7L259.5 650.6L264.4 654.2L269.1 651.3L271.7 654.4L276.3 654.4L277.2 650.9L273.9 649.0L273.4 646.2L274.3 644.1L276.3 643.4L274.8 639.1L276.5 637.4L279.6 639.3L280.5 638.0L281.6 640.1L285.5 640.4L285.7 642.6L281.9 644.7L282.8 649.3L285.1 650.3L284.5 653.2L282.6 653.9L283.4 655.2L282.1 658.8L283.5 659.4L282.5 661.0L284.8 666.0L286.2 665.8L285.5 664.4L287.5 664.6L287.9 663.1L290.6 665.0L289.9 666.7L287.8 665.9L288.5 667.4L287.0 668.2L288.0 670.1L286.2 670.3L284.4 673.4L285.8 677.7L281.5 678.8L279.4 683.1L284.6 688.8L288.8 690.2L288.1 691.6L290.1 694.2L291.2 698.8L294.9 698.8L299.0 704.2L302.2 704.8L306.0 712.5L307.8 719.7L307.2 721.5L304.1 722.9L300.2 727.0L289.0 732.1L275.5 730.0L273.1 726.8L271.4 726.5L268.5 721.0L268.6 716.6L263.7 716.0L259.7 717.5L258.6 713.6L254.3 714.6L251.7 709.4L250.0 708.6L247.3 709.5L241.3 703.9L242.0 702.3L236.4 694.5L229.2 694.5L231.0 692.5L228.9 688.5L230.0 685.8L225.7 683.7L224.3 681.7L224.3 676.9L227.1 677.5L230.1 675.3L229.5 670.8L230.7 670.9L231.3 669.3L235.1 672.5L234.3 675.4L236.9 676.6L237.3 674.1L240.9 672.5L244.6 673.9L243.6 672.3L247.2 668.2L249.6 671.4L255.2 667.1L252.3 658.5Z","labelX":259.6,"labelY":689.9,"colorIndex":0,"showLabel":true},{"key":"Chamarajanagar","alsoCovers":[],"nameEn":["Chamarajanagar"],"nameKn":["ಚಾಮರಾಜನಗರ"],"path":"M330.6 750.2L331.5 748.4L330.6 747.2L332.1 746.4L330.2 744.7L335.5 746.7L336.2 743.9L338.6 743.6L338.5 739.5L342.9 738.2L344.8 739.8L345.1 736.0L347.1 733.0L345.2 730.3L347.3 726.2L353.8 726.7L354.7 724.1L361.4 726.5L363.8 723.9L369.1 726.4L369.0 723.8L371.0 722.1L373.5 724.9L378.2 723.3L378.5 724.8L380.4 725.0L383.6 723.5L383.9 722.2L381.5 722.2L379.0 718.8L375.2 718.7L378.3 716.3L377.6 712.8L374.4 713.0L373.0 711.1L373.9 708.5L375.2 708.2L375.0 706.1L377.0 706.7L377.0 710.1L379.0 712.2L384.9 714.7L389.0 710.2L389.3 706.5L391.5 707.8L391.7 710.9L393.8 710.3L394.1 711.4L394.6 707.2L402.5 704.2L403.7 701.1L402.6 699.5L401.1 699.7L407.3 697.6L407.0 692.4L412.9 693.1L417.2 691.9L420.8 693.6L423.2 692.6L425.2 695.3L433.7 694.8L435.5 698.7L438.6 699.7L437.9 703.1L441.3 704.1L452.0 703.3L464.3 706.0L466.1 710.4L468.8 713.2L462.0 721.1L458.6 730.0L444.6 732.0L440.3 730.8L439.4 736.8L436.9 735.4L437.4 741.0L433.4 750.0L430.1 747.2L427.1 746.9L424.0 749.0L419.9 745.0L419.3 745.9L415.4 744.5L414.2 746.5L401.3 748.7L402.0 754.2L398.2 752.6L395.8 749.1L397.1 747.8L396.5 746.9L393.0 748.6L394.0 746.6L391.4 744.4L388.2 744.7L387.0 748.3L385.4 746.8L380.7 746.4L378.7 752.9L376.1 755.3L373.3 761.1L373.3 762.8L375.7 765.5L374.5 767.5L365.4 765.0L347.1 765.4L345.4 764.6L344.5 759.1L340.6 755.9L337.5 757.2L335.1 760.2L330.9 759.4L329.5 755.3L332.0 753.7L330.3 752.0Z","labelX":420.4,"labelY":719,"colorIndex":3,"showLabel":true},{"key":"Tumakuru","alsoCovers":[],"nameEn":["Tumakuru"],"nameKn":["ತುಮಕೂರು"],"path":"M390.4 494.1L390.7 490.2L395.5 489.3L398.3 492.4L401.9 491.2L401.8 485.4L404.0 479.5L406.5 479.0L410.1 481.2L414.2 481.6L419.1 479.2L418.8 485.3L427.0 486.0L426.5 490.4L428.5 491.1L429.9 494.0L432.5 493.0L431.5 487.7L429.4 486.5L428.4 483.1L429.5 480.0L431.6 479.6L433.5 480.4L433.8 482.1L435.3 481.6L435.7 485.5L438.6 484.3L440.6 485.7L440.1 490.7L442.5 495.4L441.1 498.4L437.4 496.9L430.0 496.6L428.5 500.7L425.7 501.2L425.3 504.1L429.7 501.6L430.7 502.5L426.6 508.4L424.6 508.6L423.8 511.7L429.7 513.2L428.8 517.4L433.3 516.4L434.5 517.5L434.2 520.4L431.9 520.7L432.2 522.9L434.2 523.9L430.5 526.7L430.4 524.8L426.3 525.0L425.4 523.8L427.7 519.9L425.5 519.3L426.7 515.9L424.2 515.4L422.7 511.8L418.0 513.8L417.5 511.3L415.0 513.7L407.7 513.6L407.6 514.8L405.3 514.8L403.0 510.0L398.1 510.5L397.8 509.3L392.9 508.4L393.1 505.7L391.6 504.4L392.7 496.6ZM324.4 559.1L326.5 557.6L327.5 559.4L329.2 557.5L330.6 559.2L332.3 557.6L334.8 557.9L335.4 555.0L339.4 554.6L342.8 551.6L342.4 550.0L347.1 547.5L346.4 544.9L351.6 542.9L352.5 545.0L356.9 544.5L358.1 547.4L359.4 538.9L362.1 536.0L362.8 537.6L364.6 538.0L366.2 537.8L366.6 536.4L366.4 532.2L362.0 525.6L365.0 524.8L364.6 523.7L366.2 522.9L371.4 523.8L370.9 521.6L374.9 518.8L374.7 517.0L372.2 515.5L373.6 513.0L377.2 514.0L378.8 509.9L381.0 508.2L382.3 508.9L384.8 505.0L387.4 506.2L387.3 508.6L383.2 509.3L383.1 511.8L390.0 515.9L389.4 518.9L394.3 521.9L394.9 524.8L392.4 525.2L391.5 529.6L387.8 533.1L389.0 534.6L389.9 541.7L391.7 542.4L392.5 538.1L394.5 538.3L396.6 541.8L398.7 542.0L399.2 539.4L407.6 539.9L408.2 537.7L406.1 535.3L407.2 532.8L403.1 530.0L405.1 528.7L407.2 529.2L406.5 524.8L408.8 522.8L408.5 528.6L410.4 529.3L411.7 526.9L412.4 528.8L414.0 525.0L415.3 525.3L416.1 531.0L419.3 530.9L421.7 529.0L423.1 532.4L426.8 530.3L428.8 531.8L433.9 531.4L432.3 535.2L434.3 536.3L435.2 534.8L437.1 535.3L437.2 545.3L435.7 545.3L436.3 548.3L430.4 549.2L426.8 554.6L426.9 558.0L428.8 559.5L429.8 564.0L427.9 570.6L425.4 571.9L426.9 575.8L425.2 576.1L424.4 585.4L421.7 586.1L421.4 587.3L418.6 587.3L418.6 588.7L417.4 587.3L415.4 588.3L413.3 587.0L412.3 592.3L409.2 590.9L408.7 592.9L409.6 594.4L408.6 595.9L411.1 599.3L410.6 600.7L406.5 599.8L402.1 602.9L401.8 606.3L399.8 604.6L397.5 606.9L398.3 610.5L397.1 615.7L402.1 618.4L400.0 620.0L400.8 620.7L403.7 619.4L404.8 621.2L403.2 622.2L406.0 624.3L406.8 627.4L406.1 629.0L404.2 628.5L403.5 630.7L400.9 631.0L403.4 631.7L403.4 634.7L406.2 638.1L404.7 639.4L405.3 642.3L402.7 643.7L400.0 643.4L399.4 645.9L398.2 645.2L397.8 646.6L396.9 645.3L395.0 646.4L394.5 643.2L391.0 644.7L391.1 642.8L389.9 643.5L387.3 640.6L382.4 640.5L379.1 636.0L378.8 630.7L376.4 629.9L376.6 628.5L379.5 628.3L379.1 627.0L376.1 627.5L373.8 625.6L373.8 622.7L375.2 622.2L371.3 618.5L368.3 618.3L366.6 616.3L359.0 616.1L356.9 614.1L354.8 619.2L350.8 619.3L349.7 615.7L350.1 614.2L351.5 614.3L350.5 612.7L342.1 611.7L341.0 612.7L338.8 610.0L336.6 610.2L336.6 604.2L334.8 604.2L330.5 599.8L326.3 598.9L327.2 595.6L324.8 593.8L323.8 582.1L327.3 581.7L331.2 572.9L329.9 571.7L330.8 569.6L328.9 567.0L332.0 564.8L329.3 564.7L325.6 562.5Z","labelX":373.9,"labelY":579.7,"colorIndex":1,"showLabel":true},{"key":"Bengaluru South","alsoCovers":[],"nameEn":["Bengaluru South"],"nameKn":["ಬೆಂಗಳೂರು ದಕ್ಷಿಣ"],"path":"M410.7 600.6L412.1 604.7L418.0 603.9L417.3 607.5L418.9 605.8L419.8 608.4L421.9 608.8L421.6 615.1L423.6 615.4L422.7 619.1L424.5 625.8L424.9 631.7L424.0 632.7L427.1 633.4L428.5 635.2L431.7 632.9L433.7 633.0L432.5 633.6L433.3 635.6L434.5 635.7L432.6 638.2L434.3 639.8L435.9 639.0L438.7 640.0L439.2 640.9L437.7 641.4L438.1 645.4L441.3 648.0L442.2 646.2L442.9 648.0L444.3 645.5L444.9 646.2L446.1 643.8L445.7 640.9L447.4 645.2L448.9 642.3L450.7 644.5L450.4 645.9L448.5 646.0L447.8 648.3L446.5 648.2L447.6 651.3L450.3 649.1L452.5 650.5L450.7 655.8L451.5 658.9L450.0 660.3L448.9 667.0L449.6 670.9L452.6 671.5L449.3 674.7L451.7 675.0L453.6 672.8L454.4 673.7L452.5 686.4L448.7 689.7L446.0 694.4L439.5 695.5L437.8 699.3L435.5 698.7L433.7 694.8L425.2 695.3L423.1 692.6L423.6 686.9L421.8 683.7L419.1 683.7L417.4 676.1L414.0 675.6L413.9 674.2L412.9 675.1L414.2 672.4L412.8 672.2L411.7 674.0L407.1 674.0L405.0 676.4L402.8 674.3L403.4 670.5L401.7 666.8L403.0 666.8L403.1 665.1L401.0 661.1L402.5 659.4L400.3 658.7L400.0 654.0L397.5 653.5L397.9 646.3L398.2 645.2L399.4 645.9L400.0 643.4L402.7 643.7L405.3 642.3L404.7 639.4L406.2 638.1L403.4 634.7L403.4 631.7L400.9 631.0L403.5 630.7L404.2 628.5L406.1 629.0L406.8 627.4L406.0 624.3L403.2 622.2L404.8 621.2L403.7 619.4L400.8 620.7L400.0 620.0L402.1 618.4L397.1 615.7L398.3 610.5L397.5 606.9L399.8 604.6L401.8 606.3L402.1 602.9L406.5 599.8Z","labelX":421.2,"labelY":654.2,"colorIndex":2,"showLabel":true},{"key":"Mandya","alsoCovers":[],"nameEn":["Mandya"],"nameKn":["ಮಂಡ್ಯ"],"path":"M323.4 656.1L325.5 651.3L323.3 648.1L324.9 646.2L323.4 645.0L324.1 642.4L322.6 642.2L322.4 640.1L326.0 640.0L326.1 637.9L327.5 637.7L327.8 636.2L330.1 636.3L330.6 634.2L331.8 634.9L331.8 638.3L333.9 638.5L336.5 641.1L334.8 642.9L336.8 643.9L338.3 642.4L338.6 644.4L339.3 643.0L338.3 640.3L341.2 638.2L341.4 636.6L347.2 636.0L347.4 633.2L349.1 632.4L349.1 628.2L351.2 626.5L350.7 623.6L352.9 621.9L352.8 619.5L354.8 619.2L356.9 614.1L359.0 616.1L366.6 616.3L368.3 618.3L371.3 618.5L375.2 622.2L373.8 622.7L373.8 625.6L376.1 627.5L379.1 627.0L379.5 628.3L376.6 628.5L376.4 629.9L378.8 630.7L379.1 636.0L382.4 640.5L387.3 640.6L389.9 643.5L391.1 642.8L391.0 644.7L394.5 643.2L395.0 646.4L396.9 645.3L398.2 646.5L397.5 653.5L400.0 654.0L400.3 658.7L402.5 659.4L401.0 661.1L403.1 665.1L403.0 666.8L401.7 666.8L403.4 670.5L402.8 674.3L405.0 676.4L407.1 674.0L411.7 674.0L412.8 672.2L414.2 672.4L412.9 675.1L413.9 674.2L414.0 675.6L417.4 676.1L419.1 683.7L421.8 683.7L423.6 686.9L423.1 692.7L420.8 693.6L417.2 691.9L412.9 693.1L407.0 692.4L407.3 697.6L401.1 699.7L400.8 701.8L398.0 701.7L398.0 700.1L389.6 699.5L389.1 694.8L387.2 694.4L387.7 692.7L389.3 693.4L389.9 692.1L388.7 689.9L387.7 690.5L387.7 687.9L385.6 687.9L385.8 690.6L382.1 691.2L383.2 689.4L382.4 687.8L383.9 687.9L382.2 678.5L380.4 679.1L380.0 682.0L373.1 682.7L373.1 684.6L371.3 684.3L370.1 687.6L366.3 688.1L365.1 693.2L364.0 691.5L362.8 691.7L361.6 687.5L359.6 688.1L357.8 686.4L357.9 683.9L353.9 683.4L351.7 686.3L346.1 688.0L346.1 684.8L344.9 683.8L346.2 680.2L334.5 674.1L334.5 671.9L331.5 669.0L332.0 668.0L330.4 666.4L328.3 666.9L328.8 664.7L326.9 664.4ZM387.1 698.1L387.0 696.2L388.2 696.5L388.3 698.1ZM379.8 687.5L379.8 685.7L381.7 685.0L381.3 687.3Z","labelX":362.3,"labelY":657.4,"colorIndex":0,"showLabel":true},{"key":"Mysuru","alsoCovers":[],"nameEn":["Mysuru"],"nameKn":["ಮೈಸೂರು"],"path":"M290.6 665.0L293.3 664.3L293.9 666.7L297.8 667.6L300.3 671.1L300.2 669.6L304.0 667.5L304.1 664.1L308.6 664.3L308.1 662.6L310.9 657.3L313.6 658.0L314.5 659.8L317.0 658.4L317.1 660.2L319.1 658.9L319.7 660.9L321.9 660.7L321.5 658.3L323.4 656.1L326.9 664.4L328.8 664.7L328.3 666.9L330.4 666.4L332.0 668.0L331.5 669.0L334.5 671.9L334.5 674.1L346.2 680.2L344.9 683.8L346.1 684.8L346.1 688.0L351.7 686.3L353.9 683.4L357.9 683.9L357.8 686.4L359.6 688.1L361.6 687.5L362.8 691.7L364.0 691.5L365.1 693.2L366.3 688.1L370.1 687.6L371.3 684.3L373.1 684.6L373.1 682.7L380.0 682.0L380.4 679.1L382.2 678.5L383.9 687.9L382.4 687.8L383.2 689.4L382.1 691.2L385.8 690.6L385.6 687.9L387.7 687.9L387.7 690.5L388.7 689.9L389.9 692.1L389.3 693.4L387.7 692.7L387.2 694.4L389.1 694.8L389.6 699.5L398.0 700.1L398.0 701.7L400.8 701.8L401.1 699.7L402.6 699.5L403.7 701.2L402.5 704.2L394.6 707.2L394.1 711.4L393.8 710.3L391.7 710.9L391.5 707.8L389.3 706.5L389.0 710.2L384.9 714.7L379.0 712.2L377.0 710.1L377.0 706.7L375.0 706.1L375.2 708.2L373.9 708.5L373.0 711.1L374.4 713.0L377.6 712.8L378.3 716.3L375.2 718.7L379.0 718.8L381.5 722.2L383.9 722.2L383.6 723.5L380.4 725.0L378.5 724.8L378.2 723.3L373.5 724.9L371.0 722.1L369.0 723.8L369.1 726.4L363.8 723.9L361.4 726.5L354.7 724.1L353.8 726.7L347.3 726.2L345.2 730.3L347.1 733.0L345.1 736.0L344.8 739.8L342.9 738.2L338.5 739.5L338.6 743.6L336.2 743.9L335.5 746.7L330.2 744.7L332.1 746.4L330.6 747.2L331.5 748.4L330.7 750.2L324.4 752.4L321.0 751.0L317.1 744.6L311.3 745.1L309.7 739.5L308.0 738.5L304.5 738.6L300.8 740.7L300.1 739.8L300.0 727.2L304.1 722.9L307.2 721.5L307.8 719.7L306.0 712.5L302.2 704.8L299.0 704.2L294.9 698.8L291.2 698.8L290.1 694.2L288.1 691.6L288.8 690.2L284.6 688.8L279.4 683.1L281.5 678.8L285.8 677.7L284.4 673.4L286.2 670.3L288.0 670.1L287.0 668.2L288.5 667.4L287.8 665.9L289.9 666.7ZM387.1 698.1L388.3 698.1L388.2 696.5L387.0 696.2ZM379.8 687.5L381.3 687.3L381.7 685.0L379.8 685.7Z","labelX":330.4,"labelY":707.8,"colorIndex":1,"showLabel":true},{"key":"Vijayapura","alsoCovers":[],"nameEn":["Vijayapura"],"nameKn":["ವಿಜಯಪುರ"],"path":"M296.3 164.6L295.2 167.2L298.7 169.4L297.6 173.0L301.6 174.2L307.7 180.4L311.0 180.5L313.3 184.1L315.4 184.1L316.7 182.4L322.5 182.0L324.8 180.1L326.1 182.4L325.1 185.2L328.7 184.9L325.4 187.8L328.2 189.7L328.0 192.3L329.0 192.6L332.2 189.5L333.3 200.3L332.9 201.9L329.2 202.3L329.1 206.7L332.1 207.6L332.7 205.9L335.2 207.5L335.9 210.2L332.6 210.7L332.6 212.3L333.1 213.8L335.2 213.9L336.4 216.9L332.2 217.0L332.2 215.6L326.6 214.8L324.4 218.9L330.6 219.5L330.3 221.1L326.9 220.8L326.3 224.2L332.9 224.7L333.3 227.0L331.5 229.7L334.5 229.1L335.5 230.6L334.5 229.9L331.8 231.9L329.7 237.4L332.7 250.5L332.8 259.0L322.3 260.1L322.4 263.9L320.1 263.3L320.5 268.7L318.3 270.2L319.4 271.4L319.0 275.2L323.9 275.8L324.4 280.2L322.3 284.6L318.9 285.5L317.1 288.9L305.9 285.6L302.8 285.9L295.5 283.1L294.5 281.0L295.3 277.4L291.3 273.4L284.2 273.7L269.4 266.1L260.3 263.9L251.5 258.3L248.0 258.1L245.0 261.7L243.2 257.1L241.5 256.0L237.9 257.3L236.6 261.1L232.7 261.5L231.1 259.7L234.2 255.2L233.4 251.7L236.5 251.5L236.8 246.6L235.1 245.8L237.3 242.7L234.1 239.6L231.6 231.7L236.8 232.0L237.1 226.8L230.0 224.4L226.2 224.8L225.6 218.4L227.3 214.3L221.3 213.6L221.9 204.6L227.0 202.0L230.6 203.5L234.4 201.2L236.3 202.5L237.0 205.7L240.9 203.2L244.8 203.5L245.3 198.9L248.8 199.5L248.9 203.0L251.5 203.1L252.4 204.7L255.7 203.5L255.1 196.0L252.3 196.0L253.2 193.9L255.9 193.1L255.4 187.8L253.2 187.8L250.6 181.7L254.4 177.4L253.9 172.3L253.6 171.2L250.9 171.2L250.3 169.0L248.5 167.9L249.1 163.7L246.2 163.1L245.5 159.7L249.5 158.8L248.8 155.8L251.4 149.8L255.8 152.1L257.3 156.9L261.6 155.8L264.8 157.4L267.7 161.1L269.7 160.8L270.5 155.7L273.5 156.8L274.5 159.5L276.9 156.1L277.9 157.8L277.6 162.5L281.6 166.1L288.5 164.5L290.0 162.9L294.8 162.7Z","labelX":286.2,"labelY":229.1,"colorIndex":3,"showLabel":true}]}
```


END OF FILE. After creating all five files, tell me and wait for my approval of your plan.
