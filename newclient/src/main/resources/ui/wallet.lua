require("ui/components/component")
require("ui/components/frame")
require("ui/components/label")
require("ui/components/statbox")
require("ui/chatmodel")

local MARGIN = 4
local ROW_HEIGHT = 20

Wallet = extend({}, Component)

function Wallet:New(parent, width, height)
	local this = BorderPanel:New(parent, width, height)
	extend(this, self)

	this.heading = Label:New(this, "Wallet", 18)
	this.heading:SetPoint("TOPCENTER", this, "TOPCENTER", 0, -2)
	this.heading:SetColor(0.7, 1.0, 1.0, 1.0)

	this.currencies = {}
	this:UpdateGui()

	local updateFunction = function(_, currency, amount, total)
		this:UpdateGui()
		this.currencies[currency]:SetValue(total)
	end

	RegisterEvent("MONEY_AWARDED", updateFunction)
	RegisterEvent("MONEY_SUBTRACTED", updateFunction)

	return this
end

function Wallet:UpdateGui()
	local offset = MARGIN
	for currency, total in pairs(GetMoney()) do
		if (self.currencies[currency] == nil) then
			local properties = {width = self:GetWidth() - 2 * MARGIN, height = ROW_HEIGHT}
			self.currencies[currency] = StatBox:New(self, currency, properties)
			self.currencies[currency]:SetValue(total)
			self.currencies[currency]:SetPoint("TOPCENTER", self.heading, "BOTTOMCENTER", 0, -offset)
			offset = offset + ROW_HEIGHT + MARGIN
		end
	end
end