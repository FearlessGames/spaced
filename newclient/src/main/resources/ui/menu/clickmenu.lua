require("ui/components/button")
require("lua/core/inheritance")
require("ui/components/container")


ClickMenu = {}
local buttons = {}

function ClickMenu:New(parentContainer)
	local this = Container:New(parentContainer)
	extend(this, self)
	return this
end

function ClickMenu:AddButton(caption, callback)
	local actionButton = Button:New(self, 40, 40, caption)
	actionButton:Hide()
	actionButton:AddListener("OnClick", function()
		self:Clear()
		callback()
	end)
	table.insert(buttons, actionButton)
end

function ClickMenu:AddMenu(caption, menu)
	local menuButton = Button:New(self, 40, 40, text)
	menuButton:AddListener("OnClick", function()
		self:Clear()
		menu:Draw()
	end)
	menuButton:Hide()
	table.insert(buttons, menuButton)
end

function ClickMenu:Draw(x, y)
	for i, button in ipairs(buttons) do
		if (i == 1) then
			button:SetPoint("TOPLEFT", self, "TOPLEFT", x, y)
		else
			button:SetPoint("TOPLEFT", buttons[i - 1], "TOPRIGHT", 2, 0)
		end
		button:Show()
	end
end

function ClickMenu:Clear()
	self:Hide()
	for k in pairs(buttons) do
		buttons[k] = nil
	end
end

function ClickMenu:Hide()
	for _, button in pairs(buttons) do
		button:Hide()
	end
end

