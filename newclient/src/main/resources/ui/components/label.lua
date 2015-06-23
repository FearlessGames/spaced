require("ui/components/component")

do
	local CreateLabel = CreateLabel
	local SetAlign = SetAlign
	local SetLabelText = SetLabelText
	local GetLabelText = GetLabelText
	local SetFont = SetFont

	Label = extend({}, Component)


	function Label:New(parent, caption, size, font)
		local this = Component:New(CreateLabel(parent.base, caption, size, font))
		extend(this, self)
		return this
	end

	function Label:NewRaw(label)
		local this = Component:New(label)
		extend(this, self)
		return this
	end

	function Label:SetAlign(align)
		SetAlign(self.base, align)
	end

	function Label:SetText(text)
		SetLabelText(self.base, text)
	end

	function Label:GetText()
		return GetLabelText(self.base)
	end

	function Label:SetFont(size, font)
		SetFont(self.base, size, font)
	end
end

CreateLabel = nil
SetAlign = nil
SetLabelText = nil
GetLabelText = nil
SetFont = nil