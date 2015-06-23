require("ui/uisetup")
require("ui/components/borderpanel")
require("ui/components/label")

local stats = BorderPanel:New(uiParent, 170, 20)
stats:SetPoint("TOPRIGHT", uiParent, "TOPRIGHT", -25, -4)
stats:SetColor(0.8, 0.8, 0.8, 0.5)

stats.zone = Label:New(stats, "-", 12)
stats.zone:SetPoint("MIDLEFT", stats, "TOPLEFT", 3, -12)
stats.zone:SetColor(0.5, 1, 0.3, 1)

local zoneTextShadow = Label:New(uiParent, "", 42)
zoneTextShadow:SetPoint("MIDCENTER", uiParent, "MIDCENTER", 2, 234)
zoneTextShadow:SetText("")
zoneTextShadow:SetColor(0, 0, 0, 1)
zoneTextShadow:Hide()

local zoneText = Label:New(uiParent, "", 42)
zoneText:SetPoint("MIDCENTER", uiParent, "MIDCENTER", 0, 236)
zoneText:SetText("")
zoneText:SetColor(0.5, 1, 0.3, 1)
zoneText:Hide()

local fadeOutElapsed = 0
local function startFadeOutZoneName()
	local name = stats.zone:GetText()
	zoneText:SetText(name)
	zoneText:Show()
	zoneTextShadow:SetText(name)
	zoneTextShadow:Show()
	fadeOutElapsed = 0
end

local TIMEOUT = 2.0

zoneText:AddListener("OnUpdate", function(self, timeElapsed)
	fadeOutElapsed = fadeOutElapsed + timeElapsed
	local alpha = (TIMEOUT - fadeOutElapsed) / TIMEOUT
	if alpha < 0 then
		zoneText:Hide()
		zoneTextShadow:Hide()
	end
	zoneText:SetColor(0.5, 1, 0.3, alpha)
	zoneTextShadow:SetColor(0, 0.3, 0, alpha)
end)

zoneChangedEventHandlers = {
	ZONE_CHANGED = function(old, new)
		stats.zone:SetText(new:GetName())
		startFadeOutZoneName()
	end
}

local ZoneChangedOnEventCallback = function(event, ...)
	zoneChangedEventHandlers[event](...)
end

RegisterEvent("ZONE_CHANGED", ZoneChangedOnEventCallback)
