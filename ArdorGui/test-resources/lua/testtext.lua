label = Label:New(uiParent, "Label1", 50)
label2 = Label:New(uiParent, "Label2", 70)
label3 = Label:New(uiParent, "Label3", 30)
label4 = Label:New(uiParent, "-", 30)

local width = label:GetWidth()
local height = label:GetHeight()
frame = CreateFrame(200, 100, uiParent)
frame:SetPoint("MIDCENTER", uiParent, "MIDCENTER", 0, 0)
label:SetPoint("TOPCENTER", frame, "BOTTOMCENTER", 0, 0)
label2:SetPoint("BOTTOMLEFT", frame, "TOPRIGHT", 0, 0)
label3:SetPoint("BOTTOMCENTER", frame, "TOPLEFT", 0, 0)
label4:SetPoint("TOPCENTER", label2, "BOTTOMCENTER", 0, 0)

label4:SetText("Label4")
--label4:SetPoint("TOPCENTER", label2, "BOTTOMCENTER", 0, 0)

pic = Picture:New(uiParent, "ball.png")
pic:SetPoint("BOTTOMRIGHT", frame, "TOPLEFT", -10, 0)

local filled = CreateFilledPanel(100, 200, "gui/frame/FrameTest16px.png", uiParent)
filled:SetPoint("BOTTOMRIGHT", pic, "TOPRIGHT", 0, 0)
local border = BorderPanel:New(uiParent, 100, 200)
border:SetPoint("TOPLEFT", filled, "TOPRIGHT", 0, 0)

local chatWidth = 100
local chatFontSize = 20
edit = CreateEditBox(chatWidth - 10, chatFontSize + 11, chatFontSize + 11, uiParent)
edit:SetPoint("TOPCENTER", label, "BOTTOMCENTER", 0, 0)

align1 = Label:New(uiParent, "Align1", 30)
align1:SetAlign("LEFT")
align2 = Label:New(uiParent, "Align2", 30)
align2:SetAlign("MIDDLE")
align3 = Label:New(uiParent, "Align3", 30)
align3:SetAlign("RIGHT")

align1:SetPoint("TOPLEFT", uiParent, "TOPLEFT", 100, 0)
align2:SetPoint("TOPLEFT", uiParent, "TOPLEFT", 100, -50)
align3:SetPoint("TOPLEFT", uiParent, "TOPLEFT", 100, -100)