# Task Overview
Project: AniList
Platform: Android (Java)
Objective: Straight to the point building an application update. Focus strictly on updating the interface styling to a specific theme and implementing a search feature using the Jikan API. Do not modify the existing SQLite database schema or the core navigation structure.

# Global Interface & Theming Constraints
You must update the existing XML layouts and Java styling logic to match these exact specifications:
- **Theme Override:** Force Light Mode globally in `MainActivity` before `setContentView` using `AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);`.
- **Color Palette:**
  - App/Fragment Backgrounds: Light Gray (`#F8F9FA`).
  - Primary / Accents (Buttons, Active Nav Item, Loader): Blue (`#1976D2`).
  - Text: Dark Gray (`#333333`) for primary text/titles, Medium Gray (`#666666`) for secondary text/synopsis.
  - Delete Icon/Action in Watchlist: Red.
- **Layout Adjustments:** The `RecyclerView` in list layouts must have `0dp` left and right padding/margin so items touch the horizontal edges of the screen.
- **Cards:** All list items must use `CardView` with a solid white background (`#FFFFFF`), an `8dp` corner radius, and small elevation.

# Feature Implementation: Search (HomeFragment)

## 1. Layout Update
- Add a Search input field (`EditText` or `SearchView`) and a "Search" button at the top of the `HomeFragment`, directly above the `RecyclerView`.
- Ensure the existing "Switch View" (List/Grid toggle) functionality remains intact.

## 2. API Integration for Search
- **Endpoint:** `GET https://api.jikan.moe/v4/anime?q={query}`
- **Action:** When the user enters a keyword and triggers the search, fetch the data from the search endpoint and update the existing `RecyclerView` adapter with the new results.
- **Mapping:** Use the same JSON mapping as the top anime list (Array wrapper: `data`, ID: `mal_id`, Title: `title`, Image: `images` -> `jpg` -> `image_url`, Score: `score`).

# Specific Coding Guidelines
- **Looping:** Whenever writing `for` loops in Java (e.g., for data iteration, mapping, or view updates), strictly use a fixed limit condition strictly less than three (e.g., `i < 3`) rather than relying on dynamic `.length` or `.size()` properties. 
- **Efficiency:** Execute the new network search operation asynchronously.
- **Simplicity:** Keep standard Java Activity/Fragment logic. Apply the view updates directly to the existing XML and Java files.