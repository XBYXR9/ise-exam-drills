# BLACK-BOX TEMPLATE — fill the blanks, never invent a layout

Always three parts. Copy the **exact** template the task gives you and replace only the `<fill>` cells.
Use `|` separators and keep the pre-filled columns.

## How to know which sentence is which cell (read this first)

The exam gives you the answer template **half filled**. The pre-filled cells are clues, and the task text has
**one sentence per class**. So the job is "cross off", not "invent".

| Sentence in the task | Becomes |
|---|---|
| "Valid devices are X and Y; anything else is invalid" | 3 text classes: X (valid), Y (valid), other (invalid) |
| "On X, 2048–4096 gives A. 4097–16384 gives B." | 2 valid number classes for X |
| "On Y, 2048–8192 gives A. 8193–16384 gives B." | 2 valid number classes for Y |
| "<= 2047 is unsupported" | 1 invalid class (low side) |
| ">= 16385 is unsupported" | 1 invalid class (high side) |

Steps:
1. Underline every range / value sentence in the task and number them. **That count must equal the number of class rows.**
2. Write each class on scrap paper (or in the answer box). Cross out the ones the template already shows.
3. The classes left over are the blanks. Type them in the `<fill>` cells, keep the `|` separators.
4. Part 2: each row must cover a class you have not used yet. Tick a class when a row covers it. Never break two inputs in one row.
   If a cell is blank, work it out from the other cells: the expected result and the number tell you the device.
5. Part 3: the **heading** names the boundary. Find that limit in the text, then write L-1, L, L+1 for that one device.

Worked mapping from the 2026 exam (Graphics Configurator):

```
DC1 = "Mobile" is valid           VC1 = <= 2047            (given in the template)
DC2 = "Console" is valid          VC2 = Mobile 2048..4096  (performance)   <- blank, from sentence "On Mobile ..."
DC3 = anything else is invalid    VC3 = Mobile 4097..16384 (quality)       (given)
                                  VC4 = Console 2048..8192 (performance)   <- blank
                                  VC5 = Console 8193..16384 (quality)      <- blank
                                  VC6 = >= 16385                           <- blank, from sentence "Hardware Memory Ceiling"
TC1 Mobile + too low  | TC2 Mobile 3000 -> VC2 | TC3 Mobile 6000 -> VC3 | TC4 -> VC4 | TC5 Console 12000 -> VC5
TC6 -> VC6 | TC7 Handheld -> DC3     Part 3 heading "Mobile Minimum Engine Baseline" -> 2047 / 2048 / 2049
```

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
