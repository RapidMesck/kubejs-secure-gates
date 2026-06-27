# KubeJS Secure Gates

KubeJS Secure Gates is a NeoForge mod for Minecraft 1.21.1 that lets KubeJS scripts safely block player actions before they complete on the server.

The mod is intentionally generic. It does not know about your progression system, quests, captures, ranks, permissions, or any other server-specific rule. It only exposes secure KubeJS events, and your scripts decide whether an action should be allowed or denied.

## Features

- Blocks craft result pickup before the item enters the player inventory.
- Blocks right-click item use.
- Blocks block placement.
- Blocks interaction with placed blocks.
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

`autoCraft` is registered for API stability, but automatic crafter interception is not part of the current MVP.

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

event.pos
event.hand
event.action

event.denied
event.cancelled
event.message
```

IDs are exposed as strings, for example:

```text
minecraft:diamond_pickaxe
minecraft:ender_pearl
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

This is enforced through `ResultSlot#mayPickup`, so normal click and shift-click pickup are blocked server-side.

## Item Use Gate

Triggered when a player right-clicks with an item.

```js
SecureGateEvents.useItem(event => {
  if (event.itemId == 'minecraft:ender_pearl') {
    event.deny('You cannot use Ender Pearls yet.')
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

## Block Break Gate

Triggered when a player attempts to break a block.

```js
SecureGateEvents.breakBlock(event => {
  if (event.itemId == 'minecraft:diamond_pickaxe') {
    event.deny('You cannot break blocks with this item yet.')
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

The mod does not implement progression logic. All progression checks should live in KubeJS scripts.

## Current Limitations

- `recipeId` tracking is not implemented yet; use `event.resultId` for craft rules.
- Visual hiding of locked craft results is not implemented yet.
- Automatic Crafter blocking is not implemented yet.
- Custom modded crafting menus may need specific mixins if they do not use vanilla result slots.

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