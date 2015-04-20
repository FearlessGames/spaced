local statTypesInOrder = {}

local function populate()
	table.insert(statTypesInOrder, "ATTACK_RATING")
	table.insert(statTypesInOrder, "STAMINA")
	table.insert(statTypesInOrder, "SHIELD_CHARGE")
	table.insert(statTypesInOrder, "SHIELD_EFFICIENCY")
	table.insert(statTypesInOrder, "SHIELD_RECOVERY")
	table.insert(statTypesInOrder, "COOL_RATE")
	table.insert(statTypesInOrder, "SPEED")
end

function statTypes()
	local i = 0
	local function ipairs_it()
		i = i+1
		local v = statTypesInOrder[i]
		if v ~= nil then
			return v
		else
			return nil
		end
	end
	return ipairs_it
end

populate()
