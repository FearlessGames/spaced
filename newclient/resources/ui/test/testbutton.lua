require("ui/uisetup")
require("ui/components/filledpanel")
require("ui/components/picture")
require("ui/dragdrop")

local dnd  = DragDrop:New(uiParent)

local background = FilledPanel:New(uiParent, {width = uiParent:GetWidth(), height = uiParent:GetHeight(), texture = "gui/frame/flatwhite.png"})
background:SetName("bg")
background:SetColor(0.3, 0.3, 0.3, 1)

local panel = FilledPanel:New(uiParent, {width = 200, height = 150, texture = "gui/frame/flatwhite.png"})
panel:SetName("red")
panel:SetColor(0.9, 0.1, 0.3, 1)
panel:SetPoint("BOTTOMRIGHT", uiParent, "MIDRIGHT", -20, 0)


--local button = ActionButton:New(uiParent, "textures/gui/abilityicons/fortitude", 50, 50)
--button:CenterOn(uiParent)
local picture = Picture:New(panel, "textures/gui/abilityicons/fortitude")
picture:CenterOn(panel)

picture:SetName("MyButton")

local target = Picture:New(uiParent, "textures/gui/abilityicons/heal")
target:SetPoint("BOTTOMLEFT", uiParent, "BOTTOMLEFT", 100, 300)

target.OnDrop = function(target, dragged)
	print("Successful drop", target, dragged)
end

dnd:RegisterDropTarget(target)
dnd:RegisterDraggable(picture)
