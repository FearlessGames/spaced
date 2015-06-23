require("ui/components/component")
require("ui/components/texturable")

do
	local CreatePicture = CreatePicture

	Picture = extend({}, Component)
	Picture = extend(Picture, Texturable)


	function Picture:New(parent, texture)
		local this = Component:New(CreatePicture(parent.base, texture))
		extend(this, self)
		return this
	end
end

CreatePicture = nil