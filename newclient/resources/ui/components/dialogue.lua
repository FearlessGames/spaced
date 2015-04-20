require("ui/components/component")
require("ui/components/borderpanel")
require("ui/components/picture")
require("ui/components/button")
require("ui/components/label")

do
	Dialogue = extend({}, Component)

	function Dialogue:New(parent, title, textureName, buttonText)
		local this = BorderPanel:New(parent, 100, 100)
		extend(this, self)

		local bg = Picture:New(this, textureName)
		this:SetSize(bg:GetSize())
		bg:CenterOn(this)

		local button = Button:New(bg, 64, 64, buttonText)

		button:SetPoint("MIDCENTER", bg, "MIDCENTER", 46, -16)

		button:AddListener("OnClick", function(self)
			this:FireEvent("OnAccept")
		end)


		local header = Label:New(bg, "You are dead!", 20)
		header:SetPoint("MIDCENTER", bg, "MIDCENTER", 50, 42)

		return this
	end
end