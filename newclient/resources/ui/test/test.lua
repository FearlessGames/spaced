require("ui/uisetup")
require("ui/components/label")
require("ui/components/picture")
require("ui/components/frame")
require("ui/components/filledpanel")
require("ui/components/borderpanel")
require("ui/components/editbox")

local background = FilledPanel:New(uiParent, {width = uiParent:GetWidth(), height = uiParent:GetHeight(), texture = "gui/frame/flatwhite.png"})
background:SetColor(0.3, 0.3, 0.3, 1)

label = Label:New(background, "Label1", 50)
label2 = Label:New(background, "Label2", 70)
label3 = Label:New(background, "Label3", 30)
label4 = Label:New(background, "-", 30)

local width = label:GetWidth()
local height = label:GetHeight()
frame = Frame:New(background, 200, 100, "Frame", false)
frame:SetPoint("MIDCENTER", background, "MIDCENTER", 0, 0)
label:SetPoint("TOPCENTER", frame, "BOTTOMCENTER", 0, 0)
label2:SetPoint("BOTTOMLEFT", frame, "TOPRIGHT", 0, 0)
label3:SetPoint("BOTTOMCENTER", frame, "TOPLEFT", 0, 0)
label4:SetPoint("TOPCENTER", label2, "BOTTOMCENTER", 0, 0)

label4:SetText("Label4")
--label4:SetPoint("TOPCENTER", label2, "BOTTOMCENTER", 0, 0)

pic = Picture:New(background, "gui/rocket.png")
pic:SetPoint("BOTTOMRIGHT", frame, "TOPLEFT", -10, 0)

local filled = FilledPanel:New(background, {width = 100, height = 200, texture = "gui/frame/frame16.png"})
filled:SetPoint("BOTTOMRIGHT", pic, "TOPRIGHT", 0, 0)
local border = BorderPanel:New(background, 100, 200)
border:SetPoint("TOPLEFT", filled, "TOPRIGHT", 0, 0)

local chatWidth = 100
local chatFontSize = 20
edit = EditBox:New(background, chatWidth - 10, chatFontSize + 11, chatFontSize + 11)
edit:SetPoint("TOPCENTER", label, "BOTTOMCENTER", 0, 0)

panel = BorderPanel:New(background, 100, 200)
panel:SetPoint("TOPLEFT", background, "TOPLEFT", 100, 0)

align1 = Label:New(background, "Align1", 30)
align1:SetAlign("LEFT")
align2 = Label:New(background, "Align2", 30)
align2:SetAlign("MIDDLE")
align3 = Label:New(background, "Align3", 30)
align3:SetAlign("RIGHT")

align1:SetPoint("TOPLEFT", background, "TOPLEFT", 100, 0)
align2:SetPoint("TOPLEFT", background, "TOPLEFT", 100, -50)
align3:SetPoint("TOPLEFT", background, "TOPLEFT", 100, -100)