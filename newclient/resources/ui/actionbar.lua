require("ui/unitfunctions")
require("ui/spellbutton")
require("ui/actionslot")
require("ui/components/container")

ActionBar = extend({}, Container)

function ActionBar:New(slots, point, x, y, buttonSize, padding, isHorizontal)
	local this = Container:New(uiParent)
	extend(this, self)

	this.background = Container:New(this)
	this.background:SetPoint("TOPRIGHT", this, "TOPRIGHT", 0, 0)

	this:SetPoint(point, uiParent, point, x, y)
	this.slots = {}
	this.buttonSize = buttonSize

	local xOffset = 0
	local yOffset = 0
		
	if(isHorizontal) then
		xOffset = buttonSize + padding
	else
		yOffset = buttonSize + padding
	end

	for index=1, slots do
		local slot = ActionSlot:New(this, index, buttonSize, buttonSize)
      slot:SetPoint("TOPLEFT", this, "TOPLEFT", (index - 1) * xOffset, (index - 1) * yOffset)
		this.slots[index] = slot
	end

	return this;
end

function ActionBar:GetActionSlot(index)
	return self.slots[index]
end

function ActionBar:AddMacroButton(index, button, keyBinding)
	button:SetPoint("TOPLEFT", self.slots[index], "TOPLEFT", 0, 0)
	if(keyBinding ~= nil) then
		self.slots[index]:SetKeyBinding(keyBinding, keyBinding)
	end
	self.slots[index]:SetActionButton(button)
end


function ActionBar:AddSpellButton(index, spell)
	local castTime = spell:GetCastTime() / 1000
	local name = spell:GetName()
	local rangeStart, rangeEnd = spell:GetRange()
	local heat = spell:GetHeat()
	local picture = GetSpellPicture(name)

	if not picture then
		print("Could not find picture for spell " .. name)
		return
	end

	local button = SpellButton:New(self.slots[index], picture, self.buttonSize, self.buttonSize, castTime, spell, self.slots[index])
	button:SetPoint("TOPLEFT", self.slots[index], "TOPLEFT", 0, 0)
	self.slots[index]:SetActionButton(button)

   button:AddListener("OnUpdate", function(self, timeElapsed)
		if(HasTarget()) then
			local distance = GetUnitDistance(GetTarget())
			if(distance < rangeStart or distance > rangeEnd) then
				button.icon:SetColor(0.6, 0.6, 0.6, 0.5)
				return
			else
				local stats = UnitStats(GetSelf())
				local currentHeat = stats:GetCurrentHeat()
				local maxHeat = stats:GetMaxHeat()
				if(currentHeat + heat > maxHeat) then
					button.icon:SetColor(1, 0, 0, 0.7)
					return
				end
			end
		end
		button.icon:SetColor(1, 1, 1, 1)
   end)
end

function ActionBar:RemoveSpellButton(index)
	self.slots[index]:SetActionButton(nil)
end

function ActionBar:Clear()
	for i, v in ipairs(self.slots) do
		self:RemoveSpellButton(i)
	end
end

