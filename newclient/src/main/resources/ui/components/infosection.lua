require("ui/util/defaultparams")
require("ui/util/panels")
require("ui/components/container")
require("ui/components/label")
require("ui/components/statbox")
require("ui/fonts")


InfoSection = extend({}, Container)

local defaultParams = {
	width = 100,
	height = 30,
	color = {0.25, 0.25, 0.25, 0.8},
	margin = {
		vertical = 4,
		horizontal = 4
	},
	headline = {
		text = "",
		size = 20,
		font = GetBodyFont(),
		color = {1, 1, 1, 1}
	},
	text = {
		size = 12,
		font = GetFont("arial"),
		color = {1, 1, 0.6, 1 },
	},
	elementSpacing = 2
}

function InfoSection:New(parent, params)
	local properties = setDefault(params, defaultParams)

	local this = Container:New(parent)
	extend(this, self)


	local contentFrame = createPanel(properties.border.enabled, this, properties)
	contentFrame:SetColor(unpack(params.color))

	local heading = Label:New(this, properties.headline.text, properties.headline.size, properties.headline.font)
	heading:SetColor(unpack(properties.headline.color))

	this.contentFrame = contentFrame
	this.heading = heading
	this.properties = properties

	heading:SetPoint("BOTTOMLEFT", contentFrame, "TOPLEFT", 0, 0)

	this.elements = {}
	this.elementsById = {}
	this.updaters = {}

	this:Resize()
	return this
end

function InfoSection:SetHeading(text)
	self.heading:SetText(text)
	self:Resize()
end

function InfoSection:AddStat(identifier, name, properties)
	properties = setDefault(properties, self.properties.stat)
	local statBox = StatBox:New(self.contentFrame, name, properties)
	return self:AddElement(identifier, statBox, self.properties.elementSpacing, function(text) statBox:SetValue(text) end)
end

function InfoSection:AddText(identifier, text, textProperties)
	textProperties = setDefault(textProperties, self.properties.text)
	local label = Label:New(self.contentFrame, text, textProperties.size, textProperties.font)
	label:SetColor(textProperties.color)
	return self:AddElement(identifier, label, self.properties.elementSpacing, function(text) label:SetText(text) end)
end

function InfoSection:AddSection(identifier, properties)
	properties = setDefault(properties, self.properties.section)
	properties = setDefault(properties, self.properties)
	local component = InfoSection:New(self.contentFrame, properties)
	return self:AddElement(identifier, component, properties.elementSpacing, function(text) component:SetHeading(text) end)
end

function InfoSection:AddElement(identifier, component, spacing, updater)

	self.elementsById[identifier] = component
	if not updater then
		updater = function(text) print(string.format("Trying to update %s with %s", identifier, text)) end
	end
	self.updaters[identifier] = updater

	table.insert(self.elements, {component = component, padding = { w = 0, h = spacing}, identifier = identifier})
	self:Resize()

	component:AddListener("OnResize", function() self:Resize() end)
	component:AddListener("OnShow", function() self:Show() end)
	self:AddListener("OnHide", function() component:Hide() end)
	self.identifier = identifier
	return component
end

function InfoSection:GetElementById(identifier)
	local element = self.elementsById[identifier]
	if element then
		return element, self.updaters[identifier]
	end

	for k, v in pairs(self.elementsById) do
		if v.GetElementById then
			local sub, u = v:GetElementById(identifier)
			if	sub then
				return sub, u
			end
		end
	end
end

function InfoSection:SetText(identifier, text, noConversion)
	local t, update = self:GetElementById(identifier)
	assert(t, "No such field: " .. tostring(identifier))
	if not text then
		return
	end
	local finalText = text
	if not noConversion then
		if type(text) == "table" then
			finalText = table.concat(text, "\n")
		else
			finalText = tostring(text)
		end
	end
	update(finalText)
	t:Show()
	return t
end


function InfoSection:Reset()
	self:Hide()
	self:SetSize(100,  30)
end

function InfoSection:Show()
	Component.Show(self)
	self:Resize()
end


function InfoSection:Resize()
	local lastComponent = nil
	for index, component in visible(self.elements) do
		if not lastComponent then
			component.component:SetPoint("TOPLEFT", self.contentFrame, "TOPLEFT", self.properties.margin.horizontal, -self.properties.margin.vertical)
		else
			component.component:SetPoint("TOPLEFT", lastComponent.component, "BOTTOMLEFT", 0, -component.padding.h)
		end
		lastComponent = component
	end

	local height = 0
	local width = 0
	for _, text in visible(self.elements) do
		width = math.max(width, text.component:GetWidth() + text.padding.w)
		height = height + text.padding.h + text.component:GetHeight()
	end
	local twoVerticalMargins = 2 * self.properties.margin.vertical
	local twoHorizontalMargins = 2 * self.properties.margin.horizontal

	local contentWidth = width + twoHorizontalMargins
	local contentHeight = height + twoVerticalMargins


	local hw, hh = self.heading:GetSize()

	local containerWidth = math.max(contentWidth, hw)
	local containerHeight = contentHeight + hh
	self.contentFrame:SetSize(containerWidth, contentHeight)
	self:SetSize(containerWidth, containerHeight)
end

function visible(t)
	local function ipairs_it(t, i)
		i = i+1
		local v = t[i]
		if v ~= nil then
			if v.component:IsVisible() then
				return i,v
			else
				return ipairs_it(t, i)
			end
		else
			return nil
		end
	end
	return ipairs_it, t, 0
end

