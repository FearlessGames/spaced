require("ui/test/testsetup")
require("ui/components/tooltip")
require("ui/components/infosection")
require("ui/components/statbox")
--[[
local stat = StatBox:New(uiParent, "Test", {width = {min = 70, max = 120}})
stat:SetValue(45000)
local stat2 = StatBox:New(uiParent, "Test2")
stat:SetWidth(500)
stat:CenterOn(uiParent)
stat2:SetPoint("TOPLEFT", stat, "BOTTOMLEFT", 0, -4)
]]

local tooltip = Tooltip:New(uiParent)
tooltip:SetName("tooltip")


--tooltip:AddText("name", "Name", {size = 22})
--tooltip:AddStat("x", "x")
--tooltip:AddStat("y", "y")

local info = tooltip:AddSection("rangedDamage",{width = 192, headline = {text = "Ranged Weapon"}})
info:SetName("info")

info:AddStat("damage", "Base Dmg"):SetValue("40-56")
info:AddStat("crit", "Critical damage"):SetValue("185%")
info:AddStat("attackTime", "Attack time modifier"):SetValue("75%")

local info2 = tooltip:AddSection("meleeDamage",{width = 192, headline = {text = "Melee Weapon"}})
info2:AddStat("meleeDamage", "Base Dmg"):SetValue("40-56")
info2:AddText("foo", "Some text")

tooltip:Show(100, 100)



--info:CenterOn(uiParent)
--local info2 = InfoSection:New(uiParent, {headline = {text = "Info2", size = 60}})
local counter = 0
uiParent:AddListener("OnMouseDown", function(_, button, x, y)
--	tooltip:SetText("name", button)
	tooltip:SetText("foo", "some text that is way too long to fit inside the frame unless it is resized " .. x)
--	tooltip:SetText("x", x)
--	tooltip:SetText("y", y)
--	tooltip:SetText("attackTime", string.format("%d, %d", x, y))
	--tooltip:Show(x, y)
end)