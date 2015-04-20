require("lua/core/actionbutton")

local texts = {}
local buttons = {}
local keys = {[0] = "ONE", "TWO", "THREE", "FOUR"}

local space = 80;
local numButtons = 3;
for i = 0, numButtons - 1 do
	-- TODO: This looks really broken, remove?
	buttons[i] = SpellButton:New(uiParent, "textures/gui/icons/IconAttackCrosshair", 70, 70, 1 + i, 1 + i, keys[i])
	texts[i] = Label:New(uiParent, "Ready", 20)
	texts[i]:SetPoint("BOTTOMLEFT", uiParent, "MIDCENTER", space * i, -50)
	buttons[i].base:SetPoint("BOTTOMLEFT", uiParent, "MIDCENTER", space * i, 0)
	buttons[i].base:AddListener("StartAction", function() texts[i]:SetText("Casting") end)
	buttons[i].base:AddListener("PerformAction", function() texts[i]:SetText("Cooldown") end)
	buttons[i].base:AddListener("CancelAction", function() texts[i]:SetText("Canceled") end)
	buttons[i].base:AddListener("ActionReady", function() texts[i]:SetText("Ready") end)
	OnKeyUp(keys[i], function() buttons[i].button:FireEvent("OnClick", "LeftButton") end)
end
