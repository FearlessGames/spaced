require("ui/components/component")
require("ui/components/fontutil")

do
	local CreateTextArea = CreateTextArea
	local GetTextAreaText = GetTextAreaText
	local SetTextAreaText = SetTextAreaText


	TextArea = extend({}, Component)

	function TextArea:New(parent, width, height, fontsize, font)
		local this = Component:New(CreateTextArea(parent.base, width, height))
		this.size = fontsize
		this.font = font
		this.text = ""
		extend(this, self)
		return this
	end

	function TextArea:SetText(text)
		self.text = text
		SetTextAreaText(self.base, buildText(self.text, self.size, self.font))
	end

	function TextArea:GetText()
		return self.text
	end
end

CreateTextArea = nil
GetTextAreaText = nil
SetTextAreaText = nil