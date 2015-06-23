require("ui/components/container")
require("ui/components/picture")

local stateIcons = {
	normal = "gui/button/normal16.png",
	over = "gui/button/over16.png",
	down = "gui/button/down16.png"
}

Overlay = extend({}, Container)

function Overlay:New(parent)
	local this = Container:New(parent)
	extend(this, self)
	this:SetSize(parent:GetSize())
	local stateOverlay = Picture:New(this, stateIcons["normal"])
	stateOverlay:SetSize(this:GetSize())
	stateOverlay:CenterOn(this)

	this.state = stateOverlay

	local overOverlay = Picture:New(this, stateIcons["over"])
	overOverlay:SetSize(this:GetSize())
	overOverlay:CenterOn(this)
	overOverlay:SetAlpha(0)

	this.over = overOverlay

	return this
end

local function fadeAwayNoShrink(self, timeElapsed)
	if (self:IsVisible()) then
		local a = self:GetAlpha()
		a = a - timeElapsed * 8
		if (a < 0) then
			a = 1
			self:Hide()
			self:RemoveListener("OnUpdate")
		end
		self:SetAlpha(a)
	end
end


local function Update(this)
	if(this.isDown) then
		this.state:SetTexture(stateIcons["down"])
	elseif(this.isMouseOver) then
		this.state:SetTexture(stateIcons["normal"])
		this.over:RemoveListener("OnUpdate")
		this.over:SetAlpha(1)
		this.over:Show()
	else
		this.state:SetTexture(stateIcons["normal"])
		this.over:AddListener("OnUpdate", fadeAwayNoShrink)
	end
end



function Overlay:MouseEnter()
	self.isMouseOver = true
	Update(self)
end

function Overlay:MouseLeave()
	self.isMouseOver = false
	Update(self)
end

function Overlay:MouseDown()
	self.isDown = true
	Update(self)
end

function Overlay:MouseUp()
	self.isDown = false
	Update(self)
end