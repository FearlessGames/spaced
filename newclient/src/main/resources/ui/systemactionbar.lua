require("ui/chatmodel")
require("ui/macroactionbutton")
require("ui/inventory")

local systembar = ActionBar:New(6, "BOTTOMRIGHT", -41, 42, 40, 0, false)
systembar:Clear()

local spellBookButton = MacroActionButton:New(systembar, "gui/icons/IconSpellBook", 40, 40)
spellBookButton:SetCallback(function() playerSpellBook:Toggle() end)
spellBookButton:SetTooltipText("Spellbook")
systembar:AddMacroButton(1, spellBookButton, "P")


local inventoryButton = MacroActionButton:New(systembar, "gui/inventory/icon_inventory", 40, 40)
inventoryButton:SetCallback(function() GetPlayerInventory():Toggle() end)
inventoryButton:SetTooltipText("Inventory")
systembar:AddMacroButton(2, inventoryButton, "B")

local worldMapButton = MacroActionButton:New(systembar, "gui/icons/IconRadarStateMap", 40, 40)
worldMapButton:SetCallback(function() worldMap:Toggle() end)
worldMapButton:SetTooltipText("World map")
systembar:AddMacroButton(3, worldMapButton, "M")

local unstuckButton = MacroActionButton:New(systembar, "gui/icons/unstuck", 40, 40)
unstuckButton:SetCallback(function() Unstuck() end)
unstuckButton:SetTooltipText("Unstuck")
systembar:AddMacroButton(6, unstuckButton)


local systembar2 = ActionBar:New(2, "BOTTOMRIGHT", -162, 42, 40, 0, true)
systembar2:Clear()

local togglePaperDollButton = MacroActionButton:New(systembar2, "gui/icons/IconPaperdollButton", 40, 40)
togglePaperDollButton:SetCallback(function() playerPaperDoll:Toggle() end)
togglePaperDollButton:SetTooltipText("Paperdoll")
systembar2:AddMacroButton(1, togglePaperDollButton, "C")



