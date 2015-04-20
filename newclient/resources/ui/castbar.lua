require("ui/unitfunctions")

local t -- time spent casting
local name, casttime, school

local castbarContainer = Picture:New(uiParent, "gui/unitframe/healthbackplate")
castbarContainer:SetWidth(154)
castbarContainer:SetHeight(18)
castbarContainer:SetPoint("TOPLEFT", uiParent, "TOPLEFT", 125, -130)
castbarContainer:Hide()

local castbarFrame = ProgressBar:New(castbarContainer, 150, 16, 0.75, 0.5, 1)
castbarFrame:SetPoint("MIDCENTER", castbarContainer, "MIDCENTER", 0, 0)

local castbarText = Label:New(castbarContainer, "", 14)
castbarText:SetPoint("MIDCENTER", castbarFrame, "MIDCENTER", 0, 0)
castbarText:SetColor(1, 1, 1, 1)

castbarFrame:AddListener("OnUpdate", function(self, timeElapsed)
    if t then
        t = t + timeElapsed
        local p = t / casttime
        castbarFrame:Update(t, casttime, p)
        
        local rem = casttime - t
        --local opacity = (rem < 0.5) and 2*rem or 1
        local opacity = 1
        local red = 0.5 + 0.5*math.cos(2*math.pi*t)
        local green = 1-- + 0.5*math.cos(math.pi*t)
        local blue = 0.5 + 0.5*math.cos(math.pi*t^2)

        castbarFrame:SetColor(red, green, blue, opacity)
        castbarText:SetColor(1, 1, 1, opacity)
        castbarContainer:SetColor(1, 1, 1, opacity)
	end

end)


RegisterEvent("SPELLCAST_STARTED", function(event, attacker, target, spell)
	if IsSelf(attacker) then
		casttime = spell:GetCastTime() / 1000
		t = 0
		castbarFrame:Update(0, casttime, 0)
		castbarText:SetText(spell:GetName())
		castbarContainer:Show()
	end
end)

RegisterEvent("SPELLCAST_STOPPED", function(event, attacker, spell)
    if IsSelf(attacker) then
    	castbarFrame:SetColor(1, 0, 0, 1)
   	castbarFrame:Update(casttime, casttime, 1)
      t = nil
     castbarContainer:Hide()
    end
end)

RegisterEvent("SPELLCAST_COMPLETED", function(event, attacker, spell)
    if IsSelf(attacker) then
        t = nil
        castbarContainer:Hide()
    end
end)

