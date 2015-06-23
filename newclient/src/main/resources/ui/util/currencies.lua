require("ui/uisetup")
require("ui/components/picture")

local currencies = {}

do
	currencies["Network credits"] = {icon = "gui/icons/IconNetworkCredits.png"}
	currencies["Republican dollars"] = {icon = "gui/icons/IconRepublicanDollars.png"}
end

function GetCurrencyData(currency)
	return currencies[currency]
end

function GetCurrencySymbol(currency, parent)
	local currencySymbol = Picture:New(parent or uiParent, GetCurrencyData(currency).icon)
	currencySymbol:SetTooltipText(currency)
	return currencySymbol
end

function NullPrice()
	local price = {}
	local meta = {
		__index = {
			GetAmountAsString = function() return "###" end,
			GetCurrency = function() return "Network credits" end,
			GetAmount = function() return 0 end
		}
	}
	setmetatable(price, meta)
	return price
end