require("ui/uisetup")
require("ui/components/picture")
require("ui/components/label")
require("ui/components/borderpanel")
require("ui/components/progressbar")


local connectionFrameBackground = Picture:New(uiParent, "gui/frame/flatwhite.png")
local connectionFramePicture = Picture:New(connectionFrameBackground, "gui/screens/nbeach.jpg")

local winSizeWidth = uiParent:GetWidth()
local winSizeHeight = uiParent:GetHeight()


local picSizeWidth = winSizeWidth - 25
local aspectRatio_800_424 = 800/424
local picSizeHeight = picSizeWidth/aspectRatio_800_424
connectionFrameBackground:SetWidth(winSizeWidth)
connectionFrameBackground:SetHeight(winSizeHeight)
connectionFrameBackground:SetColor(0.0, 0.0, 0.0, 1)
connectionFramePicture:SetWidth(picSizeWidth - 2)
connectionFramePicture:SetHeight(picSizeHeight - 2)
local connectionFrameFrame = BorderPanel:New(connectionFramePicture, picSizeWidth, picSizeHeight)
connectionFrameFrame:SetColor(0.8, 0.5, 1, 0.1)

connectionFrameBackground:CenterOn(uiParent)
connectionFramePicture:CenterOn(connectionFrameBackground)

local connectionArrivalBannerPicture = Picture:New(connectionFramePicture, "gui/screens/rally_scene.png")
connectionArrivalBannerPicture:CenterOn(connectionFramePicture)



local connectionFrameTextHeadDropShadow = Label:New(connectionFrameFrame, "Entering World", 45)
connectionFrameTextHeadDropShadow:SetPoint("BOTTOMCENTER", connectionArrivalBannerPicture, "TOPCENTER", 0, -4)
connectionFrameTextHeadDropShadow:SetColor(0.0, 0.0, 0, 1)

local connectionFrameTextHead = Label:New(connectionFrameFrame, "Entering World", 45)
connectionFrameTextHead:SetPoint("MIDCENTER", connectionFrameTextHeadDropShadow, "MIDCENTER", -1, 1)
connectionFrameTextHead:SetColor(0.98, 0.95, 1, 1)


local connectionFrameTextDropShadow = Label:New(connectionFramePicture, "please wait for stasis deactivation...", 28)
connectionFrameTextDropShadow:SetPoint("MIDCENTER", connectionFramePicture, "MIDCENTER", 1, -(0.5 * picSizeHeight - 38)-1)
connectionFrameTextDropShadow:SetColor(0.0, 0.0, 0, 1)
local connectionFrameText = Label:New(connectionFramePicture, "please wait for stasis deactivation...", 28)
connectionFrameText:SetPoint("MIDCENTER", connectionFramePicture, "MIDCENTER", 0, -(0.5 * picSizeHeight - 38))
connectionFrameText:SetColor(0.99, 0.99, 0.95, 1)



local progress = ProgressBar:New(connectionFrameFrame, connectionFrameFrame:GetWidth(), 32, 0.4, 1, 0)
progress:SetPoint("TOPLEFT", connectionFrameFrame , "BOTTOMLEFT", 0, -5)
progress:SetColor(0.37, 1, 0,37, 1)
progress:Hide()
local max
function updateProgress(event, remaining)
	if not max then
		 max = remaining
		 progress:Show()
	end
	max = math.max(max, remaining)
	local prog = max - remaining
	local percentage = prog / max
	progress:Update(prog, max,  percentage)
end

RegisterEvent("LOAD_UPDATE", updateProgress)