-- Button state active fakery
local activestate = 0
--
function ToggleGodMode()
	if activestate == 0 then
		SetGodMode(true)
		AcceptRess()
		activestate = 1
	else
		SetGodMode(false)
		activestate = 0
	end
end

