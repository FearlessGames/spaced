require("core/inheritance")

DragDrop = {}

local MAX_DRAG_START_DISTANCE = 20
local DRAG_START_MILLIS_PRESSED = 150

function DragDrop:New(parent)
	local this = {}
	extend(this, self)
	this.parent = parent
	this.dropTargets = {}
	return this
end

function DragDrop:IsDragging()
	return self.component ~= nil
end

local function getLayoutData(component)
	local x, y, myPoint, parent, parentPoint = component:GetPoint()
	local data = {x = x, y = y, myPoint = myPoint, parent = parent, parentPoint = parentPoint}
	return data
end

function DragDrop:StartDrag(component)
	if self:IsDragging() then
		self:AbortDrag()
	end
	print("StartDrag", component, component.base)

	self.cx, self.cy = component:GetHudPosition()
	self.mx, self.my = GetMousePosition()
	self.layoutData = getLayoutData(component)
	component:SetParent(self.parent)
	--component:SetPoint("BOTTOMLEFT", self.parent, "BOTTOMLEFT", self.cx, self.cy)
	component:SetPosition(self.cx, self.cy)

	self.component = component

	component:AddListener("OnUpdate", function()
  		self:OnUpdate()
  	end)
end

function DragDrop:FinishDrag()
	local consumed = self:HandleDrop()
	if(not consumed) then
		self:ResetComponent()
	end
	self.component = nil
end

function DragDrop:HandleDrop()
	local mx, my = GetMousePosition()
	for _, listener in pairs(self.dropTargets) do
		if(listener:IsInside(mx, my)) then
			listener:OnDrop(self.component)
			return true
		end
	end
	return false
end

function DragDrop:ResetComponent()
	local data = self.layoutData
	local wrappedParent = {base = data.parent}
	self.component:SetParent(wrappedParent)
	self.component:SetPoint(data.myPoint, wrappedParent, data.parentPoint, data.x, data.y)
	self.component:RemoveListener("OnMouseUp")
end

function DragDrop:AbortDrag()
	self:ResetComponent()
	self.component = nil
end

function DragDrop:RegisterDropTarget(target)
	if(not target.OnDrop) then
		error("No callback OnDrop defined for target")
	end
	table.insert(self.dropTargets, target)
end

function DragDrop:OnUpdate()
	if self.component then
		local mx, my = GetMousePosition()
		local x = self.cx + mx - self.mx
		local y = self.cy + my - self.my
		self.component:SetPosition(x, y)
		--self.component:SetPoint("BOTTOMLEFT", self.parent, "BOTTOMLEFT", x, y)
	end
end


function DragDrop:RegisterDraggable(dragComponent, activationArea)
	activationArea = activationArea or dragComponent
	activationArea:SetCanBeActive(true)
	activationArea:AddListener("OnMouseDown", function(this, button)

		local timeDown = GetTime()
		local mx, my = GetMousePosition()
		activationArea:AddListener("OnUpdate", function(component, timeSinceLast)
			local timePassed = GetTime() - timeDown
			if (timePassed > DRAG_START_MILLIS_PRESSED) then
				activationArea:RemoveListener("OnUpdate")
				local x, y = GetMousePosition()
				if(math.abs(x - mx) < MAX_DRAG_START_DISTANCE and math.abs(y - my) < MAX_DRAG_START_DISTANCE) then
					self:StartDrag(dragComponent)
				end	
			end
		end)
		activationArea:AddListener("OnMouseUp", function()
			activationArea:RemoveListener("OnUpdate")
			self:FinishDrag()
			activationArea:RemoveListener("OnMouseUp")
		end)
	end)
end

dragDrop = DragDrop:New(uiParent)