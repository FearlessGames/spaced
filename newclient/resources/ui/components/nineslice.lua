require("ui/components/container")

do
	local CreateNineSlicePanel = CreateNineSlicePanel

	NineSlice = extend({}, Container)

	function NineSlice:New(parent, params, height)
		local this = Container:NewRaw(CreateNineSlicePanel(parent.base, params.width, params.height, params.texture, params.inset))
		extend(this, self)
		return this
	end
end

CreateNineSlicePanel = nil