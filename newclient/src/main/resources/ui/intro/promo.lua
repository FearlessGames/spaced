require("ui/uisetup")
require("ui/cutscene/cutscene")
require("ui/components/filledpanel")
require("ui/variablestore")
require("ui/fonts")

local savedVars = VariableStore:Get("promo")


	local winSizeWidth, winSizeHeight = uiParent:GetSize()
	local halfWidth = winSizeWidth/2
	local halfHeight = winSizeHeight/2
	local background = FilledPanel:New(uiParent, {width = winSizeWidth, height = winSizeHeight, texture = "gui/frame/flatwhite.png"} )
	background:SetColor(0.0, 0.0, 0.0, 1)
if(not savedVars["skip"]) then

	local function anchorMidCenter(element)
		element.anchor = "MIDCENTER"
		element.parentAnchor = "MIDCENTER"
	end

	local numberOfStars = 0
	local random = newrandom()

	local script = function(cutscene)
		local smallStars = {}
		for i = 1, numberOfStars do
			local smallStar = cutscene:Image("cutscenes/intro/refugee/smallstar.png")
			smallStar.speed = 1 / (random:random() or 0.5)
			smallStar.alpha = random:random(30, 80) / 100
			smallStar.scale = random:random(60, 100) / 100

			anchorMidCenter(smallStar)

			cutscene:SetPosition(smallStar, random:random(-halfWidth, halfWidth), random:random(-halfHeight, halfHeight))
			cutscene:Zoom(smallStar, 1, smallStar.scale, 1)
			smallStar:SetAlpha(0)
			table.insert(smallStars, smallStar)
		end

		local spaced = cutscene:Image("cutscenes/intro/refugee/spaced.png")
		anchorMidCenter(spaced)
		local fearless = cutscene:Image("cutscenes/intro/refugee/fearless_games_inverted.png")
		anchorMidCenter(fearless)


		fearless:SetAlpha(0)
		spaced:SetAlpha(0)


		cutscene:Sleep(1000)
		cutscene:Zoom(fearless, 1, 0.8, 1)
		cutscene:Fade(fearless, 0, 1,2500)
		cutscene:Sleep(1000)
		for i = 1, #smallStars do
			cutscene:Fade(smallStars[i], 0, smallStars[i].alpha, 1000)
		end
		cutscene:Sleep(2500)

		for i = 1, #smallStars do
			local s = smallStars[i]
			local x, y = s:GetPoint()
			cutscene:LinearMove(s, x * s.speed, y * s.speed, 1500)
		end
		cutscene:Zoom(fearless, 1.0, 20, 1000)

		cutscene:Sleep(600)
		cutscene:Fade(fearless, 1,0,400)


		for i = 1, #smallStars do
			local s = smallStars[i]
			cutscene:Fade(s, s.alpha, 0, 1000)
		end
		cutscene:Sleep(2000)
		cutscene:Fade(spaced, 0,1,1500)

		cutscene:Sleep(5000)
		cutscene:Fade(spaced,1,0,1500)
		cutscene:Sleep(2500)
	end

	local cutscene = CutsceneFrame:New(background, script)
	cutscene:AddListener("OnEnd", function()
		PromoEnded()
	end)
	cutscene:Start()
else
	local script = function(cutscene)
		cutscene:Sleep(100)
	end
	local cutscene = CutsceneFrame:New(background, script)
	cutscene:AddListener("OnEnd", function()
		PromoEnded()
	end)
	cutscene:Start()
end