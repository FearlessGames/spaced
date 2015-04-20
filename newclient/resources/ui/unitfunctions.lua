function UnitIsReference(entity, reference)
	return entity == GetEntityFromUnitId(reference)
end

function GetSelf()	
	return GetEntityFromUnitId("player")
end

function IsSelf(entity)
	return GetSelf() == entity
end

function GetTarget()
	return GetSelf():GetTarget()
end

function GetToT()
	local target = GetTarget()
	if(target) then
		return target:GetTarget()
end
	return nil
end

function IsTarget(entity)
    return GetTarget() == entity
end


function IsToT(entity)
    return GetToT() == entity
end

local focus
function SetFocusTarget(unit)
	focus = unit
end

function GetFocusTarget()
	return focus
end

function CanAttack(entity)
	return entity and entity:IsAlive() and not IsSelf(entity)
end

function HasTarget()
	return GetTarget() ~= nil
end

function ResolveUnitIds(entity)
	local result = {}
	if(not entity) then
		return result
	end
	if(entity == GetSelf()) then
		table.insert(result, "player")
	end
	if(entity == GetTarget()) then
		table.insert(result, "target")
	end
	if(entity == GetToT()) then
		table.insert(result, "targettarget")
	end
	if(entity == GetFocusTarget()) then
		table.insert(result, "focus")
	end
	return result
end
