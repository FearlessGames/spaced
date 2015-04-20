require("ui/components/container")
require("ui/components/area")
require("ui/components/picture")
require("ui/components/label")
ActionSlot = extend({}, Container)

function ActionSlot:New(actionbar, index, width, height)
	local this = Container:New(actionbar)
	extend(this, self)
	this:SetSize(width, height)
	this:SetName("ActionSlot.base")

	this.transition = Picture:New(this, "textures/gui/button_overlays/GUIButtonMouseOverState.png")
	this.transition:SetSize(width, height)
	this.transition:SetColor(0, 1, 0, 1)
	this.transition:Hide()

	this.button = Area:New(this, width, height)
	this.button:SetName("ActionSlot.button")

	this:SetCanBeActive(true)

	this.button:AddListener("OnMouseDragEnter", function(target)
		if(this.actionButton ~= nil) then
			this.actionButton:Hide()
		end
	   this.transition:Show()
	end)
	this.button:AddListener("OnMouseDragLeave", function(target)
	   this.transition:Hide()
		if(this.actionButton ~= nil) then
			this.actionButton:Show()
		end
	end)
	this.button:AddListener("OnMouseDragDrop", function(target)
		if(GetPickedUpSpell() == nil) then
			return
		end
		if(this.actionButton ~= nil) then
			QueueSpellPickUp(this.actionButton.spell)
		end
		
		local spell = GetSpellByName(GetPickedUpSpell())
	   actionbar:AddSpellButton(index, spell)
	   this.transition:Hide()
	end)
	
	this.actionButton = nil
	this.keyBinding = nil
	this.keyBindingText = ""

	return this
end


function ActionSlot:SetKeyBinding(key, shownText)
	self.keyBinding = key
	self.keyBindingText = tostring(shownText) -- hax for now
end


function ActionSlot:SetActionButton(button)
	if (button ~= nil and self.keyBinding ~= nil) then
		local text_back = Label:New(button, self.keyBindingText, 30)
		text_back:SetPoint("TOPRIGHT", button, "TOPRIGHT", -7, -9)
		text_back:SetFont(12)
		text_back:SetColor(0, 0, 0, 1)

		local text = Label:New(button, self.keyBindingText, 30)
		text:SetPoint("TOPRIGHT", button, "TOPRIGHT", -6, -8)
		text:SetFont(12)
		text:SetColor(1, 1, 0, 1)

		OnKeyDown(self.keyBinding, function() button.button:FireEvent("OnClick", "LeftButton") end)
	elseif (self.actionButton ~= nil) then
		self.actionButton:RemoveFromParent()
		OnKeyDown(self.keyBinding, function() end)
	end
	self.actionButton = button
end
