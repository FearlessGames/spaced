require("ui/components/component")

do
	local CreateContainer = CreateContainer
	local SetBorderLayout = SetBorderLayout
	local SetBorderLayoutData = SetBorderLayoutData

	Container = extend({}, Component)

	function Container:New(parent)
		local this = Component:New(CreateContainer(parent.base))
		extend(this, self)
		return this
	end

	function Container:NewRaw(rawContainer)
		local this = Component:New(rawContainer)
		extend(this, self)
		return this
	end

	function Container:SetBorderLayout()
		SetBorderLayout(self.base)
	end

end

CreateContainer = nil
SetBorderLayout = nil
SetBorderLayoutData = nil
