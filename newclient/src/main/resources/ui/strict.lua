function setstrict(env)
	env = env or getfenv(0)
	env.__visited__ = {}
	
	local meta = {
		__index = function(t, k)
			local v = env[k]
			if v == nil and not env.__visited__[k] then
				error(k .. " has not been defined in environment")
			end
			return v
		end,
		__newindex = function(t, k, v)
			if env[k] ~= nil then
				error(k .. " is already defined in environment")
			end
			env.__visited__[k] = true
			env[k] = v
		end
	}
	
	-- to avoid crashing the interpreter
	env.__visited__._PROMPT = true
	env.__visited__._PROMPT2 = true
	env.__visited__.debug = true
	
	setfenv(0, setmetatable({}, meta))
end

