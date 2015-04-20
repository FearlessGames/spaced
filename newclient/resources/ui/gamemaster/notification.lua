require("ui/chatmodel")

RegisterEvent("GM_FAILURE_NOTIFICATION", function(event, message)
	chatModel:AddLine("[GM] " .. message)
end)

RegisterEvent("GM_SUCCESS_NOTIFICATION", function(event, message)
	local text = "[GM] " .. message
	print(text)
	chatModel:AddLine(text)
end)