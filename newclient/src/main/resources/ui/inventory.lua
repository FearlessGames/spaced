require("ui/chatmodel")
require("ui/components/actionbutton")
require("ui/trade")
require("ui/components/tooltip")
require("ui/components/container")
require("ui/components/label")
require("ui/dragdrop")
require("ui/wallet")
require("ui/fonts")

local superTooltip = ItemTooltip:New(uiParent)

local SLOTS_ROWS = 4
local SLOTS_COLS = 6


function IsEquippable(item)
	local usages = item:GetUsages()
	for _, usage in ipairs(usages) do
		if (usage == "EQUIP") then
			return true
		end
	end

	return false
end

local ItemSlot = {}

function ItemSlot:New(parent, size)
	local container = Container:New(parent)
	extend(container, self)
	container:SetSize(size, size)
	container.transition = Picture:New(container, "textures/gui/button_overlays/GUIButtonMouseOverState")
	container.transition:SetColor(0, 1, 0, 1)
	container.transition:Hide()
	container.button = Area:New(container, size, size)
	container:SetCanBeActive(true)
	container.item = nil

	return container
end

function ItemSlot:SetItem(item)
	self.item = item
end


Inventory = {}

function Inventory:New()
	local container = Frame:New(uiParent, 600, 290, "Inventory", true)
	extend(container, self)
	container:SetResizable(false)
	container:SetPoint("BOTTOMRIGHT", uiParent, "BOTTOMRIGHT", -84, 84)
	container.content = container.contentPanel
	local wallet = Wallet:New(container.content, 200, container.content:GetHeight())
	wallet:SetPoint("TOPRIGHT", container.content, "TOPRIGHT", 0, 0)

	container:Hide()
	container.slots = {}
	container.stacks = {}

	for slotId = 1, SLOTS_ROWS * SLOTS_COLS do
		local slot = ItemSlot:New(container.content, 64)
		slot:SetName("InventorySlot" .. slotId)
		slot:SetPoint("TOPLEFT", container.content, "TOPLEFT", ((slotId - 1) % SLOTS_COLS) * 65, -math.floor((slotId - 1) / SLOTS_COLS) * 65 - 5)
		container.slots[slotId] = slot
		container.stacks[slotId] = {}
	end

	return container
end


function Inventory:Toggle()
	if (self:IsVisible()) then
		self:Hide()
	else
		self:Show()
	end
end

function Inventory:GetItem(position)
	for _, item in pairs(self.stacks[position].items) do
		return item
	end
end

function Inventory:CreateItemButton(item, position)
	local slot = self.slots[position]
	local this = ActionButton:New(slot, item:GetIconPath(), 64, 64)

	this:Show()

	this.count = Label:New(this, "1", 18, GetFont("arialOutlined"))
	this.count:SetPoint("BOTTOMLEFT", this, "BOTTOMLEFT", 4, 4)
	this.count:Hide()

	--local inventorySelf = self
	this.button:AddListener("OnMouseEnter", function(that)
		superTooltip:ShowItem(self:GetItem(position))
	end)
	this.button:AddListener("OnMouseLeave", function(that)
		superTooltip:Hide()
	end)

	this:AddClickListener(function(target, buttonType)
		if (buttonType == "RightButton") then
			if (IsButtonDown("LSHIFT")) then
				DeleteItem(self:GetItem(position))
			elseif (IsTradeInProgress()) then
				-- todo add an entire stack instead
				GetCurrentTradeOffer():AddItem(self:GetItem(position))
			elseif (GetActiveVendor()) then
				SellItems(GetActiveVendor(), position)
			elseif (item:IsOfType("CONSUMABLE")) then
				UseItem(self:GetItem(position))
			elseif (IsEquippable(self:GetItem(position))) then
				this:Disable()
				superTooltip:Hide()
				local item = self:GetItem(position)
				print("Equippable!", item)
				Equip(item)
			end
		end
	end)
	dragDrop:RegisterDraggable(this, this.button)
	return this
end

function Inventory:AddItem(item, position)
	print("AddItem", item, position)
	if (not self.stacks[position].button) then
		print("No button before")
		local button = self:CreateItemButton(item, position)
		local items = {}
		table.insert(items, item)

		self.stacks[position].button = button
		self.stacks[position].items = items
	else
		print("Adding to stack")
		table.insert(self.stacks[position].items, item)
		self.stacks[position].button.count:SetText(tostring(#self.stacks[position].items))
		self.stacks[position].button.count:Show()
	end
end

function Inventory:RemoveItem(item, position)
	local stack = self.stacks[position]
	for index, item2 in pairs(stack.items) do
		if item2:GetUUID() == item:GetUUID() then
			print("RemoveItem - Found item")
			table.remove(stack.items, index)
			self.stacks[position].button.count:SetText(tostring(#stack.items))
			if #stack.items == 1 then
				self.stacks[position].button.count:Hide()
			elseif #stack.items == 0 then
				print("RemoveItem - last item")
				stack.button:Hide()
				stack.button:RemoveFromParent()
				self.stacks[position] = {}
			end
		end
	end
end

function Inventory:Refresh()
	local inventory = GetInventory()

	-- clear old inv
	for _, stack in pairs(self.stacks) do
		if stack.button then
			stack.button:Hide()
			stack.button:RemoveFromParent()
			stack = {}
		end
	end

	for index, itemStack in pairs(inventory:GetItems()) do
		for _, item in pairs(itemStack) do
			self:AddItem(item, index)
		end
	end
end


local playerInventory = Inventory:New()

function GetPlayerInventory()
	return playerInventory
end


RegisterEvent("INVENTORY_UPDATED", function() playerInventory:Refresh() end)

RegisterEvent("INVENTORY_ADD_ITEM", function(event, inventory, item, position)
--	local s = ("%s added to inventory."):format(item:GetName())
	playerInventory:AddItem(item, position)
--	chatModel:AddLine(s)
end)

RegisterEvent("INVENTORY_REMOVE_ITEM", function(event, inventory, item, position)
--	local s = ("%s removed from inventory."):format(item:GetName())
--	chatModel:AddLine(s)
	playerInventory:RemoveItem(item, position)
end)

RegisterEvent("EQUIP_ITEM_FAILED", function(event, entity, item, failMessage)
	if IsSelf(entity) then
		local s = ("Failed to equip item '%s' with message '%s'."):format(item:GetName(), failMessage)
		playerInventory:RemoveItem(item)
		chatModel:AddLine(s)
	end
end)

playerInventory:Refresh()

