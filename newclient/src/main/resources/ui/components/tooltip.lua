require("ui/uisetup")
require("ui/components/infosection")
require("ui/components/component")
require("ui/components/price")
require("ui/components/label")
require("ui/lang/auras")
require("ui/lang/stats")
require("ui/util/stats")
require("ui/util/currencies")


local MAXIMUM_NUMBER_OF_AURAS = 5

Tooltip = extend({}, Component)

local defaultProperties = {
	border = {
		enabled = true
	},
	color = {1, 1, 1, 1},
	width = 150,
	height = 300,
	text = {
		size = 16,
		font = GetFont("arial"),
		color = {1, 1, 1, 1}
	},
	stat = {
		border = {
			enabled = false
		},
		color = {0.25, 0.25, 0.25, 0.0},
	},
	section = {
		border = {
			enabled = false
		},
		color = {0.25, 0.25, 0.25, 0.8}
	}
}

local GRANT_COLOR = { 0.4, 0.85, 0.5, 1 }
local MISSING_COLOR = { 0.95, 0.4, 0.15, 1 }


function Tooltip:New(parent)
	local this = InfoSection:New(parent, defaultProperties)
	this.parent = parent
	extend(this, self)

	this:Hide()
	return this
end

function Tooltip:UpdatePosition(mx, my)
	if not self:IsVisible() then
		return
	end
	if not (mx and my) then
		mx, my = GetMousePosition()
	end
	if mx == self.lastX and my == self.lastY then
		return
	end
	self.lastX = mx
	self.lastY = my
	mx = mx + self.offsetX
	my = my - self.offsetY
	local pw, ph = self.parent:GetSize()
	local w, h = self:GetSize()
	mx = math.min(mx, pw - w)
	my = math.max(my, h)
	self:SetPoint("TOPLEFT", self.parent, "TOPLEFT", mx, my - ph)
	if self.extra then
		self.extra:SetPoint("TOPRIGHT", self, "BOTTOMRIGHT", 0, -5)
	end
end


function Tooltip:Show(mx, my, offsetX, offsetY)
	self.offsetX = offsetX or 25
	self.offsetY = offsetY or 25
	self:Layout()
	Component.Show(self)
	self:UpdatePosition(mx, my)
	self:AddListener("OnUpdate", function() self:UpdatePosition() end)
	if self.extra then
		self.extra:RemoveListener("OnUpdate")
	end
end

function Tooltip:Hide()
	self:RemoveListener("OnUpdate")
	Container.Hide(self)
	self.item = nil
end

function Tooltip:Layout()
	self:Resize()
end

ItemTooltip = extend({}, Tooltip)


function ItemTooltip:New(parent)
	local this = Tooltip:New(parent)
	extend(this, self)

	this:AddText("slots", "slots", setDefault({size = 12}, this.properties.text))
	local mainSectionProperties = setDefault({ headline = { text = "Name"}}, this.properties.section)
	local main = this:AddSection("name", mainSectionProperties)

	local statsSectionProperties = setDefault({ headline = { text = "Stats", size = 12}, border = {enabled = true}}, this.properties.section)
	local stats = main:AddSection("stats", statsSectionProperties)

	local statProperties = setDefault({}, this.properties.stat)
	for statType in statTypes() do
		stats:AddStat(statType, GetStatDisplayName(statType), statProperties)
	end

	local grantSectionProperties = setDefault({ headline = { text = "Grants:", size = 12}, border = {enabled = true}}, this.properties.section)
	local grants = main:AddSection("grants", grantSectionProperties)

	local grantProperties = setDefault({size = 12, font = GetFont("arial"), color = GRANT_COLOR}, this.properties.text)
	for i = 1, MAXIMUM_NUMBER_OF_AURAS do
		grants:AddText("grant" .. i, "aura name", grantProperties)
	end

	local price = Price:New(main, NullPrice())
	main:AddElement("price", price, 6, function(newPrice)
		price:SetValue(newPrice)
	end)

	this.extra = SpellTooltip:New(this)
	return this
end

function ItemTooltip:ShowItem(item)
	if item == self.item then
		return
	end
	self:Reset()

	local grantIndex = 1
	for i, aura in ipairs(item:GetEquipAuras()) do
		for j, mod in ipairs(aura:GetMods()) do
			self:SetText(mod:GetStatType(), mod:GetValue())
		end
		if aura:IsKey() then
			self:SetText("grant" .. grantIndex, GetAuraDisplayName(aura:GetName()))
			grantIndex = grantIndex + 1
		end
	end
	self:SetText("slots", table.concat(item:GetItemTypes(), " "))
	self:SetText("name", item:GetName())
	if item:GetPrice():GetAmount() ~= 0 then
		self:SetText("price", item:GetPrice(), true)
	end

	if item:GetOnClickSpell() then

		self.extra:ShowSpell(item:GetOnClickSpell(), "On click:")
	else
		self.extra:Hide()
	end
	self:BringToFront()
	self:Show()
	self.item = item
end

SpellTooltip = extend({}, Tooltip)

function SpellTooltip:New(parent)
	local this = Tooltip:New(parent)
	extend(this, self)

	this:AddText("heading", "Heading", {text = {color = GRANT_COLOR}})

	local mainSectionProperties = setDefault({ headline = { text = "Name", size = 22, font = GetFont("arial")}}, this.properties.section)
	local main = this:AddSection("name", mainSectionProperties)

	local onTargetEffectSectionProperties = setDefault({ headline = { text = "Effect on target:", size = 14}, border = {enabled = true}}, this.properties.section)
	local onTargetSection = main:AddSection("effectOnTarget", onTargetEffectSectionProperties)
	local effectStatProperties = setDefault({height = 26, font = GetFont("eras")}, this.properties.stat)
	onTargetSection:AddStat("damage1", "Damage", effectStatProperties)
	onTargetSection:AddStat("heal1", "Heal", effectStatProperties)
	onTargetSection:AddStat("cool1", "Cool", effectStatProperties)
	onTargetSection:AddStat("recover", "Recover", effectStatProperties)

	local onCasterEffectSectionProperties = setDefault({ headline = { text = "Effect on caster:", size = 14}, border = {enabled = true}}, this.properties.section)
	local onCasterSection = main:AddSection("effectOnCaster", onCasterEffectSectionProperties)
	onCasterSection:AddStat("damage2", "Damage", effectStatProperties)

	local detailsSectionProperties = setDefault({ headline = { text = "Details:", size = 14}, border = {enabled = true}}, this.properties.section)
	local details = main:AddSection("details", detailsSectionProperties)

	local statProperties = setDefault({}, this.properties.stat)
	details:AddStat("casttime", "Cast time", statProperties)
	details:AddStat("cooldown", "Cooldown", statProperties)
	details:AddStat("heat", "Heat gained", statProperties)
	details:AddStat("range", "Range", statProperties)

	local requiredSectionProperties = setDefault({ headline = { text = "Required:", size = 14}, border = {enabled = true}}, this.properties.section)
	local requiredSection = main:AddSection("requiredSection", requiredSectionProperties)

	local requireProperties = setDefault({size = 12, font = GetFont("arial"), color = GRANT_COLOR}, this.properties.text)
	for i = 1, MAXIMUM_NUMBER_OF_AURAS do
		requiredSection:AddText("required" .. i, "aura name", requireProperties)
	end


	local flavourSectionProperties = setDefault({headline = {size = 12, text = ""}, border = {enabled = true}}, this.properties.section)
	local flavourSection = main:AddSection("flavourSection", flavourSectionProperties)
	flavourSection:AddText("flavour", "Flavour", setDefault({size = 16, font = GetFont("arial"), color = {1.0, 0.9, 0.7, 1.0}}, this.properties.text))
	return this
end

local function BuildActiveAuraIdSet()
	local activeAuras = GetAllAuras(GetSelf())
	local idSet = {}
	for _, aura in pairs(activeAuras) do
		idSet[aura:GetUUID()] = true
	end
	return idSet
end

local function BuildRangeText(effect)
	local min, max = effect:GetRange()
	local rangeText = min
	if min ~= max then
		rangeText = min .. " - " .. max
	end
	return rangeText
end

function SpellTooltip:ShowSpell(spell, heading)
	self:Reset()
	local effects = spell:GetEffects()
	if(#effects == 1 and effects[1]:GetType() == "GRANT_SPELL") then
		spell = effects[1]:GetSpell()
		heading = "Learn " .. heading
	end
	effects = spell:GetEffects()
	local effectInfo = {
		DAMAGE = function(effect)
			local rangeText = BuildRangeText(effect)
			self:SetText("damage1", rangeText)
		end,
		SELF_DAMAGE = function(effect)
			local rangeText = BuildRangeText(effect)
			self:SetText("damage2", rangeText)
		end,
		HEAL = function(effect)
			local rangeText = BuildRangeText(effect)
			self:SetText("heal1", rangeText)
		end,
		COOL = function(effect)
			local rangeText = BuildRangeText(effect)
			self:SetText("cool1", rangeText)
		end,
		RECOVER = function(effect)
			local rangeText = BuildRangeText(effect)
			self:SetText("recover", rangeText)
		end,
		GRANT_SPELL = function()
		end,
		UNKNOWN = function(effect)
			print("Unknown spell effect " .. effect:GetType())
		end
	}
	for k, v in pairs(effects) do
		effectInfo[v:GetType()](v)
	end
	local startRange, endRange = spell:GetRange()
	local range = endRange .. " meters"
	if (startRange ~= 0) then
		range = startRange .. " - " .. range
	end
	self:SetText("range", range)


	local castTime = "Instant"
	if (spell:GetCastTime() > 0) then
		castTime = spell:GetCastTime() / 1000 .. "s"
	end

	self:SetText("heading", heading)
	self:SetText("name", spell:GetName())
	if spell:GetHeat() ~= 0 then
		self:SetText("heat", spell:GetHeat())
	end

	self:SetText("casttime", castTime)
	local cooldowns = spell:GetCooldowns()
	local longestCD = 0
	for index, c in ipairs(cooldowns) do
		local cd = c:GetMax()
		if cd > longestCD then
			longestCD = cd
		end
	end

	if longestCD > 0 then
		local cd = longestCD  .. "s"
		self:SetText("cooldown", cd)
	end

	local flavour = GetSpellFlavour(spell:GetName())
	self:SetText("flavour", flavour)

	local activeAuraIds = BuildActiveAuraIdSet()
	for index, aura in ipairs(spell:GetRequiredAuras()) do
		local name = GetAuraDisplayName(aura:GetName())
		local label = self:SetText("required" .. index, name)
		if activeAuraIds[aura:GetUUID()] then
			label:SetColor(GRANT_COLOR)
		else
			label:SetColor(MISSING_COLOR)
		end
	end

	self:Layout()
	self:BringToFront()
	self:Show()
end


AuraTooltip = extend({}, Tooltip)

function AuraTooltip:New(parent)
	local this = Tooltip:New(parent)
	extend(this, self)

	local mainSectionProperties = setDefault({ headline = { text = "Name", size = 22, font = GetFont("arial")}}, this.properties.section)
	local main = this:AddSection("name", mainSectionProperties)

	main:AddStat("duration", "Duration")
	main:AddStat("timeleft", "Time left")

	local statProperties = setDefault({}, this.properties.stat)
	for statType in statTypes() do
		main:AddStat(statType, GetStatDisplayName(statType), statProperties)
	end

	return this
end

function AuraTooltip:ShowAura(aura)
	self:Reset()
	self:SetText("name", aura:GetName())
	if (aura:GetDuration() > 0) then
		self:SetText("duration", formatTime(aura:GetDuration()))
	end
	if (aura:GetTimeLeft() > -1) then
		self:SetText("timeleft", formatTime(aura:GetTimeLeft()))
	end
	local stats = {}
	for j, mod in ipairs(aura:GetMods()) do
		self:SetText(mod:GetStatType(), mod:GetValue())
	end

	self:Layout()
	self:BringToFront()
	self:Show()
end

-- A tooltip for displaying just a title and a general text
InfoTooltip = extend({}, Tooltip)

function InfoTooltip:New(parent)
	local this = Tooltip:New(parent)
	extend(this, self)

	this:AddText("title", "Title")
	this:AddText("text", "Text")
	return this
end

function InfoTooltip:ShowInfo(title, text)
	self:Reset()
	self:SetText("title", title)
	self:SetText("text", text)
	self:Layout()
	self:BringToFront()
	self:Show()
end