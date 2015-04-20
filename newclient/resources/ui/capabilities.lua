require("ui/components/button")
require("ui/unitfunctions")
require("ui/menu/clickmenu")
require("ui/uisetup")

local function vendorFunction(target)
	return function()
		if (target) then
			GetVendorStock(target)
		end
	end
end

local function tradeFunction(target)
	return function()
		if (target) then
			InitTrade(target)
		end
	end
end

local capabilityToFunction = {
	VENDOR = vendorFunction,
	TRADE = tradeFunction,
}

local function mouseHandler(_, button, x, y)
	if (currentMenu) then
		currentMenu:RemoveListener("OnUpdate")
		currentMenu:Clear()
	end
	if button == "RightButton" then
		currentHoverTarget = GetCurrentHover()
		if (currentHoverTarget and not IsSelf(currentHoverTarget) and IsInRange(currentHoverTarget, INTERACTION_RANGE)) then
			currentMenu = ClickMenu:New(uiParent)
			currentMenu:AddListener("OnUpdate", function (self, timeElapsed)
				if not (IsInRange(currentHoverTarget, INTERACTION_RANGE)) then
					currentMenu:RemoveListener("OnUpdate")
					currentMenu:Clear()
					currentMenu = nil;
				end
			end)
			currentCapabilities = currentHoverTarget:GetInteractionCapabilities()
			for _, v in pairs(currentCapabilities) do
				if capabilityToFunction[v] then
					local outer = capabilityToFunction[v]
					local inner = outer(currentHoverTarget)
					currentMenu:AddButton(v, inner)
				end
			end
			currentMenu:Draw(x, y)
		end
	end
end

local currentMenu

uiParent:AddListener("OnMouseDown", mouseHandler)