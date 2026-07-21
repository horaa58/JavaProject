# Project editing requirements

- Preserve support for both Apache NetBeans and Visual Studio Code.
- Do not delete or ignore `build.xml`, `manifest.mf`, `nbproject/project.xml`, or `nbproject/project.properties`.
- Keep generated and user-specific files ignored: `build/`, `dist/`, `bin/`, `out/`, and `nbproject/private/`.
- After structural or build changes, run `scripts/verify-project.ps1` and `scripts/test.ps1`.
- The project must remain compatible with Java 17, and its working directory must remain the repository root so the `data/` paths continue to work.
