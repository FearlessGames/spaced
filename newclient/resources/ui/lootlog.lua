require("ui/components/container")
require("ui/components/label")
require("ui/components/picture")
require("ui/components/panel")
require("ui/components/area")

local ITEM_DISPLAY_TIME = 8000
local ITEM_HEIGHT = 35
local H_PADDING = 4

local ITEM_OPACITY = 0.3

local FADE_OUT_TIME = 200

local itemTooltip = ItemTooltip:New(uiParent)


local LootLogItem = {}
extend(LootLogItem, Component)

function LootLogItem:NewItem(parent, item)
	local this = Panel:New(parent, {width = 256, height = (ITEM_HEIGHT + 2 * H_PADDING), color = {1, 1, 1, ITEM_OPACITY}})
	extend(this, self)
	local icon = Picture:New(this:GetContent(), item:GetIconPath())
	icon:SetSize(ITEM_HEIGHT, ITEM_HEIGHT)
	icon:SetPoint("MIDLEFT", this:GetContent(), "MIDLEFT", 0, 0)
	icon:SetAlpha(0.6)
	this:GetBorder():SetAlpha(ITEM_OPACITY)
	local name = Label:New(this:GetContent(), item:GetName(), 16)
	name:SetPoint("MIDLEFT", icon, "MIDRIGHT", 5, 0)

	this.name = name
	this.icon = icon
	this.timeAdded = GetTime()
	local area = Area:New(this, this:GetWidth(), this:GetHeight())
	area:AddListener("OnMouseEnter", function(self)
		itemTooltip:ShowItem(item)
	end)
	area:AddListener("OnMouseLeave", function(self)
		itemTooltip:Hide()
	end)
	return this
end

function LootLogItem:SetAlpha(alpha)
	Component.SetAlpha(self, alpha)
	self.icon:SetAlpha(alpha * 2)
	self.name:SetAlpha(alpha)
end

function LootLogItem:IsTimeToRemove(now)
	return now - self.timeAdded > ITEM_DISPLAY_TIME
end

local LootLog = {}
extend(LootLog, Container)

function LootLog:New(parent, width, height)
	local this = Container:New(parent)
	extend(this, self)
	this:SetSize(width, height)

	this.items = {}
	uiParent:AddListener("OnUpdate", function(component, timeElapsed)
		this:Update(GetTime())
	end)
	return this
end


function LootLog:Update(now)
	self:Layout()
	for index, listItem in ipairs(self.items) do
		if listItem:IsTimeToRemove(now) then
			if not listItem.removeAnimationTime then
				listItem.removeAnimationTime = now
			end
			listItem.animation = function(currentTime)
				local timeElapsed = currentTime - listItem.removeAnimationTime

				if(timeElapsed >= FADE_OUT_TIME) then
					listItem.animation = nil
					listItem:RemoveFromParent()
					listItem:Hide()
					table.remove(self.items, index)
					self:Layout()
				end
				listItem:SetAlpha(ITEM_OPACITY * (FADE_OUT_TIME - timeElapsed)/FADE_OUT_TIME)
			end
		end
		if listItem.animation then
			listItem.animation(now)
		end
		if listItem.desiredHeight <= listItem.actualHeight then
			listItem.actualHeight = math.max(listItem.actualHeight - 4, listItem.desiredHeight)
			listItem:SetPoint("TOPLEFT", self, "TOPLEFT", 0, -listItem.actualHeight)
		end
	end
end

function LootLog:Layout()
	local height = 0
	for index, listItem in ipairs(self.items) do
		height = height + listItem:GetHeight()
		listItem.desiredHeight = height
	end
end

function LootLog:Add(item)
	table.insert(self.items, item)
	local height = 0
	for index, listItem in ipairs(self.items) do
		height = height + listItem:GetHeight()
	end
	item.actualHeight = height
	item:SetPoint("TOPLEFT", self, "TOPLEFT", 0, -height)
end

local lootLog = LootLog:New(uiParent, 200, 400)
lootLog:SetPoint("BOTTOMRIGHT", uiParent, "BOTTOMRIGHT", -150, 140)
local eventHandlers = {
	RECEIVED_LOOT = function(item)
		local logItem = LootLogItem:NewItem(lootLog, item)
		lootLog:Add(logItem)
	end
}

local function lootCallback(event, ...)
	eventHandlers[event](...)
end

RegisterEvent("RECEIVED_LOOT", lootCallback)

local function test()
	local someItem = nil
	for k, v in pairs(GetPlayerInventory().items) do
		someItem = v.item
	end
	print("Test loot with", someItem:GetName())
	FireGlobalEvent("RECEIVED_LOOT", someItem)
	FireGlobalEvent("RECEIVED_LOOT", someItem)
end

OnKeyDown("O", test)
