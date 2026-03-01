# MineColonies-Ato-bot-1.16.5

## Colony Intelligence & Automation (client-side Forge addon)

This repository now includes a Java source scaffold for a strictly client-side addon mod with mod id `colony_ai` and a modular architecture for:

- HappyVisual-inspired central menu (`M`) with tabs: Logistics, Combat, AFK/Survival, Settings.
- Builder HUD overlay cards rendered with `MatrixStack` and per-worker progress bars.
- Smart Courier logistics loop, including chunk-loader patrol behavior over active construction sites.
- Combat safety checks for citizen protection and priority pickup behavior for `minecolonies:ancienttome`.
- AFK survival routines with auto-food slot selection based on combat/peace priorities.
- Safety controls: input blocking while GUI is open, emergency 100-tick pause on movement keys, and kill switch on `BACKSPACE`.

> Note: MineColonies, Baritone, Spartan Weaponry, Comforts, and Sophisticated Backpacks integration points are implemented as facades/hooks so real API calls can be wired in once the ForgeGradle build + dependencies are added.
