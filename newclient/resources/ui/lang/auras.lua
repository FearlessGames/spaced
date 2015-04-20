local auraNames = {}
-- To add names here add lines like
--auraNames["aura-name-on-server"] = "Aura name to show up in the UI"
auraNames["melee-equipped"] = "Melee weapon equipped"
auraNames["ranged-equipped"] = "Ranged weapon equipped"


function GetAuraDisplayName(auraName)
    local mapped = auraNames[auraName]
    return mapped or auraName
end