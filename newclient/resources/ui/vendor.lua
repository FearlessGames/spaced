require("ui/chatmodel")
require("ui/components/actionbutton")
require("ui/components/container")
require("ui/components/tooltip")
require("ui/components/label")
require("ui/fonts")
require("ui/components/filledpanel")
require("ui/components/price")

local SLOTS_ROWS = 7
local superTooltip = ItemTooltip:New(uiParent)
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

SoldPrice = {}
function SoldPrice:New()
	return self
end

function SoldPrice:GetCurrency()
	return "Sold"
end

function SoldPrice:GetAmountAsString()
	return "Sold"
end

SoldItem = {}
function SoldItem:New()
	return self
end
function SoldItem:GetIconPath()
	return "textures/items/icons/solditem"
end
function SoldItem:GetName()
	return "Sold"
end

function SoldItem:GetPrice()
	return "Sold"
end

function SoldItem:GetPrice()
	return SoldPrice:New()
end

function SoldItem:GetUUID()
	return "poopjob"
end

function SoldItem:GetEquipAuras()
	return {}
end
function SoldItem:GetItemTypes()
	return {}
end


VendorStock = {}

function VendorStock:AddItems(allItems, vendor)
	self.vendor = vendor
	self.lastpage = math.ceil(#allItems / SLOTS_ROWS)

	self.itemsForSale = allItems
	self.pageplate = Label:New(self.content, self.currentpage .. "/" .. self.lastpage, 20 ,GetBodyFont())
	self.pageplate:SetPoint("BOTTOMCENTER", self.content, "BOTTOMCENTER", 0, 18)
	self.pageplate:SetColor(1,1,1,1)

	self:TurnPage(1)
end

function VendorStock:RemoveItem(item)

	for k, v in ipairs(self.itemsForSale) do
		if item:GetUUID() == v:GetUUID() then
			self.itemsForSale[k] = SoldItem:New()
		end
	end

	for k, v in pairs(self.items) do
		local item2 = v.item
		if item2:GetUUID() == item:GetUUID() then
			v:RemoveFromParent()
			self.amounts[k]:RemoveFromParent()
			self.currencys[k]:RemoveFromParent()
			self.names[k]:RemoveFromParent()
		end
	end
	self:TurnPage(self.currentpage)
end

function VendorStock:ClearStock()
	for k, v in ipairs(self.items) do
		v:RemoveFromParent()
		self.items[k] = nil
		self.amounts[k]:RemoveFromParent()
		self.amounts[k] = nil
		self.names[k]:RemoveFromParent()
		self.names[k] = nil
	end
end

function VendorStock:TurnPage(pageNumber)
	local lastItemToShow = pageNumber*SLOTS_ROWS
	local firstItemToShow = (lastItemToShow-SLOTS_ROWS)+1
	local position = 1
	self:ClearStock()

	for i, item in ipairs(self.itemsForSale) do
		if(i >= firstItemToShow and i <= lastItemToShow) then
			local item = self.itemsForSale[i]
			self:AddItem(self.content, self.slots, item, self.items, position, self.vendor)
			position = position + 1
		end
	end

	self.pageplate:SetText(pageNumber .. "/" .. self.lastpage)
end

function VendorStock:New()
	local container = Frame:New(uiParent, 240, 410, "Uknown Stock", true)
	local background = FilledPanel:New(container, {width = 240, height = 420, texture = "gui/frame/vendorbg.png"})
	extend(container, self)
	container:SetResizable(false)
	container:SetPoint("BOTTOMRIGHT", uiParent, "BOTTOMRIGHT", -84, 84)
	container.content = container.contentPanel
	container:Hide()
	container.slots = {}
	container.items = {}
	container.amounts = {}
	container.currencys = {}
	container.names = {}
	container.currentpage = 1
	container.itemsForSale = {}

	for slotId = 1, SLOTS_ROWS do
		local slot = ItemSlot:New(container.content, 44)
		slot:SetPoint("TOPLEFT", container.content, "TOPLEFT", 18, -((math.floor(slotId - 1) * 45)+20))
		container.slots[slotId] = slot
		container.items[slotId] = nil
	end

	previousPage = ActionButton:New(container, "textures/gui/left_arrow", 32, 32)
	previousPage:SetPoint("BOTTOMLEFT", container, "BOTTOMLEFT", 18, 18)
	previousPage:Show()

	previousPage:AddClickListener(function(target, buttonType)
		if(buttonType == "LeftButton") then
			if (container.currentpage > 1) then
				container.currentpage = container.currentpage - 1
				container:TurnPage(container.currentpage)
			end
		end
	end)

	local nextPage = ActionButton:New(container,"textures/gui/right_arrow" , 32, 32)
	nextPage:SetPoint("BOTTOMRIGHT", container, "BOTTOMRIGHT", -30, 18)
	nextPage:Show()

	nextPage:AddClickListener(function(target, buttonType)
		if (buttonType == "LeftButton") then
			if (container.currentpage < container.lastpage) then
				container.currentpage = container.currentpage+1
				container:TurnPage(container.currentpage)
			end
		end
	end)

	return container
end

function VendorStock:AddItem(content, slots, item, items, position, vendor)
	local button = ActionButton:New(content, item:GetIconPath(), 44, 44)
	button.item = item

	button:SetPoint("TOPLEFT", slots[position], "TOPLEFT", 0, 0)
	button:Show()
	items[position] = button


	button.button:AddListener("OnMouseEnter", function(self)
		superTooltip:ShowItem(item)
	end)

	button.button:AddListener("OnMouseLeave", function(self)
		superTooltip:Hide()
	end)

	button:AddClickListener(function(target, buttonType)
		if (buttonType == "RightButton") then
			BuyItem(vendor, item)
		end
	end)

	local name = Label:New(content, item:GetName(), 14, GetBodyFont())
	name:SetPoint("TOPLEFT", slots[position], "TOPRIGHT", 1, -4)
	name:SetColor(1,1,1,1)

	local amount = Price:New(content, item:GetPrice())
	amount:SetPoint("BOTTOMLEFT", slots[position], "BOTTOMRIGHT", 1, 6)
	amount:SetColor(1,1,1,1)

	self.amounts[position] = amount
	self.names[position] = name
end

function VendorStock:Reset(vendor)
	self:SetTitle(vendor:GetName() .. " Stock")
	self:RemoveListener("OnClose")

	self:AddListener("OnClose", function()
		self:RemoveListener("OnUpdate")
		StopVendoring(vendor)
	end)
end

local vendorStock = VendorStock:New()
local vendorEventHandlers = {
	VENDOR_STOCK_ITEMS = function(vendor, items)
		StartVendoring(vendor)

		vendorStock:Reset(vendor)
		vendorStock:AddItems(items, vendor)
		vendorStock:Show()
		vendorStock:RemoveListener("OnUpdate")
		vendorStock:AddListener("OnUpdate", function (self, timeElapsed)
			if not (IsInRange(vendor, INTERACTION_RANGE)) then
				vendorStock:RemoveListener("OnUpdate")
				chatModel:AddLine("Vendor not in range!")
				vendorStock:Hide()
				StopVendoring(vendor	)
			end
		end)
	end,
	VENDOR_OUT_OF_RANGE = function()
		chatModel:AddLine("Vendor is out of range")
	end,
	VENDOR_CANNOT_AFFORD = function()
		chatModel:AddLine("Cannot afford item")
	end,
	VENDOR_ADDED_ITEM = function(newVendorItem)
		print("VENDOR_ADDED_ITEM", newVendorItem)
		table.insert(VendorStock.itemsForSale, newVendorItem)
		VendorStock.lastpage = math.ceil(#VendorStock.itemsForSale / SLOTS_ROWS)
		VendorStock:TurnPage(VendorStock.currentpage)
		VendorStock.pageplate:SetText(VendorStock.currentpage .. "/" .. VendorStock.lastpage)
	end,
	VENDOR_BOUGHT_ITEM = function(item)
		vendorStock:RemoveItem(item)
	end,
	VENDOR_DESPAWNED = function()
		chatModel:AddLine("Vendor has died! aborting purchase")
		vendorStock:Hide()
		vendorStock:RemoveFromParent()
		vendorStock:RemoveListener("OnUpdate")
		StopVendoring(nil)
	end,
	VENDOR_INVENTORY_FULL = function()
		chatModel:AddLine("Inventory full")
	end
}

local function VendorEventCallback(event, ...)
	vendorEventHandlers[event](...)
end

ResetVendoring()

RegisterEvent("VENDOR_STOCK_ITEMS", VendorEventCallback)
RegisterEvent("VENDOR_OUT_OF_RANGE", VendorEventCallback)
RegisterEvent("VENDOR_CANNOT_AFFORD", VendorEventCallback)
RegisterEvent("VENDOR_ADDED_ITEM", VendorEventCallback)
RegisterEvent("VENDOR_BOUGHT_ITEM", VendorEventCallback)
RegisterEvent("VENDOR_DESPAWNED", VendorEventCallback)
RegisterEvent("VENDOR_INVENTORY_FULL", VendorEventCallback)
