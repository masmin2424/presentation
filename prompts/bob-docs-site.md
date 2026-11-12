# IBM Bob prompt: turn Markdown docs into a modern documentation site

Paste the prompt below into IBM Bob at the root of the repository.
Adjust the values in square brackets before running it.

```text
You are a documentation architect. Turn the existing Markdown
files in [./docs] into a modern documentation site.

1. Analyze first
- Read every .md file in [./docs] and the README.
- Propose a site structure (sections, page order, sidebar)
  and wait for my approval before changing anything.

2. Scaffold
- Set up Docusaurus (latest, TypeScript) in [./website].
- Enable: dark mode, local search, code highlighting with
  copy button, Mermaid diagrams, mobile-friendly navigation.

3. Migrate content
- Move each .md file into the approved structure.
- Add front matter (title, description, sidebar_position).
- Fix heading levels, broken links and image paths.
- Split pages longer than ~800 words into logical sub-pages.
- Do NOT change technical meaning or invent content. If
  something is unclear or missing, list it in TODO.md.

4. Polish
- Create a landing page: short intro, 3-4 feature cards,
  a "Get started" button.
- Add a Getting Started page built from existing content.
- Use callouts (note, tip, warning) where the text already
  implies them.
- Replace text-described flows with Mermaid diagrams.
- Theme: clean, modern, primary color [#0F62FE].

5. Verify
- Run the build and fix all errors and broken-link warnings.
- Give me the commands to run locally and a summary of
  what you changed.
```
