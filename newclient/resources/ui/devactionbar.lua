require("ui/macroactionbutton")
require("ui/devbar_get_item")
require("ui/actionbar")

if IsGm() then
	local gmbar = ActionBar:New(8, "MIDLEFT", 3, -60, 40, 0, false)
	gmbar:SetName("GM bar base")
	gmbar:Clear()

	local reloadFxButton = MacroActionButton:New(gmbar, "gui/devbar/icon_rl_fx.png", 40, 40)
	reloadFxButton:SetCallback(ReloadFx)
	gmbar:AddMacroButton(1, reloadFxButton)

	local reloadUiButton = MacroActionButton:New(gmbar, "gui/icons/IconReloadUi.png", 40, 40)
	gmbar:AddMacroButton(2, reloadUiButton)
	reloadUiButton:SetCallback(ReloadUi)

	local reloadZoneButton = MacroActionButton:New(gmbar, "gui/devbar/icon_reload_zone.png", 40, 40)
	gmbar:AddMacroButton(3, reloadZoneButton)
	reloadZoneButton:SetCallback(ReloadZone)

	local toggleBoundsButton = MacroActionButton:New(gmbar, "gui/devbar/icon_view_bounds.png", 40, 40)
	gmbar:AddMacroButton(4, toggleBoundsButton)
	toggleBoundsButton:SetCallback(ToggleBounds)

	local toggleNormalsButton = MacroActionButton:New(gmbar, "gui/devbar/icon_view_normals.png", 40, 40)
	gmbar:AddMacroButton(5, toggleNormalsButton)
	toggleNormalsButton:SetCallback(ToggleNormals)

	local runInterpreterButton = MacroActionButton:New(gmbar, "gui/devbar/icon_lol_lua.png", 40, 40)
	gmbar:AddMacroButton(6, runInterpreterButton)
	runInterpreterButton:SetCallback(Interpreter)

	local toggleGodModeButton = MacroActionButton:New(gmbar, "gui/devbar/icon_god_mode.png", 40, 40)
	toggleGodModeButton:SetCallback(ToggleGodMode)
	gmbar:AddMacroButton(7, toggleGodModeButton, "G")

	local getItemsButton = MacroActionButton:New(gmbar, "gui/devbar/icon_get_items.png", 40, 40)
	getItemsButton:SetCallback(function() getItems:Toggle() end)
	gmbar:AddMacroButton(8, getItemsButton)
end