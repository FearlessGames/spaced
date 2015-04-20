local SIZE_START_TAG = "[size=%d]"
local SIZE_END_TAG = "[/size]"
local FONT_START_TAG = "[f=%s]"
local FONT_END_TAG = "[/f]"
function buildText(text, size, font)
	local t = ""
	if(font) then
		t = t .. string.format(FONT_START_TAG, font:GetName())
	end
	t = t	.. string.format(SIZE_START_TAG, size) .. text .. SIZE_END_TAG
	if(font) then
		t = t .. FONT_END_TAG
	end
	--print(t)
	return t
end
