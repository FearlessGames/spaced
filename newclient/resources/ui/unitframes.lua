--require("ui/test/testsetup")

require("ui/unitfunctions")
require("ui/components/progressbar")
require("ui/components/picture")
require("ui/components/borderpanel")
require("ui/components/label")
require("ui/fonts")
require("ui/mathutil")

-- maps frame name to current entity
local entitymapping = {}

-- maps entities to the frames they're in
local entityframes = {}

local function addEntityFrame(entity, newFrame)
	if not entityframes[entity] then
	 	entityframes[entity] = {}
	 end
	 entityframes[entity][newFrame] = true
end

local function removeEntityFrame(entity, oldFrame)
	if not entity then
		return
	end
	if not entityframes[entity] then
		return
	 end
	 entityframes[entity][oldFrame] = nil
end


local updateStats = function(entity) -- TODO make this a member function
	if not entity then return end
	local frames = entityframes[entity]
	if frames then
		local stats = UnitStats(entity)
		for frame in pairs(frames) do
			frame:Update(stats:GetCurrentHealth(), stats:GetMaxHealth(), stats:GetCurrentHeat(), stats:GetMaxHeat(), stats:GetCoolRate(), stats:GetShieldStrength(), stats:GetMaxShield())
		end
	end
end


local function stateAnimator (self, timeElapsed)
	-- current is between 0 and 2.   0-1 is going from white to red. 1-2 is going from red to white
	-- 0 is white (not in combat)
	-- 1 is red
	-- 2 is white
	self.current = self.current + (timeElapsed / 0.5)
	if not inCombat and self.current > 2 then
		self.current = 2
	else
		self.current = self.current % 2
	end
	local color = math.abs(1 - self.current)
	self:SetColor(1 - color, color, color, 1.0 - (color*0.9))
end

local function setupSettings(settings)
	settings.scale = settings.scale or 1
	settings.width = settings.width or 240
	settings.height = settings.height or 86
	settings.barHeight = settings.barHeight or 16
	settings.portraitSize = settings.portraitSize or 78
	if(not settings.name) then
		settings.name = {}
	end
	settings.name.fontSize = settings.name.fontSize or 20
	settings.name.fontColor = settings.name.fontColor or {r = 0.9, g = 0.9, b = 0.8, a = 1.0}

	if(not settings.stats) then
		settings.stats = {}
	end
	settings.stats.fontSize = settings.stats.fontSize or 16
	settings.stats.barWidth = settings.stats.barWidth or 152


	settings.stats.healthColor = settings.stats.healthColor or {r = 0.4, g = 1, b = 0.15 }
	settings.stats.shieldColor = settings.stats.shieldColor or {r = 0.6, g = 0.84, b = 0.91}
	settings.stats.heatColor = settings.stats.heatColor or {r = 1, g = 0.75, b = 0.1}
end


UnitFrame = extend({}, Component)


function UnitFrame:New(parent, prefix, settings)
	setupSettings(settings)
	local unit = Component:New(BorderPanel:New(parent, settings.scale * settings.width, settings.scale * settings.height).base)
	extend(unit, self)
	
	local barHeight = settings.scale * settings.barHeight

	unit:SetName(string.format("unitframe(%s).base", prefix))

	unit.state = Picture:New(unit, "gui/unitframe/unitstate.png")
	local stateWidth, stateHeight = unit.state:GetSize()
	unit.state:SetSize(settings.scale * stateWidth, settings.scale * stateHeight)
	unit.state:SetPoint("TOPCENTER", unit, "TOPCENTER", settings.scale * 41, settings.scale * -2)
	unit.state:SetColor(0, 0, 0, 0)
	if(prefix == "player") then
		unit.state.current = 0
		unit.state:AddListener("OnUpdate", stateAnimator)
	end

	local fontSize = round(settings.scale * settings.stats.fontSize)

	local font = GetFont("eras")


	local healthColor = settings.stats.healthColor
	unit.health = ProgressBar:New(unit, settings.scale * settings.stats.barWidth, barHeight, healthColor.r, healthColor.g, healthColor.b)
	unit.health:SetTextMode(TextMode.ABSOLUTE)
	unit.health:SetName(string.format("unitframe(%s).health", prefix))
	unit.health:SetPoint("TOPRIGHT", unit, "TOPRIGHT", settings.scale * -6, settings.scale * -31)
	unit.health:SetFont(fontSize, font)
	unit.health:SetTooltipText("Health")


	local shieldColor = settings.stats.shieldColor
	unit.shield = ProgressBar:New(unit, settings.scale * settings.stats.barWidth, barHeight, shieldColor.r, shieldColor.g, shieldColor.b)
	unit.shield:SetName(string.format("unitframe(%s).shield", prefix))
	unit.shield:SetTextMode(TextMode.ABSOLUTE)
	unit.shield:SetPoint("TOPLEFT", unit.health, "BOTTOMLEFT", 0, settings.scale * -2)
	unit.shield:SetFont(fontSize, font)
	unit.shield:SetTooltipText("Shield strength")

	local heatColor = settings.stats.heatColor
	unit.heat = ProgressBar:New(unit, settings.scale * settings.stats.barWidth, barHeight, heatColor.r, heatColor.g, heatColor.b)
	unit.heat:SetTextMode(TextMode.ABSOLUTE)
	unit.heat:SetName(string.format("unitframe(%s).heat", prefix))
	unit.heat:SetPoint("TOPLEFT", unit.shield, "BOTTOMLEFT", 0, settings.scale * -2)
	unit.heat:SetFont(fontSize, font)
	unit.heat:SetTooltipText("Heat")

	unit:AddListener("OnUpdate", function(self, timeElapsed)
		updateStats(GetEntityFromUnitId(prefix))
	end)

	unit.name = Label:New(unit, "-", settings.scale * settings.name.fontSize, font)

	unit.name:CenterOn(unit.state)
	local fontColor = settings.name.fontColor
	unit.name:SetColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a)
	unit:SetCanBeActive(true) -- Makes the picture clickable
	unit:AddListener("OnClick", function(self, button)
	 	SetTarget(entitymapping[prefix])
	end)

	unit.face = Picture:New(unit, "gui/unitframe/icon_player.png")
	unit.face:SetSize(settings.scale * settings.portraitSize, settings.scale * settings.portraitSize)
	unit.face:SetName(string.format("unitframe(%s).portrait", prefix))

	unit.prefix = prefix
	unit.settings = settings

	return unit;
end


function UnitFrame:SetUnit(unit, type)
	type = type or ""
	local unitName = unit:GetName()
	self.name:SetText(type .. unitName)
	local portrait = UnitPortrait(unit)

	self.face:SetTexture("gui/unitframe/" .. portrait .. ".png")
	local portraitSize = self.settings.scale * self.settings.portraitSize
	self.face:SetSize(portraitSize, portraitSize)

	self.face:SetPoint( "TOPLEFT", self, "TOPLEFT", self.settings.scale * 3, self.settings.scale -3 )
end

function UnitFrame:Update(currentHealth, maxHealth, currentHeat, maxHeat, cooldownRate, shieldPower, maxShield)
		local heatPercentage = currentHeat / maxHeat
		local healthPercentage = currentHealth / maxHealth
		local shieldPercentage = shieldPower / maxShield
		currentHeat = math.ceil(currentHeat)
		shieldPower = math.floor(shieldPower)
		maxShield = math.floor(maxShield)
		local healthDeficit = math.ceil(maxHealth - currentHealth)
		currentHealth = math.ceil(currentHealth)
		self.currentHealth = currentHealth
		self.healthDeficit = healthDeficit
		self.currentHeat = currentHeat
		self.maxHeat = maxHeat
		self.cooldownRate = cooldownRate
		self.shieldPower = shieldPower
		self.maxShield = maxShield
		if(currentHealth == 0) then
			shieldPower = 0
			shieldPercentage = 0
		end
		self.heat:Update(currentHeat, maxHeat, heatPercentage)
		self.health:Update(currentHealth, maxHealth, healthPercentage)
		self.shield:Update(shieldPower, maxShield, shieldPercentage)
		DoLayout(self.base)
end




playerFrame = UnitFrame:New(uiParent, "player", {scale = 0.9})
playerFrame:SetPoint("TOPLEFT", uiParent, "TOPLEFT", 10, -14)
playerFrame:Hide()

entitymapping["player"] = GetSelf()
entityframes[GetSelf()] = {[playerFrame] = true}

local targetFrame = UnitFrame:New(uiParent, "target", {scale = 0.9 })
targetFrame:SetPoint("TOPLEFT", playerFrame, "TOPRIGHT", 10, 0)
targetFrame:Hide()

local totFrame = UnitFrame:New(uiParent, "targettarget", {scale = 0.8})
totFrame:SetPoint("TOPLEFT", targetFrame, "TOPRIGHT", 10, 0)
totFrame:Hide()

local focusFrame = UnitFrame:New(uiParent, "focus", {scale = 0.8})
focusFrame:SetPoint("TOPLEFT", uiParent, "TOPLEFT", 50, -180)
focusFrame:Hide()

local inCombat = false

local frameByName = {player = playerFrame, target = targetFrame, targettarget = totFrame, focus = focusFrame}
function GetUnitframeByName(name)
	return frameByName[name]
end


local function updateTargetOfTargetFrame(tot)
	local oldEntity = entitymapping["targettarget"]
	removeEntityFrame(oldEntity, totFrame)

	if(tot == nil) then
		totFrame:Hide()
		entitymapping["targettarget"] = nil
	else
		entitymapping["targettarget"] = tot
		addEntityFrame(tot, totFrame)
		totFrame:Show()
		updateStats(tot)

		totFrame:SetUnit(tot)
	end
end

local function updateTargetFrame()
	local oldEntity = entitymapping["target"]
	removeEntityFrame(oldEntity, targetFrame)

	local target = GetTarget()
	if(target == nil) then
		targetFrame:Hide()
		updateTargetOfTargetFrame(nil)
		entitymapping["target"] = nil
	else
		entitymapping["target"] = target
		addEntityFrame(target, targetFrame)
		targetFrame:Show()
		updateStats(target)

		targetFrame:SetUnit(target)
		updateTargetOfTargetFrame(target:GetTarget())
	end
end


local function updatePlayerFrame()
	local entity = GetEntityFromUnitId("player");
	playerFrame.name:SetText(entity:GetName())
	updateStats(entity)
	playerFrame:SetUnit(GetSelf())
	playerFrame:Show()
	updateTargetFrame()
end

playerFrameEventHandlers = {
	PLAYER_LOGIN = updatePlayerFrame,
	UNIT_COMBAT = function(attacker, target)
        updateStats(target)
        updateStats(attacker)
	end,
	UNIT_STATS_UPDATED = function(target)
	    updateStats(target)
	end,
	PLAYER_ENTERED_COMBAT = function()
		inCombat = true
	end,
	PLAYER_LEFT_COMBAT = function()
		inCombat = false
	end,
	UI_READY = function()
		updatePlayerFrame()
  		updateTargetFrame()
	end,
	UNIT_CHANGED_TARGET = function(targetter, target)
		if targetter == GetSelf() then
			updateTargetFrame()
		end
		if targetter == GetTarget() then
			updateTargetOfTargetFrame(target)
		end
	end
}


local PlayerFrameOnEventCallback = function(event, ...)
	playerFrameEventHandlers[event](...)
end

local function AddFocusFrame(cmd, s)
	local unit
	if not s or s == "" then
		unit = GetTarget()
	else
		unit = GetEntityFromUnitId(s)
	end
	local oldEntity = entitymapping["focus"]
	removeEntityFrame(oldEntity, focusFrame)
	entitymapping["focus"] = unit
	SetFocusTarget(unit)
	if unit then
		addEntityFrame(unit, focusFrame)
      updateStats(unit)
		focusFrame:Show()
		focusFrame:SetUnit(unit, "Focus: ")
	else
		focusFrame:Hide()
	end
end

RegisterEvent("UNIT_COMBAT", PlayerFrameOnEventCallback)
RegisterEvent("UNIT_STATS_UPDATED", PlayerFrameOnEventCallback)
RegisterEvent("UNIT_CHANGED_TARGET", PlayerFrameOnEventCallback)
RegisterEvent("PLAYER_LOGIN", PlayerFrameOnEventCallback)
RegisterEvent("UI_READY", PlayerFrameOnEventCallback)
RegisterEvent("PLAYER_ENTERED_COMBAT", PlayerFrameOnEventCallback)
RegisterEvent("PLAYER_LEFT_COMBAT", PlayerFrameOnEventCallback)
RegisterEvent("COMBAT_STATISTICS_UPDATE", PlayerFrameOnEventCallback)

RegisterSlashCommand(AddFocusFrame, "Adds unit frame for unit", "/focus")


-- Can be used to debug the state
--[[
function df()
	for k, v in pairs(entityframes) do
		for x, b in pairs(v) do
			print(k:GetName(), x.prefix)
		end
	end
end
--]]