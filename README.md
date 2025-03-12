![Sort It Out!: A capable inventory sorting mod that works either client-side or server-side](https://cdn.jamalam.tech/mod-assets/sort-it-out-banner.png)

<div align="center">

[Report Issues](https://github.com/JamCoreModding/sort-it-out) • [Chat on Discord](https://discord.jamalam.tech) • [CurseForge](https://curseforge.com/minecraft/mc-mods/sort-it-out) • [Modrinth](https://modrinth.com/mod/sort-it-out)

</div>

<div align="center">

![A screenshot of a unsorted inventory, and a sorted inventory to the right of it](https://cdn.jamalam.tech/mod-assets/sort-it-out-screenshot.png)

</div>

Sort It Out! is an inventory sorting mod that:

- has **configurable sorting**;
- works when installed **only on the server** (even with vanilla clients!);
- works when installed **only on the client**;
- and allows **data-driven custom sort button locations**. 

## How to Use

There are multiple ways to trigger sorting:

1. Double-click an empty slot (configurable) (_client or server_).
2. Click the sort button (_client only_).
3. Press the sort keybind (default is `I`) (_client only_).

## How to Configure

Sort It Out! can be configured via an in-game configuration screen which can be accessed in Mod Menu (Fabric/Quilt) or the mods screen (Forge/NeoForge). In the case that the mod is not installed on the clientside, players can use the `/sortitout preferences` command to view and update their preferences (which will persist across rejoins and server restarts).

- `Packet Send Interval`: defines the interval, in ticks, at which packets are sent when sorting on the client side (this will not take effect if the mod is installed on the serverside). Too low of an interval may cause a server to kick you.
- `Invert Sorting`: whether to invert the sorting order.
- `Comparators`: a list of comparators to use when comparing item stacks. If the first comparator states that the stacks are equal, the second will be used to break the tie (and so on).
- `Slot Sorting Trigger`: the action to use to trigger a sort by clicking on a slot. Defaults to double clicking an empty slot, but can be set to clicking the offhand key when hovering a slot.


## FAQ

### Does this need to be installed on the server/client?

Sort It Out! works in any configuration - as long as the mod is installed on one of the server or the client.

- **Server only**: any player without the mod (even vanilla clients) will be able to double-click to sort (by default) and use `/sortitout` to change their preferences.
- **Client only**: players can join any server regardless of whether the server has the mod and will be able to double-click or use the sort buttons to sort. The config screen can be used to change preferences.
- **Server and Client** (recommended): players can sort using double-click or the sort buttons. The command or config screen can be used to change preferences.

### How do I configure the location of sort buttons in screens?

See the docs page [here](https://docs.jamalam.tech/sort-it-out/customizing-sort-buttons/#adding-a-custom-definition).

### I think I found a bug!

Report any issues you find on the [GitHub issue tracker](https://github.com/JamCoreModding/sort-it-out/issues).

[![Rent a server with Bisect Hosting: Use Code jamalam to get 25% off](https://www.bisecthosting.com/partners/custom-banners/e0cc6668-0d29-40ff-9820-4d4f5433198a.webp)](https://bisecthosting.com/jamalam)