require("ui/components/borderpanel")
require("ui/components/picture")
require("ui/components/label")
require("ui/unitfunctions")

local navigationFrame = BorderPanel:New(uiParent, 202, 202)
navigationFrame:SetPoint("TOPRIGHT", uiParent, "TOPRIGHT", -10, -30)
navigationFrame:SetColor(0.5, 0.5, 0.5, 0.7)

navigationFrame.map = Picture:New(navigationFrame, "worldmap/landsend1024.png")
navigationFrame.map:SetPoint("MIDCENTER", navigationFrame, "MIDCENTER", 0, 0)
navigationFrame.map:SetSize(200, 200)

navigationFrame.text = Label:New(navigationFrame, "", 12)
navigationFrame.text:SetPoint("BOTTOMLEFT", navigationFrame, "BOTTOMLEFT", 3, -52)
navigationFrame.text:SetColor(1.0, 1.0, 1.0, 1.0)

navigationFrame.position = Picture:New(navigationFrame, "gui/minimap/player_position.png")
navigationFrame.position:SetSize(16, 16)

	-- navigationFrame.position:SetPoint("MIDCENTER", navigationFrame, "MIDCENTER", 44, 44)
	
local oldx, oldy, oldz
local t = 0
navigationFrame:AddListener("OnUpdate", function(self, timeElapsed)
	local player = GetSelf()

	local x, y, z = GetUnitPosition(player)
	navigationFrame.position:SetPoint("MIDCENTER", navigationFrame, "TOPLEFT", 1 * x/4096*200, - 1 * z/4096*200)

	local rx, ry, rz = player:GetRotation():GetDirection()
	local angle = math.atan2(-rz, -rx)
	navigationFrame.position:Rotate(angle);

	t = t + timeElapsed
	if t > 0.5 then
		local speed = 0
		if oldx then
			speed = math.sqrt((x - oldx)^2 + (y - oldy)^2 + (z - oldz)^2) / t
		end
		t = 0
		oldx, oldy, oldz = x, y, z
		local text = string.format(" x = %.1f, z = %.1f,\n Alt: %.1f m", x, z, y)
		local speedtext = string.format("\n Speed: %.2f m/s", speed)
		navigationFrame.text:SetText(text .. speedtext)
	end
end)
