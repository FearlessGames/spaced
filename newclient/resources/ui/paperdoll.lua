require("ui/mathutil")
require("ui/chatmodel")
require("ui/components/container")
require("ui/components/picture")
require("ui/components/area")
require("ui/components/label")
require("ui/components/borderpanel")
require("ui/components/rtt")
require("ui/components/tooltip")
require("ui/dragdrop")
require("ui/components/statbox")

local items = {}

local slotData = {
	HEAD = { icon = "gui/paperdoll/head.png", name = "Head" },
	CHEST = { icon = "gui/paperdoll/chest.png", name = "Chest" },
	LEGS = { icon = "gui/paperdoll/legs.png", name = "Legs" },
	FEET = { icon = "gui/paperdoll/feet.png", name = "Feet" },
	NECK = { icon = "gui/paperdoll/neck.png", name = "Neck" },
	BACK = { icon = "gui/paperdoll/back.png", name = "Back" },
	BELT = { icon = "gui/paperdoll/belt.png", name = "Belt" },
	UTILITY = { icon = "gui/paperdoll/utility.png", name = "Utility" },
	LEFT_WRIST = { icon = "gui/paperdoll/lwrist.png", name = "Left Wrist" },
	RIGHT_WRIST = { icon = "gui/paperdoll/rwrist.png", name = "Right Wrist" },
	OFF_HAND = { icon = "gui/paperdoll/lhand.png", name = "Off hand" },
	MAIN_HAND = { icon = "gui/paperdoll/rhand.png", name = "Main hand" }
}

local unsupportedSlots = { BELT = true, NECK = true }

local function AddTooltip(statBox, statName)
	local tt = GetStatDescription(statName)
	if #tt > 0 then
		statBox:SetTooltipText(tt)
	end
end


local function BuildStatBox(parent, statName, properties, componentAbove, horizontalOffset)
	local statBox = StatBox:New(parent, string.lower(GetStatDisplayName(statName)), properties)
	statBox:SetPoint("TOPLEFT", componentAbove, "BOTTOMLEFT", horizontalOffset or 0, -2)
	AddTooltip(statBox, statName)
	return statBox
end


local superTooltip = ItemTooltip:New(uiParent)
PaperDoll = extend({}, Container)

function PaperDoll:New()
	local container = Frame:New(uiParent, 300, 474, "Paperdoll", true)
	extend(container, self)
	container:SetResizable(false)
	local contentPanel = container:GetContentPanel()
	container.bg = Picture:New(contentPanel, "gui/frame/flatwhite.png")
	container.bg:SetSize(container:GetSize())
	local shade = 0.25
	container.bg:SetColor(shade, shade, shade, 1)
	container.bg:CenterOn(contentPanel)

	container.characterModel = Rtt:New(contentPanel, 150, 272, 370, 370, "models/players/player_male.xmo")
	container.characterModel:SetPoint("TOPCENTER", contentPanel, "TOPCENTER", 0, -5)
	container.characterModel:SetModelOffset(0, 25)


	container:AddListener("OnShow", function()
		container.characterModel:AddListener("OnUpdate", function()
			container.characterModel:Update()
		end)
		container.characterModel:Show()
	end)

	container:AddListener("OnHide", function()
		container.characterModel:RemoveListener("OnUpdate")
		container.characterModel:Hide()
	end)

	container.slots = {}

	container:CreateSlots()

	-- Stats Frame (some kind of primitive info design)
	container.statsContainer = Container:New(contentPanel)
	container.statsContainer:SetPoint("BOTTOMCENTER", contentPanel, "BOTTOMCENTER", 0, 5)
	container.statsContainer:SetSize(296, 130)

	container.statsFrame = BorderPanel:New(container.statsContainer, container.statsContainer:GetSize())

	container.statsTextHeader = Label:New(container.statsContainer, "Character Stats", 14)
	container.statsTextHeader:SetPoint("TOPCENTER", container.statsContainer, "TOPCENTER", 0, -2)
	container.statsTextHeader:SetColor(0.7, 1.0, 1.0, 1.0)

	local FROM_TOP = 18
	local BOX_WIDTH = 143
	local statProperties = { width = BOX_WIDTH, height = 20 }

	container.stamina = BuildStatBox(container.statsContainer, "STAMINA", statProperties, container.statsContainer)
	container.stamina:SetPoint("TOPLEFT", container.statsContainer, "TOPLEFT", 3, -FROM_TOP)
	container.hitpoints = BuildStatBox(container.statsContainer, "HITPOINTS", statProperties, container.stamina)
	container.coolrate = BuildStatBox(container.statsContainer, "COOL_RATE", statProperties, container.hitpoints)
	container.maxHeat = BuildStatBox(container.statsContainer, "MAX_HEAT", statProperties, container.coolrate)
	container.speed = BuildStatBox(container.statsContainer, "SPEED", statProperties, container.maxHeat)

	container.maxShield = BuildStatBox(container.statsContainer, "MAX_SHIELD", statProperties, container.statsContainer)
	container.maxShield:SetPoint("TOPRIGHT", container.statsContainer, "TOPRIGHT", -3, -FROM_TOP)
	container.shieldEfficiency = BuildStatBox(container.statsContainer, "SHIELD_EFFICIENCY", statProperties, container.maxShield)
	container.shieldRecovery = BuildStatBox(container.statsContainer, "SHIELD_RECOVERY", statProperties, container.shieldEfficiency)
	container.attackRating = BuildStatBox(container.statsContainer, "ATTACK_RATING", statProperties, container.shieldRecovery)

	container.attackModifier = BuildStatBox(container.statsContainer, "ATTACK_MODIFIER", statProperties, container.attackRating)

	return container
end

function PaperDoll:UpdateStats(entity)
	local unitStats = UnitStats(entity)

	self.stamina:SetValue(round(unitStats:GetStamina(), 2))

	if (unitStats:GetStamina() > unitStats:GetBaseStamina()) then
		self.stamina.value:SetColor(0, 1, 0, 1)
	else
		self.stamina.value:SetColor(1, 1, 1, 1)
	end

	self.hitpoints:SetValue(round(unitStats:GetMaxHealth(), 2))
	self.coolrate:SetValue(round(unitStats:GetCoolRate(), 2))
	self.maxHeat:SetValue(round(unitStats:GetMaxHeat(), 2))
	self.maxShield:SetValue(round(unitStats:GetMaxShield(), 2))
	self.shieldEfficiency:SetValue(round(unitStats:GetShieldEfficiency(), 2))
	self.shieldRecovery:SetValue(round(unitStats:GetShieldRecovery(), 2))
	self.speed:SetValue(round(unitStats:GetSpeedModifier(), 2))
	self.attackRating:SetValue(unitStats:GetAttackRating())
	self.attackModifier:SetValue(round(unitStats:GetAttackModifier(), 3))
end


function PaperDoll:Toggle()
	if (self:IsVisible()) then
		self:Hide()
	else
		self:Show()
	end
end


PaperDollItem = extend({}, Container)

function PaperDollItem:New(parent, item)
	local container = Container:New(parent)
	extend(container, self)
	container.icon = Picture:New(container, item:GetIconPath())

	container.button = Area:New(container, 64, 64)
	container.slot = nil
	container.item = item

	container.button:AddListener("OnMouseEnter", function()
		superTooltip:ShowItem(item)
	end)
	container.button:AddListener("OnMouseLeave", function()
		superTooltip:Hide()
	end)
	container.button:AddListener("OnMouseDown", function(target, mouseButton)
		if (container.slot ~= nil) then
			container.slot.button:FireEvent("OnMouseDown", mouseButton)
		end
	end)
	container.button:AddListener("OnMouseUp", function(target, mouseButton)
		if (container.slot ~= nil) then
			container.slot.button:FireEvent("OnMouseUp", mouseButton)
		end
	end)

	return container
end

function PaperDollItem:SetSlot(slot)
	self.slot = slot
end


PaperDollSlot = extend({}, Container)
PaperDollSlot.SLOT_SIZE = 64

function PaperDollSlot:New(parent, containerType)
	local container = Picture:New(parent, slotData[containerType].icon)
	extend(container, self)
	container:SetSize(self.SLOT_SIZE, self.SLOT_SIZE)

	container.button = Area:New(container, self.SLOT_SIZE, self.SLOT_SIZE)
	container.button:CenterOn(container)
	container.button:SetTooltipText(slotData[containerType].name)

	container.itemIcon = nil

	container.currentlyEquipped = nil

	container.button:AddListener("OnMouseDown", function(target, mouseButton)
		if mouseButton == "RightButton" then
			if container.currentlyEquipped ~= nil then
				container.currentlyEquipped:Disable()
				Unequip(container.currentlyEquipped.slotType)
			end
		elseif mouseButton == "LeftButton" then
			if container.currentlyEquipped ~= nil then
				UseItem(container.currentlyEquipped.item)
			end
		end
	end)

	local dropHandler = {}

	dropHandler.OnDrop = function(handler, draggable)
		if (draggable.item) then
			if (draggable.item:GoesInSlot(containerType)) then
				Equip(draggable.item, containerType)
			else
				dragDrop:AbortDrag()
			end
		end
	end

	dropHandler.IsInside = function(_, x, y)
		return container:IsInside(x, y)
	end
	dragDrop:RegisterDropTarget(dropHandler)
	return container
end


function PaperDollSlot:SetSlotItem(clientItem, slotType)
	if (clientItem ~= nil) then
		if self.slotItem ~= nil then
			self.slotItem:Hide()
			self.slotItem = nil
		end
		self.slotItem = PaperDollItem:New(self, clientItem)
		self.slotItem:SetSlot(self)
		self.currentlyEquipped = self.slotItem
		self.currentlyEquipped.slotType = slotType
	else
		if self.slotItem ~= nil then
			self.slotItem:Hide()
			self.slotItem = nil
		end
		self.currentlyEquipped = nil
	end
end

function PaperDollSlot:GetSlotItem(slotType)
	return self.slotItem.item
end

local function CreateSlot(slots, parent, containerType)
	local slot = PaperDollSlot:New(parent, containerType)
	slots[containerType] = slot
	return slot
end

function PaperDoll:CreateSlots()
	local SPACING = 5
	local slots = self.slots
	local parent = self:GetContentPanel()
	local neck = CreateSlot(slots, parent, "NECK")
	neck:SetPoint("TOPRIGHT", self.characterModel, "TOPLEFT", -SPACING, 0)
	local back = CreateSlot(slots, parent, "BACK")
	back:SetPoint("TOPRIGHT", neck, "BOTTOMRIGHT", 0, -SPACING)
	local belt = CreateSlot(slots, parent, "BELT")
	belt:SetPoint("TOPRIGHT", back, "BOTTOMRIGHT", 0, -SPACING)
	local util = CreateSlot(slots, parent, "UTILITY")
	util:SetPoint("TOPRIGHT", belt, "BOTTOMRIGHT", 0, -SPACING)
	local leftwrist = CreateSlot(slots, parent, "LEFT_WRIST")
	leftwrist:SetPoint("TOPRIGHT", util, "BOTTOMRIGHT", 0, -SPACING)

	local lefthand = CreateSlot(slots, parent, "OFF_HAND")
	lefthand:SetPoint("TOPLEFT", leftwrist, "TOPRIGHT", SPACING, 0)


	local head = CreateSlot(slots, parent, "HEAD")
	head:SetPoint("TOPLEFT", self.characterModel, "TOPRIGHT", SPACING, 0)
	local chest = CreateSlot(slots, parent, "CHEST")
	chest:SetPoint("TOPLEFT", head, "BOTTOMLEFT", 0, -SPACING)
	local legs = CreateSlot(slots, parent, "LEGS")
	legs:SetPoint("TOPLEFT", chest, "BOTTOMLEFT", 0, -SPACING)
	local feet = CreateSlot(slots, parent, "FEET")
	feet:SetPoint("TOPLEFT", legs, "BOTTOMLEFT", 0, -SPACING)
	local rightwrist = CreateSlot(slots, parent, "RIGHT_WRIST")
	rightwrist:SetPoint("TOPLEFT", feet, "BOTTOMLEFT", 0, -SPACING)

	local righthand = CreateSlot(slots, parent, "MAIN_HAND")
	righthand:SetPoint("TOPRIGHT", rightwrist, "TOPLEFT", -SPACING, 0)
end


RegisterEvent("EQUIPPED_ITEM", function(event, entity, item, containerType)
	if IsSelf(entity) then
		playerPaperDoll.slots[containerType]:SetSlotItem(item, containerType)
		playerPaperDoll.characterModel:Equip(item, containerType)
	end
end)

RegisterEvent("UNEQUIPPED_ITEM", function(event, entity, item, containerType)
	if IsSelf(entity) then
		local currentItem = playerPaperDoll.slots[containerType]:GetSlotItem()
		if (currentItem:GetUUID() == item:GetUUID()) then
			playerPaperDoll.slots[containerType]:SetSlotItem(nil, containerType)
			playerPaperDoll.characterModel:Unequip(containerType)
		end
	end
end)

RegisterEvent("UNEQUIP_ITEM_FAILED", function(event, entity, item, failMessage)
	if IsSelf(entity) then
		local s = ("Failed to unequip item '%s' with message '%s'."):format(item:GetName(), failMessage)

		for k, slot in pairs(playerPaperDoll.slots) do
			if (slot.slotItem ~= nil and slot.slotItem.item:GetUUID() == item:GetUUID()) then
				slot.currentlyEquipped:Enable()
			end
		end
		chatModel:AddLine(s)
	end
end)

playerPaperDoll = PaperDoll:New()
playerPaperDoll:SetPoint("MIDCENTER", uiParent, "MIDCENTER", -90, 50)
playerPaperDoll:Hide()

RegisterEvent("UI_READY", function(event)

	for slot, _ in pairs(slotData) do
		local item
		if not unsupportedSlots[slot] then
			item = GetEquipmentSlotInfo(slot)
		end
		if (item ~= nil) then
			playerPaperDoll.slots[slot]:SetSlotItem(item, slot)
			playerPaperDoll.characterModel:Equip(item, slot)
		end
	end

	playerPaperDoll:UpdateStats(GetSelf())
end)

RegisterEvent("UNIT_STATS_UPDATED", function(event, player)
	if player == GetSelf() then
		playerPaperDoll:UpdateStats(player)
	end
end)
