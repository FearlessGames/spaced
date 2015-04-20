function formatTime(duration)
	if(duration <= 0) then
		return ""
	end
	local minutes = math.floor(duration / 60000)
	local seconds = math.ceil((duration % 60000)/1000)

	if(minutes > 0) then
		return minutes .. " min"
   end
	return seconds .. ""
end