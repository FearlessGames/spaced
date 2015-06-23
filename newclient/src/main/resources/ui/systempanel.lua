function systempanel_Create(prefix)
	local systempanel = {}

	systempanel.state = Picture:New(uiParent, "/gui/systempanel/systemstateborder.png")
	systempanel.frame = Picture:New(systempanel.state, "gui/systempanel/cornerframe.png")

	systempanel.button1back = Picture:New(systempanel.frame, "gui/systempanel/buttonback40.png")
	systempanel.button1back:SetPoint("BOTTOMRIGHT", systempanel.frame, "BOTTOMRIGHT", -3, 3)

	
	systempanel.button2back = Picture:New(systempanel.frame, "gui/systempanel/buttonback40.png")
	systempanel.button2back:SetPoint("MIDRIGHT", systempanel.button1back, "MIDLEFT", -3, 0)

	
	systempanel.button3back = Picture:New(systempanel.frame, "gui/systempanel/buttonback40.png")
	systempanel.button3back:SetPoint("MIDRIGHT", systempanel.button2back, "MIDLEFT", -3, 0)

	
	systempanel.button4back = Picture:New(systempanel.frame, "gui/systempanel/buttonback40.png")
	systempanel.button4back:SetPoint("BOTTOMCENTER", systempanel.button1back, "TOPCENTER", 0, 3)

	
	systempanel.button5back = Picture:New(systempanel.frame, "gui/systempanel/buttonback40.png")
	systempanel.button5back:SetPoint("BOTTOMCENTER", systempanel.button4back, "TOPCENTER", 0, 3)

	

	systempanel.button1 = Picture:New(systempanel.button1back, "gui/icons/IconRadarStateGreen.png")
	systempanel.button1:SetWidth(40)
	systempanel.button1:SetHeight(40)
	systempanel.button2 = Picture:New(systempanel.button2back, "gui/icons/IconTechPathUplink.png")
	systempanel.button2:SetWidth(40)
	systempanel.button2:SetHeight(40)
	systempanel.button3 = Picture:New(systempanel.button3back, "gui/icons/IconPathTechnology.png")
	systempanel.button3:SetWidth(40)
	systempanel.button3:SetHeight(40)
	systempanel.button4 = Picture:New(systempanel.button4back, "gui/icons/IconRadarStateMap.png")
	systempanel.button4:SetWidth(40)
	systempanel.button4:SetHeight(40)
	systempanel.button5 = Picture:New(systempanel.button5back, "gui/icons/IconRadarStatePurple.png")
	systempanel.button5:SetWidth(40)
	systempanel.button5:SetHeight(40)

	return systempanel;
end

playerSystempanel = systempanel_Create("player")
playerSystempanel.state:SetPoint("BOTTOMRIGHT", uiParent, "BOTTOMRIGHT", 0, 0)
