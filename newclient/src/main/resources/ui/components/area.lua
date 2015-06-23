require("ui/components/container")

do
	local CreateArea = CreateArea

	Area = extend({}, Container)

	function Area:New(parent, width, height)
		local this = Container:NewRaw(CreateArea(parent.base, width, height))
		extend(this, self)
		this.parent = parent
		return this
	end
end

CreateArea = nil