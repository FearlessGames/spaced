require("lua/core/inheritance")
require("lua/core/component")
require("lua/core/actionbutton")

Frame = extend({}, Component)

function Frame:New(parent, width, height, titleCaption)
	local this = Component:New(parent, width, height)
	extend(this, self)

--[[ This backplate is needed as a layer behind the base frame, i.e: before it in the code	

	backplate = Picture:New(uiParent, "gui/frame/flatwhite")
	backplate:SetWidth(width - 2)
	backplate:SetHeight(height - 2)
	backplate:SetColor(0.0, 0.0, 0.1, 0.4)	

frame "frame" layer   ---------------|-----------------|-------  opaque frame border, rounds off corners and adds dropshadows etc
content layer         ------------------|--|--|--|--|----------  icons for inventory, text for chat, images, buttons etc
frame backplate layer ----------------|---------------|--------  variable transparency background layer
3D game world canvas  -----------------------------------------

	]]
	
	this.base:SetCanBeActive(true)
	this.base:SetDraggable(true)

	local headerHeight = 22
	local header = CreateFrame(width, headerHeight, this.base)
	this.header = header;
	header:SetPoint("TOPCENTER", this.base, "TOPCENTER", 0, -1);
	header:BringToFront()

	local title = Label:New(header, titleCaption, headerHeight - 8)
	this.title = title
	title:SetPoint("MIDCENTER", header, "MIDCENTER", 0, 0)

	local closeButtonTexture = "textures/gui/frame/frame_close_over"
	local closeButton = ActionButton:New(header, closeButtonTexture, headerHeight - 4, headerHeight - 4)
	closeButton:SetPoint("TOPRIGHT", header, "TOPRIGHT", -2, -2)
	closeButton:SetColor(0.99, 0.99, 0.99, 1)

	closeButton:AddClickListener(function()  this:Hide() end)

	local content = CreateContainer(this.base)
	this.content = content
	content:SetSize(width -4, height - (4 + headerHeight))
	this.content:SetPoint("TOPLEFT", header, "BOTTOMLEFT", 6, -4)

	return this
end
