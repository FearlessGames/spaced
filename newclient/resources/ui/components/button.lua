require("ui/components/component")

do
	local CreateButton = CreateButton
	local SetButtonText = SetButtonText

	Button = extend({}, Component)

	function Button:New(parent, width, height, caption)
		local this = Component:New(CreateButton(parent.base, width, height, caption))
		extend(this, self)
		return this
	end

	function Button:SetText(text)
		SetButtonText(self.base, text)
	end
end

CreateButton = nil
SetButtonText = nil
