require("ui/macroactionbutton")
require("ui/components/container")
require("ui/components/frame")
require("ui/components/label")
require("ui/components/picture")
require("ui/components/button")
require("ui/unitfunctions")

GetItems = {}

function GetItems:New()
	local this = Container:New(uiParent)
	extend(this, self)
	this.frame = Frame:New(uiParent, 600, 300, "GetItems", true)
	this.frame:SetColor(1.0, 1.0, 1.0, 1.0)
	this.frame:SetPoint("MIDCENTER", uiParent, "MIDCENTER", 0, 0)
	this.frame:Hide()

	this.button = Button:New(this.frame, 20, 20, "")
	this.button:SetPoint("MIDCENTER", this.frame, "TOPRIGHT", -4, -4)
	this.button:AddListener("OnClick", function() this:Toggle() end)

	this.text = Label:New(this.frame, "Get Items - WIP", 28)
	this.text:SetPoint("TOPCENTER", this.frame, "TOPCENTER", 0, -22)
	this.text:SetColor(1, 0.9, 0.6, 1)
	
	this.textinfo = Label:New(this.frame, "Beware: klicking all 4 buttons will FILL your inventory with stuff! (No more room after that)", 14)
	this.textinfo:SetPoint("TOPCENTER", this.frame, "TOPCENTER", 0, -52)
	this.textinfo:SetColor(0.1, 0.0, 0.1, 1)

	this.textinfo2 = Label:New(this.frame, "1: Mk5 Set, 2: weapons, 3: Jetpack&Misc, 4: Misc Armor ", 14)
	this.textinfo2:SetPoint("TOPCENTER", this.frame, "TOPCENTER", 0, -82)
	this.textinfo2:SetColor(0.1, 0.0, 0., 1)

	this.itembuttonPicture = Picture:New(this.frame, "items/icons/chest_mk5_red.png")
	this.itembutton = Button:New(this.frame, 64, 64, "")
	this.itembuttonPicture:SetPoint("MIDCENTER", this.itembutton, "MIDCENTER", 0, 0)
	this.itembutton:SetPoint("BOTTOMLEFT", this.frame, "BOTTOMLEFT", 4, 4)
	this.itembutton:AddListener("OnClick", function() this:ArmorMk5() end)
	this.itembuttonPicture:SetPoint("MIDCENTER", this.itembutton, "MIDCENTER", 0, 0)
	
	this.itembutton2Picture = Picture:New(this.frame, "items/icons/combat_knife.png")
	this.itembutton2 = Button:New(this.frame, 64, 64, "")
	this.itembutton2Picture:SetPoint("MIDCENTER", this.itembutton, "MIDCENTER", 0, 0)
	this.itembutton2:SetPoint("BOTTOMLEFT", this.itembutton, "BOTTOMRIGHT", 4, 0)
	this.itembutton2:AddListener("OnClick", function() this:Weapons() end)
	this.itembutton2Picture:SetPoint("MIDCENTER", this.itembutton2, "MIDCENTER", 0, 0)

	this.itembutton4Picture = Picture:New(this.frame, "items/icons/jetpack.png")
	this.itembutton4 = Button:New(this.frame, 64, 64)
	this.itembutton4Picture:SetPoint("MIDCENTER", this.itembutton2, "MIDCENTER", 0, 0)
	this.itembutton4:SetPoint("BOTTOMLEFT", this.itembutton2, "BOTTOMRIGHT", 4, 0)
	this.itembutton4:AddListener("OnClick", function() this:Misc() end)
	this.itembutton4Picture:SetPoint("MIDCENTER", this.itembutton4, "MIDCENTER", 0, 0)
	
	this.itembutton3Picture = Picture:New(this.frame, "items/icons/chest_mk4_red.png")
	this.itembutton3 = Button:New(this.frame, 64, 64)

	this.itembutton3:SetPoint("BOTTOMLEFT", this.itembutton4, "BOTTOMRIGHT", 4, 0)
	this.itembutton3Picture:SetPoint("MIDCENTER", this.itembutton3, "MIDCENTER", 0, 0)
	this.itembutton3:AddListener("OnClick", function() this:ArmorMk4() end)
	this.itembutton3Picture:SetPoint("MIDCENTER", this.itembutton3, "MIDCENTER", 0, 0)
		
	return this
end

local function GM_GetItem(item, quantity)
	GM_GiveItem(GetSelf():GetName(), item, quantity)
end

function GetItems:ArmorMk5()
	GM_GetItem("Mk5 TN Helmet", 1)
	GM_GetItem("Mk5 TN Chest", 1)
	GM_GetItem("Mk5 TN Legs", 1)
	GM_GetItem("Mk5 TN Boots", 1)	
end

function GetItems:ArmorMk4()
	GM_GetItem("Mk4 Helmet", 1)
	GM_GetItem("Mk4 Chest", 1)
	GM_GetItem("Mk4 Legs", 1)	
	GM_GetItem("Mk3 Helmet", 1)
	GM_GetItem("Mk3 Chest", 1)
	GM_GetItem("Mk3 Legs", 1)	
	GM_GetItem("Mk2 Helmet", 1)
	GM_GetItem("Mk2 Chest", 1)
	GM_GetItem("Mk2 Boots", 1)		
	
end

function GetItems:Weapons()
	GM_GetItem("Combat Knife", 2)
	GM_GetItem("e-Blade", 2)
	GM_GetItem("Lazor Pistol", 2)	
	GM_GetItem("Mk1 Assault Rifle", 1)
	GM_GetItem("Plasma Halberd", 1)	
	GM_GetItem("Learn Volatile Combustion", 1)	
	GM_GetItem("Learn Recharge", 1)		
end

function GetItems:Misc()
	GM_GetItem("Beginners Jetpack", 1)	
	GM_GetItem("Shades", 1)	
	GM_GetItem("Leather Hat", 1)	
	GM_GetItem("Cap", 1)
	GM_GetItem("s-11", 1)	
	GM_GetItem("Disc-4", 1)
	GM_GetItem("Simple Wrist Computer", 1)
	GM_GetItem("Tri Claw", 2)	
end

function GetItems:Toggle()
	if(self.frame:IsVisible()) then
	   self.frame:Hide()
	else
	   self.frame:Show()
	end
end


getItems = GetItems:New()
