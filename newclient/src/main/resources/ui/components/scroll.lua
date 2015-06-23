require("ui/components/component")

do
	local CreateScrollPanel = CreateScrollPanel
	local SetViewport = SetViewport

	ScrollPanel = extend({}, Component)

	function ScrollPanel:New(parent, width, height, viewport)
		local this = Component:New(CreateScrollPanel(parent.base, width, height, viewport.base))
		extend(this, self)
		return this
	end
end

CreateScrollPanel = nil
SetViewport = nil