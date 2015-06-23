require("ui/components/container")
require("ui/components/area")
require("ui/components/picture")
require("ui/components/overlay")

ActionButton = extend({}, Container)

local function fadeAway(self, timeElapsed)
	if (self:IsVisible()) then
		local a = self:GetAlpha()
		a = a - timeElapsed * 3
		if (a < 0) then
			a = 1
			self:Hide()
		end
		self:SetSize(84 * (1 - a), 84 * (1 - a))
		self:CenterOn(self.parent)
		self:SetAlpha(a)
	end
end

function ActionButton:ShowTransition()
	self.transition:Show()
	self.transition:SetAlpha(1)
end

function ActionButton:New(parent, iconFile, width, height)
	local this = Container:New(parent)
	extend(this, self)
	this:SetSize(width, height)


	local icon = Picture:New(this, iconFile)
	icon:SetSize(width, height)

	icon:SetPoint("MIDCENTER", this, "MIDCENTER", 0, 0)

	this.overlay = Overlay:New(this)

	local button = Area:New(this, width, height)
	button:SetName("ActionButton.button")
	button:CenterOn(this)

	local function onClick(self, button)
		if this.OnClick then
			this:OnClick(button)
		end
	end
	button:AddListener("OnClick", onClick)

	button:AddListener("OnMouseEnter", function()
		this.overlay:MouseEnter()
	end)
	button:AddListener("OnMouseLeave", function()
		this.overlay:MouseLeave()
	end)
	button:AddListener("OnMouseDown", function()
		this.overlay:MouseDown()
	end)
	button:AddListener("OnMouseUp", function()
		this.overlay:MouseUp()
	end)

	this.transition = Picture:New(this, "textures/gui/button/transit64.png")
	this.transition.parent = this
	this.transition:CenterOn(this)
	this.transition:SetColor(0, 1, 0, 1)
	this.transition:Hide()
	this.transition:AddListener("OnUpdate", fadeAway)

	local function showTransition()
		this:ShowTransition()
	end

	this:AddListener("StartAction", showTransition)
	this:AddListener("PerformAction", showTransition)
	this:AddListener("CancelAction", showTransition)
	this:AddListener("ActionReady", showTransition)

	this.icon = icon
	this.button = button

	return this
end

function ActionButton:SetColor(...)
	self.icon:SetColor(...)
end

function ActionButton:AddClickListener(listener)
	self.button:AddListener("OnClick", listener)
end