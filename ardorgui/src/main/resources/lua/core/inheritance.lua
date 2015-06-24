--
-- Generic OO stuff:
--

function extend(index, parent)
	for k, v in pairs(parent) do
		if(k ~= "super" ) then
			index[k] = v
		end
	end
	return index
end

function deep_copy(_table)
   local cloned = {}

	if type(_table) ~= "table" then
		return _table
	else
		for k, v in pairs(_table) do
      	cloned[k] = deep_copy(v)
		end
	end
	
	return cloned
end

function deep_extend(index, parent)
   local cloned = deep_copy(index)

	for k, v in pairs(parent) do
      if type(v) ~= "table" then
      	cloned[k] = v
      else
      	if(index[k] ~= nil) then
      		cloned[k] = deep_extend(index[k], v)
      	else
      		cloned[k] = deep_extend({}, v)
      	end
      end
	end

	return cloned
end