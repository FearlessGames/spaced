require("ui/uisetup")
require("ui/timer")
require("ui/components/texturebutton")
require("ui/components/filledpanel")
require("ui/components/button")
require("ui/components/editbox")
require("ui/components/label")
require("ui/components/panel")
require("ui/fonts")

local function addFront(button, path)
	button.front = Picture:New(button, path)
end

local LOGIN_FRAME_COLOR = { 0.11, 0.05, 0.23, 0.80 }
local PANEL_COLOR = { 1, 1, 1, 0.15 }
local EDIT_COLOR = { 0.8, 0.5, 1, 1 }

local connectionFrameBackground = Picture:New(uiParent, "gui/frame/flatwhite.png")
local winSizeWidth = uiParent:GetWidth()
local winSizeHeight = uiParent:GetHeight()
local globalSalts
connectionFrameBackground:SetSize(winSizeWidth, winSizeHeight)
connectionFrameBackground:SetColor(0.0, 0.0, 0.0, 1)
connectionFrameBackground:SetPoint("MIDCENTER", uiParent, "MIDCENTER", 0, 0)


local picSizeWidth = winSizeWidth - 24
local aspectRatio_800_424 = 800 / 424
local picSizeHeight = picSizeWidth / aspectRatio_800_424

local connectionFrame = Panel:New(uiParent, { width = picSizeWidth, height = picSizeHeight, texture = "gui/screens/ltown.jpg" })
connectionFrame:SetPoint("MIDCENTER", connectionFrameBackground, "MIDCENTER", 0, 0)




local loginFrame = Panel:New(uiParent, { width = 360, height = 300 })
loginFrame:SetPoint("MIDCENTER", connectionFrame, "MIDCENTER", 0, 0)
loginFrame:SetColor(LOGIN_FRAME_COLOR)

local loginFrameHeader = Label:New(loginFrame, "Enter Account Information", 28, GetHeadline1())
loginFrameHeader:SetPoint("MIDCENTER", loginFrame, "TOPCENTER", 0, 22)
loginFrameHeader:SetColor(1, 1, 1, 1)

local accountPanel = Panel:New(loginFrame:GetContent(), { width = 280, height = 85, border = { enabled = false } })
accountPanel:SetPoint("TOPCENTER", loginFrame:GetContent(), "TOPCENTER", 0, 0)
accountPanel:SetColor(PANEL_COLOR)
local accountHeadline = Label:New(accountPanel:GetContent(), "Account name", 20, GetHeadline2())
accountHeadline:SetColor(1, 1, 1, 1)
accountHeadline:SetPoint("TOPLEFT", accountPanel:GetContent(), "TOPLEFT", 0, 0)

local accountName = EditBox:New(accountPanel:GetContent(), 250, 36, 24, GetInputFont())
accountName:SetText(accountSettings:getAccount())
accountName:SetColor(EDIT_COLOR)
accountName:SetPoint("TOPLEFT", accountHeadline, "BOTTOMLEFT", 0, -2)

local passwordPanel = Panel:New(loginFrame:GetContent(), { width = 280, height = 85, border = { enabled = false } })
passwordPanel:SetPoint("TOPCENTER", accountPanel, "BOTTOMCENTER", 0, -10)
passwordPanel:SetColor(PANEL_COLOR)
local passwordHeadline = Label:New(passwordPanel:GetContent(), "Password", 20, GetHeadline2())
passwordHeadline:SetColor(1, 1, 1, 1)

passwordHeadline:SetPoint("TOPLEFT", passwordPanel:GetContent(), "TOPLEFT", 0, 0)

local password = EditBox:NewPassword(passwordPanel:GetContent(), 250, 36, 24, GetInputFont())
password:SetText(accountSettings:getPassword())
password:SetColor(EDIT_COLOR)
password:SetPoint("TOPLEFT", passwordHeadline, "BOTTOMLEFT", 0, -2)

local authenticatorFrame = Panel:New(uiParent, { width = 360, height = 300 })
authenticatorFrame:Hide();
authenticatorFrame:SetPoint("MIDCENTER", connectionFrame, "MIDCENTER", 0, 0)
authenticatorFrame:SetColor(LOGIN_FRAME_COLOR);

local authenticatorFrameHeader = Label:New(authenticatorFrame, "Enter Authenticator Key", 28, GetHeadline1())
authenticatorFrameHeader:SetPoint("MIDCENTER", authenticatorFrame, "TOPCENTER", 0, 22)
authenticatorFrameHeader:SetColor(1, 1, 1, 1)

local authenticatorKey = EditBox:New(authenticatorFrame:GetContent(), 250, 36, 24, GetInputFont())
authenticatorKey:SetColor(EDIT_COLOR)
authenticatorKey:SetPoint("MIDCENTER", authenticatorFrame, "MIDCENTER", 0, 0)

function authenticateWithAuthenticator()
	local key = authenticatorKey:GetText()
	local name = accountName:GetText()
	local pwd = password:GetText()
	AuthenticateAccount(name, pwd, globalSalts, key)
end

authenticatorButton = Button:New(authenticatorFrame:GetContent(), 94, 44, "Authenticate")
authenticatorButton:SetPoint("BOTTOMCENTER", authenticatorFrame, "BOTTOMCENTER", 0, 0)
authenticatorButton:AddListener("OnClick", authenticateWithAuthenticator)

loginButton = Button:New(loginFrame:GetContent(), 94, 44, "Login")
loginButton:SetPoint("TOPRIGHT", passwordPanel, "BOTTOMRIGHT", 0, -20)


function loginAccount(...)
	RequestAuthSalts(accountName:GetText())
	print("requesting the salts")
end


loginButton:AddListener("OnClick", loginAccount)
-- Auto login trick - change to your settings
if accountSettings:getAutoLogin() then
	RequestAuthSalts(accountSettings:getAccount())
end




function loginFailed(event, message)
	password:SetText("")
	local failMessage = Label:New(loginFrameBackplate, message, 20, GetFont("verdana"))
	failMessage:SetColor(1.0, 0.2, 0.2, 1)
	failMessage:SetPoint("BOTTOMCENTER", loginFrameBackplate, "BOTTOMCENTER", 0, 4)

	local failLabel = Label:New(loginFrameBackplate, "LOGIN FAILED", 35, GetFont("verdana"))
	failLabel:SetColor(1.0, 0.2, 0.2, 1)
	failLabel:SetPoint("BOTTOMCENTER", failMessage, "TOPCENTER", 0, 4)
end

function receivedSalts(event, salts)
	print("received the salts")
	local name = accountName:GetText()
	local pwd = password:GetText()
	globalSalts = salts
	LoginAccount(name, pwd, salts)
end

function needsAuthenticator()
	loginFrame:Hide()
	authenticatorFrame:Show()
end

RegisterEvent("LOGIN_FAILED", loginFailed)
RegisterEvent("RECEIVED_SALTS", receivedSalts)
RegisterEvent("REQUIRES_AUTHENTICATOR_AUTHENTICATION", needsAuthenticator)