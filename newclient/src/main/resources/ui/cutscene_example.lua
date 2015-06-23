require("ui/uisetup")
require("ui/cutscene/cutscene")
require("ui/components/picture")
require("ui/components/label")


local testLabel = Label:New(uiParent, "Testing, testing", 12)
testLabel:SetPoint("TOPCENTER", uiParent, "TOPCENTER", 0, -20)
testLabel:SetAlpha(0.3)

local script = function(cutscene)
	local pic = cutscene:Image("gui/abilityicons/fortitude.png")
	local text = cutscene:Text("It was a dark and cold night")
	local missionText = cutscene:Text("Mission: go and kill them!")
	while true do
		cutscene:Fade(pic, 0, 0.5, 600)
		cutscene:Sleep(1000)
		text:SetText("... and borg bigs were roaming the forest.")
		cutscene:SetPosition(pic, 0, 0)
		cutscene:Fade(pic, 0.5, 1, 300)
		cutscene:Sleep(500)
		missionText:SetPoint("TOPLEFT", text, "TOPLEFT", 0, -10)
		cutscene:LinearMove(missionText, 500, -500, 1000)
		cutscene:Sleep(1000)
		cutscene:Fade(pic, 1, 0, 600)
		cutscene:Sleep(2000)
	end
end

cutscene = CutsceneFrame:New(uiParent, script)
cutscene:Start()
