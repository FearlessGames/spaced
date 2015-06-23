local stats = {}

do
	stats["STAMINA"] = {name = "Stamina", desc = "Increases your maximum hitpoints\nEach point increases your hp by 7"}
	stats["HITPOINTS"] = {name = "Max hit points", desc = ""}
	stats["SHIELD_CHARGE"] = {name = "Shield charge", desc = ""}
	stats["SHIELD_EFFICIENCY"] = {name = "Shield efficiency", desc = ""}
	stats["COOL_RATE"] = {name = "Cool rate", desc = "Increases how much heat is cooled per second"}
	stats["SPEED"] = {name = "Speed modifier", desc = ""}
	stats["SHIELD_RECOVERY"] = {name = "Shield recovery", desc = ""}
	stats["ATTACK_RATING"] = {name = "Attack rating", desc = "" }
	stats["MAX_HEAT"] = {name = "Max heat", desc = "" }
	stats["MAX_SHIELD"] = {name = "Max shield", desc = "" }
	stats["ATTACK_MODIFIER"] = {name = "Attack modifier", desc = "" }
end

local function GetStatData(statName)
	return stats[statName] or {}
end

function GetStatDisplayName(statName)
    local mapped = GetStatData(statName)
    return mapped.name or statName
end

function GetStatDescription(statName)
	local mapped = GetStatData(statName)
	return mapped.desc or ""
end