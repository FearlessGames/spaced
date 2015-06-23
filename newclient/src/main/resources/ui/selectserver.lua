require("ui/uisetup")
require("ui/timer")
require("ui/components/filledpanel")
require("ui/components/picture")
require("ui/components/container")
require("ui/components/label")
require("ui/components/texturebutton")
require("ui/fonts")
require("ui/cutscene/cutscene")



local function addFront(button, path)
	button.front = Picture:New(button, path)
end

-- Create connection UI



local winSizeWidth = uiParent:GetWidth()
local winSizeHeight = uiParent:GetHeight()
local picSizeWidth = winSizeWidth - 25
local aspectRatio_800_424 = 800/424
local connectionFrameBackground = FilledPanel:New(uiParent, {width = winSizeWidth, height = winSizeHeight, texture = "gui/frame/flatwhite.png" })
local connectionFramePicture = Picture:New(connectionFrameBackground, "gui/screens/tfland.jpg")

local picSizeHeight = picSizeWidth/aspectRatio_800_424
connectionFrameBackground:SetColor(0.0, 0.0, 0.0, 1)
connectionFrameBackground:SetPoint("MIDCENTER", uiParent, "MIDCENTER", 0, 0)
connectionFramePicture:SetWidth(picSizeWidth - 2)
connectionFramePicture:SetHeight(picSizeHeight - 2)
connectionFramePicture:SetPoint("MIDCENTER", connectionFrameBackground, "MIDCENTER", 0, 0)

local connectionFrameFrame = Container:New(connectionFramePicture)
connectionFrameFrame:SetSize(picSizeWidth, picSizeHeight)
connectionFrameFrame:SetColor(0.8, 0.5, 1, 0.01)
connectionFrameFrame:SetPoint("MIDCENTER", connectionFramePicture, "MIDCENTER", 0, 0)

local script = function(cutscene)
	local pic = cutscene:Image("gui/rocket.png")
	cutscene:SetPosition(pic, -300, -0)
	local r = newrandom()
	cutscene:Sleep(r:random(2000, 10000))
	while true do
		local y = r:random(50, winSizeHeight - 100)
		cutscene:SetPosition(pic, -300, -y)
		cutscene:LinearMove(pic, winSizeWidth, -y + r:random(200, 400) , 2000)
		cutscene:Sleep(2000)
		cutscene:Sleep(r:random(2000, 10000))
	end
end
local backgroundAnimation = CutsceneFrame:New(connectionFrameFrame, script)
backgroundAnimation:Start()

local connectionBannerPicture = Picture:New(uiParent, "gui/screens/FG_A3d_banner.png")
connectionBannerPicture:SetPoint("BOTTOMLEFT", connectionFramePicture, "BOTTOMLEFT", -3, -14)

local connectionAlphaBannerPicture = Picture:New(connectionFramePicture, "gui/screens/preAlphaBanner.png")
connectionAlphaBannerPicture:SetPoint("TOPLEFT", connectionFramePicture, "TOPLEFT", 0, 0)

local function connectToServer(self)
	accountSettings:setAutoLoginServer(self.host)
	accountSettings:setAutoLoginPort(self.port)
	Connect(self.host, self.port)
end

local function updateButton(button, serverInfo)
	button.name = serverInfo.serverName
	button.nameText:SetText(button.name)
	button.nameText:SetPoint("MIDCENTER", button, "TOPCENTER", 0, -30)

	button.host = serverInfo.host
	button.port = serverInfo.port
	button.hostText:SetText(button.host)
	button.hostText:SetPoint("MIDCENTER", button, "TOPCENTER", 0, -70)

	local statusTemp = serverInfo.status
	button.status = serverInfo.status
	if statusTemp == "MAINTENANCE" or statusTemp == "UNKNOWN" or statusTemp == "STARTING" then
		-- do nothing
	else
		if statusTemp == "ONLINE" and not serverInfo.validProtocol then
		 	button.status = "PROTOCOL_MISMATCH"
		 	statusTemp = "Protocol mismatch"
		elseif serverInfo.onlineCount then
			statusTemp = statusTemp .. ": " .. serverInfo.onlineCount
		end
	end
	button.statusText:SetText(statusTemp)
	button.statusText:SetPoint("MIDCENTER", button, "TOPCENTER", 0, -100)
	button:UpdateColor()

end

local function createConnectButton(parent, x, y, serverInfo)

	local button = TextureButton:New(parent, 252, 124, "gui/login/systembutton.png")

	button:SetPoint("MIDCENTER", parent, "MIDCENTER", x, -y)

	button.nameText = Label:New(button.contentPanel, "-", 34, GetHeadline1())
	button.nameText:SetAlign("MIDDLE")
	button.nameText:SetPoint("MIDCENTER", button, "TOPCENTER", 0, -30)

	button.hostText = Label:New(button.contentPanel, "-", 16, GetBodyFont())
	button.hostText:SetAlign("MIDDLE")
	button.hostText:SetPoint("MIDCENTER", button, "TOPCENTER", 0, -70)

	button.statusText = Label:New(button.contentPanel, "-", 20, GetHeadline2())
	button.statusText:SetAlign("MIDDLE")
	button.statusText:SetPoint("MIDCENTER", button, "TOPCENTER", 0, -100)
	
	button:Show()
	button:AddListener("OnClick", connectToServer)

	updateButton(button, serverInfo)
	return button
end
local connectionFrame = Container:New(uiParent)
connectionFrame:SetSize(406, 402)
--[[local connectionFrameBackground = Picture:New(uiParent, "gui/login/background")
		connectionFrameBackground:SetWidth(1020)
		connectionFrameBackground:SetHeight(764)
		connectionFrameBackground:SetColor(0.8, 0.8, 0.8, 1) ]]--
local connectionFrameHeader = Picture:New(connectionFrame, "gui/login/loginheader.png")

connectionFrame:SetPoint("MIDCENTER", uiParent, "MIDCENTER", 0, 0)
connectionFrameHeader:SetPoint("TOPCENTER", connectionFrame, "TOPCENTER", 0, 68)
connectionFrame:SetColor(0.8, 0.5, 1, 0)
connectionFrame:Show()


local connectButtons = {}

local function numelements (t)
  local n = 0
  for x in pairs(t) do
   n = n +1
  end
  return n
end

-- Auto login trick - change to your settings
local function autoLogin(screen)
	if accountSettings:getAutoLogin() then
		Connect( accountSettings:getAutoLoginServer(),  accountSettings:getAutoLoginPort())
	end
end

-- Setup state changes

local function updateState()
	runEvery(UpdateServers, 5)
	autoLogin(1)
end

local servers = {}

local function updateServerList(serverInfo)
	local button = servers[serverInfo.serverName]

   updateButton(button, serverInfo)
end

for i, serverInfo in pairs(GetServers()) do
	local button = createConnectButton(connectionFrame, 0, -250 + 150*i, serverInfo)
	servers[serverInfo.serverName] = button
end

local function connectionFrameOnEvent(event, arg1)
	if event == "GOT_SERVER_INFO" then
		updateServerList(arg1)
	end
end

RegisterEvent("GOT_SERVER_INFO", connectionFrameOnEvent)

updateState()

UpdateServers()

