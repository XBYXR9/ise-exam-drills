# ISE HN 2026 (endterm, 7 Aug 2026) — every exercise, solved and executable

The paper you just sent: **1 h 40 min, 100 points** — Unit Testing 20, REST 25, Modeling 20,
Black-box 15, Quiz 20. The graded result sheet is the best study guide there is, because it
says exactly where marks went. Everything below is checked against it.

| # | Exercise | Pts | Your 2026 result | Runnable here |
|---|---|---|---|---|
| 1 | Unit testing — `CloudServer` (implement 2 methods + write 5 tests) | 20 | **5 / 20** | `ise.testing.exam_cloudserver` |
| 2 | REST — Ticket Manager, 5 endpoints + service layer | 25 | **11.1 / 25** | `ise.rest.exam_ticket` (`gradlew runTicketServer`, port 8083) |
| 3 | Modeling — Deployment diagram (hospital) | 20 | 18 / 20 | §3 below |
| 4 | Black-box — Graphics Configurator | 15 | 12 / 15 | `ise.blackbox.exam_graphics` |
| 5 | Quiz | 20 | 17.5 / 20 | §5 below |

The two exercises that lost 24 points were the two programming ones. Both were lost to
**specific, avoidable mistakes**, listed per exercise.

```bash
gradlew.bat test --tests "ise.testing.exam_cloudserver.*"
gradlew.bat test --tests "ise.rest.TicketResourceTest"
gradlew.bat test --tests "ise.blackbox.exam_graphics.*"
```

---

## §1 — Unit testing: CloudServer (20 pts, 1 pt per test case × 10)

Code: [CloudServer.java](../src/main/java/ise/testing/exam_cloudserver/CloudServer.java) ·
tests: [CloudServerTest.java](../src/test/java/ise/testing/exam_cloudserver/CloudServerTest.java).

### The two methods — two traps in the starter code

```java
public AllocationResult allocateTask(ServerTier taskTier, double taskLoad) {
    boolean specialised = serverTier == taskTier;
    double actualRamCost = specialised ? taskLoad * 0.6 : taskLoad * 1.5;
    if (ram < actualRamCost) {
        return new AllocationResult(false, 0.0, 0.0);      // check BEFORE touching ram
    }
    ram -= actualRamCost;
    double processingSpeed = (optimizationLevel * 80.0) / (taskLoad + 10.0);
    if (specialised) processingSpeed += 40.0;
    return new AllocationResult(true, processingSpeed, actualRamCost);
}

public double freeRam(double amount) {
    if (amount <= 0.0) return ram;
    double effectiveRam = amount * (1.2 + (optimizationLevel * 0.05));
    ram = Math.min(ram + effectiveRam, 600.0 + (optimizationLevel * 40.0));   // CAP, not %
    return ram;
}
```

| Trap in the starter | Why it is wrong |
|---|---|
| `ram = ram % (600 + level*40.0)` | `%` **wraps around** to a small number. "Never exceeds the cap" means `Math.min`. The test `freeRam(60000)` would return 520, not 680. |
| `optimizationLevel * 80` (int) | Fine here only because the divisor is a `double`. If both sides were `int` you would get **integer division**. Write `80.0`. |

### The five tests — what was wrong in the starter tests

| # | Mistake in the starter | Fix |
|---|---|---|
| 1 | `cloudServer.allocateTask(...)` — return value **thrown away**, then `allocationResult.isSuccess()` on a never-assigned field → `NullPointerException` | `AllocationResult result = server.allocateTask(...)` |
| 2 | `assertEquals(allocationResult.getActualRamCost(), (2*80)/(5+10)+40)` — **integer division** (`160/15 = 10`), and cost compared against a *speed* | `assertEquals((2 * 80.0) / (5.0 + 10.0) + 40.0, result.getProcessingSpeed(), 1e-9)` |
| 3 | `assertEquals(cloudServer.getRam(), 5*0.6)` — `5*0.6` is the **cost**, not the RAM left (server started with 50) | `assertEquals(50.0 - 5.0 * 0.6, server.getRam(), 1e-9)` |
| 4 | No delta on doubles, arguments backwards (`actual, expected`) | `assertEquals(expected, actual, delta)` |
| 5 | `testFreeRamNormal` expected `10 * 1.3 = 13` — forgot the server already held 10 | expected is `10 + 13 = 23` |
| 6 | Starter failed-allocation test used RAM `0` — passes even if the guard is wrong in many ways | Use RAM `10`, task needs `15`: proves "unchanged" is a real claim |
| 7 | Tests "not implemented yet" (`fail("Test is not implemented yet")`) | 3 of your 5 lost marks. **Write the easy test, push, move on.** |

> The grader's message *"You must use assertions (assertEquals/assertTrue) and actually test the
> allocateTask() method"* means: it ran the test against a broken implementation and the test
> still passed (or never called the method).

### Numbers worth having in your head (level = 2, as in the tests)

| Scenario | Result |
|---|---|
| server 50 GB, `HIGH_MEM`, task `HIGH_MEM` load 5 | cost 3.0 · RAM left 47.0 · speed 160/15 + 40 = **50.666…** |
| server 50 GB, `STANDARD`, task `HIGH_MEM` load 5 | cost 7.5 · RAM left 42.5 · speed **10.666…** |
| server 10 GB, `STANDARD`, task `HIGH_MEM` load 10 | needs 15 → **fails**, RAM stays 10, speed 0.0, cost 0.0 |
| server 7.5 GB, task needing exactly 7.5 | **succeeds**, ends at 0 (`<` not `<=`) |
| `freeRam(10)` from 10 GB | 10 + 10 × 1.3 = **23.0** |
| `freeRam(60000)` | capped at 600 + 80 = **680.0** |

The mutation drill proves these tests are strong: four broken `CloudServer`s (modulo instead of
cap, no +40 bonus, no same-tier discount, `<=` instead of `<`) each turn exactly one test red.

---

## §2 — REST: Ticket Manager (25 pts, ~2.8 pts per test)

Code: [TicketService](../src/main/java/ise/rest/exam_ticket/TicketService.java),
[TicketResource](../src/main/java/ise/rest/exam_ticket/TicketResource.java) ·
tests: [TicketResourceTest](../src/test/java/ise/rest/TicketResourceTest.java) (11 tests, every row of the status table).

You scored 4/9. The failures, from the grader output:

| Grader message | Meaning | Cause and fix |
|---|---|---|
| `Status expected:<400> but was:<200>` (create) | `POST` with an id was accepted | `if (ticket.getId() != null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);` |
| `Status expected:<200> but was:<500>` (get one) | server crashed | usually `@PathVariable Long id` while the mapping says `{ticketId}` — **names must match** — or an `Optional.get()` on empty. Use `@PathVariable Long ticketId`. |
| `Status expected:<400> but was:<200>` (update) | id mismatch not checked | `if (!Objects.equals(ticketId, ticket.getId())) → 400` |
| `Status expected:<204> but was:<500>` (delete) | threw instead of returning | delete must be **204 always**, even for unknown ids. `removeIf`, never `get()`. |
| `expected: not <null>` (update service) | update branch returned `null` on success | return the **stored** ticket after copying fields; only return `null` when not found |

### The service, in the order the exam lists it

```java
public Ticket saveTicket(Ticket ticket) {
    if (ticket.getId() == null) {
        ticket.setId(nextId); nextId++;         // assign, THEN increment
        tickets.add(ticket);
        return ticket;                          // not null!
    }
    Optional<Ticket> stored = findTicketById(ticket.getId());
    if (stored.isEmpty()) return null;          // update must FAIL, never create
    Ticket t = stored.get();
    t.setTitle(ticket.getTitle()); t.setDescription(ticket.getDescription());
    t.setPriority(ticket.getPriority()); t.setStatus(ticket.getStatus());
    return t;
}
```

`getAllTickets()` returns `new ArrayList<>(tickets)` (the TODO says *a copy*). `findTicketById`
compares with **`.equals()`**, never `==` — `Long` objects above 127 are different objects.

### The five endpoints

```java
@RestController @RequestMapping("/tickets")
public class TicketResource {
    @PostMapping                         createTicket(@RequestBody Ticket t)               400 if id != null, else 200
    @GetMapping("/{ticketId}")           getTicket(@PathVariable Long ticketId)            404 if absent
    @GetMapping                          getAllTickets()                                   200
    @PutMapping("/{ticketId}")           updateTicket(@PathVariable Long ticketId,
                                                      @RequestBody Ticket t)               400 mismatch, 404 null from service
    @DeleteMapping("/{ticketId}")        deleteTicket(@PathVariable Long ticketId)         204 always
}
```

POST returns **200 not 201** — the exam's table says 200. Follow the table, not REST convention.

---

## §3 — Deployment diagram: the hospital (20 pts, you got 18)

Where the 2 lost points went — both **literal**:

| Lost | Why |
|---|---|
| Central Pharmacy Server `«node»` instead of `«machine»` | The statement said "is a **machine**". Copy the word. |
| Missing **NFC** path between Smart Badge and Transport Terminal | Statement 1: the badge talks to the Dispensing Cart **and** the Transport Terminal. Every sentence containing "communicates" is one line. |

### The full solution

```
«device» Smart Badge ───NFC─── «device» Dispensing Cart ───TLS─── «machine» Central Pharmacy Server
   ▭ «artifact» Staff Identity Token      ▭ «artifact» Cart UI                  ▭ «component» Pharmacy Manager ○─
        │                                 ▭ «component» Dispenser ─⊂                         ▲ "Prescription Authorization Service"
       NFC
        │
«device» Transport Terminal ───gRPC─── «machine» Central Logistics Server
   ▭ «component» Dispatch Controller ─⊂ ─⊂           ▭ «component» Dispatch Manager ○─ ○─
        two sockets, one per interface:   "Transport Scheduling Service", "Orderly Availability Service"
```

| Statement says | You draw |
|---|---|
| "X is a **device**" | 3-D box `«device»` |
| "X is a **machine**" | 3-D box `«machine»` — not `«node»` |
| "runs the Y **artifact**" | plain box `«artifact»` **nested inside** the node |
| "deploys the Z **component**" | `«component»` box nested inside |
| "communicates securely with … over TLS" | plain line **between the two nodes**, label `TLS` (no arrowhead) |
| "requires one interface provided by a component in …" | socket `⊂` on the requiring component, lollipop `○` on the providing one, **named after the service** ("Prescription Authorization Service" — never "Interface") |
| "requires **two** interfaces" | two separate lollipop/socket pairs, two names |

Rules the rubric rewards: nothing extra (extra nodes/paths are penalised), the socket points at
the lollipop in the right direction (requirer → provider), and a model that is incomplete is not
graded partially.

---

## §4 — Black-box testing: Graphics Configurator (15 pts, you got 12)

Executable: [GraphicsConfiguratorBlackBoxTest](../src/test/java/ise/blackbox/exam_graphics/GraphicsConfiguratorBlackBoxTest.java) — every row below is a real assertion.

### The submission, filled in (paste exactly this)

```
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
```

### What cost you the 3 points — read this twice

| Your answer | Grader's comment | Correct |
|---|---|---|
| VC2 listed as `vramMB >= 16385`, VC4/VC5 as **PC** classes | "vramMB range paired with the invalid deviceType inputs are **not necessary**" | VC2 and VC4 are the two **missing performance bands** (Mobile 2048–4096, Console 2048–8192). |
| TC11 = `4096` | "The boundary for the minimum engine baseline with Mobile is **2048**" | The target boundary is the one named in the heading: baseline **2048**. |
| TC12 = `16385` | "This is not one value above the boundary (= **2049**)" | Three points are *L−1, L, L+1* = **2047, 2048, 2049**, all around the **same** limit. |

**The rule:** read the heading of Part 3 ("Mobile Minimum Engine Baseline"), find that limit in
the text (≤ 2047 fails ⇒ first valid value 2048), then write limit−1, limit, limit+1. Don't wander
to a different boundary.

Other rules proven by the test: one invalid input at a time (TC1, TC6 and TC7 each break only one
parameter), `Mobile` and `Console` are case-sensitive, `VC1`/`VC6` apply to both devices.

---

## §5 — Quiz: the questions that were on the paper (20 pts)

| # | Topic | Answer | Why |
|---|---|---|---|
| 1 | Software characteristics (select all) | "Software has no natural locality" and "SE must create suitable forms of distance and isolation", and "reuse can be lucrative when little customisation" | "Reuse is always inexpensive" is false. You scored 0.6 — one wrong tick or miss. |
| 2 | Functional vs non-functional | Functional: *grade quizzes*, *issue certificate*, *enrol in courses*. Non-functional: *usable by visually impaired*, *stream without buffering on 5 Mbps*, *back up every 24 h* | Functional = **what** it does. Non-functional = **how well** (accessibility, performance, reliability). |
| 3 | INVEST — "error messages should be *helpful*" | **Testable** | "Helpful" cannot be verified. |
| 4 | `git add README.md` → state? | **Staged** | modified → *add* → staged → *commit* → committed. |
| 5 | Which line checks 0 °C = 32 °F | `assertEquals(32.0, result, 0.001)` **and** `assertTrue(result == 32.0)` | Grade each option for truth. `0*9/5+32` is exactly 32.0 so `==` passes. |
| 6 | Same login setup repeated across acceptance tests | **Common Includes** | Same setup, different features. (*Parameterized* includes = same logic, different data.) |
| 7 | Users can't figure out basic tasks | **Usability** | |
| 8 | Run new backend on real traffic, users see nothing | **Dark Launching** | *Canary* = a small share of users see it. *Blue-green* = switch whole environments. *Feature toggle* = flag on/off. |
| 9 | Cognitive complexity increases when… | **Linear flow is interrupted** and **nesting depth increases** | Declaring a method or long names add nothing. |
| 10 | Catch vulnerabilities / null pointers on every PR before build | **Automated Static Analysis (ASA)** — you answered 0/1 | Static = examines code without running it. Unit testing is *dynamic*. |
| 11 | Effective review meeting | **Focus on finding and logging defects; defer solutions** | Fixing in the meeting wastes it. |

---

## What to do differently tomorrow (the order that earns points)

1. **Clone, make a trivial change, commit, push** — before reading anything (step 12 of the BIE setup).
2. Quiz first, 15 minutes max.
3. **REST second** (25 pts, biggest block). Push after **every** endpoint.
4. Unit testing: write the **two easy tests first**; never leave `fail("not implemented")`. For every test: assign the result, assert it, delta on doubles, expected first.
5. Black-box: copy the heading of Part 3, find that exact limit.
6. Modeling last: copy words from the statement exactly (`«machine»` vs `«node»`), one line per "communicates".
