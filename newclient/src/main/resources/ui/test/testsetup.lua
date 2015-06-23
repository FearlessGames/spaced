require("ui/uisetup")
require("se/hiflyer/moc/moc")

local players = {}
local function selfHelper()
	return players["player"]
end

players["player"] = {
	GetName = function()
		return "MyName"
	end,
	GetTarget = function()
		return selfHelper()
	end,
	IsAlive = function()
		return true
	end
}

function GetEntityFromUnitId(id)
	return players[id]
end

function RegisterSlashCommand(...)
end

function UnitPortrait(unit)
	return "icon_player"
end

local function createStats(baseSta, Sta, Cool, Hp, MaxHp, HpRegen, Heat, MaxHeat, ShieldPower, MaxShield)
	local t = {GetBaseStamina = function() return baseSta end,
				GetStamina = function() return Sta end,
				GetCoolRate = function() return Cool end,
				GetCurrentHealth = function() return Hp end,
				GetMaxHealth = function() return MaxHp end,
				GetHealthRegenRate = function() return HpRegen end,
				GetCurrentHeat = function() return Heat end,
				GetMaxHeat = function() return MaxHeat end,
				GetShieldStrength = function() return ShieldPower end,
				GetMaxShield = function() return MaxShield end}
	return t
end

unitStats = {
	[GetEntityFromUnitId("player")] = createStats(3, 5, 1, 30, 35, 0, 20, 100, 45, 80)
}

function UnitStats(entity)
	return unitStats[entity]
end


function GetUnitPosition(entity)
	return 10, 20, 30
end