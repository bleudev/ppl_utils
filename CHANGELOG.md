# 2.5.1
## Bug fixes
[/] Restart bar doesn't work (bleudev [#20](https://github.com/bleudev/ppl_utils/pull/20))

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