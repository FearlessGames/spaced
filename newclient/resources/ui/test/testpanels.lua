require("ui/uisetup")
require("ui/components/filledpanel")
require("ui/components/panel")
require("ui/components/picture")

local background = FilledPanel:New(uiParent, {width = uiParent:GetWidth(), height = uiParent:GetHeight(), texture = "gui/frame/flatwhite.png"})
background:SetColor(1, 0, 1, 1)

local frame = Panel:New(background, {width = 600, height = 600, border = {texture = "gui/frame/frame16.png", inset = 3}, contentMargin = 3})
frame:SetColor(0.3, 0.8, 0.3, 1)
frame:CenterOn(uiParent)

frame:SetSize(400,400)
frame:CenterOn(uiParent)

local loginFrame = Panel:New(uiParent, {width = 360, height = 300, color = {0.11, 0.95, 0.23, 0.70}, border = {inset = 4}})

local pic = Picture:New(frame:GetContent(), "gui/abilityicons/shieldfield")
pic:SetPoint("TOPLEFT", frame:GetContent(), "TOPLEFT", 0, 0)

background:AddListener("OnMouseDown", function(event, button, x, y)
	frame:SetSize(x, y)
	frame:CenterOn(uiParent)
end)


