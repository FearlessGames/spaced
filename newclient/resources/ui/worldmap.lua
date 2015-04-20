require("ui/components/container")
require("ui/components/picture")
require("ui/components/button")
require("ui/components/label")

WorldMap = extend({}, Container)

WorldMap = {}
function WorldMap:New()
	local this = Container:New(uiParent)
	extend(this, self)

	this.frame = Picture:New(uiParent, "worldmap/landsend1024.png")
	this.frame:SetWidth(600)
	this.frame:SetHeight(600)
	this.frame:SetColor(1.0, 1.0, 1.0, 1.0)
	this.frame:SetPoint("MIDCENTER", uiParent, "MIDCENTER", 0, 0)
	this.frame:Hide()

	this.button = Button:New(this.frame, 20, 20, "")
	this.button:SetPoint("MIDCENTER", this.frame, "TOPRIGHT", -4, -4)
	this.button:AddListener("OnClick", function() this:Toggle() end)

	this.compass = Picture:New(this.frame, "worldmap/compass.png")
	this.compass:SetPoint("TOPLEFT", this.frame, "TOPLEFT", 0, 6)
	this.compass:SetWidth(124)
	this.compass:SetHeight(124)

	this.text = Label:New(this.frame, "Worldmap Prototye", 28)
	this.text:SetPoint("TOPCENTER", this.frame, "TOPCENTER", 0, -22)
	this.text:SetColor(1, 0.9, 0.6, 1)
		
	return this
end

function WorldMap:Toggle()
	if(self.frame:IsVisible()) then
	   self.frame:Hide()
	else
	   self.frame:Show()
	end
end


worldMap = WorldMap:New()
