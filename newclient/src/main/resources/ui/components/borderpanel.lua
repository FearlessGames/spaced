require("ui/components/nineslice")
require("ui/util/defaultparams")

BorderPanel = extend({}, Container)

local defaults =  {
	texture = "gui/frame/frame16.png",
	inset = 3
}

function BorderPanel:New(parent, params, height)
	if(type(params) ~= "table") then
		params = {width = params, height = height}
	end
	params = setDefault(params, defaults)

	local this = NineSlice:New(parent, params)
	extend(this, self)
	return this
end
