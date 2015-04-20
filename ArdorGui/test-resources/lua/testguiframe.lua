require("lua/core/frame")
require("lua/core/actionbutton")

toolTip:Hide()
local frame1 = Frame:New(uiParent, 400, 300, "Big frame")
local frame2 = Frame:New(uiParent, 300, 200, "Snel frame")
frame2:SetPoint("TOPLEFT", frame1, "BOTTOMRIGHT", 0, 0)

