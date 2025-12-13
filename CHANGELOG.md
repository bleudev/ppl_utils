# 2.7.1
## Fixes
[-] Disable thicken fog\
[/] Join/leave messages are visible with Figura installed\
[/] Global chat screen effect is visible on servers doesn't support it

# 2.7
## Depends updates
[/] Minimal midnightlib version now is `1.9.1`
## New features
[+] Render red screen when an error occurs (bleudev [#27](https://github.com/bleudev/ppl_utils/pull/27))\
[+] `/sit` and `/lay` commands keybinds (bleudev [#27](https://github.com/bleudev/ppl_utils/pull/27))
## Bug fixes
[/] Mod menu config doesn't work on 1.21.6+ (bleudev [#27](https://github.com/bleudev/ppl_utils/pull/27))\
[/] Restart bar doesn't work (bleudev [#27](https://github.com/bleudev/ppl_utils/pull/27))

# 2.6
## New features
[+] Toggle global chat keybind (with text overlay in chat with animation)\
[+] Send to global chat keybind (to open chat with global chat command)
## Fixes
[+] Added `neo.play.pepeland.net` to pepeland ips. If you play at it, you're welcome)

# 2.5
## Breaking changes
[-] Many classes and packages were renamed. Please, if you're developer which mod depends on `ppl_utils`
see [#11](https://github.com/bleudev/ppl_utils/pull/11) and update your mod
## Fixes
[/] Remove lobby button from `lobby` world (bleudev [#10](https://github.com/bleudev/ppl_utils/pull/10))
## Optimisations
[+] Use regexes in `extractPlayer()` (join/leave messages rendering)\
[/] Reformat class structure (bleudev [#11](https://github.com/bleudev/ppl_utils/pull/11))
## New features
[+] Restart indicator (client side boss bar) (bleudev [#11](https://github.com/bleudev/ppl_utils/pull/11))

# 2.4
## Depends updates
[+] `fabric-loom` plugin version was updated to `1.13.3`\
[+] Minimal `fabricloader` version is `0.18.0`

## New features
[+] Added comments in config to improve user experience\
[+] Added ability to change lobby button style (between Pepeland logo and monochrome version)

# 2.3.1
## Bug fixes
[/] Specified players join/leave messages doesn't appear with [Chat Heads](https://modrinth.com/mod/chat-heads) (bleudev [#7](https://github.com/bleudev/ppl_utils/pull/7))

# 2.3
## New features
[+] Add the ability to specify players whose join/leave messages will always be displayed

# 2.2
## New features
[+] `Go to the lobby` key\
[+] Toggle join/leave messages rendering

# 2.1
## Minecraft updates
[+] Update for 1.21.6-1.21.8
## Fixes
[/] World border precision changed to `10^3`\
[-] Deleted unused `com.bleudev.ppl_utils.client.impl.LobbyButtonText` class

# 2.0
## Breaking changes
[/] Rewrite from Kotlin to Java
## New features
[+] World border size debug hud entry (thanks `wiihxhx` for idea)\
[+] Beta mode\
[+] Set logo, description, links, badges and other metadata
## Updates
[+] Updated lobby button rendering