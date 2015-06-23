require("ui/actionbar")
require("ui/components/picture")

MainActionBar = extend({}, ActionBar)

local keybindings = {
	"ONE",
	"TWO",
	"THREE",
	"FOUR",
	"FIVE",
	"SIX",
	"SEVEN",
	"EIGHT",
	"NINE",
	"ZERO"
}
function MainActionBar:New(slots, point, x, y, buttonSize, padding, isHorizontal)
	local this = ActionBar:New(slots, point, x, y, buttonSize, padding, isHorizontal)
	extend(this, ActionBar)

	this.state = Picture:New(this.background, "gui/mainactionbar/playerstateborder.png")
	this.state:SetPoint("TOPLEFT", this.background, "TOPLEFT", -17, 10)

	this.frame = Picture:New(this.state, "gui/mainactionbar/mainbar.png")
	this.frame:SetPoint(point, this.state, point, 0, 0)

	return this
end


playerActionbar = MainActionBar:New(8, "BOTTOMCENTER", -245, 56, 64, 6, true)
playerActionbar:Clear()

for index, slot in ipairs(playerActionbar.slots) do
	slot:SetKeyBinding(keybindings[index], index)
end


RegisterEvent("SPELLCAST_STOPPED", function(event, attacker, spell)
	if IsSelf(attacker) then
		for index, slot in ipairs(playerActionbar.slots) do
			if slot.actionButton ~= nil and slot.actionButton.spell == spell then
				slot.actionButton:CancelAction()
			end
		end
	end
end)

RegisterEvent("SPELLCAST_STARTED", function(event, attacker, target, spell)
	if IsSelf(attacker) then
      for index, slot in ipairs(playerActionbar.slots) do
      	if slot.actionButton ~= nil and slot.actionButton.spell == spell then
				slot.actionButton:StartCast()
			end
		end
	end
end)

RegisterEvent("COOLDOWN_CONSUMED", function(event, cooldown)
	for index, slot in ipairs(playerActionbar.slots) do
		local button = slot.actionButton
		if button and button.spell then
			-- TODO: consider only updating spells that have this cooldown
			local max, current, timeleft = button.spell:GetCurrentCooldown()
			button:StartCooldown(current, max)
		end
	end
end)
