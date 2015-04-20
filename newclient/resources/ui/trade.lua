require("ui/uisetup")
require("ui/chatmodel")
require("ui/components/component")
require("ui/components/frame")
require("ui/components/borderpanel")
require("ui/components/label")
require("ui/components/overlay")
require("ui/components/area")
require("ui/components/button")
require("ui/unitfunctions")


function IsTradeInProgress()
	return GetCurrentTradeOffer() ~= nil
end

local SLOT_SIZE = 50
local SLOT_VSPACING = 15
local DEFAULT_TEXURE = "textures/gui/icons/IconUnknown.png"

TradeFrame = extend({}, Component)

local function createSlot(parent, idx)
	local container = Container:New(parent)
	container:SetSize(SLOT_SIZE, SLOT_SIZE)

	container:SetPoint("TOPLEFT", parent, "TOPLEFT", 20, - (SLOT_SIZE + SLOT_VSPACING) * (idx -1) - 2*SLOT_VSPACING)

	local slot = BorderPanel:New(container, SLOT_SIZE, SLOT_SIZE)
	slot:CenterOn(container)
	local itemIcon = Picture:New(container, DEFAULT_TEXURE)
	itemIcon:SetSize(container:GetSize())
	itemIcon:CenterOn(container)
	itemIcon:Hide()
	local overlay = Overlay:New(container)

	local button = Area:New(container, slot:GetSize())
	button:CenterOn(container)

	local function onClick(self, button)
		if slot.OnClick then
			slot:OnClick(button)
		end
	end
	button:AddListener("OnClick", onClick)

	button:AddListener("OnMouseEnter", function()
		overlay:MouseEnter()
	end)
	button:AddListener("OnMouseLeave", function()
		overlay:MouseLeave()
	end)
	button:AddListener("OnMouseDown", function()
		overlay:MouseDown()
	end)
	button:AddListener("OnMouseUp", function()
		overlay:MouseUp()
	end)
	return container, itemIcon
end

function TradeFrame:ToggleAcceptRetract()
	self.acceptButton:Toggle()
	self.retractButton:Toggle()
end

function TradeFrame:New(parent)
	local this = Frame:New(parent, 450, 500, "Trade", true)
	extend(this, self)
	local frameContentPane = this:GetContentPanel()
	frameContentPane:SetBorderLayout()
	local myPane = BorderPanel:New(frameContentPane, 200, 400)
	local othersPane = BorderPanel:New(frameContentPane, 200, 400)
	local buttonPane = BorderPanel:New(frameContentPane, frameContentPane:GetWidth() - 2, 50)
	buttonPane:SetBorderLayout()
	myPane:SetColor(1, 1, 1, 0.3)
	othersPane:SetColor(1, 1, 1, 0.3)
	buttonPane:SetColor(1, 1, 1, 0.6)
	myPane:SetBorderLayoutData("WEST")
	othersPane:SetBorderLayoutData("EAST")
	buttonPane:SetBorderLayoutData("SOUTH")

	local myName = Label:New(myPane, "MyName", 12)
	local othersName = Label:New(othersPane, "OthersName", 12)
	myName:SetPoint("TOPCENTER", myPane, "TOPCENTER", 0,-2)
	othersName:SetPoint("TOPCENTER", othersPane, "TOPCENTER", 0,-2)
	this.myName = myName
	this.othersName = othersName

	this.myItems = {}
	this.othersItems = {}
	for i = 1, 6 do
		local slot, icon = createSlot(myPane, i)
		this.myItems[i] = {slot = slot, icon = icon}
		local slot2, icon2 = createSlot(othersPane, i)
		this.othersItems[i] = {slot = slot2, icon = icon2}
	end

	local acceptButton = Button:New(buttonPane, 100, 30, "Accept")
	acceptButton:SetBorderLayoutData("EAST")
	acceptButton:AddListener("OnClick", function()
		GetCurrentTradeOffer():Accept()
		this:ToggleAcceptRetract()
	end)

	local rejectButton = Button:New(buttonPane, 100, 30, "Reject")
	rejectButton:SetBorderLayoutData("WEST")
	rejectButton:AddListener("OnClick", function()
		GetCurrentTradeOffer():Reject()
	end)
	local retractButton = Button:New(buttonPane, 100, 30, "Retract")
	retractButton:SetBorderLayoutData("EAST")
	retractButton:AddListener("OnClick", function()
		GetCurrentTradeOffer():Retract()
		this:ToggleAcceptRetract()
	end)
	retractButton:Hide()
	this.retractButton = retractButton
	this.acceptButton = acceptButton
	this:AddListener("OnClose", function()
		GetCurrentTradeOffer():Reject()
	end)

	return this
end

function TradeFrame:SetOther(other)
	self.myName:SetText(GetSelf():GetName())
	self.othersName:SetText(other:GetName())
	DoLayout(self.base)
end

function TradeFrame:AddItemToEntry(item, entry)
	local width, height = entry.icon:GetSize()
	entry.icon:SetTexture(item:GetIconPath())
	entry.icon:SetSize(width, height)
	entry.icon:Show()
	entry.item = item
end

function TradeFrame:AddItem(item, itemList)
	for i, entry in ipairs(itemList) do
		if(entry.item == nil) then
			self:AddItemToEntry(item, entry)
			break
		end
	end
end

function TradeFrame:Reset()
	for i, entry in ipairs(self.myItems) do
		local width, height = entry.icon:GetSize()
		entry.icon:SetTexture(DEFAULT_TEXURE)
		entry.icon:SetSize(width, height)
		entry.icon:Hide()
		entry.item = nil
	end
	for i, entry in ipairs(self.othersItems) do
		local width, height = entry.icon:GetSize()
		entry.icon:SetTexture(DEFAULT_TEXURE)
		entry.icon:SetSize(width, height)
		entry.icon:Hide()
		entry.item = nil
	end
	self.othersName:SetText("")
	self.retractButton:Hide()
	self.acceptButton:Show()
end

local tradeFrame = TradeFrame:New(uiParent)
function GetTradeFrame()
	return tradeFrame
end
tradeFrame:Hide()

local tradeEventHandlers = {
	TRADE_INITIATED = function(other)
		tradeFrame:SetOther(other)
		tradeFrame:Show()
	end,
	TRADE_ITEM_ADDED = function(item, byMe)
		if(byMe) then
			tradeFrame:AddItem(item, tradeFrame.myItems)
		else
			tradeFrame:AddItem(item, tradeFrame.othersItems)
		end
	end,
	TRADE_COMPLETED = function()
		tradeFrame:Reset()
		tradeFrame:Hide()
	end,
	TRADE_ABORTED = function()
		tradeFrame:Reset()
		tradeFrame:Hide()
		chatModel:AddLine("Trade was aborted")
	end,
	TRADE_FAILED_TO_COMPLETE = function(reason)
		tradeFrame:Reset()
		tradeFrame:Hide()
		chatModel:AddLine("Trade failed to complete: " .. reason)
	end,
	TRADE_FAILED_TO_ADD_ITEM = function(reason)
		chatModel:AddLine("Failed to offer item: " .. reason)
	end
}

local function TradeEventCallback(event, ...)
	tradeEventHandlers[event](...)
end

RegisterEvent("TRADE_INITIATED", TradeEventCallback)
RegisterEvent("TRADE_INIT_FAILED", TradeEventCallback)
RegisterEvent("TRADE_ITEM_ADDED", TradeEventCallback)
RegisterEvent("TRADE_FAILED_TO_ADD_ITEM", TradeEventCallback)
RegisterEvent("TRADE_FAILED_TO_COMPLETE", TradeEventCallback)
RegisterEvent("TRADE_COMPLETED", TradeEventCallback)
RegisterEvent("TRADE_ABORTED", TradeEventCallback)


--Interpreter()