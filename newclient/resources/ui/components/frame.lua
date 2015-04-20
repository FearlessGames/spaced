require("ui/components/container")

do
	local CreateFrame = CreateFrame
	local GetContentPanel = GetContentPanel
	local SetResizable = SetResizable
	local SetTitle = SetTitle

	Frame = extend({}, Container)

	function Frame:New(parent, width, height, title, closable)
		local this, contentPanel = Component:New(CreateFrame(parent.base, width, height, title, closable))
		extend(this, self)
		this.parent = parent
		this.contentPanel = Container:NewRaw(GetContentPanel(this.base))
		return this
	end

	function Frame:SetResizable(resizable)
		SetResizable(self.base, resizable)
	end

	function Frame:GetContentPanel()
		return self.contentPanel
	end

	function Frame:SetTitle(title)
		SetTitle(self.base, title)
	end

end

CreateFrame = nil
GetContentPanel = nil
SetResizable = nil
SetTitle = nil
