require("lua/core/inheritance")

do
	local SetPoint = SetPoint
	local SetPosition = SetPosition
	local GetPoint = GetPoint
	local SetVisible = SetVisible
	local SetColor = SetColor
	local GetColor = GetColor
	local SetEnabled = SetEnabled
	local IsVisible = IsVisible
	local AddListener = AddListener
	local RemoveListener = RemoveListener
	local FireEvent = FireEvent
	local GetComponentSize = GetComponentSize
	local SetSize = SetSize
	local BringToFront = BringToFront
	local SetName = SetName
	local GetName = GetName
	local SetAlpha = SetAlpha
	local GetAlpha = GetAlpha
	local SetBorderLayoutData = SetBorderLayoutData
	local HasFocus = HasFocus
	local SetFocus = SetFocus
	local SetCanBeActive = SetCanBeActive
	local Rotate = Rotate
	local SetParent = SetParent
	local SetTooltipText = SetTooltipText
	local GetHudPosition = GetHudPosition
	local GetRect = GetRect


	Component = {}

	function Component:New(baseComponent)
		local this = {base = baseComponent}
		extend(this, self)

		return this
	end

	function Component:SetPoint(selfAnchor, relative, relativeAnchor, x, y)
		SetPoint(self.base, selfAnchor, relative.base, relativeAnchor, x, y)
	end

	function Component:SetPosition(x, y)
		SetPosition(self.base, x, y)
	end

	function Component:GetPoint()
		return GetPoint(self.base)
	end


	function Component:IsVisible()
		return IsVisible(self.base)
	end

	function Component:Hide()
		SetVisible(self.base, false)
	end

	function Component:Show()
		SetVisible(self.base, true)
	end

	function Component:Toggle()
		if(self:IsVisible()) then
			self:Hide()
		else
			self:Show()
		end
	end

	function Component:Enable()
		SetEnabled(self.base, true)
	end

	function Component:Disable()
		SetEnabled(self.base, false)
	end

	function Component:SetColor(r,g,b,a)
		if type(r) == 'table' then
			r, g, b, a = unpack(r)
		end
		SetColor(self.base, r,g,b,a)
	end

	function Component:GetColor()
		return GetColor(self.base)
	end

	function Component:SetAlpha(value)
		SetAlpha(self.base, value)
	end

	function Component:GetAlpha()
		return GetAlpha(self.base)
	end

	function Component:AddListener(event, script)
		AddListener(self.base, event, function(otherself, ...)
			return script(self, ...)
		end)
	end

	function Component:RemoveListener(event)
		RemoveListener(self.base, event)
	end

	function Component:FireEvent(event, ...)
		FireEvent(self.base, event, ...)
	end

	function Component:GetSize()
		return GetComponentSize(self.base)
	end

	function Component:GetWidth()
		local width, height = self:GetSize()
		return width
	end

	function Component:GetHeight()
		local width, height = self:GetSize()
		return height
	end

	function Component:SetSize(width, height)
		SetSize(self.base, width, height)
	end

	function Component:SetWidth(width)
		self:SetSize(width, self:GetHeight())
	end

	function Component:SetHeight(height)
		self:SetSize(self:GetWidth(), height)
	end

	function Component:BringToFront()
		BringToFront(self.base)
	end

	function Component:CenterOn(otherComponent)
		self:SetPoint("MIDCENTER", otherComponent, "MIDCENTER", 0, 0)
	end

	function Component:SetName(name)
		SetName(self.base, name)
	end

	function Component:GetName()
		return GetName(self.base)
	end

	function Component:SetBorderLayoutData(data)
		SetBorderLayoutData(self.base, data)
	end

	function Component:SetFocus()
		SetFocus(self.base)
	end

	function Component:HasFocus()
		return HasFocus(self.base)
	end

	function Component:ClearFocus()
		SetFocus(nil)
	end

	function Component:SetCanBeActive(active)
		SetCanBeActive(self.base, active)
	end

	function Component:Rotate(degrees)
		Rotate(self.base, degrees)
	end

	function Component:RemoveFromParent(deferred)
		SetParent(self.base, nil, deferred)
	end

	function Component:SetParent(parent)
		SetParent(self.base, parent.base)
	end

	function Component:SetTooltipText(text)
		SetTooltipText(self.base, text)
	end

	function Component:GetHudPosition()
		return GetHudPosition(self.base)
	end

	function Component:GetRect()
		return GetRect(self.base)
	end

	function Component:IsInside(x, y)
		local componentX, componentY, w, h = self:GetRect()
		return (x >= componentX) and (x <= componentX + w) and (y >= componentY) and (y <= componentY + h)
	end
end

GetPoint = nil
SetPoint = nil
SetVisible = nil
SetColor = nil
GetColor = nil
SetEnabled = nil
IsVisible = nil
AddListener = nil
RemoveListener = nil
FireEvent = nil
GetComponentSize = nil
SetSize = nil
BringToFront = nil
SetName = nil
GetName = nil
SetAlpha = nil
GetAlpha = nil
SetBorderLayoutData = nil
HasFocus = nil
SetFocus = nil
SetCanBeActive = nil
Rotate = nil
SetParent = nil
SetTooltipText = nil
GetHudPosition = nil
GetRect = nil
SetPosition = nil