# dataHandling

A small Java practice project on the Stream API, working up to reading a real CSV
dataset and filtering it from the console.

No build tool, no dependencies — a single source file run directly by the JDK.

## Requirements

- **JDK 25 or newer.** `Main.java` is a *compact source file*: it declares
  `void main()` at the top level with no class wrapper and no `import`
  statements, relying on the implicit import of `java.base`. Developed on JDK 26.
- No Maven/Gradle, no external libraries.

## Project layout

```
dataHandling/
├── src/
│   ├── Main.java                                 all the code
│   └── WA_Fn-UseC_-HR-Employee-Attrition.xls     the dataset (see note below)
├── dataHandling.iml                              IntelliJ module
└── .gitignore
```

## Running it

**IntelliJ:** open the project and run `Main.java`. The default working directory
is the project root, which is what the relative CSV path expects.

**Command line:**

```bash
cd /path/to/dataHandling
java src/Main.java
```

The program prints the Stream API practice output, then prompts for an age range.
Enter two integers separated by a space or newline:

```
30 40
```

> The CSV path is relative (`src/WA_Fn-UseC_-HR-Employee-Attrition.xls`), so the
> working directory must be the project root. If it isn't, the program prints a
> clear message with the path it tried instead of a stack trace.

## Example output

```
=== Employees aged 30 to 40 ===

Age  Department               JobRole                         Income  Attrition     Gender
----------------------------------------------------------------------------------------
30   Research & Development   Laboratory Technician            2,693        No       Male
30   Research & Development   Laboratory Technician            2,206        No       Male
30   Research & Development   Laboratory Technician            5,126        No       Male
...

404 of 1470 employees matched.
```

## The dataset

IBM HR Analytics Employee Attrition & Performance — 1470 employees, 35 columns
(age, department, job role, income, attrition, satisfaction scores, tenure, …).

**The `.xls` extension is misleading: the file is plain CSV text,** not an Excel
binary. That is why a `BufferedReader` can read it directly with no Apache POI.
Specifics that the parser depends on:

- UTF-8 **with a BOM** on the first byte — stripped explicitly, otherwise the
  first column is named `"﻿Age"` and every lookup of `"Age"` misses
- CRLF line endings — handled transparently by `readLine()`
- **No quoted fields**, and exactly 35 values on every line, so a plain
  `split(",", -1)` is safe here

That last point is the one to revisit if the data ever changes: a CSV exported
from Excel with commas inside values would need a real parser
(e.g. Apache Commons CSV) in place of `split`.

## What the code does

**Part 1 — Stream API practice.** `filter`, `map`, `flatMap`, `sorted`, `reduce`,
`anyMatch`, `findAny` / `findFirst`, and `Stream.concat` over small in-memory lists.

**Part 2 — reading and filtering the CSV:**

1. `Files.newBufferedReader(...)` opens the file with an explicit UTF-8 charset.
2. The header line is read, the BOM stripped, and turned into an `idx` map of
   column name → position. Columns are therefore looked up **by name**, so the
   file's own column order does not matter.
3. `br.lines()` bridges the `BufferedReader` into a `Stream<String>`, which is
   mapped line → `String[]` → `Employee`.
4. `toEmployee` is the boundary where text becomes typed data:
   `Integer.parseInt` for `Age` and `MonthlyIncome`, `trim()` on everything.
5. The resulting `List<Employee>` is filtered, sorted by age, and printed as an
   aligned table with `printf`.

```java
record Employee(int age, String department, String jobRole,
                int monthlyIncome, String attrition, String gender) {}
```

## Current filters

| Filter | Source | Behaviour |
|---|---|---|
| Age range | read from stdin via `Scanner` | inclusive on both ends |
| Gender | hard-coded | `gender().startsWith("M")` — males only |

## Adding another filter

For a column already in the `Employee` record, it is one line:

```java
.filter(e -> e.monthlyIncome() >= 5000 && e.monthlyIncome() <= 20000)
```

For a column that is not in the record yet, three edits — and the last two must
stay in the same position, since records and constructor calls are positional:

1. add the component to `record Employee(...)`
2. add the matching `cells[idx.get("ColumnName")].trim()` argument in `toEmployee`
3. add the `.filter(...)`

Keep `.filter()` before `.sorted()` so only the surviving rows get sorted.

## Notes

- `out/` is git-ignored; the compiled classes are not tracked.
- `printf` silently ignores extra arguments — if a column stops appearing in the
  output, count the `%` specifiers against the arguments.
