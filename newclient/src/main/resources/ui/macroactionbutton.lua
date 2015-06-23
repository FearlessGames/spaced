require("ui/components/actionbutton")

MacroActionButton = extend({}, ActionButton)

function MacroActionButton:SetCallback(callback)
	self.callback = callback
end

function MacroActionButton:OnClick(event)
	if(self.callback ~= nil) then
		self.callback()
	else
		print("Callback is null")
	end
end

