require("ui/uisetup")
require("ui/cutscene/cutscene")
require("ui/components/filledpanel")
require("ui/components/container")
require("ui/variablestore")
require("ui/fonts")



local ADDON_NAME = "intro"
local savedVars = VariableStore:Get(ADDON_NAME)
if(not savedVars["played"]) then
--if (true) then

	local winSizeWidth, winSizeHeight = uiParent:GetSize()
	local background = FilledPanel:New(uiParent, { width = winSizeWidth, height = winSizeHeight, texture = "gui/frame/flatwhite.png" })
	background:SetColor(0.0, 0.0, 0.0, 1)
	background:BringToFront()
	local container = Container:New(background)
	container:SetSize(winSizeWidth, winSizeHeight)


	local textFrame = FilledPanel:New(background, { width = 800, height = 50, texture = "gui/frame/flatwhite.png" })
	textFrame:SetColor(0.9, 0.9, 0.9, 1)
	textFrame:SetPoint("BOTTOMCENTER", background, "BOTTOMCENTER", 0, 0)
	textFrame:BringToFront()

	local script = function(cutscene)

		local dread = cutscene:Image("cutscenes/intro/space/dread.png")
		local stars = cutscene:Image("cutscenes/intro/space/stars.png")
		local theReturn = cutscene:Image("cutscenes/intro/space/return.png")
		local bioStation = cutscene:Image("cutscenes/intro/space/bio_station.png")
		local theFearless2 = cutscene:Image("cutscenes/intro/space/fearless2.png")
		local theFearless = cutscene:Image("cutscenes/intro/space/fearless.png")
		local arrival = cutscene:Image("cutscenes/intro/space/arrival.png")

		local text = cutscene:Text("", textFrame)
		text:SetFont(GetCutsceneFont())
		local text2 = cutscene:Text("", textFrame)
		text2:SetFont(GetCutsceneFont())

		local function hideTexts()
			text:Hide()
			text2:Hide()
		end

--		while true do
			text:SetPoint("TOPCENTER", textFrame, "TOPCENTER", 0, 0)
			text2:SetPoint("BOTTOMCENTER", textFrame, "BOTTOMCENTER", 0, 0)
			hideTexts()
			stars:SetAlpha(0)
			theReturn:SetAlpha(0)
			dread:SetAlpha(0)
			bioStation:SetAlpha(0)
			theFearless:SetAlpha(0)
			theFearless2:SetAlpha(0)
			arrival:SetAlpha(0)
			cutscene:Sleep(1000)
			textFrame:SetColor(0.9, 0.9, 0.9, 1)

			text:SetText("At the end of the Fourth Age")
			text2:SetText("humanity left for interspellar space")
			text:Show()
			cutscene:Sleep(4000)
			cutscene:Fade(stars, 0, 0.8, 2500)
			cutscene:Zoom(stars, 6, 1, 7000)

			text2:Show()
			cutscene:Sleep(3000)
			cutscene:Fade(stars, 0.8, 0, 1500)

			cutscene:Sleep(1500)
			hideTexts()
			cutscene:Sleep(3000)

			text:SetText("The lucky were scattered among the stars")
			text2:SetText("most now live aboard biosphere stations.")
			text:Show()

			cutscene:Sleep(2000)
			cutscene:Fade(bioStation, 0, 0.9, 9000)
			cutscene:Zoom(bioStation, 1.4, 1.6, 9000)
			cutscene:Sleep(1000)
			text2:Show()
			cutscene:Sleep(4000)
			hideTexts()
			cutscene:Fade(bioStation, 0.9, 0, 4500)
			cutscene:Sleep(4500)

			text:SetText("At the dawn of the sixth age")
			text2:SetText("one of the first dreadnoughts reappeared at Zigma Sector.")
			text:Show()
			cutscene:Fade(theReturn, 0, 0.4, 3000)
			cutscene:Zoom(theReturn, 1.4, 1.6, 14000)
			cutscene:Sleep(3000)
			cutscene:Fade(theReturn, 0.4, 0.9, 6000)
			text2:Show()

			cutscene:Sleep(4000)
			hideTexts()
			cutscene:Sleep(3000)

			text:SetText("After which Zigma went silent.")
			text:Show()
			cutscene:Sleep(1000)
			cutscene:Zoom(theReturn, 1, 0, 50)
			cutscene:Sleep(200)
			theReturn:SetAlpha(0)
			cutscene:Sleep(4000)
			hideTexts()

			cutscene:Sleep(3000)
			text:SetText("Your Corvette is ordered to depart for investigation.")
			text:Show()

			theFearless.parentAnchor = "MIDCENTER"
			theFearless.anchor = "MIDCENTER"
			theFearless2.parentAnchor = "MIDCENTER"
			theFearless2.anchor = "MIDCENTER"
			local w, h = theFearless2:GetSize()
			cutscene:LinearMoveBy(theFearless2, w/2, 100, 6500)
			cutscene:Zoom(theFearless2, 1, 1.2, 6500)
			cutscene:Fade(theFearless2, 0, 1, 1500)
			cutscene:Sleep(1000)
			w, h = theFearless:GetSize()
			cutscene:LinearMoveBy(theFearless, -w/2, -100, 5500)
			cutscene:Zoom(theFearless, 1, 1.2, 5500)
			cutscene:Fade(theFearless, 0, 1, 3000)
			cutscene:Sleep(4000)
			hideTexts()

			cutscene:Fade(theFearless, 1, 0, 1500)
			cutscene:Fade(theFearless2, 1, 0, 1500)

			cutscene:Fade(arrival, 0, 1, 3000)
			cutscene:Zoom(arrival, 1.3, 1.6, 4000)
			text:SetText("After almost a year of cryo sleep you and the crew are awoken.")
			text2:SetText("It's time to gear up and get in shape before investigating the incident.")
			text:Show()
			cutscene:Sleep(3000)
			text2:Show()
			cutscene:Sleep(3000)
			hideTexts()
			cutscene:Sleep(1000)
			cutscene:Fade(arrival, 1, 0, 1000)
			cutscene:Sleep(2000)

	end

	cutscene = CutsceneFrame:New(container, script)
	cutscene:AddListener("OnEnd", function()
		background:Hide()
		savedVars["played"] = true
		VariableStore:Save(ADDON_NAME)
	end)
	cutscene:Start()
end

