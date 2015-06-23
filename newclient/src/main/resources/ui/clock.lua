require("ui/components/borderpanel")
require("ui/components/label")

local clockFrame = BorderPanel:New(uiParent, 48, 18)
clockFrame:SetPoint("TOPRIGHT", uiParent, "TOPRIGHT", -220, -80)
clockFrame:SetColor(0.5, 0.5, 0.5, 0.7)

clockFrame.text = Label:New(clockFrame, "", 12)
clockFrame.text:SetPoint("MIDLEFT", clockFrame, "TOPLEFT", 3, -12)
clockFrame.text:SetColor(0.8, 0.8, 0.8, 0.8)

clockFrame:AddListener("OnUpdate", function(self, timeElapsed)
	clockFrame.text:SetText(GetTimeOfDay())
end)
