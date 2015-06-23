require("ui/components/borderpanel")
require("ui/components/label")

local stats = BorderPanel:New(uiParent, 128, 72)
stats:SetPoint("TOPRIGHT", uiParent, "TOPRIGHT", -220, -4)
stats:SetColor(0.5, 0.5, 0.5, 0.7)

stats.fps = Label:New(stats, "", 12)
stats.fps:SetPoint("MIDLEFT", stats, "TOPLEFT", 3, -14)
stats.fps:SetColor(0.8, 0.8, 0.8, 0.8)

stats.latency = Label:New(stats, "", 12)
stats.latency:SetPoint("MIDLEFT", stats, "TOPLEFT", 3, -29)
stats.latency:SetColor(0.8, 0.8, 0.8, 0.8)

stats.up = Label:New(stats, "", 12)
stats.up:SetPoint("MIDLEFT", stats, "TOPLEFT", 3, -44)
stats.up:SetColor(0.8, 0.8, 0.8, 0.8)

stats.down = Label:New(stats, "", 12)
stats.down:SetPoint("MIDLEFT", stats, "TOPLEFT", 3, -59)
stats.down:SetColor(0.8, 0.8, 0.8, 0.8)

local totalTime = 0
local totalFrames = 0
stats:AddListener("OnUpdate", function(self, timeElapsed)
	totalFrames = totalFrames + 1
	totalTime = totalTime + timeElapsed
	if totalTime > 0.5 then
		local fps = totalFrames / totalTime
		local down, up, latency = GetNetStats()
		self.fps:SetText(string.format("FPS: %.1f", fps))
		self.latency:SetText(string.format("Latency: %.1dms", latency))
		self.up:SetText(string.format("Up: %.1db/s", up))
		self.down:SetText(string.format("Down: %.1db/s", down))
		totalTime = 0
		totalFrames = 0
	end
end)
