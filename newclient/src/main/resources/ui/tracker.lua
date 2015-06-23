-- create private index
local index = {}

local mt = {}


function track(t, idx, newIdx)
	local proxy = {}
	proxy[index] = { table = t, indexCallback = idx, newIndexCallback = newIdx }
	setmetatable(proxy, mt)
	return proxy
 end


-- create metatable
mt.__index = function (t,k)
		if(t[index].indexCallback) then
			t[index].indexCallback(t, k, t[index].table[k])
		end
		return t[index].table[k]   -- access the original table
	end
mt.__newindex = function (t,k,v)
		if(type(v) == 'table') then
			t[index].table[k] = track(v, t[index].indexCallback, t[index].newIndexCallback)   -- update original table
		else
			t[index].table[k] = v   -- update original table
		end
		if(t[index].newIndexCallback) then
			t[index].newIndexCallback(t, k, v)
		end
end
