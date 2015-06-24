print "Running GUI example"

frame = CreateFrame(600, 400, uiParent)
pic = Picture:New(uiParent, "textures/gui/ray")
pic:SetWidth(320)
pic:SetHeight(240)

testButton = CreateButton(100, 40, frame)
testButton:SetPoint("TOPRIGHT", frame, "TOPRIGHT", -5, -5)
testButton:AddListener("OnClick", function (self, button)
	if(button == "LeftButton") then
		self:SetPoint("TOPLEFT", frame, "TOPLEFT", 5, -5)
	elseif (button == "RightButton") then
		self:SetPoint("TOPRIGHT", frame, "TOPRIGHT", -5, -5)
	end
end)
testButton:AddListener("OnMouseEnter", function(self)
	toolTip:Show()
	toolTip:SetText("Hej")
	toolTip:BringToFront()
	toolTip:SetPoint("BOTTOMLEFT", self, "TOPLEFT", 0, 0)
end)
testButton:AddListener("OnMouseLeave", function(self)
	toolTip:SetText("")
	toolTip:Hide()
end)

text = CreateTextField(300, 400, 14, uiParent)
text:SetPoint("TOPLEFT", pic, "TOPLEFT", 0, 0)
input = CreateEditBox(300, 30, 22, uiParent)
input:SetPoint("BOTTOMLEFT", frame, "BOTTOMLEFT", 150, 5)
input:AddListener("OnAction", function(self, textcontents)
	text:AddLine(textcontents)
	self:Clear()
end)

local prog = 0
progressCircle = CreateProgressCircle(80, 80, uiParent)
progressCircle:SetColor(0.0, 0.0, 0.0, 0.5)
progressCircle:AddListener("OnUpdate", function(self, timeElapsed)
    prog = (prog + timeElapsed) % 2
    self:SetProgress(prog >= 1 and 2 - prog or prog)
end)

local p = 0
progressBarPanel = CreateFrame(pic:GetWidth(), 40, uiParent)
progressBarPanel:SetPoint("TOPLEFT", pic, "BOTTOMLEFT", 0, 0)
progressBar = ProgressBar:New(progressBarPanel, pic:GetWidth() - 2, 38, 1, 0.2, 0.4)
progressBar:SetColor(1.0, 0.0, 0.0, 1.0)
progressBar:AddListener("OnUpdate", function(self, timeElapsed)
    p = (p + timeElapsed) % 2
    self:SetProgress(p >= 1 and 2 - p or p)
end)

scrollBar = CreateScrollBar(frame:GetWidth(), 30, uiParent)
scrollBar:SetPoint("TOPLEFT", frame, "BOTTOMLEFT", 0, 0)
name = Label:New(uiParent, "Value: " .. scrollBar:GetValue(), 20)
name:SetPoint("BOTTOMLEFT", scrollBar, "BOTTOMLEFT", 300, 15)
scrollBar:AddListener("ValueChanged", function(self, value)
	name:SetText("Value: " .. value)
end)