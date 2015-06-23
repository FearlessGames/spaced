require"ui/unitfunctions"
require("ui/components/picture")
require("ui/components/label")
require("ui/fonts")

local actionTypes = {
	WOUND = function(attacker, target, damage, school)
		local msg = ""
		if IsSelf(attacker) then
			msg = string.format("You damage %s for %d", target:GetName(), damage)
		elseif(IsSelf(target)) then
			msg = string.format("%s damages you for %d", attacker:GetName(), damage)
		end
		CreateScrollingText(msg, 0.8, 0.5, 0.3)
	end,
	MISS = function(attacker, target, damage, school)
		local msg = ""
		if IsSelf(attacker) then
			msg = string.format("You miss %s", target:GetName())
		elseif(IsSelf(target)) then
			msg = string.format("%s misses you", attacker:GetName())
		end
		CreateScrollingText(msg, 0.7, 0.2, 0.4)
	end,
	ABSORB = function(attacker, target, damage, school)
		local msg = ""
		if IsSelf(attacker) then
			msg = string.format("%s absorbs %d of your damage", target:GetName(), damage)
		elseif(IsSelf(target)) then
			msg = string.format("Your shield absorbs %d of %ss damage ", damage, attacker:GetName())
		end
		CreateScrollingText(msg, 0.2, 0.4, 0.7)
	end,
	HEAL = function(attacker, target, damage, school)
		local msg = ""
		if IsSelf(attacker) then
			msg = string.format("You heal %s for %d", target:GetName(), damage)
		elseif(IsSelf(target)) then
			msg = string.format("%s heals you for %d", attacker:GetName(), damage)
		end
		CreateScrollingText(msg, 0.2, 0.8, 0.2)
	end
}

RegisterEvent("UNIT_COMBAT", function(event, attacker, target, actionType, damage, school)
	if(damage ~= 0) then
		actionTypes[actionType](attacker, target, damage, school)
	end
end)

RegisterEvent("PLAYER_ENTERED_COMBAT", function(event)
	CreateScrollingText(string.format("Entering combat"), 0.3, 0.2, 1)
end)

RegisterEvent("PLAYER_TARGET_CHANGED", function(event)
end)

RegisterEvent("PLAYER_LEFT_COMBAT", function(event)
	CreateScrollingText(string.format("Leaving combat"), 0, 0, 1)
end)

RegisterEvent("ENTITY_GAINED_AURA", function(event, entity, aura)
	if(IsSelf(entity)) then
    	CreateScrollingText(string.format("+ gained %s", aura:GetName()), 0.8, 0.8, 0)
	end
end)
RegisterEvent("ENTITY_LOST_AURA", function(event, entity, aura)
	if(IsSelf(entity)) then
		CreateScrollingText(string.format("- lost %s", aura:GetName()), 0.8, 0.8, 0)
	end
end)

RegisterEvent("UNIT_DIED", function(event, entity)
	if(IsTarget(entity) or IsToT(entity)) then
    	CreateScrollingText(string.format("%s died", entity:GetName()), 0, 0, 1)
	end
end)


-- Scrolling Combat Text
do
	local sctContainer = Container:New(uiParent)
	sctContainer:SetPoint("TOPLEFT", uiParent, "TOPLEFT", 500, 0)
	local lowest
	local minspeed = 100
	local framePool = {}
	function CreateScrollingText(text, r, g, b)
        local f = pairs(framePool)()
        if f then
            framePool[f] = nil
        else
            f = Label:New(sctContainer, "placeholder", 24, GetFont("consolas"))
            f.x = 0
            f.y = 0
            f:AddListener("OnUpdate", function(self, timeElapsed)
            	if not f.speed or f.speed < minspeed then
            		f.speed = minspeed
            	end
                f.y = f.y - f.speed * timeElapsed
                if f.y < -30 then
                    self:Hide()
                    framePool[f] = true
                end
                self:SetPoint("TOPLEFT", sctContainer, "TOPLEFT", f.x, -f.y)
            end)

        end
        f:SetText(text)
        f:SetColor(r, g, b, 1)
        f.x = 0
    		f.y = math.max(230, (lowest and lowest.y or 0) + 30)
    		minspeed = 100 + (f.y - 230)
        f:Show()
        lowest = f
    end
end

