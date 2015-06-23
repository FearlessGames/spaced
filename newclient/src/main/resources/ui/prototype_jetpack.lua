-- Button state active fakery
local activestate = 0
--
function ToggleJetPack()
	if activestate == 0 then
		SetHelicopterMode(true)
		activestate = 1
	else
		SetHelicopterMode(false)
		activestate = 0
	end
end