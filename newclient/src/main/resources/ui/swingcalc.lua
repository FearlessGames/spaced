require"ui/unitfunctions"

function combatTimeCalc_Create(prefix)
	local combatTimer = {}

combatTimer = BorderPanel:New(uiParent, 20, 20)
combatTimer:SetPoint("BOTTOMLEFT", uiParent, "BOTTOMLEFT", 125, 125)
combatTimer:SetColor(0.3, 0.3, 0.3, 1.0)
combatTimer:Hide()

firstSwingProgress = CreateProgressCircle(22, 22, uiParent)
firstSwingProgress:SetColor(0.0, 1.0, 0.6, 1.0)
firstSwingProgress:SetPoint("BOTTOMLEFT", uiParent, "BOTTOMLEFT", 140, 138)

nextSwingProgress = CreateProgressCircle(50, 50, uiParent)
nextSwingProgress:SetColor(0.0, 1.0, 0.6, 1.0)
nextSwingProgress:SetPoint("MIDLEFT", uiParent, "MIDRIGHT", 90, 90)
nextSwingProgress:Hide()

--- Misses & hits ...

combatStats = BorderPanel:New(uiParent, 250, 28)
combatStats:SetPoint("BOTTOMLEFT", uiParent, "BOTTOMLEFT", 75, 222)
combatStats:SetColor(0.5, 0.5, 0.5, 0.0)
combatStats.misstext = Label:New(combatStats, "", 14)
combatStats.misstext:SetPoint("MIDLEFT", combatStats, "MIDLEFT", 0, 0)
combatStats.misstext:SetColor(0.5, 0.5, 0.5, 0.5)
combatStats.hittext = Label:New(combatStats, "", 14)
combatStats.hittext:SetPoint("MIDLEFT", combatStats, "MIDLEFT", 0, 18)
combatStats.hittext:SetColor(0.5, 0.5, 0.5, 0.5)

---
local hits = 0
local misses = 0
local kills = 0
local nrKills = 0
local firstSwing = 0
local nextSwing = 0
local previousSwing = 0
local starttimer = 0
local applytimer = 0

local swingtime = BorderPanel:New(uiParent, 250, 28)
swingtime:SetPoint("BOTTOMLEFT", uiParent, "BOTTOMLEFT", 85, 135)
swingtime:SetColor(0.5, 0.5, 0.5, 0.0)
swingtime.text = Label:New(swingtime, "", 18)
swingtime.text:SetPoint("MIDLEFT", swingtime, "MIDLEFT", 0, 0)
swingtime.text:SetColor(0.5, 0.5, 0.5, 0.5)

swingtime.text:AddListener("OnUpdate", function(self, timeElapsed)
	local nextswingtime = nextSwing * 10
	self:SetText(string.format("time ticker: %.2f", nextswingtime))
end)

local previousswingtime = BorderPanel:New(uiParent, 250, 28)
previousswingtime:SetPoint("BOTTOMLEFT", uiParent, "BOTTOMLEFT", 135, 165)
previousswingtime:SetColor(0.5, 0.5, 0.5, 0.0)
previousswingtime.text = Label:New(previousswingtime, "", 18)
previousswingtime.text:SetPoint("MIDLEFT", previousswingtime, "MIDLEFT", 0, 0)
previousswingtime.text:SetColor(0.5, 0.5, 0.5, 0.5)

previousswingtime.text:AddListener("OnUpdate", function(self, timeElapsed)
	local previousTime = previousSwing * 10
	self:SetText(string.format("last recorded attack time: %.2f", previousTime))
end)

firstSwingProgress:AddListener("OnUpdate", function(self, timeElapsed)
	if (firstSwing == 0) then
	firstSwing = (firstSwing + timeElapsed * 0.4) % (1)
	self:SetProgress(firstSwing >= 1 and 2 - firstSwing or firstSwing)
	else 
	nextSwing = (nextSwing + timeElapsed * 0.1) % (1)
	self:SetProgress(nextSwing >= 1 and 2 - nextSwing or nextSwing)
	end
end)


function feedbackHandlers.UNIT_COMBAT(attacker, target, actionType, damage, school)
	if IsSelf(attacker) then
		if nextSwing > 0 then
		previousSwing = nextSwing
		end
	
		if(actionType == "WOUND") then
		hits = hits + 1
		combatStats.hittext:SetText(("Hits:" .. hits))	
		elseif(actionType == "MISS") then
		misses = misses + 1
		combatStats.misstext:SetText(("Misses:" .. misses))	
		end
	
		nextSwing = 0
	end
end
function feedbackHandlers.PLAYER_ENTERED_COMBAT()

end
function feedbackHandlers.PLAYER_LEFT_COMBAT()

end
function feedbackHandlers.UNIT_DIED()

end

local function eventHandler(event, ...)
	local t = feedbackHandlers[event]
	if t then
		t(...)
	end
end

-- function feedbackHandlers.UNIT_COMBAT(target, actionType, damage, school)
--    if(actionType == "WOUND") then
	

--	elseif(actionType == "MISS") then
--	misses = misses + 1
--	combatStats.text:SetText(("Misses:" .. misses))	
--	end
-- end
	
RegisterEvent("PLAYER_ENTERED_COMBAT", eventHandler)
RegisterEvent("PLAYER_LEFT_COMBAT", eventHandler)
RegisterEvent("UNIT_COMBAT", eventHandler)
RegisterEvent("UNIT_DIED", eventHandler)

	return combatTimer;
end

timers = combatTimeCalc_Create( "player")
timers:SetPoint("BOTTOMLEFT", uiParent, "BOTTOMLEFT", 25, 25)

