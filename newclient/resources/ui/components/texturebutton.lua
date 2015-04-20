require("ui/components/component")
require("ui/components/picture")
require("ui/components/area")

TextureButton = extend({}, Component)

local function getColor(self, r, g, b, a)
	if self.status == "MAINTENANCE" then
		return r*1, g*0.1, b*0.1, a*1
	elseif self.status == "UNKNOWN" then
		return r*0.2, g*0.2, b*0.2, a*1
	elseif self.status == "STARTING" then
		return r*0.5, g*0.5, b*0.5, a*1
	elseif self.status == "PROTOCOL_MISMATCH" then
		return r*1, g*1, b*0.2, a*1
	else
		return r*1, g*1, b*1, a
	end
end

function TextureButton:UpdateColor()
	if self.mousedown then
		if self.over then
			self:SetColor(getColor(self, 0.7, 0.7, 0.7, 1))
		else
			self:SetColor(getColor(self, 0.8, 0.8, 0.8, 1))
		end
	else
		if self.over then
			self:SetColor(getColor(self, 1, 1, 1, 1))
		else
			self:SetColor(getColor(self, 0.8, 0.8, 0.8, 1))
		end
	end
end

local function buttonOnEnter(button)
	button.parent.over = true
	button.parent:UpdateColor()
end

local function buttonOnLeave(button)
	button.parent.over = false
	button.parent:UpdateColor()
end

local function buttonOnMouseDown(button)
	button.parent.mousedown = true
	button.parent:UpdateColor()
end

local function buttonOnMouseUp(button)
	button.parent.mousedown = false
	button.parent:UpdateColor()
end

local function bindEvents(button)
	button:AddListener("OnMouseEnter", buttonOnEnter)
	button:AddListener("OnMouseLeave", buttonOnLeave)
	button:AddListener("OnMouseDown", buttonOnMouseDown)
	button:AddListener("OnMouseUp", buttonOnMouseUp)
end

function TextureButton:AddListener(event, script)
	local wrapper = function(...)
		local arguments = {...}
		table.remove(arguments, 1)
		table.insert(arguments, 1, self)
		script(unpack(arguments))
	end
	self.interactionArea:AddListener(event, wrapper)
end

function TextureButton:New(parent, width, height, texture)
	local this = Picture:New(parent, texture)
	extend(this, self)
	this.contentPanel = Container:New(this)
	this:SetSize(width - 2, height - 2)
	this.contentPanel:SetSize(this:GetSize())
	this.contentPanel:CenterOn(this)
	this.interactionArea = Area:New(this, width, height)

	buttonOnLeave(this.interactionArea)
	bindEvents(this.interactionArea)
	return this
end