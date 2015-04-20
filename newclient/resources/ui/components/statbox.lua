require("ui/components/component")
require("ui/components/label")
require("ui/util/defaultparams")
require("ui/util/panels")

StatBox = extend({}, Component)

local defaultParams = {
	width = 143,
	height = 20,
	text = {
		color = {1.0, 0.9, 0.7, 1.0 },
		font = GetFont("arial")
	},
	value = {
		color = {1, 1, 1, 1}
	},
	margin = 4,
	border = {
		enabled = true
	},
	color = {1, 1, 1, 1}
}

function StatBox:New(parent, labelText, params)
	params = setDefault(params, defaultParams)

	local this = createPanel(params.border.enabled, parent, params)
	extend(this, self)
	this:SetColor(unpack(params.color))
	this.properties = params
	local margin = params.margin
	this.fontSize = params.height - 2 * margin
	this.text = Label:New(this, labelText, this.fontSize, this.properties.font)
	this.text:SetPoint("MIDLEFT", this, "MIDLEFT", margin, 0)
	this.text:SetColor(unpack(params.text.color))
	this.value = Label:New(this, "###", this.fontSize, this.properties.font)
	this.value:SetPoint("MIDRIGHT", this, "MIDRIGHT", -margin, 0)
	this.value:SetColor(unpack(params.value.color))
	return this
end

function StatBox:SetValue(value)
	self.value:SetText(tostring(value))
end