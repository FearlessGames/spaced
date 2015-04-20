require("ui/uisetup")

local functions = {}

uiParent:AddListener("OnUpdate", function(self, timeElapsed)
	for f, t in pairs(functions) do
		local total = t.current
		local seconds = t.seconds
		total = total + timeElapsed
		if total > seconds then
			f()
			total = 0
		end
		t.current = total
	end
end)

function runEvery(f, seconds)
	functions[f] = {seconds = seconds, current = 0}
end

function cancelTimer(f)
	functions[f] = nil
end

