# Part 1: Equivalence Classes

### `deviceType`

| Class | Valid/Invalid | Inputs |
|---|---|---|
| DC1 | Valid | `Mobile` |
| DC2 | Valid | `Console` |
| DC3 | Invalid | any other value or empty input, e.g. `Handheld`, `PC`, `` |

### `vramMB`

| Class | Valid/Invalid | Inputs |
|---|---|---|
| VC1 | Invalid | `vramMB <= 2047` |
| VC2 | Valid | `deviceType = Mobile` with `2048 <= vramMB <= 4096` |
| VC3 | Valid | `deviceType = Mobile` with `4097 <= vramMB <= 16384` |
| VC4 | Valid | `deviceType = Console` with `2048 <= vramMB <= 8192` |
| VC5 | Valid | `deviceType = Console` with `8193 <= vramMB <= 16384` |
| VC6 | Invalid | `vramMB >= 16385` |

# Part 2: Representative Test Cases

| Test Case | deviceType | vramMB | Expected Result |
|---|---|---|---|
| TC1 | `Mobile` | `1000` | `unsupported` |
| TC2 | `Mobile` | `3000` | `performance` |
| TC3 | `Mobile` | `6000` | `quality` |
| TC4 | `Console` | `5000` | `performance` |
| TC5 | `Console` | `12000` | `quality` |
| TC6 | `Console` | `24000` | `unsupported` |
| TC7 | `Handheld` | `6000` | `unsupported` |

# Part 3: Boundary Values

#### Mobile Minimum Engine Baseline
*Tested with `deviceType = Mobile`:*

| Test Case | vramMB | Expected Result |
|---|---|---|
| TC10 | `2047` | `unsupported` |
| TC11 | `2048` | `performance` |
| TC12 | `2049` | `performance` |
