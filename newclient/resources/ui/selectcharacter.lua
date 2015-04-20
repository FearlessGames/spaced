require("ui/uisetup")
require("ui/timer")
require("ui/components/texturebutton")
require("ui/components/picture")
require("ui/components/borderpanel")
require("ui/components/rtt")
require("ui/components/label")

local function addFront(button, path)
	button.front = Picture:New(button, path)
end

local connectButtons = {}
local selectedButton


--my god this file is a mess
-- Background Picture
local winSizeWidth = uiParent:GetWidth()
local winSizeHeight = uiParent:GetHeight()
local picSizeWidth = winSizeWidth - 25
local aspectRatio_800_424 = 800/424
local picSizeHeight = picSizeWidth/aspectRatio_800_424

local connectionFrameBackground = Picture:New(uiParent, "gui/frame/flatwhite.png")
connectionFrameBackground:SetWidth(winSizeWidth)
connectionFrameBackground:SetHeight(winSizeHeight)
connectionFrameBackground:SetColor(0.0, 0.0, 0.0, 1)

local connectionFrameFrame = BorderPanel:New(connectionFrameBackground, picSizeWidth, picSizeHeight)
connectionFrameFrame:CenterOn(connectionFrameBackground)
connectionFrameFrame:SetColor(0.8, 0.5, 1, 0.1)


local connectionFramePicture = Picture:New(connectionFrameFrame, "gui/screens/disco.jpg")
connectionFramePicture:SetWidth(picSizeWidth - 2)
connectionFramePicture:SetHeight(picSizeHeight - 2)
connectionFramePicture:CenterOn(connectionFrameFrame)

local characterFrame = BorderPanel:New(connectionFramePicture, 400, 446)
characterFrame:CenterOn(connectionFramePicture)
characterFrame:SetColor(0.8, 0.5, 1, 1)

local characterFrameBackground = Picture:New(characterFrame, "gui/frame/flatwhite.png")
		characterFrameBackground:SetWidth(396)
		characterFrameBackground:SetHeight(442)
		characterFrameBackground:SetColor(0.14, 0.02, 0.26, 0.7)
		characterFrameBackground:CenterOn(characterFrame)

local characterImageFrame = BorderPanel:New(characterFrame, 190, 320)
characterImageFrame:SetColor(0.8, 0.8, 1, 0.6)
characterImageFrame:SetPoint("TOPLEFT", characterFrame, "TOPLEFT", 20, -40)

local characterFrameHeader = Picture:New(characterFrame, "gui/login/csheader.png")
characterFrameHeader:SetPoint("MIDCENTER", characterFrame, "TOPCENTER", 0, 17)

local selectedCharView = nil

local function createCharView(xmo)
	local selectedCharView = Rtt:New(characterImageFrame, 190, 320, 440, 440, xmo)
	selectedCharView:SetModelOffset(0, 30)
	selectedCharView:Hide()

	selectedCharView:CenterOn(characterImageFrame)
	local rotAngle = 0.84
	local angleIncrement = 0.01
	selectedCharView:AddListener("OnUpdate", function() --need to do this till i got some callback for when textures are loaded on model
		rotAngle = rotAngle + angleIncrement
		if(rotAngle > 2*math.pi) then
			rotAngle = 0
		end
		if(rotAngle < 0) then
			rotAngle = math.pi*2
		end
		selectedCharView:RotateYAxis(rotAngle)
		selectedCharView:Update()
	end)
	return selectedCharView
end



-- Set up character selection

local randomAnimations = {"WALK", "RUN", "IDLE", "DANCE1", "DANCE2", "DANCE3"}
local function selectCharacter(button)
	if(button.entity ~= nil and selectedButton ~= nil and button.entity:GetUUID() == selectedButton.entity:GetUUID()) then
	   EnterWorld()
		return;
	end
	if(selectedButton) then
		selectedButton.frame:SetAlpha(0)
	end
	selectedButton = button
	RequestCharInfo(selectedButton.entity)
	selectedButton.frame:SetAlpha(1)
	if(not selectedCharView) then
		selectedCharView = createCharView(button.entity:GetXmoPath())
	else
		selectedCharView:Load(button.entity:GetXmoPath())
	end
	selectedCharView:Show()

	local randomNumber = newrandom():random(1, #randomAnimations)
	selectedCharView:PlayAnimation(randomAnimations[randomNumber])


	accountSettings:setAutoLoginCharacter(button.entity:GetUUID())
	enterWorldButton:Enable()

	button.frame:SetColor(0.7, 1, 0.7, 1)
	--button.interactionArea:RemoveListener("OnMouseLeave")
	--button.interactionArea:RemoveListener("OnMouseEnter")
end

function EnterWorld()
	if(selectedButton) then
		LoginCharacter(selectedButton.entity:GetUUID())
	end
end

local characterButtons = {}
function getCharacterButton(index, parent)
	if not characterButtons[index] then
		characterButtons[index] = TextureButton:New(parent, 120, 40, "gui/login/systembutton.png")
	end
	return characterButtons[index]
end

local lastButtonIndex = 0
local function createCharacterButton(parent, entity)

	local button = getCharacterButton(lastButtonIndex, parent)
	local buttonframe = BorderPanel:New(button.contentPanel, 122, 42)
	button:SetPoint("TOPLEFT", parent, "TOPCENTER", 40, -lastButtonIndex*60 - 40)
	buttonframe:SetColor(0.8, 0.5, 1, 1)
	buttonframe:SetAlpha(0)

	buttonframe:CenterOn(button)
	button.frame = buttonframe

	local text = Label:New(button.contentPanel, entity:GetName(), 18)
	text:SetPoint("MIDCENTER", button, "MIDCENTER", 0, -1)
	button.text = text

	button.entity = entity
	button.status="ok"
	button:Show()
	button:AddListener("OnClick", selectCharacter)

	lastButtonIndex = lastButtonIndex + 1

	return button
end


local function setupCharacters(t)
	local selectedFirst = false
	for i,character in ipairs(t) do
		local button = createCharacterButton(characterFrame, character)
		if(not selectedFirst) then
			selectCharacter(button)
			selectedFirst = true
		end
	end
end
		
if accountSettings:getAutoLogin() then
	LoginCharacter(accountSettings:getAutoLoginCharacter())
end


local createNewCharacterButton = TextureButton:New(uiParent, 190, 40, "gui/login/systembutton.png")
createNewCharacterButton:SetPoint("MIDCENTER", uiParent, "MIDCENTER", -98, -195)
local text = Label:New(createNewCharacterButton.contentPanel, "Create new character", 18)
text:SetPoint("MIDCENTER", createNewCharacterButton, "MIDCENTER", 0, -1)


createNewCharacterButton:AddListener("OnClick", function()
	CreateCharacterScreen()
end)

enterWorldButton = TextureButton:New(uiParent, 190, 40, "gui/login/systembutton.png")
enterWorldButton:SetPoint("MIDCENTER", uiParent, "MIDCENTER", 98, -195)
enterWorldButton:Disable()
local text = Label:New(enterWorldButton.contentPanel, "Enter world", 18)
text:SetPoint("MIDCENTER", enterWorldButton, "MIDCENTER", 0, -1)
enterWorldButton:AddListener("OnClick", function()
	EnterWorld()
end)

local function handlePlayerDataUpdate(event, playerPk, gear, isGm)
	if(selectedButton.entity:GetUUID() == playerPk) then
		for slot, item in pairs(gear) do
			selectedCharView:EquipModel(item, slot)
		end
	end
end

RegisterEvent("CHARSELECT_PLAYER_DATA_UPDATE", handlePlayerDataUpdate)

setupCharacters(GetCharacters())

