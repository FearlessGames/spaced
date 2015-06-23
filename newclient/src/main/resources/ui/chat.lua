require("ui/uisetup")
require("ui/textinputhandler")
require("ui/components/frame")
require("ui/components/panel")
require("ui/components/editbox")
require("ui/components/label")
require("ui/components/textarea")
require("ui/components/actionbutton")
require("ui/variablestore")
require("ui/fonts")

local savedVars = VariableStore:Get("chat")

ChatWindow = {}
local size = getAndSet(savedVars, "chatSize", { width = 460, height = 218})
local chatFontSize = 18

local EDIT_FRAME_ALPHA = {
	DISABLE = 0.3,
	ENABLE = 1.0
}

local function updateSavedPosition(frame)
	local x, y = frame:GetHudPosition()
	local pos = savedVars["chatPosition"]
	pos.x = x
	pos.y = y
	VariableStore:Save("chat")
end

local function updateSavedSize(frame)
	local w, h = frame:GetSize()
	local size = savedVars["chatSize"]
	size.width = w
	size.height = h
	VariableStore:Save("chat")
end

local function positionButtons(container)
	container.upButton:SetPoint("TOPLEFT", container, "TOPRIGHT", -20, -4)
	container.endButton:SetPoint("BOTTOMLEFT", container, "BOTTOMRIGHT", -20, 42)
	container.downButton:SetPoint("BOTTOMLEFT", container, "BOTTOMRIGHT", -20, 58)
end


function ChatWindow:New(parent, width, height, fontSize)
	local container = Frame:New(parent, width, height, "Chat", false)
	extend(container, self)
	--container:SetResizable(false)
	local pos = getAndSet(savedVars, "chatPosition", { x = 50, y = 80, chatAnchor = "BOTTOMLEFT", parentAnchor = "BOTTOMLEFT" })
	container:SetPoint(pos.chatAnchor, uiParent, pos.parentAnchor, pos.x, pos.y)
	container:SetAlpha(0.3)
	container:AddListener("OnEndMove", updateSavedPosition)
	container:AddListener("OnEndResize", updateSavedSize)
	container:AddListener("OnMove", positionButtons)
	container:AddListener("OnResize", positionButtons)
	container:AddListener("OnEndResize", positionButtons)
	container:AddListener("OnEndMove", positionButtons)

	container.contentPanel:SetName("chat.contentPanel")
	container.contentPanel:SetBorderLayout()

	local inputHeight = chatFontSize + 10
	container.inputFrame = Panel:New(container.contentPanel, {width = (width - 5), height = inputHeight})
	container.inputFrame:SetName("inputFrame")
	container.inputFrame:SetBorderLayout()

	container.inputFrame:SetColor(0.7, 0.66, 0.72, 1.0)
	container.inputFrame:GetBorder():SetAlpha(EDIT_FRAME_ALPHA.DISABLE)
	container.inputFrame:SetBorderLayoutData("SOUTH")

	container.chatEdit = EditBox:New(container.inputFrame, width - 10, inputHeight - 2, chatFontSize, GetFont("consolas"))
	container.chatEdit:SetName("chatEdit")
	container.chatEdit:SetColor(1, 1, 1, 0)

	container.chatEdit:SetBorderLayoutData("CENTER")

	container.chatEdit:SetColor(0.7, 0.7, 0.7, 0.0)
	container.chatEdit:Hide()

	local contentHeight = container.contentPanel:GetHeight()
	container.text = TextArea:New(container.contentPanel, width, contentHeight - inputHeight, 14, GetFont("consolas"))
	container.text:SetName("chatTextField")

	container.text:SetBorderLayoutData("CENTER")

	container.text:SetColor(0.0, 0.0, 0.45, 1.0)

	container.upButton = ActionButton:New(uiParent, "gui/scroll/UpArrow.png", 14, 14)
	container.upButton:SetTooltipText("Page Up")
	container.endButton = ActionButton:New(uiParent, "gui/scroll/EndArrow.png", 14, 14)
	container.endButton:SetTooltipText("End")
	container.downButton = ActionButton:New(uiParent, "gui/scroll/DownArrow.png", 14, 14)
	container.downButton:SetTooltipText("Page Down")
	positionButtons(container)
	return container
end

function ChatWindow:OnUpdate(model)
	local text = model:GetViewableText()
	self.text:SetText(text)
	if(model:HasHistory()) then
		self.upButton:Show()
	else
		self.upButton:Hide()
	end

	if(model:IsAtTail()) then
		self.endButton:Hide()
		self.downButton:Hide()
	else
		self.endButton:Show()
		self.downButton:Show()
	end
end

playerChat = ChatWindow:New(uiParent, size.width, size.height, chatFontSize)
chatModel = ChatModel:New(11, 6)
chatModel:AddListener(playerChat)

local function StartEdit()
	playerChat.inputFrame:GetBorder():SetAlpha(EDIT_FRAME_ALPHA.ENABLE)
	playerChat.chatEdit:Show()
	playerChat.chatEdit:SetFocus()
end

local function StopEdit()
	playerChat.inputFrame:GetBorder():SetAlpha(EDIT_FRAME_ALPHA.DISABLE)
	playerChat.chatEdit:Clear()
	playerChat.chatEdit:ClearFocus()
	playerChat.chatEdit:Hide()
end

function PageUpKeyDown()
	chatModel:PageUpKeyDown()
end

function PageDownKeyDown()
	chatModel:PageDownKeyDown()
end

function EndKeyDown()
	chatModel:EndKeyDown()
end



function EnterKeyDown()
	if(playerChat.chatEdit:HasFocus()) then
		print("Shouldn't get focus now")
	else
		StartEdit()
	end
end


playerChat.upButton:AddClickListener(PageUpKeyDown)
playerChat.downButton:AddClickListener(PageDownKeyDown)
playerChat.endButton:AddClickListener(EndKeyDown)

local function editHandler(component, textcontents)
	StopEdit()
	HandleTextInput(textcontents)
end

playerChat.chatEdit:AddListener("OnAction", editHandler)

chatModel:AddLine("Welcome to the Spaced dev world.")

local chatEventHandlers = {
	PLAYER_SAY = function(player, message, t)
		chatModel:AddLine(string.format("%s: <%s> %s", FormatTime(t), player, message))
	end,
	PLAYER_WHISPER = function(fromPlayer, message, t)
		chatModel:AddLine(string.format("%s: <%s whispered> %s", FormatTime(t), fromPlayer, message))
	end,
	SELF_WHISPER = function(toPlayer, message, t)
		chatModel:AddLine(string.format("%s: <Whispered> %s: %s", FormatTime(t), toPlayer, message))
	end,
	PLAYER_EMOTE = function(name, message, t)
		chatModel:AddLine(string.format("%s: %s: %s", FormatTime(t), name, message))
	end,
	SYSTEM_MESSAGE = function(message)
		chatModel:AddLine(string.format("[SYSTEM]: %s", message))
	end,
	PLAYER_LOGGED_IN = function(player)
		chatModel:AddLine(string.format("%s logged in", player:GetName()))
	end,
	PLAYER_LOGGED_OUT = function(player, name)
		chatModel:AddLine(string.format("%s logged out", name))
	end
}

local whisper = function(cmd, s)
	local player, msg = split(s, 2)
	Whisper(player, msg)
end

RegisterSlashCommand(whisper, "Whisper a player", "/whisper", "/w")

local function ChatEventCallback(event, ...)
	chatEventHandlers[event](...)
end



RegisterEvent("PLAYER_SAY", ChatEventCallback)
RegisterEvent("PLAYER_WHISPER", ChatEventCallback)
RegisterEvent("SELF_WHISPER", ChatEventCallback)
RegisterEvent("PLAYER_EMOTE", ChatEventCallback)
RegisterEvent("SYSTEM_MESSAGE", ChatEventCallback)
RegisterEvent("PLAYER_LOGGED_IN", ChatEventCallback)
RegisterEvent("PLAYER_LOGGED_OUT", ChatEventCallback)

