require("ui/components/container")
require("ui/util/defaultparams")

do
	local CreateFilledPanel = CreateFilledPanel


	local defaultParams = {
		texture = "textures/gui/frame/flatwhite.png"
	}

	FilledPanel = extend({}, Container)

	function FilledPanel:New(parent, params)
		params = setDefault(params, defaultParams)

		local this = Container:NewRaw(CreateFilledPanel(parent.base, params.width, params.height, params.texture))
		this.properties = params
		extend(this, self)
		return this
	end
end

CreateFilledPanel = nil