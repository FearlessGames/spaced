require("ui/components/component")

do
	local SetTexture = SetTexture

	Texturable = extend({}, Component)

	function Texturable:SetTexture(texture)
		SetTexture(self.base, texture)
	end
end

SetTexture = nil
