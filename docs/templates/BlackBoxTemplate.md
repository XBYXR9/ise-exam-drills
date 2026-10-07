# BLACK-BOX TEMPLATE — fill the blanks, never invent a layout

Always three parts. Copy the **exact** template the task gives you and replace only the `<fill>` cells.
Use `|` separators and keep the pre-filled columns.

## Step 0 — read the rules and mark every limit (30 s)

```
parameter A (text):    valid values = ______ , ______      anything else (other word, "", null) = INVALID
parameter B (number):  system limits   <= ____ invalid   |   >= ____ invalid
                       band 1: ____ .. ____  -> result ______   (only for A = ______ )
                       band 2: ____ .. ____  -> result ______   (only for A = ______ )
```

## Part 1 — equivalence classes

One class per **valid text value**, one catch-all invalid class. For the number: one class per **band per
text value**, plus one invalid class per side. Class count check: `valid texts + 1`  and  `bands × texts + 2`.

```
### `parameterA`
| Class | Valid/Invalid | Inputs |
|---|---|---|
| AC1 | Valid   | `<value 1>` |
| AC2 | Valid   | `<value 2>` |
| AC3 | Invalid | any other value or empty input, e.g. `<wrong1>`, `<wrong2>`, `` |

### `parameterB`
| Class | Valid/Invalid | Inputs |
|---|---|---|
| BC1 | Invalid | `B <= <low limit - 1>` |
| BC2 | Valid   | `A = <value 1>` with `<lo> <= B <= <hi>` |
| BC3 | Valid   | `A = <value 1>` with `<hi+1> <= B <= <max>` |
| BC4 | Valid   | `A = <value 2>` with `<lo> <= B <= <hi>` |
| BC5 | Valid   | `A = <value 2>` with `<hi+1> <= B <= <max>` |
| BC6 | Invalid | `B >= <max + 1>` |
```

- "Valid" = *accepted and evaluated*, even if the result is "rejected"/"unsupported"-style. Not "the answer is yes".
- Do **not** pair invalid text with number ranges ("PC with 4097–16384" is not needed — it cost marks in 2026).
- Case matters: `Mobile` ≠ `mobile`.

## Part 2 — one representative test per class (minimal set, ONE invalid input at a time)

Pick a **middle** value of each band. Every row breaks at most one parameter.

```
| Test Case | parameterA | parameterB | Expected Result |
|---|---|---|---|
| TC1 | `<value 1>` | `<below low limit>`      | `<invalid result>`  |   <- covers AC1 + BC1
| TC2 | `<value 1>` | `<middle of band 1>`     | `<result 1>`        |   <- AC1 + BC2
| TC3 | `<value 1>` | `<middle of band 2>`     | `<result 2>`        |   <- AC1 + BC3
| TC4 | `<value 2>` | `<middle of band 1'>`    | `<result 1'>`       |   <- AC2 + BC4
| TC5 | `<value 2>` | `<middle of band 2'>`    | `<result 2'>`       |   <- AC2 + BC5
| TC6 | `<value 2>` | `<above max>`            | `<invalid result>`  |   <- AC2 + BC6
| TC7 | `<wrong text>` | `<a perfectly valid B>` | `<invalid result>` |   <- AC3 (B must be VALID here)
```

Write the **coverage note** if the task allows free text: `TC1–TC3 cover AC1 + BC1/2/3 · … · all N classes, M cases`.

## Part 3 — three-point boundary (L−1, L, L+1)

1. Read the **heading of Part 3** — it names ONE boundary ("Mobile Minimum Engine Baseline").
2. Find that limit in the text. `L` = the **first valid value** (limit is "<= 2047 invalid" → `L = 2048`).
3. Write `L−1`, `L`, `L+1` for **that device/text value only**, with the result each really gives.

```
| Test Case | parameterB | Expected Result |
|---|---|---|
| TC10 | `L-1` | `<invalid result>` |
| TC11 | `L`   | `<valid result>`   |     <- the row that earns the mark (catches >= typed as >)
| TC12 | `L+1` | `<valid result>`   |
```

Upper-limit variant: `L` = the **last valid value**, so `L−1` valid, `L` valid, `L+1` **invalid**.
If 1 is the smallest legal value, test 0 / 1 / 2.

## Last check (20 s)
- Every expected result is spelled exactly as in the task (`unsupported`, not `invalid`).
- No value is reused across rows of the same class (5 and 7 in one band = one row).
- All three values in Part 3 sit around the **same** limit.
- Both text values have their own boundary rows **if the task asks for each**.

## Quick-table version (when the task only says "write test cases for method X")

```
| Class              | Input       | Expected Output |
|--------------------|-------------|-----------------|
| Negative           | -5          | error           |
| Not a whole number | 3.14        | error           |
| Not a number       | "ABC"       | error           |
| Empty / null       | "" / null   | error           |
| Too big            | 100         | error           |
| Special value      | 0           | 1               |
| Normal             | 7           | 5040            |
| Edge below limit   | 11          | works           |
| Edge on limit      | 12          | works           |
| Edge above limit   | 13          | error           |
```
