require("ui/components/container")
require("ui/components/picture")
require("ui/components/label")
require("ui/unitframes")
require("ui/textutil")
require("ui/components/tooltip")

BuffIcon = {}
local auraTooltip = AuraTooltip:New(uiParent)

function BuffIcon:New(parent, aura, size)
	local buffName = aura:GetName()
	local duration = aura:GetDuration()
	local iconPath = aura:GetIconPath()
	local container = Container:New(parent)
	extend(container, self)
	container:SetSize(size, size)
	local icon = Picture:New(container, iconPath)
	icon:SetSize(size, size)
   icon:SetCanBeActive(true)
	icon:SetAlpha(1)

   local durationLeftText = Label:New(container, formatTime(duration), 12)
	durationLeftText:SetPoint("BOTTOMCENTER", container, "BOTTOMCENTER", 0, -20)
	durationLeftText:SetColor(1, 1, 1, 1)

	container.timeLeft = duration
	local startBlinkFrom = duration / 10 --blink for last 10% of time
   if(startBlinkFrom > 15000)  then -- max 15s
   	startBlinkFrom = 15000
   end
   
   icon:AddListener("OnMouseEnter", function(self)
			auraTooltip:ShowAura(aura)
	end)
	icon:AddListener("OnMouseLeave", function(self)
		auraTooltip:Hide()
	end)

	container:AddListener("OnUpdate", function(self, timeElapsed)
		container.timeLeft = container.timeLeft - timeElapsed * 1000
   	durationLeftText:SetText(formatTime(container.timeLeft))

   	if(container.timeLeft > 0 and container.timeLeft < startBlinkFrom) then
   	   local alpha = math.abs(math.cos(container.timeLeft/400))
			icon:SetAlpha(alpha)
   	end
	end)

	container.icon = icon
	return container
end


BuffBar = {}

function BuffBar:New(parent, size, x, y)
	local container = Container:New(parent)
	extend(container, self)
	container:SetSize(8 * size, size)

   container.buffs = {}
   container.size = size

	return container
end


function BuffBar:AddBuff(aura)
	local buffName = aura:GetName()
	if(not self.buffs[buffName]) then
		self.buffs[buffName] = BuffIcon:New(self, aura, self.size)
	else
		self.buffs[buffName].timeLeft = aura:GetDuration()
	end
	self:UpdateLayout()
end

function BuffBar:RemoveBuff(aura)
	if(self.buffs[aura:GetName()] ~= nil) then
		self.buffs[aura:GetName()]:RemoveFromParent()
		self.buffs[aura:GetName()] = nil
	end

	self:UpdateLayout()
end

function BuffBar:RemoveBuffs()
	for k, v in pairs(self.buffs) do
		v:RemoveFromParent()
		self.buffs[k] = nil
	end
end

function BuffBar:UpdateLayout()
	local x = 0
	for k,v in pairs(self.buffs) do
      v:SetPoint("TOPLEFT", self, "TOPLEFT", x, 0)
		x = x + self.size + 2
	end
end

function BuffBar:UpdateBuffs()
	self:RemoveBuffs()
	local auras = GetAuras(self.entity)
	for i, aura in ipairs(auras) do
		self:AddBuff(aura)
	end
	self:UpdateLayout()
end

local buffBars = {}
local function setupBars()
	buffBars["player"] = BuffBar:New(uiParent, 30 * playerFrame.settings.scale)
	buffBars["player"]:SetPoint("TOPLEFT", playerFrame, "BOTTOMLEFT", 0, -5)
	buffBars["player"].entity = GetSelf()
	buffBars["player"]:UpdateBuffs()

local targetFrame = GetUnitframeByName("target")
	buffBars["target"] = BuffBar:New(uiParent, 30 * targetFrame.settings.scale)
	buffBars["target"]:SetPoint("TOPLEFT", targetFrame, "BOTTOMLEFT", 0, -5)
targetFrame:AddListener("OnShow", function()
		buffBars["target"]:Show()
		buffBars["target"].entity = GetTarget()
		buffBars["target"]:UpdateBuffs()
end)
targetFrame:AddListener("OnHide", function()
		buffBars["target"]:Hide()
		buffBars["target"].entity = nil
end)

local totFrame = GetUnitframeByName("targettarget")
	buffBars["targettarget"] = BuffBar:New(uiParent, 30 * totFrame.settings.scale)
	buffBars["targettarget"]:SetPoint("TOPLEFT", totFrame, "BOTTOMLEFT", 0, -5)
totFrame:AddListener("OnShow", function()
		buffBars["targettarget"]:Show()
		buffBars["targettarget"].entity = GetToT()
		buffBars["targettarget"]:UpdateBuffs()
end)
totFrame:AddListener("OnHide", function()
		buffBars["targettarget"]:Hide()
		buffBars["targettarget"].entity = nil
end)
end

		
RegisterEvent("ENTITY_GAINED_AURA", function(event, entity, aura)
	for _, v in pairs(ResolveUnitIds(entity)) do
		buffBars[v]:AddBuff(aura)
	end
end)
		
RegisterEvent("ENTITY_LOST_AURA", function(event, entity, aura)
	for _, v in pairs(ResolveUnitIds(entity)) do
		buffBars[v]:RemoveBuff(aura)
	end
end)            


RegisterEvent("UNIT_CHANGED_TARGET", function(event, targetter, target)
	for _, v in pairs(ResolveUnitIds(target)) do
		buffBars[v].entity = target
		buffBars[v]:UpdateBuffs()
	end
end)

RegisterEvent("UI_READY", function(event)
	setupBars()
end)