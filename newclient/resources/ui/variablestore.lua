require("ui/tracker")
require("serialize")

function getAndSet(table, key, defaultValue)
	local old = table[key]
	if old then
		return old
	end
	table[key] = defaultValue
	return defaultValue;
end

do
	local SaveVarsFile = SaveVarsFile

	local savedVars = {}

	VariableStore = {}

	local function loadVars(name, path, namespace)
		local sv = loadfile(path) or loadstring(name .. " = {}")
		setfenv(sv, namespace)
		sv()
	end

	local function getPath(addon)
		return "saved/" .. addon
	end

	function VariableStore:Get(addon)
		local path = getPath(addon)
		if(not savedVars[addon]) then
			loadVars(addon, path, savedVars)
		end
		return savedVars[addon]
	end

	function VariableStore:Save(addon)
		local path = getPath(addon)
		if(savedVars[addon]) then
			local data = addon .. " = " .. pp(savedVars[addon])
			SaveVarsFile(path, data)
		end
	end
end

SaveVarsFile = nil