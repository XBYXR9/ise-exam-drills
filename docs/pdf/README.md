# PDF versions of the docs

Every Markdown file in `docs/` as a PDF, so you can open it anywhere. The text is searchable: press **Ctrl+F**
in the PDF viewer. Each PDF has bookmarks (the outline panel) built from the headings.

| PDF | What it is | Pages |
|---|---|---|
| [`CHEAT_SHEET.pdf`](CHEAT_SHEET.pdf) | One-file cheat sheet: JUnit, EasyMock, REST, black-box, UML, quiz, git, setup | 7 |
| [`ISE_Master_Cheatsheet.pdf`](ISE_Master_Cheatsheet.pdf) | Your long Ctrl+F master cheat sheet (tags like `#easymock`, `#rest`, `#quizbank`) | 26 |
| [`ISEHN_2026_EXAM.pdf`](ISEHN_2026_EXAM.pdf) | The Aug 2026 exam: your result, all 5 exercises solved, quiz answers | 8 |
| [`PRACTICE_QUESTIONS.pdf`](PRACTICE_QUESTIONS.pdf) | New practice questions with answers | 4 |
| [`MOCK_EXAM.pdf`](MOCK_EXAM.pdf) | The June 2026 mock exam, worked answers, quiz key | 6 |
| [`EXAM_DRILLS.pdf`](EXAM_DRILLS.pdf) | The mutation table: which assertion catches which bug | 6 |
| [`SCENARIO_INDEX.pdf`](SCENARIO_INDEX.pdf) | Every practice scenario and the one rule to remember | 5 |
| [`README.pdf`](README.pdf) | Project overview | 5 |
| [`templates/README.pdf`](templates/README.pdf) | Which template to use for which task | 2 |
| [`templates/BlackBoxTemplate.pdf`](templates/BlackBoxTemplate.pdf) | Black-box tables, fill-in recipe | 3 |
| [`templates/GherkinTemplate.pdf`](templates/GherkinTemplate.pdf) | Gherkin / acceptance test template | 2 |
| [`exam_answers/README.pdf`](exam_answers/README.pdf) | The 2026 exam answers, overview | 1 |
| [`exam_answers/3_deployment_diagram.pdf`](exam_answers/3_deployment_diagram.pdf) | Deployment diagram answer | 1 |
| [`exam_answers/4_blackbox_answer.pdf`](exam_answers/4_blackbox_answer.pdf) | Black-box answer, ready to paste | 1 |
| [`pdfs/README.pdf`](pdfs/README.pdf) | Index of the original PDFs | 1 |

The `.java` templates are not here because they are code: copy them from `docs/templates/`.
Links inside the PDFs that point to other `.md` files do not work; use this table instead.

To rebuild after editing a Markdown file: `pandoc file.md -f gfm -t html5 -s --embed-resources -o x.html`, then print `x.html` to PDF
with Chromium (`--headless --print-to-pdf`).
