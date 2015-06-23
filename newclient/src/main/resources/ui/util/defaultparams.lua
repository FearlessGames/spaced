function setDefault(params, defaultParams)
	if not params then
		params = {}
	end
	if not defaultParams then
		return params
	end
	local meta = getmetatable(params) or {}

	meta.__index = meta.__index or {}
	for k, v in pairs(defaultParams) do
		if not meta.__index[k] then
			meta.__index[k] = v
		end
	end
	setmetatable(params, meta)
	for k, v in pairs(defaultParams) do
		if type(v) == "table" then
			setDefault(params[k], v)
		end
	end
	return params
end
