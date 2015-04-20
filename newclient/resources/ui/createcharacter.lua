require("ui/uisetup")
require("ui/timer")
require("ui/components/texturebutton")
require("ui/components/picture")
require("ui/components/label")
require("ui/components/borderpanel")
require("ui/components/filledpanel")
require("ui/components/editbox")
require("ui/components/rtt")
require("ui/fonts")

local connectionFrameBackground = Picture:New(uiParent, "gui/frame/flatwhite")
local connectionFramePicture = Picture:New(connectionFrameBackground, "gui/screens/charcreate.jpg")

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
connectionFrameBackground:CenterOn(uiParent)
connectionFramePicture:CenterOn(connectionFrameBackground)

local connectionFrameFrame = BorderPanel:New(connectionFramePicture, picSizeWidth, picSizeHeight)
local connectionFrameShade = FilledPanel:New(connectionFrameFrame, {width = picSizeWidth, height = picSizeHeight, texture = "gui/login/systembutton.png"})
connectionFrameFrame:SetColor(0.8, 0.5, 1, 0.1)
connectionFrameShade:SetColor(0.3, 0.3, 0.3, 0.5)
connectionFrameFrame:CenterOn(connectionFramePicture)
connectionFrameShade:CenterOn(connectionFrameFrame)


local playerGender = "MALE"
local createNewCharacterButton = TextureButton:New(connectionFrameFrame, 190, 40, "gui/login/systembutton.png")
createNewCharacterButton:SetPoint("BOTTOMCENTER", connectionFrameFrame, "BOTTOMCENTER", 0, 25)
local text = Label:New(createNewCharacterButton.contentPanel, "Create character", 22)
text:CenterOn(createNewCharacterButton)


local newCharacterName = EditBox:New(connectionFrameFrame, 190, 32, 28, GetInputFont())
newCharacterName:SetPoint("BOTTOMCENTER", createNewCharacterButton, "TOPCENTER", 0, 20)
newCharacterName:SetColor(0.7, 0.7, 0.7, 0.8)



local nameText = Label:New( connectionFrameFrame, "Name: ", 22, GetHeadline1())
nameText:SetColor(1, 1, 1, 1)
nameText:SetPoint("BOTTOMLEFT", newCharacterName, "TOPLEFT", 0, 10)
nameText:SetAlign("LEFT")



local errorShadowText = Label:New(connectionFrameFrame, "", 15, GetHeadline2())
errorShadowText:SetPoint("MIDLEFT", newCharacterName, "MIDRIGHT", 8, -2)
errorShadowText:SetAlign("LEFT")
errorShadowText:SetColor(0, 0, 0, 1)
local errorText = Label:New(connectionFrameFrame, "", 15, GetHeadline2())
errorText:SetPoint("MIDLEFT", newCharacterName, "MIDRIGHT", 10, 0)
errorText:SetAlign("LEFT")
errorText:SetColor(1, 0.3, 0.3, 1)


local function createCharacter()
	CreateCharacter(newCharacterName:GetText(), playerGender)
end

createNewCharacterButton:AddListener("OnClick", createCharacter)

RegisterEvent("PLAYER_CREATED", function(event, name, uuid)
	SelectCharacterScreen()
end)

RegisterEvent("PLAYER_CREATION_FAILED", function(event, name, reason)
	errorText:SetText("FAILED: " .. reason)
	errorShadowText:SetText("FAILED: " .. reason)
end)


local backButton = TextureButton:New(connectionFrameFrame, 80, 40, "gui/login/systembutton.png")
backButton:SetPoint("MIDLEFT", createNewCharacterButton, "MIDRIGHT", 10, 0)
local backButtonText = Label:New(backButton.contentPanel, "Cancel", 18)
backButtonText:SetPoint("MIDCENTER", backButton, "MIDCENTER", 0, -1)


backButton:AddListener("OnClick", function()
	SelectCharacterScreen()
end)


local model3DView = Rtt:New(connectionFrameFrame, 220, 346, 500, 500, "models/players/player_male.xmo")
model3DView:SetModelOffset(0, 30)
model3DView:SetPoint("BOTTOMCENTER", connectionFrameFrame, "MIDCENTER", 10, -100)

local rotAngle = 0
model3DView:AddListener("OnUpdate", function()
	model3DView:RotateYAxis(rotAngle)
	model3DView:Update()
	rotAngle = rotAngle + 0.03	
end)

local choicesFrame = BorderPanel:New(connectionFrameFrame, 230, 346)
choicesFrame:SetPoint("BOTTOMRIGHT", model3DView, "BOTTOMLEFT", 0, 0)
choicesFrame:SetColor(0, 0, 0, 1)

local choicesHeaderShadow = Label:New(choicesFrame, "Customize character", 20, GetHeadline1())
choicesHeaderShadow:SetPoint("TOPCENTER", choicesFrame, "TOPCENTER", -2, -17)
choicesHeaderShadow:SetColor(0, 0, 0, 1)
local choicesHeader = Label:New(choicesFrame, "Customize character", 20, GetHeadline1())
choicesHeader:SetColor(1, 1, 1, 1)
choicesHeader:SetPoint("TOPCENTER", choicesFrame, "TOPCENTER", 0, -15)



-- Gender Selection
local genderText = Label:New(choicesFrame, "Gender: ", 18, GetHeadline2())
genderText:SetColor(1, 1, 1, 1)
genderText:SetPoint("TOPLEFT", choicesFrame, "TOPLEFT", 40, -50)

local maleSymbol = Picture:New(choicesFrame, "gui/character_creation/male_symbol")
maleSymbol:SetSize(40, 40)
maleSymbol:SetPoint("TOPLEFT", genderText, "BOTTOMLEFT", 0, -20)
maleSymbol:SetCanBeActive(true)

local maleSymbolText = Label:New(choicesFrame, "Male", 15, GetBodyFont())
maleSymbolText:SetPoint("TOPCENTER", maleSymbol, "BOTTOMCENTER", 0, -10)
maleSymbolText:SetColor(1, 1, 1 ,1)

local femaleSymbol = Picture:New(choicesFrame, "gui/character_creation/female_symbol")
femaleSymbol:SetSize(40, 40)
femaleSymbol:SetAlpha(0.6)
femaleSymbol:SetCanBeActive(true)
femaleSymbol:SetPoint("TOPLEFT", genderText, "BOTTOMLEFT", 80, -20)

		
local femaleSymbolText = Label:New(choicesFrame, "Female", 15, GetBodyFont())
femaleSymbolText:SetPoint("TOPCENTER", femaleSymbol, "BOTTOMCENTER", 0, -10)
femaleSymbolText:SetColor(1, 1, 1 ,1)


maleSymbol:AddListener("OnMouseEnter", function()
	maleSymbol:SetAlpha(1)
end)
maleSymbol:AddListener("OnMouseLeave", function()
	if (playerGender ~= "MALE") then
		maleSymbol:SetAlpha(0.6)
	end
end)
maleSymbol:AddListener("OnClick", function()
	maleSymbol:SetAlpha(1)
	femaleSymbol:SetAlpha(0.6)
	playerGender = "MALE"
	model3DView:Load("models/players/player_male.xmo")
end)

femaleSymbol:AddListener("OnMouseEnter", function()
	femaleSymbol:SetAlpha(1)
end)
femaleSymbol:AddListener("OnMouseLeave", function()
	if (playerGender ~= "FEMALE") then
		femaleSymbol:SetAlpha(0.6)
	end
end)
femaleSymbol:AddListener("OnClick", function()
	femaleSymbol:SetAlpha(1)
	maleSymbol:SetAlpha(0.6)
	playerGender = "FEMALE"
	model3DView:Load("models/players/player_female.xmo")
end)

