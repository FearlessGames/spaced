require("ui/components/component")
require("ui/components/container")
require("ui/components/filledpanel")
require("ui/components/nineslice")
require("ui/util/defaultparams")

do

	local defaults = {
		border = {
			enabled = true,
			texture = "gui/frame/border16.png",
			inset = 4
		},
		color = {1, 1, 1, 1},
		texture = "gui/frame/flatwhite.png",
		contentMargin = 3
	}

	Panel = extend({}, Container)

	function Panel:New(parent, params)

		params = setDefault(params, defaults)
		local this = Container:New(parent)
		extend(this, self)

		local margin = params.contentMargin * 2
		this.margin = margin
		local width = params.width
		local height = params.height

		this.background = FilledPanel:New(this, {width = width, height = height, texture = params.texture})
		this.border = NineSlice:New(this.background, {width = width, height = height, texture = params.border.texture, inset = params.border.inset})
		if not params.border.enabled then
			this.border:Hide()
		end
		this.content = Container:New(this.background)
		this:SetSize(width, height)
		this:SetColor(params.color)
		return this
	end

	function Panel:GetContent()
		return self.content
	end

	function Panel:GetBorder()
		return self.border
	end

	function Panel:SetColor(r, g, b, a)
		Component.SetColor(self.background, r, g, b, a)
	end

	function Panel:SetSize(width, height)
		Component.SetSize(self, width, height)
		self.border:SetSize(width, height)
		self.content:SetSize(width - 2 * self.margin, height - 2 * self.margin)
		self.background:SetSize(width, height)
		self.content:SetPoint("MIDCENTER", self, "MIDCENTER", 0, 0)
		self.border:SetPoint("MIDCENTER", self.background, "MIDCENTER", 0, 0)
	end

end