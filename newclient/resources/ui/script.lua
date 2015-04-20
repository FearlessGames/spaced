require("ui/chatmodel")
require("ui/textinputhandler")

local function wrap(...)
	return {n = select("#", ...), ...}
end

local function run(fun)
	local t = wrap(pcall(fun))
	if t[1] == true then
		for k, v in ipairs(t) do
			t[k] = tostring(v)
		end
		local returnValues = table.concat(t, ", ", 2, t.n)
		chatModel:AddLine("Result: " .. returnValues)
	else
		chatModel:AddLine("Error: " .. tostring(t[2]))
	end
end

local function slashScript(cmd, s)
	if s:sub(1, 1) == "=" then
		s = "return " .. s:sub(2)
		local f, errmsg = loadstring(s)
		if f then
			run(f)
			return
		end
		chatModel:AddLine("Error: " .. tostring(errmsg))
		return
	end
	
	local f, errmsg = loadstring("return " .. s)
	if f then
		run(f)
		return
	else
		local f, errmsg = loadstring(s)
		if f then
			run(f)
			return
		else
			chatModel:AddLine("Error: " .. tostring(errmsg))
			return
		end
	end		
end

RegisterSlashCommand(slashScript, "Executes a custom lua script", "/script", "/lua")

