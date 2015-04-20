require("ui/components/component")
require("ui/components/fontutil")

do
	local CreateEditBox = CreateEditBox
	local GetEditBoxText = GetEditBoxText
	local SetEditBoxText = SetEditBoxText

	EditBox = extend({}, Component)

	function EditBox:New(parent, width, height, fontsize, font)
		local this = Component:New(CreateEditBox(parent.base, width, height, false, fontsize, font))
		extend(this, self)
		return this
	end

	function EditBox:NewPassword(parent, width, height, fontsize, font)
		local this = Component:New(CreateEditBox(parent.base, width, height, true, fontsize, font))
		extend(this, self)
		return this
	end

	function EditBox:SetText(text)
		SetEditBoxText(self.base, text)
	end

	function EditBox:GetText()
		return GetEditBoxText(self.base)
	end

	function EditBox:Clear()
		self:SetText("")
	end
end

CreateEditBox = nil
GetEditBoxText = nil
SetEditBoxText = nil