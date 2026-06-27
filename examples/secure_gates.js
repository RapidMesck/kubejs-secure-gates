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

function denyIfMissingProgress(event, required, verb) {
  const progress = getProgress(event.player)

  if (progress >= required) {
    return
  }

  event.deny(`Voce precisa atingir progresso ${required} para ${verb}. Progresso: ${progress}/${required}.`)
}

SecureGateEvents.craft(event => {
  const required = CRAFT_REQUIREMENTS[event.resultId]
  if (!required) return

  denyIfMissingProgress(event, required, `craftar ${event.resultId}`)
})

SecureGateEvents.useItem(event => {
  const required = USE_ITEM_REQUIREMENTS[event.itemId]
  if (!required) return

  denyIfMissingProgress(event, required, `usar ${event.itemId}`)
})

SecureGateEvents.placeBlock(event => {
  const required = PLACE_BLOCK_REQUIREMENTS[event.blockId]
  if (!required) return

  denyIfMissingProgress(event, required, `colocar ${event.blockId}`)
})

SecureGateEvents.useBlock(event => {
  const required = USE_BLOCK_REQUIREMENTS[event.blockId]
  if (!required) return

  denyIfMissingProgress(event, required, `usar ${event.blockId}`)
})
