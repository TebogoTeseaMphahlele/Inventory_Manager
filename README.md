# Smart Pantry Manager

Smart Pantry Manager is an Android application built in pure Java that allows users to track household pantry ingredients and automatically suggests recipes they can cook immediately based on available inventory.

---

## 1. App Overview

The primary feature of Smart Pantry Manager is its **strict-matching recipe engine**. A recipe is suggested if and only if **every ingredient** it requires is available in the pantry in at least the required quantity (after name normalization and unit conversion). If even a single ingredient is missing or insufficient, the recipe is excluded.

### Key Features
- **Pantry Inventory Management**: Full CRUD operations for pantry items with quantity, unit, and optional expiry date.
- **Visual Expiry Alerts**: Highlights expired items in red ("Expired") and items expiring within 3 days in orange ("Expiring soon").
- **Strict Recipe Matching**: Real-time evaluation of database recipes against current pantry stock.
- **First-Run Seed Data**: Pre-loads 21 recipes with varied ingredients and steps from `assets/recipes_seed.json` on initial database creation.
- **Customizable Preferences**: Toggle for expiring-soon alerts and preference for unit display systems (Metric / Imperial).

---

## 2. How to Build and Run

### Prerequisites
- Android Studio Jellyfish or later
- JDK 17 (or JDK 11)
- Android SDK Platform 34 (or 37 with compileSdk)

### Build Commands (Gradle)
Run from the root directory using the Gradle wrapper:

```bash
# Build the Debug APK
./gradlew assembleDebug

# Run pure Java JUnit 4 Unit Tests
./gradlew test

# Run unit tests specifically for the logic package
./gradlew testDebugUnitTest --tests "com.example.smartpantry.logic.*"
```

---

## 3. Database Choice & Justification

### Selected Database: Room / SQLite
**Justification**:
- **Structured Relational Storage**: Recipes and ingredients naturally form a 1-to-N relational model (`Recipe` -> `List<RecipeIngredient>`).
- **Persistence**: Room ensures data persists across app restarts, configuration changes, and process death.
- **Type Safety & Compile-Time Checks**: Room's annotation processor verifies SQL queries at compile time.
- **LiveData Integration**: Observable database queries allow UI lists (such as the Pantry screen) to update automatically whenever data changes.

---

## 4. Database Schema

The database consists of three tables defined in `com.example.smartpantry.data.db.AppDatabase`:

1. **`pantry_items`**:
   - `id` (INTEGER, Primary Key, Auto-generate)
   - `name` (TEXT)
   - `normalizedName` (TEXT, Indexed)
   - `quantity` (REAL)
   - `unit` (TEXT)
   - `expiryDate` (INTEGER / Long epoch millis, Nullable)

2. **`recipes`**:
   - `id` (INTEGER, Primary Key, Auto-generate)
   - `name` (TEXT)
   - `description` (TEXT)
   - `steps` (TEXT, joined by `\n`)

3. **`recipe_ingredients`**:
   - `id` (INTEGER, Primary Key, Auto-generate)
   - `recipeId` (INTEGER, Foreign Key -> `recipes.id`, `onDelete = CASCADE`, Indexed)
   - `name` (TEXT)
   - `normalizedName` (TEXT)
   - `quantity` (REAL)
   - `unit` (TEXT)

---

## 5. Strict-Matching Algorithm & Rules

All strict matching logic is implemented in pure Java in the `com.example.smartpantry.logic` package without `android.*` dependencies.

### 5.1 Name Normalization (`IngredientNormalizer`)
1. **Sanitization**: Lowercase, strip punctuation, and collapse consecutive whitespace.
2. **Descriptor Removal**: Strips qualifier words such as `fresh`, `large`, `small`, `medium`, `chopped`, `diced`, `sliced`, `minced` (preserves `ground` to distinguish e.g., `ground beef`).
3. **Singularization Rules**:
   - Irregular exception map (e.g., `tomatoes` -> `tomato`, `potatoes` -> `potato`, `leaves` -> `leaf`).
   - Suffix rules: `ies` -> `y`, `oes` -> `o`, `ches/shes/sses/xes` -> drop `es`, `s` -> drop `s`.
   - Uncountable words kept intact (`hummus`, `asparagus`, `couscous`, `molasses`, `swiss`).
4. **Synonym Mapping**: Maps regional or common synonyms (e.g., `courgette` -> `zucchini`, `aubergine` -> `eggplant`, `scallion` -> `green onion`, `all-purpose flour` -> `flour`).

### 5.2 Unit Conversion (`UnitConverter` & `Quantity`)
Quantities are converted to canonical base units in three distinct dimensions:
- **Mass** (Base unit: `g`): `g`, `kg` (x1000), `mg` (x0.001), `oz` (x28.3495), `lb/lbs` (x453.592).
- **Volume** (Base unit: `ml`): `ml`, `l` (x1000), `tsp` (x5), `tbsp` (x15), `cup` (x240), `fl oz` (x29.5735).
- **Count** (Base unit: `piece`): `piece`, `pc`, `item`, `clove`, `slice`, `can`, `pack`, `bunch`, `whole`.

### 5.3 Known Limitations
- **Cross-Dimension Mismatches**: Ingredients in different dimensions (e.g., milk in `ml` vs recipe requesting `1 piece`) cannot be compared directly without density data and are treated as unsatisfied.
- **Unknown Units**: Any unrecognized unit string defaults safely to `COUNT` dimension x 1.0.

---

## 6. Screens & Navigation Structure

1. **`MainActivity`**: Host activity with a top `MaterialToolbar` and a `BottomNavigationView` managing three fragments:
   - **`PantryListFragment`**: Displays pantry inventory in a `RecyclerView`. Supports tap-to-edit, FAB to add, and swipe/button delete with confirmation dialog.
   - **`SuggestedRecipesFragment`**: Runs strict matching on a background thread and presents matching recipes.
   - **`SettingsFragment`**: Toggle for expiring-soon alerts and display unit preferences.
2. **`AddEditIngredientActivity`**: Input form with validation for adding or updating ingredients.
3. **`RecipeDetailActivity`**: Full recipe view displaying name, description, ingredient breakdown, and numbered step-by-step instructions.

---

## 7. Input Validation Rules

The `AddEditIngredientActivity` enforces the following validation rules via `TextInputLayout.setError()`:
- **Name**: Required, 2–50 characters long, and must contain at least one letter (`.*[a-zA-Z].*`).
- **Quantity**: Required, positive numeric value greater than 0 and no more than 100,000.
- **Unit**: Must be selected from the drop-down list.
- **Expiry Date**: Optional. Selecting a date in the past displays a gentle toast warning.

---

## 8. Marker Test Script

Use this script to verify strict matching behavior in the app:

### Test Case 1: Pluralization Normalization ("tomato" vs "tomatoes")
1. Add ingredient: Name = `"Tomatoes"`, Quantity = `3`, Unit = `"piece"`.
2. Navigate to **Suggested** tab.
3. **Expected Result**: **"Tomato Pasta"** appears because `"Tomatoes"` normalizes to `"tomato"` and matches the recipe requirement.

### Test Case 2: Partial Ingredients (4 of 5 Ingredients)
1. In the pantry, add:
   - `pasta` (200 g)
   - `tomatoes` (3 piece)
   - `garlic` (2 clove)
   - `olive oil` (2 tbsp)
2. Note that `cheese` is **NOT** added to the pantry.
3. Look for recipe **"Creamy Tomato Pasta"** (which requires 5 ingredients including cheese).
4. **Expected Result**: **"Creamy Tomato Pasta"** does **NOT** appear in the Suggested Recipes list (strict 5-of-5 matching enforced).
5. Add `cheese` (50 g) to the pantry.
6. **Expected Result**: **"Creamy Tomato Pasta"** now appears in Suggested Recipes.

### Test Case 3: Unit Conversion (kg vs g, cup vs ml)
1. Add `flour`: Quantity = `1`, Unit = `"kg"`.
2. Add `milk`: Quantity = `500`, Unit = `"ml"`.
3. Add `egg`: Quantity = `1`, Unit = `"piece"`.
4. Add `butter`: Quantity = `20`, Unit = `"g"`.
5. **Expected Result**: **"Pancakes"** (which requires 150 g flour, 1 cup milk, 1 egg, 20 g butter) appears in Suggested Recipes because `1 kg = 1000 g >= 150 g` and `500 ml >= 1 cup (240 ml)`.
