require("ui/components/component")

do
	local CreateRtt = CreateRtt
	local CreateEntityModelRtt = CreateEntityModelRtt

	Rtt = extend({}, Component)

	function Rtt:New(parent, width, height, rttWidth, rttHeight, xmo)
		local this = Component:New(CreateRtt(parent.base, xmo, width, height, rttWidth, rttHeight))
		this.entityModel = CreateEntityModelRtt(xmo, this.base)
		extend(this, self)
		return this
	end

	function Rtt:Load(xmoFile)
		self.entityModel:Load(xmoFile)
	end

	function Rtt:PlayAnimation(animationState)
		self.entityModel:PlayAnimation(animationState)
	end

	
	function Rtt:Update()
		if self:IsVisible() then
			self.entityModel:Update()
		end
	end

	function Rtt:Equip(item, containerType)
		self:EquipModel(item:GetModelPath(), containerType)
	end

	function Rtt:EquipModel(model, containerType)
		self.entityModel:Equip(model, containerType)
	end

	function Rtt:Unequip(containerType)
		self.entityModel:Unequip(containerType)
	end

	function Rtt:SetModelOffset(x, y)
		self.entityModel:SetModelOffset(x, y)
	end

	function Rtt:Rotate(a, b, c, d, e, f, g, h, i)
		self.entityModel:Rotate(a, b, c, d, e, f, g, h, i)
	end

	function Rtt:RotateYAxis(degrees)
		self.entityModel:RotateYAxis(degrees)
	end

	function Rtt:RotateXAxis(degrees)
		self.entityModel:RotateXAxis(degrees)
	end

	function Rtt:RotateZAxis(degrees)
		self.entityModel:RotateZAxis(degrees)
	end
end

CreateRtt = nil
CreateEntityModelRtt = nil

