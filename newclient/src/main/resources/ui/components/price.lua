require("ui/components/component")
require("ui/components/container")
require("ui/util/defaultparams")
require("ui/util/currencies")
require("ui/components/label")

Price = extend({}, Component)

local defaultParams = {
	width = 143,
	height = 20,
	text = {
		color = {1.0, 0.9, 0.7, 1.0 },
		font = GetFont("arial")
	},
	margin = 4,
	space = 6
}

function Price:New(parent, price, params)
	params = setDefault(params, defaultParams)

	local this = Container:New(parent)
	extend(this, self)
	this.properties = params
	this.price = price
	local margin = params.margin
	this.fontSize = params.height - 2 * margin
	this.amount= Label:New(this, price:GetAmountAsString(), this.fontSize, this.properties.font)
	this.amount:SetPoint("MIDLEFT", this, "MIDLEFT", margin, 0)
	this.amount:SetColor(unpack(params.text.color))

	this.symbol = GetCurrencySymbol(price:GetCurrency(), this)
	this.symbol:SetSize(this.fontSize + params.space, this.fontSize + params.space)
	this.symbol:SetPoint("MIDLEFT", this.amount, "MIDRIGHT", params.space, 0)
	return this
end

function Price:SetValue(price)
	if self.price:GetAmount() ~= price:GetAmount() then
		self.amount:SetText(price:GetAmountAsString())
	end
	if self.price:GetCurrency() ~= price:GetCurrency() then
		self.symbol:SetTexture(GetCurrencyData(price:GetCurrency()).icon)
	end
	self.price = price
end