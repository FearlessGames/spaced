-- Button state active fakery
local activestate = 0
--

local function buttonOnClick(self)
	self:GetParent():Hide()

	-- Button state active fakery
	activestate = 0
	detailsbutton.active:Hide()
end 


local function OnClickOpen(self) -- this button wants to open the Game States / details panel
	
	-- Button state active fakery
	if activestate == 0 then
		detailsbutton.active:Show()
		UnitDetails.frame:Show()
		activestate = 1
	else
		detailsbutton.active:Hide()
		UnitDetails.frame:Hide()
		activestate = 0
	end
end


local function createSfxMixer(parent)
	local dialogue = {}
	dialogue.frame = Picture:New(uiParent, "gui/frame/flatwhite")
	dialogue.frame:SetWidth(170)
	dialogue.frame:SetHeight(100)
	dialogue.frame:SetColor(0.0, 0.0, 0.1, 0.4)
	dialogue.frame.border = BorderPanel:New(dialogue.frame, 172, 102)
	dialogue.frame:SetPoint("TOPLEFT", uiParent, "TOPLEFT", 25, -430)
	dialogue.frame.border:SetPoint("MIDCENTER", dialogue.frame, "MIDCENTER", 0, 0)
	dialogue.button = CreateButton(20, 20, dialogue.frame)
	dialogue.button.frame = BorderPanel:New(dialogue.button, 22, 22)
	dialogue.button.frame:SetColor(1, 1, 1, 1)
	dialogue.button:SetColor(1, 0.6, 0.3, 1)
	dialogue.button:SetPoint("MIDCENTER", dialogue.frame, "TOPRIGHT", -4, -4)
	dialogue.button:AddListener("OnClick", buttonOnClick)
		
	dialogue.button.xtext = Label:New(dialogue.button, "x", 18)
	dialogue.button.xtext:SetPoint("MIDCENTER", dialogue.button, "MIDCENTER", 0, 2)
	dialogue.button.xtext:SetColor(0.3, 0.4, 1, 1)

	dialogue.text = Label:New(dialogue.frame, "Sound Mixer", 14)
	dialogue.text:SetPoint("TOPCENTER", dialogue.frame, "TOPCENTER", 0, -12)
	dialogue.text:SetColor(0.3, 0.4, 1, 1)

	dialogue.healthstats = Label:New(dialogue.frame, "Max Health:", 30)
	dialogue.healthstats:SetFontSize(16)
	dialogue.healthstats:SetPoint("BOTTOMCENTER", dialogue.frame, "BOTTOMCENTER", -3, 37)
	
	dialogue.killstats = Label:New(dialogue.frame, "Kills:", 30)
	dialogue.killstats:SetFontSize(16)
	dialogue.killstats:SetPoint("BOTTOMCENTER", dialogue.frame, "BOTTOMCENTER", -3, 11)
	
	dialogue.deathstats = Label:New(dialogue.frame, "Deaths:", 30)
	dialogue.deathstats:SetFontSize(16)
	dialogue.deathstats:SetPoint("BOTTOMCENTER", dialogue.frame, "BOTTOMCENTER", -3, 24)
	
	
	
	return dialogue
end




UnitDetails = createSfxMixer(playerFrame)
UnitDetails.frame:Hide()

local function UnitDetailsOpen_Create()
	local OpenButton = {}

    detailsbutton = CreateButton(22, 22, uiParent) -- button intended to open details panel
	detailsbutton:AddListener("OnClick", OnClickOpen)
	detailsbutton:SetPoint("TOPLEFT", uiParent, "TOPLEFT", 104, -74)
	detailsbutton:SetColor(0.92,0.95, 1, 1)
---  Button state Active Fakery 
	detailsbutton.active = Picture:New(detailsbutton, "gui/button_overlays/GUIButtonEnabledState")
	detailsbutton.active:SetWidth(22)
	detailsbutton.active:SetHeight(22)
--- 
	
	detailsbutton.text = Label:New(detailsbutton, "sfx", 12)
	detailsbutton.text:SetPoint("MIDCENTER", detailsbutton, "MIDCENTER", 0, 1)
	detailsbutton.text:SetColor(0.1, 0.1, 1, 1)
	detailsbutton.frame = BorderPanel:New(detailsbutton, 24, 24)
	detailsbutton.frame:SetColor(0.92,0.95, 1, 1)
	return OpenButton
end

UnitDetailsOpen = UnitDetailsOpen_Create(uiParent)

---  Button state Active Fakery 
detailsbutton.active:Hide()


