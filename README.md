# KubeJS Secure Gates

KubeJS Secure Gates is a NeoForge mod for Minecraft 1.21.1 that lets KubeJS scripts safely block player actions before they complete on the server.

The mod is intentionally generic. It does not know about your progression system, quests, ranks, permissions, or any server-specific rule. It only exposes secure KubeJS events, and your scripts decide whether an action should be allowed or denied.

## Features

- Blocks craft result pickup before the item enters the player inventory.
- Hides blocked craft results from the client-side crafting output slot.
- Blocks right-click item use, including item use on blocks.
- Blocks block placement.
- Blocks interaction with placed blocks.
- Blocks breaking blocks with restricted items.
- Blocks vanilla Crafter automatic crafting.
- Exposes a generic `SecureGateEvents` KubeJS event group.
- Sends denial messages to the player action bar.
- Keeps all rule logic in KubeJS scripts.

## Requirements

- Minecraft `1.21.1`
- NeoForge `21.1.x`
- KubeJS `2101.7.2-build.368` or compatible

## Installation

1. Build the mod:

   ```bat
   gradlew.bat build
   ```

2. Copy the generated jar from:

   ```text
   build/libs/secure_gates-1.0.0.jar
   ```

3. Place it in the server `mods` folder together with KubeJS.

4. Add your gate rules in:

   ```text
   kubejs/server_scripts/
   ```

## KubeJS Events

The mod registers the following server-side events:

```js
SecureGateEvents.craft(event => {})
SecureGateEvents.useItem(event => {})
SecureGateEvents.placeBlock(event => {})
SecureGateEvents.useBlock(event => {})
SecureGateEvents.breakBlock(event => {})
SecureGateEvents.autoCraft(event => {})
```

## Event Methods

Every event exposes:

```js
event.deny(message)
event.cancel()
```

Use `deny(message)` when you want to block the action and show a custom message.

```js
event.deny('You have not unlocked this action yet.')
```

Use `cancel()` when you want to block the action without a custom message.

```js
event.cancel()
```

## Common Event Properties

Depending on the action, events may expose:

```js
event.player

event.item
event.itemId

event.result
event.resultId

event.block
event.blockId

event.recipeId
event.pos
event.hand
event.level
event.action

event.denied
event.cancelled
event.message
```

IDs are exposed as strings, for example:

```text
minecraft:diamond_pickaxe
minecraft:flint_and_steel
minecraft:beacon
```

## Craft Gate

Triggered when a player tries to pick up a crafting result.

```js
SecureGateEvents.craft(event => {
  if (event.resultId == 'minecraft:diamond_pickaxe') {
    event.deny('You cannot craft this item yet.')
  }
})
```

This is enforced through the result slot pickup path, so normal click and shift-click pickup are blocked server-side.

## Craft Preview Visualization

The `craft` event is also fired while the server calculates the visible crafting result.

When a script denies the preview, the server synchronizes the output slot as empty to the client. This makes blocked recipes visually disappear from the crafting output slot while keeping the authoritative pickup check in place.

You can distinguish preview from pickup with:

```js
event.action == 'CRAFT_PREVIEW'
event.action == 'CRAFT_PICKUP'
```

Most scripts do not need to check `event.action`. A normal rule based on `event.resultId` applies to both preview and pickup.

## Item Use Gate

Triggered when a player uses an item, including right-clicking a block with that item.

```js
SecureGateEvents.useItem(event => {
  if (event.itemId == 'minecraft:flint_and_steel') {
    event.deny('You cannot use Flint and Steel yet.')
  }
})
```

## Block Placement Gate

Triggered when a player attempts to place a block.

```js
SecureGateEvents.placeBlock(event => {
  if (event.blockId == 'minecraft:beacon') {
    event.deny('You cannot place Beacons yet.')
  }
})
```

## Block Interaction Gate

Triggered when a player interacts with a placed block.

```js
SecureGateEvents.useBlock(event => {
  if (event.blockId == 'minecraft:enchanting_table') {
    event.deny('You cannot use Enchanting Tables yet.')
  }
})
```

## Block Break Gate

Triggered when a player attempts to break a block.

```js
SecureGateEvents.breakBlock(event => {
  if (event.itemId == 'minecraft:diamond_pickaxe') {
    event.deny('You cannot break blocks with this item yet.')
  }
})
```

## Automatic Crafter Gate

Triggered when a vanilla Crafter attempts to craft and dispense an item.

Automatic crafters do not have a player context, so `event.player` is `null`.

```js
SecureGateEvents.autoCraft(event => {
  if (event.resultId == 'minecraft:diamond_pickaxe') {
    event.deny('This item cannot be crafted automatically.')
  }
})
```

Available properties include:

```js
event.result
event.resultId
event.recipeId
event.pos
event.level
event.player // null
```

## XP Level Test Script

This example blocks diamond pickaxe and Flint and Steel until the player reaches XP level 10. It also blocks Dark Oak placement and prevents vanilla Crafters from producing protected outputs.

```js
const REQUIRED_LEVEL = 10

const BLOCKED_CRAFTS = [
  'minecraft:diamond_pickaxe',
  'minecraft:flint_and_steel'
]

const BLOCKED_USE_ITEMS = [
  'minecraft:flint_and_steel'
]

const BLOCKED_BREAK_ITEMS = [
  'minecraft:diamond_pickaxe'
]

const BLOCKED_PLACE_BLOCKS = [
  'minecraft:dark_oak_log',
  'minecraft:dark_oak_wood',
  'minecraft:stripped_dark_oak_log',
  'minecraft:stripped_dark_oak_wood',
  'minecraft:dark_oak_planks'
]

function hasRequiredLevel(player) {
  return player && player.experienceLevel >= REQUIRED_LEVEL
}

function denyLevel(event, actionName) {
  const level = event.player ? event.player.experienceLevel : 0
  event.deny(`You need level ${REQUIRED_LEVEL} to ${actionName}. Current level: ${level}/${REQUIRED_LEVEL}.`)
}

SecureGateEvents.craft(event => {
  if (!BLOCKED_CRAFTS.includes(event.resultId)) return
  if (hasRequiredLevel(event.player)) return

  denyLevel(event, `craft ${event.resultId}`)
})

SecureGateEvents.useItem(event => {
  if (!BLOCKED_USE_ITEMS.includes(event.itemId)) return
  if (hasRequiredLevel(event.player)) return

  denyLevel(event, `use ${event.itemId}`)
})

SecureGateEvents.breakBlock(event => {
  if (!BLOCKED_BREAK_ITEMS.includes(event.itemId)) return
  if (hasRequiredLevel(event.player)) return

  denyLevel(event, `break blocks with ${event.itemId}`)
})

SecureGateEvents.placeBlock(event => {
  if (!BLOCKED_PLACE_BLOCKS.includes(event.blockId)) return
  if (hasRequiredLevel(event.player)) return

  denyLevel(event, `place ${event.blockId}`)
})

SecureGateEvents.autoCraft(event => {
  if (!BLOCKED_CRAFTS.includes(event.resultId)) return

  event.deny(`Automatic crafting is blocked for ${event.resultId}.`)
})
```

## Generic Progress Example

This example uses a generic server-defined progress function. Replace `global.getPlayerProgress(player)` with whatever your modpack already provides.

```js
const CRAFT_REQUIREMENTS = {
  'minecraft:diamond_pickaxe': 25,
  'minecraft:netherite_sword': 75,
  'minecraft:beacon': 150
}

const USE_ITEM_REQUIREMENTS = {
  'minecraft:ender_pearl': 20,
  'minecraft:elytra': 100
}

const PLACE_BLOCK_REQUIREMENTS = {
  'minecraft:beacon': 150
}

const USE_BLOCK_REQUIREMENTS = {
  'minecraft:enchanting_table': 30,
  'minecraft:anvil': 10
}

function getProgress(player) {
  return global.getPlayerProgress(player)
}

function denyIfMissingProgress(event, required, actionName) {
  const progress = getProgress(event.player)

  if (progress >= required) {
    return
  }

  event.deny(`You need progress ${required} to ${actionName}. Current: ${progress}/${required}.`)
}

SecureGateEvents.craft(event => {
  const required = CRAFT_REQUIREMENTS[event.resultId]
  if (!required) return

  denyIfMissingProgress(event, required, `craft ${event.resultId}`)
})

SecureGateEvents.useItem(event => {
  const required = USE_ITEM_REQUIREMENTS[event.itemId]
  if (!required) return

  denyIfMissingProgress(event, required, `use ${event.itemId}`)
})

SecureGateEvents.placeBlock(event => {
  const required = PLACE_BLOCK_REQUIREMENTS[event.blockId]
  if (!required) return

  denyIfMissingProgress(event, required, `place ${event.blockId}`)
})

SecureGateEvents.useBlock(event => {
  const required = USE_BLOCK_REQUIREMENTS[event.blockId]
  if (!required) return

  denyIfMissingProgress(event, required, `use ${event.blockId}`)
})
```

## Design Notes

This mod blocks actions before they are completed by the server. It does not rely on removing items after the fact, inventory tick checks, or global recipe removal.

Craft preview hiding is a server-synchronized UX feature. The secure server-side pickup gate still runs independently.

The mod does not implement progression logic. All progression checks should live in KubeJS scripts.

## Current Limitations

- Craft rules currently work best by `event.resultId`. `event.recipeId` is available for craft preview and automatic crafter, but full recipe tracking for every manual craft pickup path is still limited.
- Custom modded crafting menus may need specific mixins if they do not use vanilla crafting/result-slot flows.
- Automatic crafters do not have an associated player, so player-based checks must use separate logic for `autoCraft`.

## Development

Build with:

```bat
gradlew.bat build
```

Run a development server with:

```bat
gradlew.bat runServer
```

The built jar is generated in:

```text
build/libs/
```